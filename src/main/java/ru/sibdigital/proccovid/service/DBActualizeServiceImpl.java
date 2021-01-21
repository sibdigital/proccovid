package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Сервис для актуализации БД под новую схему данных
 *
 */
@Slf4j
@Service
public class DBActualizeServiceImpl implements DBActualizeService {

    @Value("${upload.path}")
    private String uploadingDir;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private EgrulService egrulService;

    @Autowired
    private OkvedRepo okvedRepo;

    @Autowired
    private RegOrganizationOkvedRepo regOrganizationOkvedRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private DocEmployeeRepo docEmployeeRepo;

    @Autowired
    private RegOrganizationAddressFactRepo regOrganizationAddressFactRepo;

    @Transactional
    public long actualizeOrganizations(List<ClsOrganization> organizations) {
        long countActualized = 0;
        if (organizations == null) {
            return countActualized;
        }
        for (ClsOrganization organization : organizations) {
            if (organization.getActualized() != null && organization.getActualized()) {
                continue;
            }
            // добавим окведы
            addDataFromEgrul(organization);
            // актуализируем заявки
            List<DocRequest> actualRequests = actualizeRequests(organization);
            // добавим сотрудников, адреса
            addDataFromRequests(organization, actualRequests);
            // создадим принципала
            if (organization.getPrincipal() == null) {
                ClsPrincipal principal = ClsPrincipal.builder()
                        .organization(organization)
//                        .password(passwordEncoder.encode(PasswordGenerator.generatePassword(8)))
                        .password("")
                        .build();
                clsPrincipalRepo.save(principal);
                organization.setPrincipal(principal);
            }

            organization.setActivated(true);
            organization.setActualized(true);
            organization.setTimeActualization(new Timestamp(System.currentTimeMillis()));
            clsOrganizationRepo.save(organization);
            countActualized++;
        }
        return countActualized;
    }

    /**
     * Метод для переноса утверждённых заявок и её данных из дубликатов организации
     * @param organization
     */
    private void addDataFromRequests(ClsOrganization organization, List<DocRequest> requests) {
        if (requests == null) {
            return;
        }
        // соберем данные из заявок
        Set<DocPerson> persons = new HashSet<>();
        Set<DocAddressFact> addresses = new HashSet<>();
        for (DocRequest request: requests) {
            if (request.getDocPersonList() != null) {
                persons.addAll(request.getDocPersonList());
            }
            if (request.getDocAddressFact() != null) {
                addresses.addAll(request.getDocAddressFact());
            }
        }
        // добавим сотрудников
        if (persons.size() > 0) {
            for (DocPerson docPerson : persons) {
                DocEmployee docEmployee = DocEmployee.builder()
                        .organization(organization)
                        .person(docPerson)
                        .build();
                docEmployeeRepo.save(docEmployee);
            }
        }
        // добавим адреса
        if (addresses.size() > 0) {
            for (DocAddressFact docAddressFact : addresses) {
                RegOrganizationAddressFact regOrganizationAddressFact = RegOrganizationAddressFact.builder()
                        .clsOrganization(organization)
                        .docRequest(docAddressFact.getDocRequest())
                        .fullAddress(docAddressFact.getAddressFact())
                        .isHand(true)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .build();
                regOrganizationAddressFactRepo.save(regOrganizationAddressFact);
            }
        }
    }

    private List<DocRequest> actualizeRequests(ClsOrganization organization) {
        // найдем утвержденные заявки по ИНН
        List<DocRequest> allRequestsByOrgInn = docRequestRepo.getRequestsByInn(organization.getInn()).orElse(null);
        if (allRequestsByOrgInn == null) {
            return null;
        }
        // сгруппируем заявки по виду деятельности
        Map<ClsTypeRequest, List<DocRequest>> requestMap = allRequestsByOrgInn.stream().collect(Collectors.groupingBy(DocRequest::getTypeRequest));
        // в каждой группе найдем одну актуальную заявку и сформируем список заявок организации
        List<DocRequest> actualRequests = new ArrayList<>();
        for (Map.Entry<ClsTypeRequest, List<DocRequest>> entry: requestMap.entrySet()) {
            // заявки ИД текущей организации делаем историческими
            List<DocRequest> requests = entry.getValue().stream().filter(request -> Objects.equals(request.getOrganization().getId(), organization.getId())).collect(Collectors.toList());
            for (DocRequest request : requests) {
                request.setStatusActivity(ActivityStatuses.HISTORICAL.getValue());
                docRequestRepo.save(request);
            }
            // найдем последнюю утвержденную заявку среди заявок по ИНН
            DocRequest lastRequest = entry.getValue().stream().filter(request -> request.getStatusReview() == ReviewStatuses.CONFIRMED.getValue())
                    .max(Comparator.comparing(DocRequest::getTimeReview)).orElse(null);
            // если не нашли, ищем открытую
            if (lastRequest == null) {
                lastRequest = entry.getValue().stream().filter(request -> request.getStatusReview() == ReviewStatuses.OPENED.getValue())
                        .max(Comparator.comparing(DocRequest::getTimeCreate)).orElse(null);
            }
            // если не нашли, ищем прочую
            if (lastRequest == null) {
                lastRequest = entry.getValue().stream().filter(request -> request.getStatusReview() == ReviewStatuses.ACCEPTED.getValue())
                        .max(Comparator.comparing(DocRequest::getTimeCreate)).orElse(null);
            }
            if (lastRequest != null) {
                if (!Objects.equals(lastRequest.getOrganization().getId(), organization.getId())) {
                    lastRequest.setOrganization(organization);
                }
                lastRequest.setStatusActivity(ActivityStatuses.ACTIVE.getValue());
                docRequestRepo.save(lastRequest);
                actualRequests.add(lastRequest);
            }
        }
        return actualRequests;
    }

    /**
     * Метод для добавления данных к организации из ЕГРЮЛ/ЕГРИП
     * @param organization
     */
    void addDataFromEgrul(ClsOrganization organization) {
        String inn = organization.getInn();
        if (inn.length() == 10) {
            RegEgrul regEgrul = egrulService.getEgrul(inn);
            if (regEgrul != null) {
                for (RegEgrulOkved regEgrulOkved : regEgrul.getRegEgrulOkveds()) {
                    Okved okved = okvedRepo.findOkvedByIdSerial(regEgrulOkved.getIdOkved());
                    RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                            .clsOrganization(organization)
                            .okved(okved)
                            .build();
                    RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                            .regOrganizationOkvedId(regOrganizationOkvedId)
                            .isMain(regEgrulOkved.getMain())
                            .build();
                    regOrganizationOkvedRepo.save(regOrganizationOkved);
                }
            }
        } else {
            RegEgrip regEgrip = egrulService.getEgrip(inn);
            if (regEgrip != null) {
                for (RegEgripOkved regEgripOkved : regEgrip.getRegEgripOkveds()) {
                    Okved okved = okvedRepo.findOkvedByIdSerial(regEgripOkved.getIdOkved());
                    RegOrganizationOkvedId regOrganizationOkvedId = RegOrganizationOkvedId.builder()
                            .clsOrganization(organization)
                            .okved(okved)
                            .build();
                    RegOrganizationOkved regOrganizationOkved = RegOrganizationOkved.builder()
                            .regOrganizationOkvedId(regOrganizationOkvedId)
                            .isMain(regEgripOkved.getMain())
                            .build();
                    regOrganizationOkvedRepo.save(regOrganizationOkved);
                }
            }
        }
    }

    @Transactional
    public void markOrganizationAsDeleted(){
        final List<ClsOrganization> organizations = clsOrganizationRepo.getNotActualOrganization(ReviewStatuses.CONFIRMED.getValue());

        for (ClsOrganization org : organizations){
            org.setDeleted(true);
        }
        clsOrganizationRepo.saveAll(organizations);
    }
}
