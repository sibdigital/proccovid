package ru.sibdigital.proccovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cls_organization", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsOrganization {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_ORG_GEN", sequenceName = "cls_organization_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_ORG_GEN")
    private Long id;
    private String name;
    private String shortName;
    private String inn;
    private String ogrn;
    private String addressJur;
    private String okvedAdd;
    private String okved;
    private String email;
    private String phone;
    private Integer statusImport;
    private Timestamp timeImport;
    private Integer idTypeRequest;
    private Integer idTypeOrganization;
    private Integer typeTaxReporting;
    private Boolean isDeleted;
    private Timestamp timeCreate;
    private Boolean isActivated;

    @OneToOne
    @JoinColumn(name = "id_principal", referencedColumnName = "id")
    @JsonIgnore
    private ClsPrincipal principal;

    @OneToMany(mappedBy = "regOrganizationOkvedId.clsOrganization")
    private Set<RegOrganizationOkved> regOrganizationOkveds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "short_name", nullable = false, length = 255)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Basic
    @Column(name = "inn", nullable = false, length = 12)
    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    @Basic
    @Column(name = "ogrn", nullable = false, length = 13)
    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    @Basic
    @Column(name = "address_jur", nullable = false, length = 255)
    public String getAddressJur() {
        return addressJur;
    }

    public void setAddressJur(String addressJur) {
        this.addressJur = addressJur;
    }

    @Basic
    @Column(name = "okved_add", nullable = true)
    public String getOkvedAdd() {
        return okvedAdd;
    }

    public void setOkvedAdd(String okvedAdd) {
        this.okvedAdd = okvedAdd;
    }

    @Basic
    @Column(name = "okved", nullable = false, length = -1)
    public String getOkved() {
        return okved;
    }

    public void setOkved(String okved) {
        this.okved = okved;
    }

    @Basic
    @Column(name = "email", nullable = false, length = 100)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "phone", nullable = false, length = 100)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "status_import", nullable = false)
    public Integer getStatusImport() {
        return statusImport;
    }

    public void setStatusImport(Integer statusImport) {
        this.statusImport = statusImport;
    }

    @Basic
    @Column(name = "time_import", nullable = true)
    public Timestamp getTimeImport() {
        return timeImport;
    }

    public void setTimeImport(Timestamp timeImport) {
        this.timeImport = timeImport;
    }

    @Basic
    @Column(name = "id_type_request", nullable = true)
    public Integer getIdTypeRequest() {
        return idTypeRequest;
    }

    public void setIdTypeRequest(Integer idTypeRequest) {
        this.idTypeRequest = idTypeRequest;
    }

    @Basic
    @Column(name = "id_type_organization")
    public Integer getIdTypeOrganization() {
        return idTypeOrganization;
    }

    public void setIdTypeOrganization(Integer idTypeOrganization) {
        this.idTypeOrganization = idTypeOrganization;
    }

    @Basic
    @Column(name = "type_tax_reporting")
    public Integer getTypeTaxReporting() {
        return typeTaxReporting;
    }

    public void setTypeTaxReporting(Integer typeTaxReporting) {
        this.typeTaxReporting = typeTaxReporting;
    }

    @Basic
    @Column(name = "is_deleted")
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Basic
    @Column(name = "time_create")
    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Basic
    @Column(name = "is_activated")
    public Boolean getActivated() {
        return isActivated;
    }

    public void setActivated(Boolean activated) {
        isActivated = activated;
    }

    public ClsPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(ClsPrincipal principal) {
        this.principal = principal;
    }

    public Set<RegOrganizationOkved> getRegOrganizationOkveds() {
        return regOrganizationOkveds;
    }

    public void setRegOrganizationOkveds(Set<RegOrganizationOkved> regOrganizationOkveds) {
        this.regOrganizationOkveds = regOrganizationOkveds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsOrganization that = (ClsOrganization) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(inn, that.inn) &&
                Objects.equals(ogrn, that.ogrn) &&
                Objects.equals(addressJur, that.addressJur) &&
                Objects.equals(okvedAdd, that.okvedAdd) &&
                Objects.equals(okved, that.okved) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(statusImport, that.statusImport) &&
                Objects.equals(timeImport, that.timeImport) &&
                Objects.equals(idTypeRequest, that.idTypeRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, shortName, inn, ogrn, addressJur, okvedAdd, okved, email, phone, statusImport, timeImport, idTypeRequest);
    }
}
