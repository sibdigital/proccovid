package ru.sibdigital.proccovid.model;

public enum UserStatuses {
    NOT_ACTIVE(0), // новый пользователь
    ACTIVE(1),
    DEACTIVE(2); // деактивирован

    private final int value;
    private UserStatuses(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
