package ru.sibdigital.proccovid.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestSubsidyShortDto {
    private Long id;
    private OrganizationShortDto organization;
    private Date timeCreate;
    private Date timeSend;
    private SubsidyRequestStatusShortDto status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrganizationShortDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationShortDto organization) {
        this.organization = organization;
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

    public SubsidyRequestStatusShortDto getStatus() {
        return status;
    }

    public void setStatus(SubsidyRequestStatusShortDto status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestSubsidyShortDto that = (RequestSubsidyShortDto) o;
        return Objects.equals(id, that.id) && Objects.equals(organization, that.organization) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(timeSend, that.timeSend) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organization, timeCreate, timeSend, status);
    }
}
