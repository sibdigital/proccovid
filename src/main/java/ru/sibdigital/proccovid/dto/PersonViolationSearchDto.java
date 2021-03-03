package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.RegPersonViolationSearch;

import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PersonViolationSearchDto {

    private Long id;
    private Long idUser;
    private String nameUser;
    private Timestamp timeCreate;
    private String lastname;
    private String firstname;
    private String patronymic;
    private String passportData;
    private String numberFile;
    private Long numberFound;
    private Long idDistrict;
    private String districtName;

    public PersonViolationSearchDto(RegPersonViolationSearch o) {
        this.setId(o.getId());
        this.setIdUser(o.getUser().getId());
        this.setNameUser(o.getUser().getFullName());
        this.setTimeCreate(o.getTimeCreate());
        this.setLastname(o.getLastname());
        this.setFirstname(o.getFirstname());
        this.setPatronymic(o.getPatronymic());
        this.setPassportData(o.getPassportData());
        this.setNumberFile(o.getNumberFile());
        this.setNumberFound(o.getNumberFound());
        if (o.getDistrict() != null) {
            this.setIdDistrict(o.getDistrict().getId());
            this.setDistrictName(o.getDistrict().getName());
        }
    }
}
