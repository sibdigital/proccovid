package ru.sibdigital.proccovid.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cls_type_request", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ClsTypeRequest {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_TYPE_REQ_GEN", sequenceName = "cls_type_request_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_TYPE_REQ_GEN")
    private Long id;
    private String activityKind;
    private String shortName;
    private String prescription;
    private String prescriptionLink;
    private String settings;
    private int statusRegistration;
    private Timestamp beginRegistration;
    private Timestamp endRegistration;
    private int statusVisible;
    private Timestamp beginVisible;
    private Timestamp endVisible;
    private int sortWeight;
    private String consent;
    private int statusPublication;
    private Timestamp timePublication;

    @OneToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id")
    private ClsDepartment department;

    @OneToMany(mappedBy = "regTypeRequestRestrictionTypeId.clsTypeRequest")
    private Set<RegTypeRequestRestrictionType> regTypeRequestRestrictionTypes;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private AdditionalFields additionalFields;

    @OrderBy("num asc")
    @OneToMany(targetEntity = RegTypeRequestPrescription.class, mappedBy = "typeRequest", fetch = FetchType.LAZY)
    private List<RegTypeRequestPrescription> regTypeRequestPrescriptions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "activity_kind")
    public String getActivityKind() {
        return activityKind;
    }

    public void setActivityKind(String activityKind) {
        this.activityKind = activityKind;
    }

    @Basic
    @Column(name = "prescription")
    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    @Basic
    @Column(name = "prescription_link")
    public String getPrescriptionLink() {
        return prescriptionLink;
    }

    public void setPrescriptionLink(String prescriptionLink) {
        this.prescriptionLink = prescriptionLink;
    }

    @Basic
    @Column(name = "settings")
    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Basic
    @Column(name = "status_registration")
    public int getStatusRegistration() {
        return statusRegistration;
    }

    public void setStatusRegistration(int statusRegistration) {
        this.statusRegistration = statusRegistration;
    }

    @Column(name = "begin_registration")
    public Timestamp getBeginRegistration() {
        return beginRegistration;
    }

    public void setBeginRegistration(Timestamp beginRegistration) {
        this.beginRegistration = beginRegistration;
    }

    @Column(name = "end_registration")
    public Timestamp getEndRegistration() {
        return endRegistration;
    }

    public void setEndRegistration(Timestamp endRegistration) {
        this.endRegistration = endRegistration;
    }

    @Column(name = "status_visible")
    public int getStatusVisible() {
        return statusVisible;
    }

    public void setStatusVisible(int statusVisible) {
        this.statusVisible = statusVisible;
    }

    @Column(name = "begin_visible")
    public Timestamp getBeginVisible() {
        return beginVisible;
    }

    public void setBeginVisible(Timestamp beginVisible) {
        this.beginVisible = beginVisible;
    }

    @Column(name = "end_visible")
    public Timestamp getEndVisible() {
        return endVisible;
    }

    public void setEndVisible(Timestamp endVisible) {
        this.endVisible = endVisible;
    }

    @Column(name = "sort_weight")
    public int getSortWeight() {
        return sortWeight;
    }

    public void setSortWeight(int sortWeight) {
        this.sortWeight = sortWeight;
    }

    @Basic
    @Column(name = "consent")
    public String getConsent() {
        return consent;
    }

    public void setConsent(String consent) {
        this.consent = consent;
    }

    @Basic
    @Column(name = "status_publication")
    public int getStatusPublication() {
        return statusPublication;
    }

    public void setStatusPublication(int statusPublication) {
        this.statusPublication = statusPublication;
    }

    public String getStatusPublicationName(){
        String result  = "";
        if (this.statusPublication == PublicationStatuses.NOT_PUBLISHED.getValue()) {
            result = "Не опубликовано";
        } else if (this.statusPublication == PublicationStatuses.PUBLISHED.getValue()) {
            result = "Опубликовано";
        }
        return result;
    }

    @Basic
    @Column(name = "time_publication")
    public Timestamp getTimePublication() {
        return timePublication;
    }

    public void setTimePublication(Timestamp timePublication) {
        this.timePublication = timePublication;
    }

    public ClsDepartment getDepartment() {
        return department;
    }

    public void setDepartment(ClsDepartment department) {
        this.department = department;
    }

    public Set<RegTypeRequestRestrictionType> getRegTypeRequestRestrictionTypes() {
        return regTypeRequestRestrictionTypes;
    }

    public void setRegTypeRequestRestrictionTypes(Set<RegTypeRequestRestrictionType> regTypeRequestRestrictionTypes) {
        this.regTypeRequestRestrictionTypes = regTypeRequestRestrictionTypes;
    }

    public AdditionalFields getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(AdditionalFields additionalFields) {
        this.additionalFields = additionalFields;
    }

    public List<RegTypeRequestPrescription> getRegTypeRequestPrescriptions() {
        return regTypeRequestPrescriptions;
    }

    public void setRegTypeRequestPrescriptions(List<RegTypeRequestPrescription> regTypeRequestPrescriptions) {
        this.regTypeRequestPrescriptions = regTypeRequestPrescriptions;
    }

    public String getValue() {
        return this.activityKind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsTypeRequest that = (ClsTypeRequest) o;
        return id == that.id &&
                statusRegistration == that.statusRegistration &&
                Objects.equals(activityKind, that.activityKind) &&
                Objects.equals(prescription, that.prescription) &&
                Objects.equals(prescriptionLink, that.prescriptionLink) &&
                Objects.equals(settings, that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activityKind, prescription, prescriptionLink, settings, statusRegistration);
    }

    @Basic
    @Column(name = "short_name")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
