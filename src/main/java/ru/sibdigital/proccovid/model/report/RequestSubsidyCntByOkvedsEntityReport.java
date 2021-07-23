package ru.sibdigital.proccovid.model.report;

import ru.sibdigital.proccovid.dto.report.OkvedShortDto;
import ru.sibdigital.proccovid.dto.report.OrganizationShortDto;
import ru.sibdigital.proccovid.dto.report.RequestSubsidyShortDto;
import ru.sibdigital.proccovid.dto.report.SubsidyRequestStatusShortDto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class RequestSubsidyCntByOkvedsEntityReport {

    @Id
    private Long id;
    private UUID idOkved;
    private String kindCode;
    private String kindName;
    private Long idOrganization;
    private Long idDocRequest;
    private Long idRequestStatus;
    private Date timeSend;
    private Date timeCreate;
    private String organizationName;
    private String organizationShortName;
    private String organizationInn;
    private String codeStatus;
    private String nameStatus;
    private String shortNameStatus;

    @Transient
    private OrganizationShortDto organization;

    @Transient
    private OkvedShortDto okved;

    @Transient
    private SubsidyRequestStatusShortDto statusRequestSubsidy;

    @Transient
    private RequestSubsidyShortDto requestSubsidy;

    public RequestSubsidyCntByOkvedsEntityReport() {
    }

    @PostLoad
    private void postLoad() {
        OrganizationShortDto org = OrganizationShortDto.builder()
                .id(this.idOrganization)
                .name(this.organizationName)
                .shortName(this.organizationShortName)
                .inn(this.organizationInn)
                .build();
        this.organization = org;

        SubsidyRequestStatusShortDto status = SubsidyRequestStatusShortDto.builder()
                .id(this.idRequestStatus)
                .code(this.codeStatus)
                .name(this.nameStatus)
                .shortName(this.shortNameStatus)
                .build();

        this.statusRequestSubsidy = status;

        RequestSubsidyShortDto rssd = RequestSubsidyShortDto.builder()
                .id(this.idDocRequest)
                .timeCreate(this.timeCreate)
                .timeSend(this.timeSend)
                .status(this.statusRequestSubsidy )
                .organization(this.organization)
                .build();

        this.requestSubsidy = rssd;

        OkvedShortDto okv = OkvedShortDto.builder()
                .id(this.idOkved)
                .kindCode(this.kindCode)
                .kindName(this.kindName)
                .build();
        this.okved = okv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getIdOkved() {
        return idOkved;
    }

    public void setIdOkved(UUID idOkved) {
        this.idOkved = idOkved;
    }

    public String getKindCode() {
        return kindCode;
    }

    public void setKindCode(String kindCode) {
        this.kindCode = kindCode;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public Long getIdOrganization() {
        return idOrganization;
    }

    public void setIdOrganization(Long idOrganization) {
        this.idOrganization = idOrganization;
    }

    public Long getIdDocRequest() {
        return idDocRequest;
    }

    public void setIdDocRequest(Long idDocRequest) {
        this.idDocRequest = idDocRequest;
    }

    public Long getIdRequestStatus() {
        return idRequestStatus;
    }

    public void setIdRequestStatus(Long idRequestStatus) {
        this.idRequestStatus = idRequestStatus;
    }

    public Date getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Date timeSend) {
        this.timeSend = timeSend;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationShortName() {
        return organizationShortName;
    }

    public void setOrganizationShortName(String organizationShortName) {
        this.organizationShortName = organizationShortName;
    }

    public String getOrganizationInn() {
        return organizationInn;
    }

    public void setOrganizationInn(String organizationInn) {
        this.organizationInn = organizationInn;
    }

    public OrganizationShortDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationShortDto organization) {
        this.organization = organization;
    }

    public RequestSubsidyShortDto getRequestSubsidy() {
        return requestSubsidy;
    }

    public void setRequestSubsidy(RequestSubsidyShortDto requestSubsidy) {
        this.requestSubsidy = requestSubsidy;
    }

    public OkvedShortDto getOkved() {
        return okved;
    }

    public void setOkved(OkvedShortDto okved) {
        this.okved = okved;
    }

    public String getCodeStatus() {
        return codeStatus;
    }

    public void setCodeStatus(String codeStatus) {
        this.codeStatus = codeStatus;
    }

    public String getNameStatus() {
        return nameStatus;
    }

    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }

    public String getShortNameStatus() {
        return shortNameStatus;
    }

    public void setShortNameStatus(String shortNameStatus) {
        this.shortNameStatus = shortNameStatus;
    }

    public SubsidyRequestStatusShortDto getStatusRequestSubsidy() {
        return statusRequestSubsidy;
    }

    public void setStatusRequestSubsidy(SubsidyRequestStatusShortDto statusRequestSubsidy) {
        this.statusRequestSubsidy = statusRequestSubsidy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestSubsidyCntByOkvedsEntityReport that = (RequestSubsidyCntByOkvedsEntityReport) o;
        return Objects.equals(id, that.id) && Objects.equals(idOkved, that.idOkved) && Objects.equals(kindCode, that.kindCode) && Objects.equals(kindName, that.kindName) && Objects.equals(idOrganization, that.idOrganization) && Objects.equals(idDocRequest, that.idDocRequest) && Objects.equals(idRequestStatus, that.idRequestStatus) && Objects.equals(timeSend, that.timeSend) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(organizationName, that.organizationName) && Objects.equals(organizationShortName, that.organizationShortName) && Objects.equals(organizationInn, that.organizationInn) && Objects.equals(codeStatus, that.codeStatus) && Objects.equals(nameStatus, that.nameStatus) && Objects.equals(shortNameStatus, that.shortNameStatus) && Objects.equals(organization, that.organization) && Objects.equals(okved, that.okved) && Objects.equals(statusRequestSubsidy, that.statusRequestSubsidy) && Objects.equals(requestSubsidy, that.requestSubsidy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idOkved, kindCode, kindName, idOrganization, idDocRequest, idRequestStatus, timeSend, timeCreate, organizationName, organizationShortName, organizationInn, codeStatus, nameStatus, shortNameStatus, organization, okved, statusRequestSubsidy, requestSubsidy);
    }
}
