package ru.sibdigital.proccovid.model.egr;

public enum EgrReferenceBookStatuses {
    ANOTHER(Short.valueOf("0")),
    ORGANIZATION_NOT_ACTIVE(Short.valueOf("1"));

    private final Short value;
    private EgrReferenceBookStatuses(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }
}
