package ru.sibdigital.proccovid.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_history_request", schema = "public")
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class RegHistoryRequest {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_HISTORY_REQUEST_SEQ_GEN", sequenceName = "reg_history_request_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_HISTORY_REQUEST_SEQ_GEN")
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
    private Timestamp regTime;

//    @JsonIgnore
//    @OneToMany(targetEntity = DocPerson.class, mappedBy="docRequest", fetch = FetchType.LAZY)
//    private List<DocPerson> docPersonList;

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
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private ClsUser user;

    @OneToOne
    @JoinColumn(name = "id_type_request", referencedColumnName = "id")
    private ClsTypeRequest typeRequest;

    @OneToOne
    @JoinColumn(name = "id_doc_request", referencedColumnName = "id")
    private DocRequest docRequest;

    public RegHistoryRequest(){

    }

    public RegHistoryRequest(DocRequest docRequest, ClsUser clsUser){
        this.personOfficeCnt = docRequest.getPersonOfficeCnt();
        this.personRemoteCnt = docRequest.getPersonRemoteCnt();
        this.personSlrySaveCnt = docRequest.getPersonSlrySaveCnt();
        this.attachmentPath = docRequest.getAttachmentPath();
        this.statusReview = docRequest.getStatusReview();
        this.timeCreate = docRequest.getTimeCreate();
        this.statusImport = docRequest.getStatusImport();
        this.timeImport = docRequest.getTimeImport();
        this.timeReview = docRequest.getTimeReview();
        this.isAgree = docRequest.getAgree();
        this.isProtect = docRequest.getProtect();
        this.reqBasis = docRequest.getReqBasis();
        this.orgHashCode = docRequest.getOrgHashCode();
        this.rejectComment = docRequest.getRejectComment();
        this.old_department_id = docRequest.getOld_department_id();
        this.typeRequest = docRequest.getTypeRequest();
        this.regTime = new Timestamp(System.currentTimeMillis());
        this.department = docRequest.getDepartment();
        this.organization = docRequest.getOrganization();
        this.reassignedUser = docRequest.getReassignedUser();
        this.processedUser = docRequest.getProcessedUser();
        this.user = clsUser;
        this.docRequest = docRequest;
    }
//    @OneToMany(targetEntity = DocAddressFact.class, mappedBy="docRequestAddressFact", fetch = FetchType.EAGER)
//    private List<DocAddressFact> docAddressFact;

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

//    public List<DocPerson> getDocPersonList() {
//        return docPersonList;
//    }
//
//    public void setDocPersonList(List<DocPerson> docPersonList) {
//        this.docPersonList = docPersonList;
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

//    public List<DocAddressFact> getDocAddressFact() {
//        return docAddressFact;
//    }

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

//    public void setDocAddressFact(List<DocAddressFact> docAddressFact) {
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

    public Timestamp getRegTime() {
        return regTime;
    }

    public void setRegTime(Timestamp regTime) {
        this.regTime = regTime;
    }

    public ClsUser getUser() {
        return user;
    }

    public void setUser(ClsUser user) {
        this.user = user;
    }

    public ClsTypeRequest getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(ClsTypeRequest typeRequest) {
        this.typeRequest = typeRequest;
    }

    public DocRequest getDocRequest() {
        return docRequest;
    }

    public void setDocRequest(DocRequest docRequest) {
        this.docRequest = docRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegHistoryRequest that = (RegHistoryRequest) o;
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
                Objects.equals(rejectComment, that.rejectComment) &&
                Objects.equals(getOld_department_id(), that.getOld_department_id()) &&
                Objects.equals(typeRequest, that.typeRequest) &&
                //Objects.equals(docPersonList, that.docPersonList) &&
                Objects.equals(department, that.department) &&
                Objects.equals(organization, that.organization);// &&
                //Objects.equals(docAddressFact, that.docAddressFact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, personOfficeCnt, personRemoteCnt, personSlrySaveCnt, attachmentPath, statusReview, timeCreate,
                statusImport, timeImport, timeReview, isAgree, isProtect, reqBasis, orgHashCode, rejectComment, getOld_department_id(),
                typeRequest, department, organization);
    }

}
