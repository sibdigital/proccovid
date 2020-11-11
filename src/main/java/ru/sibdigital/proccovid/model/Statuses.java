package ru.sibdigital.proccovid.model;

public enum Statuses {
    NOT_ACTIVE((short) 0),
    ACTIVE((short) 1);

    private final short value;

    private Statuses(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
