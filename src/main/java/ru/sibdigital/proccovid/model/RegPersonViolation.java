package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "reg_person_violation", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegPersonViolation {

    @Id
    @SequenceGenerator(name = "REG_PERSON_VIOLATION_SEQ_GEN", sequenceName = "reg_person_violation_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_PERSON_VIOLATION_SEQ_GEN")
    @Column(name = "id", nullable = false)
    private Long id;
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
    private Date dateRegPerson;
    private String numberFile;
    private Date dateFile;
    private Boolean isDeleted;

    @OneToOne
    @JoinColumn(name = "id_type_violation", referencedColumnName = "id")
    private ClsTypeViolation typeViolation;

    @OneToOne
    @JoinColumn(name = "id_added_user", referencedColumnName = "id")
    private ClsUser addedUser;

    @OneToOne
    @JoinColumn(name = "id_updated_user", referencedColumnName = "id")
    private ClsUser updatedUser;

    @OneToOne
    @JoinColumn(name = "id_district", referencedColumnName = "id")
    private ClsDistrict district;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "time_create", nullable = false)
    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Basic
    @Column(name = "time_update")
    public Timestamp getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Timestamp timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    @Basic
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Basic
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "patronymic")
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Basic
    @Column(name = "birthday")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Basic
    @Column(name = "place_birth")
    public String getPlaceBirth() {
        return placeBirth;
    }

    public void setPlaceBirth(String placeBirth) {
        this.placeBirth = placeBirth;
    }

    @Basic
    @Column(name = "registration_address")
    public String getRegistrationAddress() {
        return registrationAddress;
    }

    public void setRegistrationAddress(String registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    @Basic
    @Column(name = "residence_address")
    public String getResidenceAddress() {
        return residenceAddress;
    }

    public void setResidenceAddress(String residenceAddress) {
        this.residenceAddress = residenceAddress;
    }

    @Basic
    @Column(name = "passport_data")
    public String getPassportData() {
        return passportData;
    }

    public void setPassportData(String passportData) {
        this.passportData = passportData;
    }

    @Basic
    @Column(name = "palce_work")
    public String getPlaceWork() {
        return placeWork;
    }

    public void setPlaceWork(String placeWork) {
        this.placeWork = placeWork;
    }

    @Basic
    @Column(name = "date_reg_person")
    public Date getDateRegPerson() { return dateRegPerson; }

    public void setDateRegPerson(Date dateRegPerson) { this.dateRegPerson = dateRegPerson; }

    @Basic
    @Column(name = "number_file")
    public String getNumberFile() {
        return numberFile;
    }

    public void setNumberFile(String numberFile) {
        this.numberFile = numberFile;
    }

    @Basic
    @Column(name = "date_file")
    public Date getDateFile() {
        return dateFile;
    }

    public void setDateFile(Date dateFile) {
        this.dateFile = dateFile;
    }

    @Basic
    @Column(name = "is_deleted", nullable = false)
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public ClsTypeViolation getTypeViolation() {
        return typeViolation;
    }

    public void setTypeViolation(ClsTypeViolation typeViolation) {
        this.typeViolation = typeViolation;
    }

    public ClsUser getAddedUser() {
        return addedUser;
    }

    public void setAddedUser(ClsUser addedUser) {
        this.addedUser = addedUser;
    }

    public ClsUser getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(ClsUser updatedUser) {
        this.updatedUser = updatedUser;
    }

    public ClsDistrict getDistrict() {
        return district;
    }

    public void setDistrict(ClsDistrict district) {
        this.district = district;
    }
}
