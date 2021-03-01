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

        if (searchCriteria.getLastname() != null && !searchCriteria.getLastname().isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("lastname"), searchCriteria.getLastname().trim().toUpperCase() + '%'));
        }
        if (searchCriteria.getFirstname() != null && !searchCriteria.getFirstname().isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("firstname"), searchCriteria.getFirstname().trim().toUpperCase() + '%'));
        }
        if (searchCriteria.getPatronymic() != null && !searchCriteria.getPatronymic().isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("lastname"), searchCriteria.getPatronymic().trim().toUpperCase() + '%'));
        }
        if (searchCriteria.getPassportData() != null && !searchCriteria.getPassportData().isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("passportData"), searchCriteria.getPassportData().trim() + '%'));
        }
        if (searchCriteria.getNumberFile() != null && !searchCriteria.getNumberFile().isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("numberFile"), searchCriteria.getNumberFile().trim() + '%'));
        }
        if (searchCriteria.getIdDistrict() != null) {
            predicates.add(criteriaBuilder.equal(root.get("district").get("id"), searchCriteria.getIdDistrict()));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
