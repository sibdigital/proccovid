package ru.sibdigital.proccovid.model;

public enum Types {
    CLASS((short) 1),
    SUBCLASS((short) 2),
    GROUP((short) 3),
    SUBGROUP((short) 4),
    KIND((short) 5);

    private final short value;

    private Types(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
