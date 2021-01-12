package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSpecification;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * Сервис для актуализации БД под новую схему данных
 *
 */
@Service
public class DBActualizeServiceImpl implements DBActualizeService {

    private final static int pageSize = 100;

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
    private RegOrganizationFileRepo regOrganizationFileRepo;

    @Autowired
    private RegOrganizationAddressFactRepo regOrganizationAddressFactRepo;

    @Override
    public void actualize() {
        ClsOrganizationSpecification specification = new ClsOrganizationSpecification();
        specification.setSearchCriteria(new ClsOrganizationSearchCriteria());
        long count = clsOrganizationRepo.count(specification);

        int pages = (int) (((count - 1) / pageSize) + 1);

//        for (int i = 0; i < pages; i++) {
//            Page<ClsOrganization> page = clsOrganizationRepo.findAll(specification, PageRequest.of(i, pageSize, Sort.by("id")));
//            for (ClsOrganization organization: page.getContent()) {

        ClsOrganization organization = clsOrganizationRepo.findById(18894L).orElse(null);

                // создадим принципала
                if (organization.getPrincipal() == null) {
                    ClsPrincipal principal = ClsPrincipal.builder()
                            .organization(organization)
//                            .password(passwordEncoder.encode(PasswordGenerator.generatePassword(8)))
                            .build();
                    clsPrincipalRepo.save(principal);
                    organization.setPrincipal(principal);
                    organization.setActivated(true); // TODO
                    clsOrganizationRepo.save(organization);
                }

                // добавим окведы
                String inn = organization.getInn();
                if (inn.length() == 10) {
                    RegEgrul regEgrul = egrulService.getEgrul(inn);
                    for (RegEgrulOkved regEgrulOkved: regEgrul.getRegEgrulOkveds()) {
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
                } else {
                    RegEgrip regEgrip = egrulService.getEgrip(inn);
                    for (RegEgripOkved regEgripOkved: regEgrip.getRegEgripOkveds()) {
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

                // добавим сотрудников, адреса, файлы
                List<DocRequest> requests = docRequestRepo.getAllRequestWithConfirmedStatus(organization.getId()).orElse(null);
                if (requests != null) {
                    for (DocRequest request: requests) {
                        // сотрудники
                        if (request.getDocPersonList() != null) {
                            for (DocPerson docPerson: request.getDocPersonList()) {
                                DocEmployee docEmployee = DocEmployee.builder()
                                        .organization(organization)
                                        .person(docPerson)
                                        .build();
                                docEmployeeRepo.save(docEmployee);
                            }
                        }

                        // файлы
                        if (request.getAttachmentPath() != null && !request.getAttachmentPath().isBlank()) {
                            String[] paths = request.getAttachmentPath().split(",");
                            for (String path : paths) {
//                                let filename = path.split('\\').pop().split('/').pop()
                                RegOrganizationFile regOrganizationFile = RegOrganizationFile.builder()
                                        .clsOrganizationByIdOrganization(organization)
                                        .attachmentPath(path)
                                        .isDeleted(false)
                                        .fileName("")
                                        .fileExtension("")
                                        .originalFileName("")
                                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                                        .build();
                                regOrganizationFileRepo.save(regOrganizationFile);
                            }
                        }

                        // адреса
                        if (request.getDocAddressFact() != null) {
                            for (DocAddressFact docAddressFact : request.getDocAddressFact()) {
                                RegOrganizationAddressFact regOrganizationAddressFact = RegOrganizationAddressFact.builder()
                                        .clsOrganization(organization)
                                        .fullAddress(docAddressFact.getAddressFact())
                                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                                        .build();
                                regOrganizationAddressFactRepo.save(regOrganizationAddressFact);
                            }
                        }
                    }
                }
//            }
//        }
    }
}
