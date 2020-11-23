package ru.sibdigital.proccovid.model;

public enum MailingListStatuses {
    NOT_VALID((short) 0),
    VALID((short) 1);

    private final short value;

    private MailingListStatuses(short value) {
        this.value = value;
    }

    public short value() {
        return this.value;
    }
}
