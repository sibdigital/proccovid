package ru.sibdigital.proccovid.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegViolationSearchSearchCriteria {

    private Timestamp beginSearchTime;
    private Timestamp endSearchTime;
    private Long idUser;
}
