package ru.sibdigital.proccovid.repository.specification;

import java.sql.Timestamp;

public class DocRequestSubsidySearchCriteria {
    private Long idDepartment;
    private String statusReview;
    private Integer idTypeRequest;
    private Integer idDistrict;
    private String innOrName;
    private Boolean isActualization;
    private Timestamp beginSearchTime;
    private Timestamp endSearchTime;


    public Long getIdDepartment() {
        return idDepartment;
    }

    public void setIdDepartment(Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    public String getStatusReview() {
        return statusReview;
    }

    public void setStatusReview(String statusReview) {
        this.statusReview = statusReview;
    }

    public Integer getIdTypeRequest() {
        return idTypeRequest;
    }

    public void setIdTypeRequest(Integer idTypeRequest) {
        this.idTypeRequest = idTypeRequest;
    }

    public Integer getIdDistrict() {
        return idDistrict;
    }

    public void setIdDistrict(Integer idDistrict) {
        this.idDistrict = idDistrict;
    }

    public Boolean getActualization() {
        return isActualization;
    }
    public void setActualization(Boolean isActualization) {
        this.isActualization = isActualization;
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
}
