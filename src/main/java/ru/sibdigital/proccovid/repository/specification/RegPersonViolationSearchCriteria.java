package ru.sibdigital.proccovid.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegPersonViolationSearchCriteria {

//    private String lastname;
//    private String firstname;
//    private String patronymic;
    private String fio;
    private String passportData;
    private String numberFile;
    private Long idDistrict;

    public boolean isNotEmpty() {
//        if (this.lastname != null && !this.lastname.isBlank()) {
//            return true;
//        }
//        if (this.firstname != null && !this.firstname.isBlank()) {
//            return true;
//        }
//        if (this.patronymic != null && !this.patronymic.isBlank()) {
//            return true;
//        }
        if (this.fio != null && !this.fio.isBlank()) {
            return true;
        }
        if (this.passportData != null && !this.passportData.isBlank()) {
            return true;
        }
        if (this.numberFile != null && !this.numberFile.isBlank()) {
            return true;
        }
        if (this.idDistrict != null) {
            return true;
        }
        return false;
    }
}
