package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.repository.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.ClsOrganizationSpecification;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Override
    public Page<ClsOrganization> getOrganizationsByCriteria(ClsOrganizationSearchCriteria searchCriteria, int page, int size) {
        ClsOrganizationSpecification specification = new ClsOrganizationSpecification();
        specification.setSearchCriteria(searchCriteria);
        Page<ClsOrganization> clsOrganizationsPage = clsOrganizationRepo.findAll(specification, PageRequest.of(page, size, Sort.by("id")));
        return clsOrganizationsPage;
    }
}
