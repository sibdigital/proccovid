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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsDepartmentDto;
import ru.sibdigital.proccovid.dto.ClsTypeRequestDto;
import ru.sibdigital.proccovid.dto.ClsUserDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSpecification;
import ru.sibdigital.proccovid.utils.DateUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
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
    ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    private ClsUserRepo clsUserRepo;

    @Autowired
    private RegHistoryRequestRepo historyRequestRepo;

    @Autowired
    private ClsTypeRequestRepo clsTypeRequestRepo;

    @Autowired
    private DocRequestPrsRepo docRequestPrsRepo;

    @Autowired
    private ClsTemplateRepo clsTemplateRepo;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SettingService settingService;

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    private static final int BUFFER_SIZE = 4096;

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
        Iterator<ClsUser> iter = clsUserRepo.findAll().iterator();
        while(iter.hasNext()) {
            if(hash_code == iter.next().hashCode()) return true;
        }
        return false;
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

    public Page<ClsTemplate> findAllClsTemplate(int page, int size) {
        return clsTemplateRepo.findAll(PageRequest.of(page, size, Sort.by("key")));
    }

    public Page<ClsPrincipal> getPrincipalsByCriteria(int page, int size) {
        return clsPrincipalRepo.findAll(PageRequest.of(page, size, Sort.by("organization.inn")));
    }

    private ClsOrganization buldOrg(Object[] obj){
        return ClsOrganization.builder()
                .inn((String)obj[0])
                .email((String)obj[1])
                .name((String)obj[2])
                .build();
    }

    public List<ClsOrganization> getOrganizationsEmailsByDocRequestStatus(int reviewStatus) {
        return clsOrganizationRepo.getOrganizationsEmailsByDocRequestStatus(reviewStatus).stream()
                .map(this::buldOrg)
                .collect(Collectors.toList());
    }

    public List<ClsOrganization> getOrganizationsEmailsByDocRequestStatusLast24HoursNotMailing(int reviewStatus, int mailingStatus, Date currDate) {
        return clsOrganizationRepo
                .getOrganizationsEmailsByDocRequestStatusLast24HoursNotMailing(reviewStatus, mailingStatus, currDate).stream()
                .map(this::buldOrg)
                .collect(Collectors.toList());
    }

    public void sendMessageToPrincipals(String type) {
        if (type == null) {
            return;
        }

        ClsTemplate clsTemplate = clsTemplateRepo.findByKey(type);
        if (clsTemplate == null) {
            return;
        }

        int size = 25;
        Page<ClsPrincipal> pagePrincipals = getPrincipalsByCriteria(0, size);
        if (pagePrincipals == null || pagePrincipals.getTotalElements() == 0) {
            return;
        }

        int totalPage = pagePrincipals.getTotalPages();
        for (int page = 0; page < totalPage; page++) {
            pagePrincipals = getPrincipalsByCriteria(page, size);
            emailService.sendMessage(pagePrincipals.getContent(), clsTemplate, new HashMap<>());
        }
    }

    public void sendMessageToOrganizations(String type) {
        if (type == null) {
            return;
        }

        ClsTemplate clsTemplate = clsTemplateRepo.findByKey(type);
        if (clsTemplate == null) {
            return;
        }

        List<ClsOrganization> organizationsEmails = getOrganizationsEmailsByDocRequestStatus(ReviewStatuses.CONFIRMED.getValue());
        sendMessage(organizationsEmails, clsTemplate);
    }

    public void sendMessageToOrganizations24(String type) {
        if (type == null) {
            return;
        }

        ClsTemplate clsTemplate = clsTemplateRepo.findByKey(type);
        if (clsTemplate == null) {
            return;
        }

        List<ClsOrganization> organizationsEmails =
                getOrganizationsEmailsByDocRequestStatusLast24HoursNotMailing(ReviewStatuses.CONFIRMED.getValue(),
                        MailingStatuses.EMAIL_SENT.value(), new Date());
        sendMessage(organizationsEmails, clsTemplate);
    }

    public void sendEmailTestMessage(String type) {
        if (type == null) {
            return;
        }

        ClsTemplate clsTemplate = clsTemplateRepo.findByKey(type);
        if (clsTemplate == null) {
            return;
        }

        ClsSettings testEmailSetting = settingService.findActualByKey("testEmail");
        String testEmail = testEmailSetting != null ? testEmailSetting.getStringValue() : "";

        List<ClsOrganization> organizationsEmails = List.of(ClsOrganization.builder()
                .inn("test")
                .email(testEmail)
                .name("test")
                .build());
        sendMessage(organizationsEmails, clsTemplate);
    }



    private void sendMessage(List<ClsOrganization> organizationsEmails, ClsTemplate clsTemplate){

        ClsSettings actualizeSubject = settingService.findActualByKey("actualizeSubject");
        ClsSettings actualizeFormAddr = settingService.findActualByKey("actualizeFormAddr");

        String formAddr = actualizeFormAddr != null ? actualizeFormAddr.getStringValue()
                :"http://rabota.govrb.ru/actualize_form"; //TODO в settings!
        int count = 0;
        for (ClsOrganization org  : organizationsEmails) {
            String link = formAddr + "?inn="+ org.getInn();
            String subject = org.getName() + ", " +
                    (actualizeSubject != null ? actualizeSubject.getStringValue()
                    : "актуализируйте утвержденную заявку на портале Работающая Бурятия");
            Map<String, String> params = Map.of(":link", link, "subject", subject);
            emailService.sendMessage(org.getEmail(), clsTemplate, params);
            count++;
            if (count % 50 == 0){
                try {
                    Thread.sleep(10_000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ClsTypeRequest saveClsTypeRequest(ClsTypeRequestDto clsTypeRequestDto) {

        ClsDepartment clsDepartment = null;
        if (clsTypeRequestDto.getDepartmentId() != null) {
            clsDepartment = clsDepartmentRepo.findById(clsTypeRequestDto.getDepartmentId()).orElse(null);
        }

        ClsTypeRequest clsTypeRequest = ClsTypeRequest.builder()
                .id(clsTypeRequestDto.getId())
                .activityKind(clsTypeRequestDto.getActivityKind())
                .shortName(clsTypeRequestDto.getShortName())
                .department(clsDepartment)
                .prescription(clsTypeRequestDto.getPrescription())
                .prescriptionLink(clsTypeRequestDto.getPrescriptionLink())
                .settings(clsTypeRequestDto.getSettings())
                .statusRegistration(clsTypeRequestDto.getStatusRegistration())
                .beginRegistration(clsTypeRequestDto.getBeginRegistration())
                .endRegistration(clsTypeRequestDto.getEndRegistration())
                .statusVisible(clsTypeRequestDto.getStatusVisible())
                .beginVisible(clsTypeRequestDto.getBeginVisible())
                .endVisible(clsTypeRequestDto.getEndVisible())
                .sortWeight(clsTypeRequestDto.getSortWeight())
                .build();

        clsTypeRequestRepo.save(clsTypeRequest);

        return clsTypeRequest;
    }

    public ClsDepartment saveClsDepartment(ClsDepartmentDto clsDepartmentDto) {

        ClsDepartment clsDepartment = ClsDepartment.builder()
                .id(clsDepartmentDto.getId())
                .name(clsDepartmentDto.getName())
                .description(clsDepartmentDto.getDescription())
                .isDeleted(clsDepartmentDto.getDeleted())
                .build();

        clsDepartmentRepo.save(clsDepartment);

        return clsDepartment;
    }

    public Page<ClsUser> getUsersByCriteria(int page, int size) {
        return clsUserRepo.findAll(PageRequest.of(page, size, Sort.by("lastname", "firstname", "patronymic")));
    }

    public ClsUser findUserByLogin(String login) {
        return clsUserRepo.findByLogin(login);
    }

    public ClsUser saveClsUser(ClsUserDto clsUserDto) {

        ClsDepartment clsDepartment = clsDepartmentRepo.findById(clsUserDto.getDepartmentId()).orElse(null);

        ClsUser clsUser = ClsUser.builder()
                .id(clsUserDto.getId())
                .idDepartment(clsDepartment)
                .lastname(clsUserDto.getLastname())
                .firstname(clsUserDto.getFirstname())
                .patronymic(clsUserDto.getPatronymic())
                .login(clsUserDto.getLogin())
                .isAdmin(clsUserDto.getAdmin())
                .build();

        if (clsUserDto.getNewPassword() != null && !clsUserDto.getNewPassword().isBlank()) {
            clsUser.setPassword(passwordEncoder.encode(clsUserDto.getNewPassword()));
        } else {
            if (clsUserDto.getId() != null) {
                ClsUser currentUser = clsUserRepo.findById(clsUserDto.getId()).orElse(null);
                if (currentUser != null) {
                    clsUser.setPassword(currentUser.getPassword());
                }
            }
        }

        clsUserRepo.save(clsUser);

        return clsUser;
    }
}
