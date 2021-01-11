package ru.sibdigital.proccovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_organization_address_fact", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegOrganizationAddressFact {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_ORG_ADDR_FACT_SEQ_GEN", sequenceName = "reg_organization_address_fact_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_ORG_ADDR_FACT_SEQ_GEN")
    private Long id;
    private boolean isDeleted = false;
    private Timestamp timeCreate;
    private Long fiasRegionObjectid;
    private Long fiasRaionObjectid;
    private Long fiasCityObjectid;
    private Long fiasStreetObjectid;
    private Long fiasHouseObjectid;
    private Long fiasObjectid;
    private String fullAddress;
    private String streetHand;
    private String houseHand;
    private String apartmentHand;
    private boolean isHand = false;

    @JoinColumn(name="id_request", referencedColumnName = "id", nullable = true)
    @OneToOne
    @JsonIgnore
    private DocRequest docRequest;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_organization", nullable = false)
    private ClsOrganization clsOrganization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "is_deleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
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
    @Column(name = "fias_objectid")
    public Long getFiasObjectid() {
        return fiasObjectid;
    }

    public void setFiasObjectid(Long fiasObjectGuid) {
        this.fiasObjectid = fiasObjectGuid;
    }

    @Basic
    @Column(name = "fias_region_objectid")
    public Long getFiasRegionObjectid() {
        return fiasRegionObjectid;
    }

    public void setFiasRegionObjectid(Long fiasRegionObjectid) {
        this.fiasRegionObjectid = fiasRegionObjectid;
    }

    @Basic
    @Column(name = "fias_raion_objectid")
    public Long getFiasRaionObjectid() {
        return fiasRaionObjectid;
    }

    public void setFiasRaionObjectid(Long fiasRaionObjectid) {
        this.fiasRaionObjectid = fiasRaionObjectid;
    }

    @Basic
    @Column(name = "fias_city_objectid")
    public Long getFiasCityObjectid() {
        return fiasCityObjectid;
    }

    public void setFiasCityObjectid(Long fiasCityObjectid) {
        this.fiasCityObjectid = fiasCityObjectid;
    }

    @Basic
    @Column(name = "fias_street_objectid")
    public Long getFiasStreetObjectid() {
        return fiasStreetObjectid;
    }

    public void setFiasStreetObjectid(Long fiasStreetObjectid) {
        this.fiasStreetObjectid = fiasStreetObjectid;
    }

    @Basic
    @Column(name = "fias_house_objectid")
    public Long getFiasHouseObjectid() {
        return fiasHouseObjectid;
    }

    public void setFiasHouseObjectid(Long fiasHouseObjectid) {
        this.fiasHouseObjectid = fiasHouseObjectid;
    }

    @Basic
    @Column(name = "full_address")
    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Basic
    @Column(name = "is_hand")
    public boolean isHand() {
        return isHand;
    }

    public void setHand(boolean hand) {
        isHand = hand;
    }

    @Basic
    @Column(name = "street_hand")
    public String getStreetHand() {
        return streetHand;
    }

    public void setStreetHand(String streetHand) {
        this.streetHand = streetHand;
    }

    @Basic
    @Column(name = "house_hand")
    public String getHouseHand() {
        return houseHand;
    }

    public void setHouseHand(String houseHand) {
        this.houseHand = houseHand;
    }

    @Basic
    @Column(name = "apartment_hand")
    public String getApartmentHand() {
        return apartmentHand;
    }

    public void setApartmentHand(String apartmentHand) {
        this.apartmentHand = apartmentHand;
    }

    @JsonIgnore
    public DocRequest getDocRequest() {
        return docRequest;
    }
    public void setDocRequest(DocRequest docRequest) {
        this.docRequest = docRequest;
    }

    public ClsOrganization getClsOrganization() {
        return clsOrganization;
    }

    public void setClsOrganization(ClsOrganization clsOrganization) {
        this.clsOrganization = clsOrganization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegOrganizationAddressFact that = (RegOrganizationAddressFact) o;
        return Objects.equals(id, that.id) &&
                //Objects.equals(organization.getId(), that.organization.getId()) &&
                Objects.equals(docRequest.getId(), that.docRequest.getId()) &&
                Objects.equals(timeCreate, that.timeCreate) &&
                Objects.equals(isDeleted, that.isDeleted) &&
                Objects.equals(fiasRegionObjectid, that.fiasRegionObjectid) &&
                Objects.equals(fiasRaionObjectid, that.fiasRaionObjectid) &&
                Objects.equals(fiasCityObjectid, that.fiasCityObjectid) &&
                Objects.equals(fiasStreetObjectid, that.fiasStreetObjectid) &&
                Objects.equals(fiasObjectid, that.fiasObjectid) &&
                Objects.equals(houseHand, that.houseHand) &&
                Objects.equals(streetHand, that.streetHand) &&
                Objects.equals(apartmentHand, that.apartmentHand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, isHand, timeCreate, fiasRegionObjectid, fiasRaionObjectid, fiasCityObjectid, fiasStreetObjectid, fiasObjectid, houseHand, streetHand, apartmentHand);
    }


}
