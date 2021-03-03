package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.RegPersonViolationSearch;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RegPersonViolationSearchSpecification implements Specification<RegPersonViolationSearch> {

    private RegPersonViolationSearchSearchCriteria searchCriteria;

    public void setSearchCriteria(RegPersonViolationSearchSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<RegPersonViolationSearch> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getBeginSearchTime() != null && searchCriteria.getEndSearchTime() != null) {
            predicates.add(criteriaBuilder.between(root.get("timeCreate").as(Timestamp.class), searchCriteria.getBeginSearchTime(), searchCriteria.getEndSearchTime()));
        } else if (searchCriteria.getBeginSearchTime() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timeCreate").as(Timestamp.class), searchCriteria.getBeginSearchTime()));
        } else if (searchCriteria.getEndSearchTime() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timeCreate").as(Timestamp.class), searchCriteria.getEndSearchTime()));
        }
        if (searchCriteria.getIdUser() != null) {
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), searchCriteria.getIdUser()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
