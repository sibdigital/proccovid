package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "reg_violation_search", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegViolationSearch {

    @Id
    @SequenceGenerator(name = "REG_VIOLATION_SEARCH_SEQ_GEN", sequenceName = "reg_violation_search_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_VIOLATION_SEARCH_SEQ_GEN")
    @Column(name = "id", nullable = false)
    private Long id;
    private Long idViolation;
    private Timestamp timeCreate;
    private String nameOrg;
    private String innOrg;
    private String numberFile;
    private Date beginDateRegOrg;
    private Date endDateRegOrg;
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
    @Column(name = "name_org")
    public String getNameOrg() {
        return nameOrg;
    }

    public void setNameOrg(String nameOrg) {
        this.nameOrg = nameOrg;
    }

    @Basic
    @Column(name = "inn_org")
    public String getInnOrg() {
        return innOrg;
    }

    public void setInnOrg(String innOrg) {
        this.innOrg = innOrg;
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
    @Column(name = "begin_date_reg_org")
    public Date getBeginDateRegOrg() {
        return beginDateRegOrg;
    }

    public void setBeginDateRegOrg(Date beginDateRegOrg) {
        this.beginDateRegOrg = beginDateRegOrg;
    }

    @Basic
    @Column(name = "end_date_reg_org")
    public Date getEndDateRegOrg() {
        return endDateRegOrg;
    }

    public void setEndDateRegOrg(Date endDateRegOrg) {
        this.endDateRegOrg = endDateRegOrg;
    }

    @Basic
    @Column(name = "number_found")
    public Long getNumberFound() {
        return numberFound;
    }

    public void setNumberFound(Long numberFound) {
        this.numberFound = numberFound;
    }

    @Basic
    @Column(name = "id_violation")
    public Long getIdViolation() { return idViolation; }

    public void setIdViolation(Long idViolation) { this.idViolation = idViolation; }

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
