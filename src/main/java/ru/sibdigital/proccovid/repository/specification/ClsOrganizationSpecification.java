package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.ClsOrganization;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
