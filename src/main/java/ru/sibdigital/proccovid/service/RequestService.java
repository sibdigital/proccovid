package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.PostFormDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSpecification;
import ru.sibdigital.proccovid.utils.DateUtils;
import ru.sibdigital.proccovid.utils.SHA256Generator;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class RequestService {

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    DocAddressFactRepo docAddressFactRepo;

    @Autowired
    DocPersonRepo docPersonRepo;

    @Autowired
    DocRequestRepo docRequestRepo;

    @Autowired
    ClsDepartmentRepo departmentRepo;

    @Autowired
    private DepUserRepo depUserRepo;

    @Autowired
    private RegHistoryRequestRepo historyRequestRepo;

    @Autowired
    private ClsTypeRequestRepo clsTypeRequestRepo;

    @Autowired
    private DocRequestPrsRepo docRequestPrsRepo;

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    private static final int BUFFER_SIZE = 4096;

    public DocRequest addNewRequest(PostFormDto postForm, RequestTypes requestType) {

        String sha256 = SHA256Generator.generate(postForm.getOrganizationInn(), postForm.getOrganizationOgrn(), postForm.getOrganizationName());

        DocRequest docRequest = null;
        try {
            docRequest = docRequestRepo.getTopByOrgHashCode(sha256).orElseGet(() -> null);

        } catch (Exception e) {
            docRequest = null;
        }
        ClsOrganization organization;

        if (docRequest != null) {
            organization = docRequest.getOrganization();
        } else {
            organization = ClsOrganization.builder()
                    .name(postForm.getOrganizationName())
                    .shortName(postForm.getOrganizationShortName())
                    .inn(postForm.getOrganizationInn())
                    .ogrn(postForm.getOrganizationOgrn())
                    .addressJur(postForm.getOrganizationAddressJur())
                    .okvedAdd(postForm.getOrganizationOkvedAdd())
                    .okved(postForm.getOrganizationOkved())
                    .email(postForm.getOrganizationEmail())
                    .phone(postForm.getOrganizationPhone())
                    .statusImport(0)
                    .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                    .idTypeRequest(requestType.getValue())
                    .build();
            organization = clsOrganizationRepo.save(organization);
        }

            List<DocPerson> personList = postForm.getPersons()
                    .stream()
                    .map(personDto -> personDto.convertToPersonEntity())
                    .collect(Collectors.toList());

            List<DocAddressFact> docAddressFactList = postForm.getAddressFact()
                    .stream()
                    .map(personDto -> personDto.convertToDocAddressFact())
                    .collect(Collectors.toList());


        //int importStatus = ImportStatuses.SUCCESS.getValue();
/*
        String filename = "error while upload";
        try {

            File uploadFolder = new File(uploadingDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }
*/
/*
            File file = new File(String.format("%s\\%s_%s",uploadingDir, UUID.randomUUID(),postForm.getAttachment().getOriginalFilename()));
            postForm.getAttachment().transferTo(file);
            filename = file.getName();
*//*


            byte[] valueDecoded = Base64.getDecoder().decode(postForm.getAttachment());

            String inputFilename = String.format("%s/%s_%s", uploadFolder.getAbsolutePath(), UUID.randomUUID(), postForm.getAttachmentFilename());
            FileOutputStream fos;

            fos = new FileOutputStream(inputFilename);
            fos.write(valueDecoded);
            fos.close();
            filename = inputFilename;

        } catch (IOException ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
*/

            String files = postForm.getAttachment();

            docRequest = DocRequest.builder()
                    .organization(organization)
                    .department(departmentRepo.getOne(postForm.getDepartmentId()))
                    .personOfficeCnt(postForm.getPersonOfficeCnt())
                    //.personOfficeFactCnt(postForm.getPersonOfficeFactCnt())
                    .personRemoteCnt(postForm.getPersonRemoteCnt())
                    .personSlrySaveCnt(postForm.getPersonSlrySaveCnt())
                    .attachmentPath(files)
                    .docPersonList(personList)
                    .docAddressFact(docAddressFactList)
                    .statusReview(0)
                    .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                    .statusImport(0)
                    .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                    .timeCreate(Timestamp.valueOf(LocalDateTime.now()))
                    .isAgree(postForm.getIsAgree())
                    .isProtect(postForm.getIsProtect())
                    .reqBasis(postForm.getReqBasis())
                    .orgHashCode(sha256)
                    .typeRequest(clsTypeRequestRepo.getOne((long) requestType.getValue()))
                    .build();

            docRequest = docRequestRepo.save(docRequest);

            DocRequest finalDocRequest = docRequest;
            docRequest.getDocAddressFact().forEach(docAddressFact -> {
                docAddressFact.setDocRequest(finalDocRequest);
            });

            docRequest.getDocPersonList().forEach(docPerson -> {
                docPerson.setDocRequest(finalDocRequest);
            });

            docAddressFactRepo.saveAll(docRequest.getDocAddressFact());
            docPersonRepo.saveAll(docRequest.getDocPersonList());

            return docRequest;
        }


    public DocRequest getLastRequestInfoByInnAndOgrnAndOrganizationName(String inn, String ogrn, String organizationName){

        String sha256 = SHA256Generator.generate(inn, ogrn, organizationName);
        /*ClsOrganization organization = clsOrganizationRepo.getFirstByHashCode(sha256).orElseGet(() -> null);
        if(organization == null) {
            return null;
        }*/

        DocRequest docRequest = docRequestRepo.getTopByOrgHashCode(sha256).orElseGet(() -> null);


        return docRequest;
    }


    public List<DocRequest> getRequestToBeWatchedByDepartment(Long id){
        return this.docRequestRepo.getAllByDepartmentId(id, ReviewStatuses.OPENED.getValue()).orElseGet(()->null);
    }


    @AfterReturning
    public DocRequest setReviewStatus(DocRequest docRequest, ReviewStatuses status){
        docRequest.setTimeReview(Timestamp.valueOf(LocalDateTime.now()));
        docRequest.setStatusReview(status.getValue());
        return docRequestRepo.save(docRequest);
    }




    public DocRequest getLastOpenedRequestInfoByInn(String inn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByInnAndStatus(inn, ReviewStatuses.OPENED.getValue()).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };


    public DocRequest getLastOpenedRequestInfoByOgrn(String ogrn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByOgrnAndStatus(ogrn, ReviewStatuses.OPENED.getValue()).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };

    public DocRequest getLasRequestInfoByInn(String inn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByInn(inn).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };

    public Optional<List<DocRequest>> getFirst100RequestInfoByDepartmentIdAndStatusAndInnOrName(Long departmentId, Integer status, String innOrName){
        //return docRequestRepo.getFirst100RequestByDepartmentIdAndStatusAndInnOrName(departmentId, status, innOrName).orElseGet(() -> null);
        return docRequestRepo.getFirst100RequestByDepartmentIdAndStatusAndInnOrName(departmentId, status, innOrName);
    };


    public DocRequest getLastRequestInfoByOgrn(String ogrn){
        List<DocRequest> docRequests = docRequestRepo.getLastRequestByOgrn(ogrn).orElseGet(() -> null);
        if(docRequests != null) return docRequests.get(0);
        return null;
    };

    public void downloadFile(HttpServletResponse response, DocRequest DocRequest) throws Exception {

        //TODO сделать проверку на наличие нескольких вложений
        // и собрать в архив

        File downloadFile = new File(DocRequest.getAttachmentPath());

        FileInputStream inputStream = new FileInputStream(downloadFile);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        response.setContentType("application/pdf");

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
    }

    public boolean isTokenValid(Integer hash_code){
        Iterator<DepUser> iter = depUserRepo.findAll().iterator();
        while(iter.hasNext()) {
            if(hash_code == iter.next().hashCode()) return true;
        }
        return false;
    }

    public String uploadFile(MultipartFile part){
        try {

            File uploadFolder = new File(uploadingDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            File file = new File(String.format("%s/%s_%s", uploadFolder.getAbsolutePath(),
                    UUID.randomUUID(), part.getOriginalFilename().replace(",", "_")));
            part.transferTo(file);

            return "{ \"status\": \"server\", \"sname\": \"" + String.format("/%s/%s", uploadingDir, file.getName()) + "\" }";

        } catch (IOException ex){
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return "{ \"status\": \"error\" }";
    }

    public List<ClsTypeRequest> getClsTypeRequests() {
        return StreamSupport.stream(clsTypeRequestRepo.findAllByOrderByIdAsc().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Page<DocRequestPrs> getRequestsByCriteria(DocRequestPrsSearchCriteria criteria, int page, int size) {
        DocRequestPrsSpecification specification = new DocRequestPrsSpecification();
        specification.setSearchCriteria(criteria);
        Page<DocRequestPrs> docRequestPrsPage = docRequestPrsRepo.findAll(specification, PageRequest.of(page, size, Sort.by("timeCreate")));
        return docRequestPrsPage;
    }

    public List<DocRequest> getRequestsByCriteria(DocRequestPrsSearchCriteria criteria) {
        DocRequestPrsSpecification specification = new DocRequestPrsSpecification();
        specification.setSearchCriteria(criteria);
        List<DocRequest> docRequests = docRequestRepo.findAll(specification, Sort.by("timeCreate"));
        return docRequests;
    }

    public ClsTypeRequest getClsTypeRequest(Long id) {
        return clsTypeRequestRepo.getOne(id);
    }

    public void generateExcel(List<DocRequest> docRequests, int type, OutputStream outputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        if (type == ReviewStatuses.ACCEPTED.getValue()) {
            XSSFSheet sheet = workbook.createSheet("Уведомления");

            int rowNum = 0;

            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue("Номер заявки");
            cell = row.createCell(1);
            cell.setCellValue("ФИО");
            cell = row.createCell(2);
            cell.setCellValue("Email");
            cell = row.createCell(3);
            cell.setCellValue("Телефон");
            cell = row.createCell(4);
            cell.setCellValue("ИНН");
            cell = row.createCell(5);
            cell.setCellValue("Способ сдачи налоговой отчетности");
            cell = row.createCell(6);
            cell.setCellValue("Район");
            cell = row.createCell(7);
            cell.setCellValue("Адрес");
            cell = row.createCell(8);
            cell.setCellValue("Тип заявки");
            cell = row.createCell(9);
            cell.setCellValue("Дата подачи");

            for (DocRequest docRequest: docRequests) {
                for (DocAddressFact docAddressFact: docRequest.getDocAddressFact()) {
                    row = sheet.createRow(rowNum++);
                    writeDocRequest(docRequest, docAddressFact, row, rowNum);
                }
            }

            workbook.write(outputStream);
        }
    }

    private void writeDocRequest(DocRequest docRequest, DocAddressFact docAddressFact, Row row, Integer num) {
        Cell cell = row.createCell(0);
        cell.setCellValue(docRequest.getId());
        cell = row.createCell(1);
        cell.setCellValue(docRequest.getOrganization().getName());
        cell = row.createCell(2);
        cell.setCellValue(docRequest.getOrganization().getEmail());
        cell = row.createCell(3);
        cell.setCellValue(docRequest.getOrganization().getPhone());
        cell = row.createCell(4);
        cell.setCellValue(docRequest.getOrganization().getInn());
        cell = row.createCell(5);
        if (docRequest.getOrganization().getTypeTaxReporting() == 1) {
            cell.setCellValue("3-НДФЛ");
        } else {
            cell.setCellValue("Налог для самозанятых");
        }
        cell = row.createCell(6);
        cell.setCellValue(docRequest.getDistrict().getName());
        cell = row.createCell(7);
        cell.setCellValue(docAddressFact.getAddressFact());
        cell = row.createCell(8);
        cell.setCellValue(docRequest.getTypeRequest().getShortName());
        cell = row.createCell(9);
        cell.setCellValue(DateUtils.dateToStr(docRequest.getTimeCreate()));
    }
}
