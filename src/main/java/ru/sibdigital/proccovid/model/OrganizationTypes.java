package ru.sibdigital.proccovid.model;

public enum OrganizationTypes {
    JURIDICAL(1),
    PHYSICAL(2),
    SELF_EMPLOYED(3),
    FILIATION(4),
    REPRESENTATION(5),
    DETACHED(6),
    IP(7),
    KFH(8);

    private final int value;

    private OrganizationTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
