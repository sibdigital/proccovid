package ru.sibdigital.proccovid.repository.specification;

import java.sql.Timestamp;

public class DocRequestSubsidySearchCriteria {
    private Long idDepartment;
    private Integer idTypeRequest;
    private String innOrName;
    private Timestamp beginSearchTime;
    private Timestamp endSearchTime;
    private String subsidyRequestStatusShortName;
    private Long subsidyId;
    private Boolean isCurrentUserAdmin;


    public Long getIdDepartment() {
        return idDepartment;
    }

    public void setIdDepartment(Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    public Integer getIdTypeRequest() {
        return idTypeRequest;
    }

    public void setIdTypeRequest(Integer idTypeRequest) {
        this.idTypeRequest = idTypeRequest;
    }

    public String getInnOrName() {
        return innOrName;
    }

    public void setInnOrName(String innOrName) {
        this.innOrName = innOrName;
    }

    public Timestamp getBeginSearchTime() {
        return beginSearchTime;
    }

    public void setBeginSearchTime(Timestamp beginSearchTime) {
        this.beginSearchTime = beginSearchTime;
    }

    public Timestamp getEndSearchTime() {
        return endSearchTime;
    }

    public void setEndSearchTime(Timestamp endSearchTime) {
        this.endSearchTime = endSearchTime;
    }

    public String getSubsidyRequestStatusShortName() {
        return subsidyRequestStatusShortName;
    }

    public void setSubsidyRequestStatusShortName(String subsidyRequestStatusShortName) {
        this.subsidyRequestStatusShortName = subsidyRequestStatusShortName;
    }

    public Long getSubsidyId() {
        return subsidyId;
    }

    public void setSubsidyId(Long subsidyId) {
        this.subsidyId = subsidyId;
    }

    public Boolean getIsCurrentUserAdmin() {
        return isCurrentUserAdmin;
    }

    public void setIsCurrentUserAdmin(Boolean isCurrentUserAdmin) {
        this.isCurrentUserAdmin = isCurrentUserAdmin;
    }
}
