package ru.sibdigital.proccovid.model;

import lombok.Builder;

import javax.persistence.*;

@Entity
@Table(name = "reg_type_request_okved", schema = "public")
@Builder(toBuilder = true)
public class RegTypeRequestOkved {

    @EmbeddedId
    private RegTypeRequestOkvedId regTypeRequestOkvedId;

    public RegTypeRequestOkved() {
    }

    public RegTypeRequestOkved(RegTypeRequestOkvedId regTypeRequestOkvedId) {
        this.regTypeRequestOkvedId = regTypeRequestOkvedId;
    }

    public RegTypeRequestOkvedId getClsTypeRequestOkvedId() {
        return regTypeRequestOkvedId;
    }

    public void setClsTypeRequestOkvedId(RegTypeRequestOkvedId regTypeRequestOkvedId) {
        this.regTypeRequestOkvedId = regTypeRequestOkvedId;
    }
}
