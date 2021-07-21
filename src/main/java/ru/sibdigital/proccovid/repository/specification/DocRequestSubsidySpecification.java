package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DocRequestSubsidySpecification implements Specification<DocRequestSubsidy> {

    private DocRequestSubsidySearchCriteria searchCriteria;

    public void setSearchCriteria(DocRequestSubsidySearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<DocRequestSubsidy> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.notEqual(root.get("subsidyRequestStatus").get("code"), "NEW"));
        predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

        if (searchCriteria.getBeginSearchTime() != null && searchCriteria.getEndSearchTime() != null) {
            predicates.add(criteriaBuilder.between(root.get("timeSend").as(Timestamp.class), searchCriteria.getBeginSearchTime(), searchCriteria.getEndSearchTime()));
        } else if (searchCriteria.getBeginSearchTime() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timeSend").as(Timestamp.class), searchCriteria.getBeginSearchTime()));
        } else if (searchCriteria.getEndSearchTime() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timeSend").as(Timestamp.class), searchCriteria.getEndSearchTime()));
        }

        if (searchCriteria.getInnOrName() != null) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.trim(root.get("organization").get("inn")), '%' + searchCriteria.getInnOrName() + '%')));
        }

        if (searchCriteria.getSubsidyId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("subsidy").get("id"), searchCriteria.getSubsidyId()));
        }

        if (searchCriteria.getSubsidyRequestStatusShortName() != null) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.trim(root.get("subsidyRequestStatus").get("shortName")), '%' + searchCriteria.getSubsidyRequestStatusShortName() + '%')));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
