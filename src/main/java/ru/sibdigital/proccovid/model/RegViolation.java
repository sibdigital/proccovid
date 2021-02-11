package ru.sibdigital.proccovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_violation", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegViolation {

    @Id
    @SequenceGenerator(name = "REG_VIOLATION_SEQ_GEN", sequenceName = "reg_violation_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_VIOLATION_SEQ_GEN")
    @Column(name = "id", nullable = false)
    private Long id;
    private Timestamp timeCreate;
    private Timestamp timeUpdate;
    private String nameOrg;
    private String opfOrg;
    private String innOrg;
    private String ogrnOrg;
    private String kppOrg;
    private Date dateRegOrg;
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
    @JoinColumn(name = "id_egrul", referencedColumnName = "id")
    @JsonIgnore
    private RegEgrul regEgrul;

    @OneToOne
    @JoinColumn(name = "id_egrip", referencedColumnName = "id")
    @JsonIgnore
    private RegEgrip regEgrip;

    @OneToOne
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @JsonIgnore
    private RegFilial regFilial;

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
    @Column(name = "time_update", nullable = false)
    public Timestamp getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(Timestamp timeUpdate) {
        this.timeUpdate = timeUpdate;
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
    @Column(name = "orf_org")
    public String getOpfOrg() {
        return opfOrg;
    }

    public void setOpfOrg(String opfOrg) {
        this.opfOrg = opfOrg;
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
    @Column(name = "ogrn_org")
    public String getOgrnOrg() {
        return ogrnOrg;
    }

    public void setOgrnOrg(String ogrnOrg) {
        this.ogrnOrg = ogrnOrg;
    }

    @Basic
    @Column(name = "kpp_org")
    public String getKppOrg() {
        return kppOrg;
    }

    public void setKppOrg(String kppOrg) {
        this.kppOrg = kppOrg;
    }

    @Basic
    @Column(name = "date_reg_org")
    public Date getDateRegOrg() {
        return dateRegOrg;
    }

    public void setDateRegOrg(Date dateRegOrg) {
        this.dateRegOrg = dateRegOrg;
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

    public RegEgrul getRegEgrul() {
        return regEgrul;
    }

    public void setRegEgrul(RegEgrul regEgrul) {
        this.regEgrul = regEgrul;
    }

    public RegEgrip getRegEgrip() {
        return regEgrip;
    }

    public void setRegEgrip(RegEgrip regEgrip) {
        this.regEgrip = regEgrip;
    }

    public RegFilial getRegFilial() {
        return regFilial;
    }

    public void setRegFilial(RegFilial regFilial) {
        this.regFilial = regFilial;
    }
}
