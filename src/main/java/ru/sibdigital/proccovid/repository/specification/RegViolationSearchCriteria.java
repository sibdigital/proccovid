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

    public boolean isNotEmpty() {
        if (this.inn != null && !this.inn.isBlank()) {
            return true;
        }
        if (this.nameOrg != null && !this.nameOrg.isBlank()) {
            return true;
        }
        if (this.numberFile != null && !this.numberFile.isBlank()) {
            return true;
        }
        if (this.beginDateRegOrg != null) {
            return true;
        }
        if (this.endDateRegOrg != null) {
            return true;
        }
        if (this.idDistrict != null) {
            return true;
        }
        return false;
    }
}
