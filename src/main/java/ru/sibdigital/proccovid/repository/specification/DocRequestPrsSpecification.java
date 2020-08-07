package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.DocRequestPrs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class DocRequestPrsSpecification implements Specification<DocRequestPrs> {

    private DocRequestPrsSearchCriteria searchCriteria;

    public void setSearchCriteria(DocRequestPrsSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<DocRequestPrs> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getIdDepartment() != null && searchCriteria.getIdDepartment() != 0) {
            predicates.add(criteriaBuilder.equal(
                    root.get("department").get("id"), searchCriteria.getIdDepartment()));
        }
        if (searchCriteria.getStatusReview() != null) {
            predicates.add(criteriaBuilder.equal(
                    root.get("statusReview"), searchCriteria.getStatusReview()));
        }
        if (searchCriteria.getIdTypeRequest() != null) {
            predicates.add(criteriaBuilder.equal(
                    root.get("typeRequest").get("id"), searchCriteria.getIdTypeRequest()));
        }
        if (searchCriteria.getIdDistrict() != null) {
            predicates.add(criteriaBuilder.equal(
                    root.get("district").get("id"), searchCriteria.getIdDistrict()));
        }
        if (searchCriteria.getInnOrName() != null) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.trim(root.get("organization").get("inn")), '%' + searchCriteria.getInnOrName() + '%'),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("organization").get("name")), '%' + searchCriteria.getInnOrName() + '%')));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
