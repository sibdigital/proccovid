package ru.sibdigital.proccovid.model.report;

import ru.sibdigital.proccovid.dto.report.ControlAuthorityShortDto;
import ru.sibdigital.proccovid.dto.report.OrganizationShortDto;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.model.ClsOrganization;

import javax.persistence.*;

@Entity
public class InspectionEntityReport {
    @Id
    private Long id;
    private Long idOrganization;
    private Long idAuthority;
    private String nameOrganization;
    private String shortNameOrganization;
    private String nameAuthority;
    private String shortNameAuthority;
    private Integer totalOrganization;
    private Integer totalAuthority;

    @Transient
    private OrganizationShortDto organization;

    @Transient
    private ControlAuthorityShortDto controlAuthority;


    public InspectionEntityReport() {

    }

    public InspectionEntityReport(Long id, Long idOrganization, Long idAuthority, String nameOrganization, String shortNameOrganization, String nameAuthority, String shortNameAuthority, Integer totalOrganization, Integer totalAuthority) {
        this.id = id;
        this.idOrganization = idOrganization;
        this.idAuthority = idAuthority;
        this.nameOrganization = nameOrganization;
        this.shortNameOrganization = shortNameOrganization;
        this.nameAuthority = nameAuthority;
        this.shortNameAuthority = shortNameAuthority;
        this.totalOrganization = totalOrganization;
        this.totalAuthority = totalAuthority;
    }

    @PostLoad
    private void postLoad() {
        OrganizationShortDto org = OrganizationShortDto.builder()
                                    .id(this.idOrganization)
                                    .name(this.nameOrganization)
                                    .shortName(this.shortNameOrganization)
                                    .build();
        this.organization = org;

        ControlAuthorityShortDto auth = ControlAuthorityShortDto.builder()
                                        .id(this.idAuthority)
                                        .name(this.nameAuthority)
                                        .shortName(this.shortNameAuthority)
                                        .build();
        this.controlAuthority = auth;
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
}