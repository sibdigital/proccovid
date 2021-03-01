package ru.sibdigital.proccovid.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegViolationSearchCriteria {

    private String inn;
    private String nameOrg;
    private String numberFile;
    private Date beginDateRegOrg;
    private Date endDateRegOrg;
    private Long idDistrict;
}
