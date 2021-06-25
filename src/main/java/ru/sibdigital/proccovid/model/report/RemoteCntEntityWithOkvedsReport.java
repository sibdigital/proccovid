package ru.sibdigital.proccovid.model.report;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class RemoteCntEntityWithOkvedsReport {
    @Id
    private Long id;
    private String shortNameOrganization;
    private String organizationInn;
    private Integer cntByDocEmployee;
    private Integer allCount;
    private Integer remoteCnt;
    private Integer officeCnt;
    private String mainOkveds;
    private String additionalOkveds;
    private String byWhatOkvedType;

    public RemoteCntEntityWithOkvedsReport() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortNameOrganization() {
        return shortNameOrganization;
    }

    public void setShortNameOrganization(String shortNameOrganization) {
        this.shortNameOrganization = shortNameOrganization;
    }

    public String getOrganizationInn() {
        return organizationInn;
    }

    public void setOrganizationInn(String organizationInn) {
        this.organizationInn = organizationInn;
    }

    public Integer getCntByDocEmployee() {
        return cntByDocEmployee;
    }

    public void setCntByDocEmployee(Integer cntByDocEmployee) {
        this.cntByDocEmployee = cntByDocEmployee;
    }

    public Integer getAllCount() {
        return allCount;
    }

    public void setAllCount(Integer allCount) {
        this.allCount = allCount;
    }

    public Integer getRemoteCnt() {
        return remoteCnt;
    }

    public void setRemoteCnt(Integer remoteCnt) {
        this.remoteCnt = remoteCnt;
    }

    public Integer getOfficeCnt() {
        return officeCnt;
    }

    public void setOfficeCnt(Integer officeCnt) {
        this.officeCnt = officeCnt;
    }

    public String getMainOkveds() {
        return mainOkveds;
    }

    public void setMainOkveds(String mainOkveds) {
        this.mainOkveds = mainOkveds;
    }

    public String getAdditionalOkveds() {
        return additionalOkveds;
    }

    public void setAdditionalOkveds(String additionalOkveds) {
        this.additionalOkveds = additionalOkveds;
    }

    public String getByWhatOkvedType() {
        return byWhatOkvedType;
    }

    public void setByWhatOkvedType(String byWhatOkvedType) {
        this.byWhatOkvedType = byWhatOkvedType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteCntEntityWithOkvedsReport that = (RemoteCntEntityWithOkvedsReport) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(shortNameOrganization, that.shortNameOrganization) &&
                Objects.equals(organizationInn, that.organizationInn) &&
                Objects.equals(cntByDocEmployee, that.cntByDocEmployee) &&
                Objects.equals(allCount, that.allCount) &&
                Objects.equals(remoteCnt, that.remoteCnt) &&
                Objects.equals(officeCnt, that.officeCnt) &&
                Objects.equals(mainOkveds, that.mainOkveds) &&
                Objects.equals(additionalOkveds, that.additionalOkveds) &&
                Objects.equals(byWhatOkvedType, that.byWhatOkvedType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortNameOrganization, organizationInn, cntByDocEmployee, allCount, remoteCnt, officeCnt, mainOkveds, additionalOkveds, byWhatOkvedType);
    }
}