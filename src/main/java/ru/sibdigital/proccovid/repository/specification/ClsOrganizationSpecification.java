package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.ClsOrganization;
import ru.sibdigital.proccovid.model.ClsPrescription;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ClsOrganizationSpecification implements Specification<ClsOrganization> {

    private ClsOrganizationSearchCriteria searchCriteria;

    public void setSearchCriteria(ClsOrganizationSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<ClsOrganization> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getInn() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.trim(root.get("inn")), searchCriteria.getInn() + '%'));
        }

        if (searchCriteria.getIdPrescription() != null) {
            Join<ClsOrganization, ClsPrescription> prescription = root.join("regOrganizationPrescriptions").join("prescription");
            predicates.add(criteriaBuilder.equal(prescription.get("id"), searchCriteria.getIdPrescription()));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
