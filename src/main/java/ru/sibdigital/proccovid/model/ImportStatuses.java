package ru.sibdigital.proccovid.model;

public enum ImportStatuses {
    SUCCESS(0),
    IMPORTED_SUCCESFULLY(1),
    READY_TO_IMPORT(2);

    private final int value;
    private ImportStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
