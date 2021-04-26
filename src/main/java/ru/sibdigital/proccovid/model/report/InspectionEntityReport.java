package ru.sibdigital.proccovid.model.report;

import ru.sibdigital.proccovid.dto.report.ControlAuthorityShortDto;
import ru.sibdigital.proccovid.dto.report.OrganizationShortDto;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
public class InspectionEntityReport {
    @Id
    private Long id;
    private Date dateOfInspection;
    private Long idOrganization;
    private Long idAuthority;
    private String nameOrganization;
    private String shortNameOrganization;
    private String innOrganization;
    private String nameAuthority;
    private String shortNameAuthority;
    private Integer totalOrganization;
    private Integer totalAuthority;

    private String inspectionResult;
    private String comment;

    @Transient
    private OrganizationShortDto organization;

    @Transient
    private ControlAuthorityShortDto controlAuthority;

    @Transient
    private String stringDateOfInspection;


    public InspectionEntityReport() {

    }

    @PostLoad
    private void postLoad() {
        OrganizationShortDto org = OrganizationShortDto.builder()
                                    .id(this.idOrganization)
                                    .name(this.nameOrganization)
                                    .shortName(this.shortNameOrganization)
                                    .inn(this.innOrganization)
                                    .build();
        this.organization = org;

        ControlAuthorityShortDto auth = ControlAuthorityShortDto.builder()
                                        .id(this.idAuthority)
                                        .name(this.nameAuthority)
                                        .shortName(this.shortNameAuthority)
                                        .build();
        this.controlAuthority = auth;

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        this.stringDateOfInspection = df.format(dateOfInspection);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOrganization() {
        return idOrganization;
    }
    public void setIdOrganization(Long idOrganization) {
        this.idOrganization = idOrganization;
    }

    public Long getIdAuthority() {
        return idAuthority;
    }
    public void setIdAuthority(Long idAuthority) {
        this.idAuthority = idAuthority;
    }

    public String getNameOrganization() {
        return nameOrganization;
    }
    public void setNameOrganization(String nameOrganization) {
        this.nameOrganization = nameOrganization;
    }

    public String getShortNameOrganization() {
        return shortNameOrganization;
    }

    public void setShortNameOrganization(String shortNameOrganization) {
        this.shortNameOrganization = shortNameOrganization;
    }

    public String getNameAuthority() {
        return nameAuthority;
    }
    public void setNameAuthority(String nameAuthority) {
        this.nameAuthority = nameAuthority;
    }

    public String getShortNameAuthority() {
        return shortNameAuthority;
    }
    public void setShortNameAuthority(String shortNameAuthority) {
        this.shortNameAuthority = shortNameAuthority;
    }

    public Integer getTotalOrganization() {
        return totalOrganization;
    }
    public void setTotalOrganization(Integer totalOrganization) {
        this.totalOrganization = totalOrganization;
    }

    public Integer getTotalAuthority() {
        return totalAuthority;
    }
    public void setTotalAuthority(Integer totalAuthority) {
        this.totalAuthority = totalAuthority;
    }

    public OrganizationShortDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationShortDto organization) {
        this.organization = organization;
    }

    public ControlAuthorityShortDto getControlAuthority() {
        return controlAuthority;
    }

    public void setControlAuthority(ControlAuthorityShortDto controlAuthority) {
        this.controlAuthority = controlAuthority;
    }

    public Date getDateOfInspection() {
        return dateOfInspection;
    }

    public void setDateOfInspection(Date dateOfInspection) {
        this.dateOfInspection = dateOfInspection;
    }

    public String getInnOrganization() {
        return innOrganization;
    }

    public void setInnOrganization(String innOrganization) {
        this.innOrganization = innOrganization;
    }

    public String getInspectionResult() {
        return inspectionResult;
    }

    public void setInspectionResult(String inspectionResult) {
        this.inspectionResult = inspectionResult;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStringDateOfInspection() {
        return stringDateOfInspection;
    }

    public void setStringDateOfInspection(String stringDateOfInspection) {
        this.stringDateOfInspection = stringDateOfInspection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InspectionEntityReport that = (InspectionEntityReport) o;
        return Objects.equals(id, that.id) && Objects.equals(dateOfInspection, that.dateOfInspection) && Objects.equals(idOrganization, that.idOrganization) && Objects.equals(idAuthority, that.idAuthority) && Objects.equals(nameOrganization, that.nameOrganization) && Objects.equals(shortNameOrganization, that.shortNameOrganization) && Objects.equals(innOrganization, that.innOrganization) && Objects.equals(nameAuthority, that.nameAuthority) && Objects.equals(shortNameAuthority, that.shortNameAuthority) && Objects.equals(totalOrganization, that.totalOrganization) && Objects.equals(totalAuthority, that.totalAuthority) && Objects.equals(inspectionResult, that.inspectionResult) && Objects.equals(comment, that.comment) && Objects.equals(organization, that.organization) && Objects.equals(controlAuthority, that.controlAuthority) && Objects.equals(stringDateOfInspection, that.stringDateOfInspection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateOfInspection, idOrganization, idAuthority, nameOrganization, shortNameOrganization, innOrganization, nameAuthority, shortNameAuthority, totalOrganization, totalAuthority, inspectionResult, comment, organization, controlAuthority, stringDateOfInspection);
    }
}