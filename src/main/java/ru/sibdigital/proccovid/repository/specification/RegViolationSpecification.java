package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.RegViolation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RegViolationSpecification implements Specification<RegViolation> {

    private RegViolationSearchCriteria searchCriteria;

    public void setSearchCriteria(RegViolationSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<RegViolation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getInn() != null && !searchCriteria.getInn().isBlank()) {
            predicates.add(criteriaBuilder.equal(root.get("innOrg"), searchCriteria.getInn().trim()));
        }
        if (searchCriteria.getNameOrg() != null && !searchCriteria.getNameOrg().isBlank()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nameOrg")), '%' + searchCriteria.getNameOrg().trim().toLowerCase() + '%'));
        }
        if (searchCriteria.getNumberFile() != null && !searchCriteria.getNumberFile().isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("numberFile"), searchCriteria.getNumberFile().trim() + '%'));
        }
        if (searchCriteria.getBeginDateRegOrg() != null && searchCriteria.getEndDateRegOrg() != null) {
            predicates.add(criteriaBuilder.between(root.get("dateRegOrg").as(Date.class), searchCriteria.getBeginDateRegOrg(), searchCriteria.getEndDateRegOrg()));
        } else if (searchCriteria.getBeginDateRegOrg() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateRegOrg").as(Date.class), searchCriteria.getBeginDateRegOrg()));
        } else if (searchCriteria.getEndDateRegOrg() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateRegOrg").as(Date.class), searchCriteria.getEndDateRegOrg()));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
