package ru.sibdigital.proccovid.model;

public enum MailingMessageStatuses {
    IS_CREATED((short) 0),
    IS_QUEUED((short) 1),
    IS_SENT((short) 2);

    private final short value;

    private MailingMessageStatuses(short value) {
        this.value = value;
    }

    public short value() {
        return this.value;
    }
}
