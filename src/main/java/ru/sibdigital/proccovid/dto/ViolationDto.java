package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
