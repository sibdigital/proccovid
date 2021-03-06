package ru.sibdigital.proccovid.service;

import org.springframework.data.domain.Page;
import ru.sibdigital.proccovid.dto.ClsOrganizationDto;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSearchCriteria;

public interface OrganizationService {

    Page<ClsOrganization> getOrganizationsByCriteria(ClsOrganizationSearchCriteria searchCriteria, int page, int size);

    void markOrganizationAsDeleted();

    void actualizeOrganizations();

    void actualizeFiles();

    void createPrescriptions();

    void saveOrganization(ClsOrganizationDto clsOrganizationDto);
}
