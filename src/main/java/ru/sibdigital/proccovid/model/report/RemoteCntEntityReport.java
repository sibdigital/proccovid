package ru.sibdigital.proccovid.model.report;

import ru.sibdigital.proccovid.dto.report.ControlAuthorityShortDto;
import ru.sibdigital.proccovid.dto.report.OrganizationShortDto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
public class RemoteCntEntityReport {
    @Id
    private Long id;
    private Date timeEdit;
    private String shortNameOrganization;
    private String organizationInn;
    private Integer allCount;
    private Integer remoteCnt;
    private Integer officeCnt;

    @Transient
    private String remotePercentString;

    @Transient
    private String stringTimeEdit;


    public RemoteCntEntityReport() {

    }

    @PostLoad
    private void postLoad() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        this.stringTimeEdit = df.format(timeEdit);

        if (allCount != 0) {
            Float remotePercent = remoteCnt*Float.valueOf("100")/allCount;

            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(2);
            this.remotePercentString = decimalFormat.format(remotePercent);
        } else {
            this.remotePercentString = "0.00";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimeEdit() {
        return timeEdit;
    }

    public void setTimeEdit(Date timeEdit) {
        this.timeEdit = timeEdit;
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

    public String getRemotePercentString() {
        return remotePercentString;
    }

    public void setRemotePercentString(String remotePercentString) {
        this.remotePercentString = remotePercentString;
    }

    public String getStringTimeEdit() {
        return stringTimeEdit;
    }

    public void setStringTimeEdit(String stringTimeEdit) {
        this.stringTimeEdit = stringTimeEdit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteCntEntityReport that = (RemoteCntEntityReport) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(timeEdit, that.timeEdit) &&
                Objects.equals(shortNameOrganization, that.shortNameOrganization) &&
                Objects.equals(organizationInn, that.organizationInn) &&
                Objects.equals(allCount, that.allCount) &&
                Objects.equals(remoteCnt, that.remoteCnt) &&
                Objects.equals(officeCnt, that.officeCnt) &&
                Objects.equals(remotePercentString, that.remotePercentString) &&
                Objects.equals(stringTimeEdit, that.stringTimeEdit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeEdit, shortNameOrganization, organizationInn, allCount, remoteCnt, officeCnt, remotePercentString, stringTimeEdit);
    }
}