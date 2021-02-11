package ru.sibdigital.proccovid.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegPersonViolationSearchCriteria {

    private String lastname;
    private String firstname;
    private String patronymic;
}
