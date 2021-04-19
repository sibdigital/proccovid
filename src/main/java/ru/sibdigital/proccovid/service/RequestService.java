package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.lang.annotation.AfterReturning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.config.ApplicationConstants;
import ru.sibdigital.proccovid.dto.*;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSpecification;
import ru.sibdigital.proccovid.scheduling.ScheduleTasks;
import ru.sibdigital.proccovid.utils.DateUtils;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class RequestService {

    private final static Logger actualizationFilesLogger = LoggerFactory.getLogger("actualizationFilesLogger");

    @Autowired
    private ApplicationConstants applicationConstants;

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
    private OkvedRepo okvedRepo;

    @Autowired
    private ClsDepartmentOkvedRepo clsDepartmentOkvedRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ClsMailingListRepo clsMailingListRepo;

    @Autowired
    private ClsMailingListOkvedRepo clsMailingListOkvedRepo;

    @Autowired
    private ClsDepartmentContactRepo clsDepartmentContactRepo;

    @Autowired
    private RegOrganizationFileRepo regOrganizationFileRepo;

    @Autowired
    private RegDocRequestFileRepo regDocRequestFileRepo;

    @Autowired
    private ClsDistrictRepo clsDistrictRepo;

    @Autowired
    private ClsRoleRepo clsRoleRepo;

    @Autowired
    private RegUserRoleRepo regUserRoleRepo;


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

    public List<ClsTypeRequest> getClsTypeRequests() {
        return StreamSupport.stream(clsTypeRequestRepo.findAll(Sort.by(Sort.Direction.DESC, "id")).spliterator(), false)
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
            try {
                String link = formAddr + "?inn="+ org.getInn();
                String subject = org.getName() + ", " +
                        (actualizeSubject != null ? actualizeSubject.getStringValue()
                        : "актуализируйте утвержденную заявку на портале " + applicationConstants.getApplicationName());
                Map<String, String> params = Map.of(":link", link, "subject", subject);
                if (org.getEmail() != null) {
                    emailService.sendMessage(org.getEmail().trim(), clsTemplate, params);
                }
                count++;
                if (count % 50 == 0){
                   Thread.sleep(10_000L);
                }
            } catch (Exception e) {
                log.error(e.toString());
                e.printStackTrace();
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
                .fullName(clsDepartmentDto.getFullName())
                .description(clsDepartmentDto.getDescription())
                .isDeleted(clsDepartmentDto.getDeleted())
                .build();

        clsDepartmentRepo.save(clsDepartment);

        if (clsDepartmentDto.getOkvedsChanged()) {
            List<ClsDepartmentOkved> list = clsDepartmentOkvedRepo.findClsDepartmentOkvedByDepartment(clsDepartment);
            clsDepartmentOkvedRepo.deleteAll(list);

            List<Okved> listOkveds = clsDepartmentDto.getOkveds();
            List<ClsDepartmentOkved> cdoList = new ArrayList<>();
            for (Okved okved : listOkveds) {
                ClsDepartmentOkved cdo = ClsDepartmentOkved.builder()
                        .department(clsDepartment)
                        .okved(okved)
                        .build();
                cdoList.add(cdo);
            }
            clsDepartmentOkvedRepo.saveAll(cdoList);
        }

        if (clsDepartmentDto.getContactsChanged()) {
            List<ClsDepartmentContact> list2 = clsDepartmentContactRepo.findAllByDepartment(clsDepartment.getId()).orElse(null);
            if (list2 != null) {
                clsDepartmentContactRepo.deleteAll(list2);
            }

            List<ClsDepartmentContactDto> contactDtoList = clsDepartmentDto.getContacts();
            List<ClsDepartmentContact> cdcList = new ArrayList<>();
            for (ClsDepartmentContactDto contactDto : contactDtoList) {
                ClsDepartmentContact cdc = ClsDepartmentContact.builder()
                        .department(clsDepartment)
                        .type(contactDto.getType())
                        .description(contactDto.getDescription())
                        .contactValue(contactDto.getContactValue())
                        .build();
                cdcList.add(cdc);
            }
            clsDepartmentContactRepo.saveAll(cdcList);
        }

        return clsDepartment;
    }

    public Page<ClsUser> getUsersByCriteria(int page, int size) {
        return clsUserRepo.findAll(PageRequest.of(page, size, Sort.by("lastname", "firstname", "patronymic")));
    }

    public List<ClsUser> getClsUsers() {
        return clsUserRepo.findAll(Sort.by("lastname", "firstname", "patronymic"));
    }

    public ClsUser findUserByLogin(String login) {
        return clsUserRepo.findByLogin(login);
    }

    public ClsUser saveClsUser(ClsUserDto clsUserDto) throws Exception {

        ClsDepartment clsDepartment = clsDepartmentRepo.findById(clsUserDto.getDepartmentId()).orElse(null);
        if (clsDepartment == null) {
            throw new Exception("Не указано подразделение");
        }

        ClsDistrict clsDistrict = clsDistrictRepo.findById(clsUserDto.getDistrictId()).orElse(null);
        if (clsDistrict == null) {
            throw new Exception("Не указан район");
        }

        ClsUser clsUser = ClsUser.builder()
                .id(clsUserDto.getId())
                .idDepartment(clsDepartment)
                .district(clsDistrict)
                .lastname(clsUserDto.getLastname())
                .firstname(clsUserDto.getFirstname())
                .patronymic(clsUserDto.getPatronymic())
                .login(clsUserDto.getLogin())
                .isAdmin(clsUserDto.getAdmin())
                .email(clsUserDto.getEmail())
                .status(clsUserDto.getStatus())
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
        saveUserRoles(clsUserDto, clsUser);

        return clsUser;
    }

    public ClsUser saveUserPassword(ClsUser clsUser, String newPassword) {
        clsUser.setPassword(passwordEncoder.encode(newPassword));
        clsUserRepo.save(clsUser);
        return clsUser;
    }

    public ClsUser setStatus(ClsUser clsUser, Integer statusValue) {
        clsUser.setStatus(statusValue);
        clsUserRepo.save(clsUser);
        return clsUser;
    }

    void saveUserRoles(ClsUserDto clsUserDto, ClsUser user) {
        Map<Long, ClsRole> roleMap = getAllRoles();

        List<UserRolesEntityDto> ured = clsUserDto.getUserRoles();
        Set<Long> activeRoleIds = ured.stream()
                                    .filter(ctr -> ctr.getStatus())
                                    .map(ctr -> ctr.getId())
                                    .collect(Collectors.toSet());

        List<RegUserRole> oldRoles = regUserRoleRepo.findAllByUser(user);
        Set<Long> oldRoleIds = oldRoles.stream()
                                .map(ctr -> ctr.getRole().getId())
                                .collect(Collectors.toSet());

        Set<Long> deletedRoleIds = (Set<Long>) getDifferences(oldRoleIds, activeRoleIds);
        regUserRoleRepo.deleteAllByUserIdAndRoleIds(user.getId(), deletedRoleIds);

        Set<Long> addedRoleIds = (Set<Long>) getDifferences(activeRoleIds, oldRoleIds);
        List<RegUserRole> rurs = new ArrayList<>();
        addedRoleIds.forEach(ctr -> {
            ClsRole role = roleMap.get(ctr);
            RegUserRole rur = RegUserRole.builder()
                                .user(user)
                                .role(role)
                                .build();
            rurs.add(rur);
        });
        regUserRoleRepo.saveAll(rurs);


    }

    private Map<Long, ClsRole> getAllRoles() {
        List<ClsRole> roleList = clsRoleRepo.findAll();
        Map<Long, ClsRole> roleMap = roleList.stream()
                                .collect(Collectors.toMap(ClsRole::getId, clsRole -> clsRole));
        return roleMap;
    }

    private <T> Set<T> getDifferences(Set<T> decreasing, Set<T> substruction) {// уменьшаемое, вычитаемое
        Set<T> difference = new HashSet<T>();
        difference.addAll(decreasing);
        difference.removeAll(substruction);
        return difference;
    }


    public ClsMailingList saveClsMailingList(ClsMailingListDto clsMailingListDto) {

        ClsMailingList clsMailingList = ClsMailingList.builder()
                .id(clsMailingListDto.getId())
                .name(clsMailingListDto.getName())
                .description(clsMailingListDto.getDescription())
                .status(clsMailingListDto.getStatus())
                .build();

        clsMailingListRepo.save(clsMailingList);

        List<ClsMailingListOkved> list = clsMailingListOkvedRepo.findClsMailingListOkvedByClsMailingList(clsMailingList);
        clsMailingListOkvedRepo.deleteAll(list);

        List<Okved> listOkveds = clsMailingListDto.getOkveds();
        for (Okved okved : listOkveds) {
            ClsMailingListOkved clsMailingListOkved = new ClsMailingListOkved();
            clsMailingListOkved.setClsMailingList(clsMailingList);
            clsMailingListOkved.setOkved(okved);
            clsMailingListOkvedRepo.save(clsMailingListOkved);
        }

        return clsMailingList;
    }

    public List<ClsMailingList> getClsMailingList() {
        return StreamSupport.stream(clsMailingListRepo.findAllByOrderByIdAsc().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<ClsDepartmentContact> getAllClsDepartmentContactByDepartmentId(Long id){
        return clsDepartmentContactRepo.findAllByDepartment(id).orElse(null);
    }

    /**
     * Метод для актуализации файлов заявок
     * @param requests
     * @param fileMap
     */
    @Transactional
    public long actualizeFiles(List<DocRequest> requests, Map<String, String> fileMap) {
        long countActualized = 0;
        if (requests == null) {
            return countActualized;
        }
        // добавим файлы
        final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();
        for (DocRequest request: requests) {
            if (request.getAttachmentPath() == null || request.getAttachmentPath().isBlank()) {
                actualizationFilesLogger.warn("Пустой attachmentPath. Ид заявки {}.", request.getId());
                continue;
            }
            String path = request.getAttachmentPath();
            String[] fullFileNames = getFileNamesFromPath(path);
            if (fullFileNames.length == 0) {
                actualizationFilesLogger.warn("Не удалось обработать attachmentPath. Ид заявки {}. {}", request.getId(), path);
                continue;
            }
            ClsOrganization organization = request.getOrganization();
            for (String fullFileName : fullFileNames) {
                File currentFile = new File(String.format("%s/%s", absolutePath, fullFileName));
                if (!currentFile.exists()) {
                    actualizationFilesLogger.warn("Файл не найден. ИД заявки {}. {}", request.getId(), fullFileName);
                    continue;
                }
                String fileExtension = getFileExtension(fullFileName);
                // переименуем файл
                final String newFileName = organization.getId().toString() + "_" + UUID.randomUUID() + fileExtension;
                fileMap.put(fullFileName, newFileName);
                //
                final String fileHash = getFileHash(currentFile);
                long size = 0;
                try {
                    size = Files.size(currentFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // сохраним информацию о файле
                RegOrganizationFile regOrganizationFile = RegOrganizationFile.builder()
                        .clsOrganizationByIdOrganization(organization)
                        .docRequestByIdRequest(request)
                        .attachmentPath(String.format("%s/%s", uploadingDir, newFileName))
                        .fileName(newFileName)
                        .originalFileName(fullFileName)
                        .isDeleted(false)
                        .fileExtension(fileExtension)
                        .fileSize(size)
                        .hash(fileHash)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .build();
                regOrganizationFileRepo.save(regOrganizationFile);
                // привяжем файл к заявке согласно новой схеме
                RegDocRequestFile regDocRequestFile = RegDocRequestFile.builder()
                        .request(request)
                        .organizationFile(regOrganizationFile)
                        .build();
                regDocRequestFileRepo.save(regDocRequestFile);
                countActualized++;
            }
        }
        return countActualized;
    }

    private String getFileHash(File file){
        String result = "NOT";
        try {
            final byte[] bytes = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
            result = DatatypeConverter.printHexBinary(hash);
        } catch (IOException ex) {
            actualizationFilesLogger.error(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            actualizationFilesLogger.error(ex.getMessage());
        }
        return result;
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    private String[] getFileNamesFromPath(String value) {
        if (value == null) {
            return new String[0];
        }
        List<String> fileNames = new ArrayList<>();
        String[] paths = value.split(",");
        for (String path: paths) {
            String[] split = path.split("\\\\");
            split = split[split.length - 1].split("/");
            String fileName = split[split.length - 1];
            if (!fileName.contains(".")) {
                return new String[0];
            }
            fileNames.add(fileName);
        }
        return fileNames.toArray(new String[0]);
    }
}
