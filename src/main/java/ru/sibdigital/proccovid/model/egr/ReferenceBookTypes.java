package ru.sibdigital.proccovid.model.egr;

public enum ReferenceBookTypes {
    SULST(Short.valueOf("0")),
    SIPST(Short.valueOf("1")),
    SPVZ(Short.valueOf("2"));

    private final Short value;
    private ReferenceBookTypes(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }
}
