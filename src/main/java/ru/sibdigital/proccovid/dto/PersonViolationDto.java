package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.RegPersonViolation;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PersonViolationDto {

    private Long id;
    private Long idTypeViolation;
    private String nameTypeViolation;
    private Long idAddedUser;
    private String nameAddedUser;
    private Long idUpdatedUser;
    private String nameUpdatedUser;
    private Timestamp timeCreate;
    private Timestamp timeUpdate;
    private String lastname;
    private String firstname;
    private String patronymic;
    private Date birthday;
    private String placeBirth;
    private String registrationAddress;
    private String residenceAddress;
    private String passportData;
    private String placeWork;
    private String numberFile;
    private Date dateFile;
    private Boolean isDeleted;

    public PersonViolationDto(RegPersonViolation regPersonViolation) {
        this.setId(regPersonViolation.getId());;
        this.setIdTypeViolation(regPersonViolation.getTypeViolation().getId());
        this.setNameTypeViolation(regPersonViolation.getTypeViolation().getName());
        this.setNameAddedUser(regPersonViolation.getAddedUser().getFullName());
        this.setNameUpdatedUser(regPersonViolation.getUpdatedUser().getFullName());
        this.setTimeCreate(regPersonViolation.getTimeCreate());
        this.setTimeUpdate(regPersonViolation.getTimeCreate());
        this.setLastname(regPersonViolation.getLastname());
        this.setFirstname(regPersonViolation.getFirstname());
        this.setPatronymic(regPersonViolation.getPatronymic());
        this.setBirthday(regPersonViolation.getBirthday());
        this.setPlaceBirth(regPersonViolation.getPlaceBirth());
        this.setRegistrationAddress(regPersonViolation.getRegistrationAddress());
        this.setResidenceAddress(regPersonViolation.getResidenceAddress());
        this.setPassportData(regPersonViolation.getPassportData());
        this.setPlaceWork(regPersonViolation.getPlaceWork());
        this.setNumberFile(regPersonViolation.getNumberFile());
        this.setDateFile(regPersonViolation.getDateFile());
        this.setIsDeleted(regPersonViolation.getDeleted());
    }

    public String getFullName() {
        String fullName = "";
        fullName += this.lastname != null ? this.lastname : "";
        fullName += this.firstname != null ? " " + this.firstname : "";
        fullName += this.patronymic != null ? " " + this.patronymic : "";
        return fullName;
    }
}
