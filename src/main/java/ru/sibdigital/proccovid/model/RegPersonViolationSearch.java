package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "reg_person_violation_search", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegPersonViolationSearch {

    @Id
    @SequenceGenerator(name = "REG_PERSON_VIOLATION_SEARCH_SEQ_GEN", sequenceName = "reg_person_violation_search_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_PERSON_VIOLATION_SEARCH_SEQ_GEN")
    @Column(name = "id", nullable = false)
    private Long id;
    private Timestamp timeCreate;
    private String lastname;
    private String firstname;
    private String patronymic;
    private String passportData;
    private String numberFile;
    private Long numberFound;

    @OneToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private ClsUser user;

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
    @Column(name = "passport_data")
    public String getPassportData() {
        return passportData;
    }

    public void setPassportData(String passportData) {
        this.passportData = passportData;
    }

    @Basic
    @Column(name = "number_file")
    public String getNumberFile() {
        return numberFile;
    }

    public void setNumberFile(String numberFile) {
        this.numberFile = numberFile;
    }

    @Basic
    @Column(name = "number_found")
    public Long getNumberFound() {
        return numberFound;
    }

    public void setNumberFound(Long numberFound) {
        this.numberFound = numberFound;
    }

    public ClsUser getUser() {
        return user;
    }

    public void setUser(ClsUser user) {
        this.user = user;
    }

    public ClsDistrict getDistrict() {
        return district;
    }

    public void setDistrict(ClsDistrict district) {
        this.district = district;
    }
}
