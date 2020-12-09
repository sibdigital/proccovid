package ru.sibdigital.proccovid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RegTypeRequestOkvedId implements Serializable {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_type_request")
    private ClsTypeRequest clsTypeRequest;

    @ManyToOne
    @JoinColumn(name="id_okved")
    private Okved okved;

    public RegTypeRequestOkvedId() {
    }

    public RegTypeRequestOkvedId(ClsTypeRequest clsTypeRequest, Okved okved) {
        this.clsTypeRequest = clsTypeRequest;
        this.okved = okved;
    }

    public ClsTypeRequest getClsTypeRequest() {
        return clsTypeRequest;
    }

    public void setClsTypeRequest(ClsTypeRequest clsTypeRequest) {
        this.clsTypeRequest = clsTypeRequest;
    }

    public Okved getOkved() {
        return okved;
    }

    public void setOkved(Okved okved) {
        this.okved = okved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegTypeRequestOkvedId that = (RegTypeRequestOkvedId) o;
        return Objects.equals(clsTypeRequest, that.clsTypeRequest) &&
                Objects.equals(okved, that.okved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clsTypeRequest, okved);
    }
}
