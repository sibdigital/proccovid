package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.RegViolation;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ViolationDto {

    private Long id;
    private Long idTypeViolation;
    private String nameTypeViolation;
    private Long idAddedUser;
    private String nameAddedUser;
    private Long idUpdatedUser;
    private String nameUpdatedUser;
    private Timestamp timeCreate;
    private Timestamp timeUpdate;
    private Long idEgrul;
    private Long idEgrip;
    private Long idFilial;
    private String nameOrg;
    private String opfOrg;
    private String innOrg;
    private String ogrnOrg;
    private String kppOrg;
    private Date dateRegOrg;
    private String numberFile;
    private Date dateFile;
    private Boolean isDeleted;
    private Long idDistrict;
    private String nameDistrict;

    public ViolationDto(RegViolation regViolation) {
        this.setId(regViolation.getId());
        if (regViolation.getTypeViolation() != null) {
            this.setIdTypeViolation(regViolation.getTypeViolation().getId());
            this.setNameTypeViolation(regViolation.getTypeViolation().getName());
        }
        if (regViolation.getAddedUser() != null) {
            this.setNameAddedUser(regViolation.getAddedUser().getFullName());
        }
        if (regViolation.getUpdatedUser() != null) {
            this.setNameUpdatedUser(regViolation.getUpdatedUser().getFullName());
        }
        this.setTimeCreate(regViolation.getTimeCreate());
        this.setTimeUpdate(regViolation.getTimeCreate());
        this.setNameOrg(regViolation.getNameOrg());
        this.setOpfOrg(regViolation.getOpfOrg());
        this.setInnOrg(regViolation.getInnOrg());
        this.setOgrnOrg(regViolation.getOgrnOrg());
        this.setKppOrg(regViolation.getKppOrg());
        this.setDateRegOrg(regViolation.getDateRegOrg());
        this.setNumberFile(regViolation.getNumberFile());
        this.setDateFile(regViolation.getDateFile());
        this.setIsDeleted(regViolation.getDeleted());
        if (regViolation.getDistrict() != null) {
            this.setIdDistrict(regViolation.getDistrict().getId());
            this.setNameDistrict(regViolation.getDistrict().getName());
        }
    }
}
