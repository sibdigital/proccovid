package ru.sibdigital.proccovid.dto;


public class ClsOkvedDto {
    private String id;
    private String value;
    private String path;
    private String name_okved;

    public ClsOkvedDto(){

    }

    public ClsOkvedDto(String id, String value){
        this.id = id;
        this.value = value;
        this.path = id;
        this.name_okved = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
