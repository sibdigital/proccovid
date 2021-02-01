package ru.sibdigital.proccovid.model.egr;

public enum EgrReferenceBookStatuses {
    ANOTHER(0),
    ORGANIZATION_NOT_ACTIVE(1);

    private final int value;
    private EgrReferenceBookStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
