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
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "doc_request", schema = "public")
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
public class DocRequestPrs implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DOC_SEQ_GEN", sequenceName = "doc_request_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOC_SEQ_GEN")
    private Long id;
    private Long personOfficeCnt;
    private Long personRemoteCnt;
    private Long personSlrySaveCnt;
    //private Long personOfficeFactCnt;
    private String attachmentPath;
    private Integer statusReview;
    private Timestamp timeCreate;
    private Integer statusImport;
    private Timestamp timeImport;
    private Timestamp timeReview;
    private Boolean isAgree;
    private Boolean isProtect;
    private String reqBasis;
    private String orgHashCode;
    private String rejectComment;
    private Long old_department_id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private AdditionalAttributes additionalAttributes;

//    @JsonIgnore
//    @OneToMany(targetEntity = DocPerson.class, mappedBy="docRequest", fetch = FetchType.LAZY)
//    private Set<DocPerson> docPersonSet;

    @OneToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id")
    private ClsDepartment department;

    @OneToOne
    @JoinColumn(name = "id_organization", referencedColumnName = "id")
    private ClsOrganization organization;

    @OneToOne
    @JoinColumn(name = "id_reassigned_user", referencedColumnName = "id")
    private ClsUser reassignedUser;

    @OneToOne
    @JoinColumn(name = "id_processed_user", referencedColumnName = "id")
    private ClsUser processedUser;

    @OneToOne
    @JoinColumn(name = "id_type_request", referencedColumnName = "id")
    private ClsTypeRequest typeRequest;

    @OneToOne
    @JoinColumn(name = "id_district", referencedColumnName = "id")
    private ClsDistrict district;

//    @OneToMany(targetEntity = DocAddressFact.class, mappedBy="docRequestAddressFact", fetch = FetchType.EAGER)
//    private Set<DocAddressFact> docAddressFact;

/*    @SequenceGenerator(name = "REQUEST_SEQ", sequenceName = "doc_request_id_seq")*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "person_office_cnt", nullable = false)
    public Long getPersonOfficeCnt() {
        return personOfficeCnt;
    }

    public void setPersonOfficeCnt(Long personOfficeCnt) {
        this.personOfficeCnt = personOfficeCnt;
    }

    @Basic
    @Column(name = "person_remote_cnt", nullable = false)
    public Long getPersonRemoteCnt() {
        return personRemoteCnt;
    }

    public void setPersonRemoteCnt(Long personRemoteCnt) {
        this.personRemoteCnt = personRemoteCnt;
    }

    @Basic
    @Column(name = "person_slry_save_cnt", nullable = false)
    public Long getPersonSlrySaveCnt() {
        return personSlrySaveCnt;
    }

    public void setPersonSlrySaveCnt(Long personSlrySaveCnt) {
        this.personSlrySaveCnt = personSlrySaveCnt;
    }

/*
    @Basic
    @Column(name = "person_office_fact_cnt", nullable = false)
    public Long getPersonOfficeFactCnt() {
        return personOfficeFactCnt;
    }

    public void setPersonOfficeFactCnt(Long personOfficeFactCnt) {
        this.personOfficeFactCnt = personOfficeFactCnt;
    }
*/

    @Basic
    @Column(name = "attachment_path", nullable = false, length = 255)
    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    @Basic
    @Column(name = "status_review", nullable = false)
    public Integer getStatusReview() {
        return statusReview;
    }

    public void setStatusReview(Integer statusReview) {
        this.statusReview = statusReview;
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
    @Column(name = "time_review", nullable = true)
    public Timestamp getTimeReview() {
        return timeReview;
    }

    @Basic
    @Column(name = "is_agree", nullable = false)
    public Boolean getAgree() {
        return isAgree;
    }

    public void setAgree(Boolean agree) {
        isAgree = agree;
    }

    @Basic
    @Column(name = "is_protect", nullable = false)
    public Boolean getProtect() {
        return isProtect;
    }

    public void setProtect(Boolean protect) {
        isProtect = protect;
    }

    @Basic
    @Column(name = "req_basis", nullable = false)
    public String getReqBasis() {
        return reqBasis;
    }

    public void setReqBasis(String reqBasis) {
        this.reqBasis = reqBasis;
    }

    @Basic
    @Column(name = "org_hash_code", nullable = false)
    public String getOrgHashCode() {
        return orgHashCode;
    }

    public void setOrgHashCode(String orgHashCode) {
        this.orgHashCode = orgHashCode;
    }

    public void setTimeReview(Timestamp timeReview) {
        this.timeReview = timeReview;
    }
//
//    public Set<DocPerson> getDocPersonSet() {
//        return docPersonSet;
//    }
//
//    public void setDocPersonSet(Set<DocPerson> docPersonSet) {
//        this.docPersonSet = docPersonSet;
//    }

    public ClsDepartment getDepartment() {
        return department;
    }

    public void setDepartment(ClsDepartment department) {
        this.department = department;
    }

    public ClsOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ClsOrganization organization) {
        this.organization = organization;
    }

    public ClsUser getReassignedUser() {
        return reassignedUser;
    }

    public void setReassignedUser(ClsUser reassignedUser) {
        this.reassignedUser = reassignedUser;
    }

    public ClsUser getProcessedUser() {
        return processedUser;
    }

    public void setProcessedUser(ClsUser processedUser) {
        this.processedUser = processedUser;
    }

    public ClsTypeRequest getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(ClsTypeRequest typeRequest) {
        this.typeRequest = typeRequest;
    }

    public ClsDistrict getDistrict() {
        return district;
    }

    public void setDistrict(ClsDistrict district) {
        this.district = district;
    }

//    public Set<DocAddressFact> getDocAddressFact() {
//        return docAddressFact;
//    }
//
//    public void setDocAddressFact(Set<DocAddressFact> docAddressFact) {
//        this.docAddressFact = docAddressFact;
//    }

    @Basic
    @Column(name = "reject_comment", nullable = false)
    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }

    @Basic
    @Column(name = "old_department_id", nullable = false)
    public Long getOld_department_id() {
        return old_department_id;
    }

    public void setOld_department_id(Long old_department_id) {
        this.old_department_id = old_department_id;
    }

    public AdditionalAttributes getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(AdditionalAttributes additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocRequestPrs that = (DocRequestPrs) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(personOfficeCnt, that.personOfficeCnt) &&
                Objects.equals(personRemoteCnt, that.personRemoteCnt) &&
                Objects.equals(personSlrySaveCnt, that.personSlrySaveCnt) &&
                Objects.equals(attachmentPath, that.attachmentPath) &&
                Objects.equals(statusReview, that.statusReview) &&
                Objects.equals(timeCreate, that.timeCreate) &&
                Objects.equals(statusImport, that.statusImport) &&
                Objects.equals(timeImport, that.timeImport) &&
                Objects.equals(timeReview, that.timeReview) &&
                Objects.equals(isAgree, that.isAgree) &&
                Objects.equals(isProtect, that.isProtect) &&
                Objects.equals(reqBasis, that.reqBasis) &&
                Objects.equals(orgHashCode, that.orgHashCode) &&
                Objects.equals(typeRequest, that.typeRequest) &&
                //Objects.equals(docPersonSet, that.docPersonSet) &&
                Objects.equals(department, that.department) &&
                Objects.equals(organization, that.organization); // &&
//                Objects.equals(docAddressFact, that.docAddressFact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, personOfficeCnt, personRemoteCnt, personSlrySaveCnt, attachmentPath, statusReview, timeCreate, statusImport, timeImport, timeReview, isAgree, isProtect, reqBasis, orgHashCode, typeRequest, department, organization);
    }
}
