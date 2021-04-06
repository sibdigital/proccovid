package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.RegViolationSearch;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ViolationSearchDto {

    private Long id;
    private Long idUser;
    private String nameUser;
    private Timestamp timeCreate;
    private String nameOrg;
    private String innOrg;
    private Date beginDateRegOrg;
    private Date endDateRegOrg;
    private String numberFile;
    private Long numberFound;
    private Long idDistrict;
    private String districtName;

    public ViolationSearchDto(RegViolationSearch o) {
        this.setId(o.getId());
        if (o.getUser() != null) {
            this.setIdUser(o.getUser().getId());
            this.setNameUser(o.getUser().getFullName());
        }
        this.setTimeCreate(o.getTimeCreate());
        this.setNameOrg(o.getNameOrg());
        this.setInnOrg(o.getInnOrg());
        this.setBeginDateRegOrg(o.getBeginDateRegOrg());
        this.setEndDateRegOrg(o.getEndDateRegOrg());
        this.setNumberFile(o.getNumberFile());
        this.setNumberFound(o.getNumberFound());
        if (o.getDistrict() != null) {
            this.setIdDistrict(o.getDistrict().getId());
            this.setDistrictName(o.getDistrict().getName());
        }
    }
}
