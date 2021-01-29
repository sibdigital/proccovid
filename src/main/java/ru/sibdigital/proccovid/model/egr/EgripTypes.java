package ru.sibdigital.proccovid.model.egr;

public enum EgripTypes {
    INDIVIDUAL_ENTREPRENEUR(Short.valueOf("0")),
    HEAD_OF_KFH(Short.valueOf("1"));

    private final Short value;
    private EgripTypes(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }
}
