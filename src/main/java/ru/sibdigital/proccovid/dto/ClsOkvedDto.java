package ru.sibdigital.proccovid.dto;

import java.util.UUID;

public class ClsOkvedDto {
    private Long id;
    private String value;
    private UUID uuid;

    public ClsOkvedDto(){

    }

    public ClsOkvedDto(Long id, String value, UUID uuid){
        this.id = id;
        this.value = value;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setType(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
