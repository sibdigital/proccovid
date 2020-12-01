package ru.sibdigital.proccovid.model;

public enum ReviewStatuses {
    NEW(100),
    OPENED(0),
    CONFIRMED(1),
    REJECTED(2),
    UPDATED(3),
    ACCEPTED(4);

    private final int value;
    private ReviewStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
