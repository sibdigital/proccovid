package ru.sibdigital.proccovid.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.sibdigital.proccovid.model.ClsControlAuthority;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ClsControlAuthoritySpecification implements Specification<ClsControlAuthority> {

    private ClsControlAuthoritySearchCriteria searchCriteria;

    public void setSearchCriteria(ClsControlAuthoritySearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<ClsControlAuthority> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getName() != null) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.trim(root.get("inn")), '%' + searchCriteria.getName() + '%'))
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
