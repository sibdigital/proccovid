package ru.sibdigital.proccovid.model;

public enum MailingStatuses {
    EMAIL_SENT((short) 0),
    INVALID_ADDRESS((short) 1),
    EMAIL_NOT_CREATED((short) 2),
    EMAIL_NOT_SENT((short) 3);

    private final short value;

    private MailingStatuses(short value) {
        this.value = value;
    }

    public short value() {
        return this.value;
    }
}
