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

    public ViolationDto(RegViolation regViolation) {
        this.setId(regViolation.getId());
        this.setIdTypeViolation(regViolation.getTypeViolation().getId());
        this.setNameTypeViolation(regViolation.getTypeViolation().getName());
        this.setNameAddedUser(regViolation.getAddedUser().getFullName());
        this.setNameUpdatedUser(regViolation.getUpdatedUser().getFullName());
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
    }
}
