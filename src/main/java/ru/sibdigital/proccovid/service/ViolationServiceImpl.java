package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsTypeViolationDto;
import ru.sibdigital.proccovid.dto.ViolationDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.RegViolationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.RegViolationSpecification;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ViolationServiceImpl implements ViolationService {

    @Autowired
    private ClsTypeViolationRepo clsTypeViolationRepo;

    @Autowired
    private ClsUserRepo clsUserRepo;

    @Autowired
    private RegEgrulRepo regEgrulRepo;

    @Autowired
    private RegEgripRepo regEgripRepo;

    @Autowired
    private RegFilialRepo regFilialRepo;

    @Autowired
    private RegViolationRepo regViolationRepo;

    @Override
    public ClsTypeViolation saveClsTypeViolation(ClsTypeViolationDto dto) throws Exception {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new Exception("Не указано наименование");
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new Exception("Не указано описание");
        }

        ClsTypeViolation clsTypeViolation = ClsTypeViolation.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        clsTypeViolationRepo.save(clsTypeViolation);

        return clsTypeViolation;
    }

    @Override
    public ClsTypeViolation getClsTypeViolation(Long id) {
        return clsTypeViolationRepo.findById(id).orElse(null);
    }

    @Override
    public List<ClsTypeViolation> getClsTypeViolations() {
        return clsTypeViolationRepo.findAll(Sort.by("name"));
    }

    @Override
    public Page<RegViolation> getViolationsByCriteria(RegViolationSearchCriteria searchCriteria, int page, int size) {
        RegViolationSpecification specification = new RegViolationSpecification();
        specification.setSearchCriteria(searchCriteria);
        Page<RegViolation> clsOrganizationsPage = regViolationRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timeCreate")));
        return clsOrganizationsPage;
    }

    @Override
    public RegViolation saveRegViolation(ViolationDto dto) throws Exception {
        if (dto.getIdTypeViolation() == null) {
            throw new Exception("Не указан вид нарушения");
        }
        ClsTypeViolation clsTypeViolation = clsTypeViolationRepo.findById(dto.getIdTypeViolation()).orElse(null);

        RegViolation regViolation;

        Timestamp time = new Timestamp(System.currentTimeMillis());

        if (dto.getId() == null) {
            ClsUser addedUser;
            if (dto.getIdAddedUser() == null) {
                throw new Exception("Не указан пользователь");
            } else {
                addedUser = clsUserRepo.findById(dto.getIdAddedUser()).orElse(null);
            }

            regViolation = RegViolation.builder()
                    .id(dto.getId())
                    .typeViolation(clsTypeViolation)
                    .addedUser(addedUser)
                    .updatedUser(addedUser)
                    .timeCreate(time)
                    .timeUpdate(time)
                    .nameOrg(dto.getNameOrg())
                    .opfOrg(dto.getOpfOrg())
                    .innOrg(dto.getInnOrg())
                    .ogrnOrg(dto.getOgrnOrg())
                    .kppOrg(dto.getKppOrg())
                    .dateRegOrg(dto.getDateRegOrg())
                    .numberFile(dto.getNumberFile())
                    .dateFile(dto.getDateFile())
                    .isDeleted(false)
                    .build();

            if (dto.getIdEgrul() != null) {
                RegEgrul regEgrul = regEgrulRepo.findById(dto.getIdEgrul()).orElse(null);
                regViolation.setRegEgrul(regEgrul);
            }

            if (dto.getIdEgrip() != null) {
                RegEgrip regEgrip = regEgripRepo.findById(dto.getIdEgrip()).orElse(null);
                regViolation.setRegEgrip(regEgrip);
            }

            if (dto.getIdFilial() != null) {
                RegFilial regFilial = regFilialRepo.findById(dto.getIdFilial()).orElse(null);
                regViolation.setRegFilial(regFilial);
            }
        } else {
            ClsUser updatedUser;
            if (dto.getIdUpdatedUser() == null) {
                throw new Exception("Не указан пользователь");
            } else {
                updatedUser = clsUserRepo.findById(dto.getIdUpdatedUser()).orElse(null);
            }

            regViolation = regViolationRepo.findById(dto.getId()).orElse(null);

            regViolation = regViolation.toBuilder()
                    .updatedUser(updatedUser)
                    .timeUpdate(time)
                    .numberFile(dto.getNumberFile())
                    .dateFile(dto.getDateFile())
                    .isDeleted(dto.getIsDeleted() == null ? false : dto.getIsDeleted())
                    .build();
        }

        regViolationRepo.save(regViolation);

        return regViolation;
    }

    @Override
    public RegViolation getRegViolation(Long id) {
        return regViolationRepo.findById(id).orElse(null);
    }
}
