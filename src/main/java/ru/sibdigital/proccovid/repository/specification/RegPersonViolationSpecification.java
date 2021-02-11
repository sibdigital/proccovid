package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.RegPersonViolation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RegPersonViolationSpecification implements Specification<RegPersonViolation> {

    private RegPersonViolationSearchCriteria searchCriteria;

    public void setSearchCriteria(RegPersonViolationSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<RegPersonViolation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getLastname() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.trim(root.get("lastname")), searchCriteria.getLastname().toUpperCase() + '%'));
        }
        if (searchCriteria.getFirstname() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.trim(root.get("firstname")), searchCriteria.getFirstname().toUpperCase() + '%'));
        }
        if (searchCriteria.getPatronymic() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.trim(root.get("lastname")), searchCriteria.getPatronymic().toUpperCase() + '%'));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
