package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsTypeViolationDto;
import ru.sibdigital.proccovid.dto.PersonViolationDto;
import ru.sibdigital.proccovid.dto.ViolationDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.*;

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

    @Autowired
    private RegPersonViolationRepo regPersonViolationRepo;

    @Autowired
    private ClsDistrictRepo clsDistrictRepo;

    @Autowired
    private RegViolationSearchRepo regViolationSearchRepo;

    @Autowired
    private RegPersonViolationSearchRepo regPersonViolationSearchRepo;

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
    public Page<RegViolation> getViolationsByCriteria(RegViolationSearchCriteria searchCriteria, int page, int size, Long idUser) {
        RegViolationSpecification specification = new RegViolationSpecification();
        specification.setSearchCriteria(searchCriteria);
        Page<RegViolation> regViolationsPage = regViolationRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timeCreate")));

        if (searchCriteria.isNotEmpty()) {
            ClsUser clsUser = null;
            if (idUser != null) {
                clsUser = clsUserRepo.findById(idUser).orElse(null);
            }
            ClsDistrict clsDistrict = null;
            if (searchCriteria.getIdDistrict() != null) {
                clsDistrict = clsDistrictRepo.findById(searchCriteria.getIdDistrict()).orElse(null);
            }
            RegViolationSearch regViolationSearch = RegViolationSearch.builder()
                    .user(clsUser)
                    .district(clsDistrict)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .innOrg(searchCriteria.getInn())
                    .nameOrg(searchCriteria.getNameOrg())
                    .numberFile(searchCriteria.getNumberFile())
                    .beginDateRegOrg(searchCriteria.getBeginDateRegOrg())
                    .endDateRegOrg(searchCriteria.getEndDateRegOrg())
                    .numberFound(regViolationsPage.getTotalElements())
                    .build();
            regViolationSearchRepo.save(regViolationSearch);
        }

        return regViolationsPage;
    }

    @Override
    public RegViolation saveRegViolation(ViolationDto dto) throws Exception {
        if (dto.getIdTypeViolation() == null) {
            throw new Exception("Не указан вид нарушения");
        }
        ClsTypeViolation clsTypeViolation = clsTypeViolationRepo.findById(dto.getIdTypeViolation()).orElse(null);

        if (dto.getIdDistrict() == null) {
            throw new Exception("Не указан район");
        }
        ClsDistrict clsDistrict = clsDistrictRepo.findById(dto.getIdDistrict()).orElse(null);

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
                    .district(clsDistrict)
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
                    .district(clsDistrict)
                    .build();
        }

        regViolationRepo.save(regViolation);

        return regViolation;
    }

    @Override
    public RegViolation getRegViolation(Long id, Long idUser) {
        ClsUser clsUser = null;
        if(idUser != null){
            clsUser = clsUserRepo.findById(idUser).orElse(null);
        }
        ClsDistrict clsDistrict = null;
        RegViolation regViolation = regViolationRepo.findById(id).orElse(null);
        if (regViolation.getDistrict().getId() != null) {
            clsDistrict = clsDistrictRepo.findById(regViolation.getDistrict().getId()).orElse(null);
        }

        RegViolationSearch regViolationSearch = RegViolationSearch.builder()
                    .user(clsUser)
                    .district(clsDistrict)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .innOrg(regViolation.getInnOrg())
                    .nameOrg(regViolation.getNameOrg())
                    .numberFile(regViolation.getNumberFile())
                    .idViolation(regViolation.getId())
                    .build();
            regViolationSearchRepo.save(regViolationSearch);

        return regViolation;
    }

    @Override
    public Page<RegPersonViolation> getPersonViolationsByCriteria(RegPersonViolationSearchCriteria searchCriteria, int page, int size, Long idUser) {
        RegPersonViolationSpecification specification = new RegPersonViolationSpecification();
        specification.setSearchCriteria(searchCriteria);
        Page<RegPersonViolation> regPersonViolationsPage = regPersonViolationRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timeCreate")));

        if (searchCriteria.isNotEmpty()) {
            ClsUser clsUser = null;
            if (idUser != null) {
                clsUser = clsUserRepo.findById(idUser).orElse(null);
            }
            ClsDistrict clsDistrict = null;
            if (searchCriteria.getIdDistrict() != null) {
                clsDistrict = clsDistrictRepo.findById(searchCriteria.getIdDistrict()).orElse(null);
            }
            RegPersonViolationSearch regPersonViolationSearch = RegPersonViolationSearch.builder()
                    .user(clsUser)
                    .district(clsDistrict)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .lastname(searchCriteria.getLastname())
                    .firstname(searchCriteria.getFirstname())
                    .patronymic(searchCriteria.getPatronymic())
                    .passportData(searchCriteria.getPassportData())
                    .numberFile(searchCriteria.getNumberFile())
                    .numberFound(regPersonViolationsPage.getTotalElements())
                    .build();
            regPersonViolationSearchRepo.save(regPersonViolationSearch);
        }

        return regPersonViolationsPage;
    }

    @Override
    public RegPersonViolation saveRegPersonViolation(PersonViolationDto dto) throws Exception {
        if (dto.getIdTypeViolation() == null) {
            throw new Exception("Не указан вид нарушения");
        }
        ClsTypeViolation clsTypeViolation = clsTypeViolationRepo.findById(dto.getIdTypeViolation()).orElse(null);

        if (dto.getIdDistrict() == null) {
            throw new Exception("Не указан район");
        }
        ClsDistrict clsDistrict = clsDistrictRepo.findById(dto.getIdDistrict()).orElse(null);

        RegPersonViolation regPersonViolation;

        Timestamp time = new Timestamp(System.currentTimeMillis());

        if (dto.getId() == null) {
            ClsUser addedUser;
            if (dto.getIdAddedUser() == null) {
                throw new Exception("Не указан пользователь");
            } else {
                addedUser = clsUserRepo.findById(dto.getIdAddedUser()).orElse(null);
            }

            regPersonViolation = RegPersonViolation.builder()
                    .id(dto.getId())
                    .typeViolation(clsTypeViolation)
                    .addedUser(addedUser)
                    .updatedUser(addedUser)
                    .timeCreate(time)
                    .timeUpdate(time)
                    .lastname(dto.getLastname().trim().toUpperCase())
                    .firstname(dto.getFirstname().trim().toUpperCase())
                    .patronymic(dto.getPatronymic() != null ? dto.getPatronymic().trim().toUpperCase() : null)
                    .birthday(dto.getBirthday())
                    .placeBirth(dto.getPlaceBirth())
                    .registrationAddress(dto.getRegistrationAddress())
                    .residenceAddress(dto.getResidenceAddress())
                    .passportData(dto.getPassportData())
                    .placeWork(dto.getPlaceWork())
                    .dateRegPerson(dto.getDateRegPerson())
                    .numberFile(dto.getNumberFile())
                    .dateFile(dto.getDateFile())
                    .isDeleted(false)
                    .district(clsDistrict)
                    .build();
        } else {
            ClsUser updatedUser;
            if (dto.getIdUpdatedUser() == null) {
                throw new Exception("Не указан пользователь");
            } else {
                updatedUser = clsUserRepo.findById(dto.getIdUpdatedUser()).orElse(null);
            }

            regPersonViolation = regPersonViolationRepo.findById(dto.getId()).orElse(null);

            regPersonViolation = regPersonViolation.toBuilder()
                    .typeViolation(clsTypeViolation)
                    .updatedUser(updatedUser)
                    .timeUpdate(time)
                    .registrationAddress(dto.getRegistrationAddress())
                    .residenceAddress(dto.getResidenceAddress())
                    .passportData(dto.getPassportData())
                    .placeWork(dto.getPlaceWork())
                    .dateRegPerson(dto.getDateRegPerson())
                    .numberFile(dto.getNumberFile())
                    .dateFile(dto.getDateFile())
                    .isDeleted(dto.getIsDeleted() == null ? false : dto.getIsDeleted())
                    .district(clsDistrict)
                    .build();
        }

        regPersonViolationRepo.save(regPersonViolation);

        return regPersonViolation;
    }

    @Override
    public RegPersonViolation getRegPersonViolation(Long id, Long idUser) {
        ClsUser clsUser = null;
        if(idUser != null){
            clsUser = clsUserRepo.findById(idUser).orElse(null);
        }
        ClsDistrict clsDistrict = null;
        RegPersonViolation regPersonViolation = regPersonViolationRepo.findById(id).orElse(null);
        if (regPersonViolation.getDistrict().getId() != null) {
            clsDistrict = clsDistrictRepo.findById(regPersonViolation.getDistrict().getId()).orElse(null);
        }

        RegPersonViolationSearch regPersonViolationSearch = RegPersonViolationSearch.builder()
                    .user(clsUser)
                    .district(clsDistrict)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .lastname(regPersonViolation.getLastname())
                    .firstname(regPersonViolation.getFirstname())
                    .patronymic(regPersonViolation.getPatronymic())
                    .passportData(regPersonViolation.getPassportData())
                    .numberFile(regPersonViolation.getNumberFile())
                    .idPersonViolation(regPersonViolation.getId())
                    .build();
            regPersonViolationSearchRepo.save(regPersonViolationSearch);

        return regPersonViolation;
    }

    @Override
    public Page<RegViolationSearch> getViolationSearchQueriesByCriteria(RegViolationSearchSearchCriteria searchCriteria, int page, int size) {
        RegViolationSearchSpecification specification = new RegViolationSearchSpecification();
        specification.setSearchCriteria(searchCriteria);
        Page<RegViolationSearch> regViolationSearchPage = regViolationSearchRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timeCreate")));
        return regViolationSearchPage;
    }

    @Override
    public RegViolationSearch getRegViolationSearch(Long id) {
        return regViolationSearchRepo.findById(id).orElse(null);
    }

    @Override
    public Page<RegPersonViolationSearch> getPersonViolationSearchQueriesByCriteria(RegPersonViolationSearchSearchCriteria searchCriteria, int page, int size) {
        RegPersonViolationSearchSpecification specification = new RegPersonViolationSearchSpecification();
        specification.setSearchCriteria(searchCriteria);
        Page<RegPersonViolationSearch> regPersonViolationSearchPage = regPersonViolationSearchRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timeCreate")));
        return regPersonViolationSearchPage;
    }

    @Override
    public RegPersonViolationSearch getRegPersonViolationSearch(Long id) {
        return regPersonViolationSearchRepo.findById(id).orElse(null);
    }
}
