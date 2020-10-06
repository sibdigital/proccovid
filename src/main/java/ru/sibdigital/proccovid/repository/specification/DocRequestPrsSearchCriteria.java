package ru.sibdigital.proccovid.repository.specification;

public class DocRequestPrsSearchCriteria {

    private Long idDepartment;
    private Integer statusReview;
    private Integer idTypeRequest;
    private Integer idDistrict;
    private String innOrName;
    private Boolean isActualization;

    public Long getIdDepartment() {
        return idDepartment;
    }

    public void setIdDepartment(Long idDepartment) {
        this.idDepartment = idDepartment;
    }

    public Integer getStatusReview() {
        return statusReview;
    }

    public void setStatusReview(Integer statusReview) {
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
}
