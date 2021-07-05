package ru.sibdigital.proccovid.model.report;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class OrganizationCountByOkvedsReport {
    @Id
    private UUID id;
    private String kindCode;
    private String kindName;
    private Integer employeeCount;
    private Integer organizationCount;

    public OrganizationCountByOkvedsReport() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Integer getOrganizationCount() {
        return organizationCount;
    }

    public void setOrganizationCount(Integer organizationCount) {
        this.organizationCount = organizationCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationCountByOkvedsReport that = (OrganizationCountByOkvedsReport) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(kindCode, that.kindCode) &&
                Objects.equals(kindName, that.kindName) &&
                Objects.equals(employeeCount, that.employeeCount) &&
                Objects.equals(organizationCount, that.organizationCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kindCode, kindName, employeeCount, organizationCount);
    }
}