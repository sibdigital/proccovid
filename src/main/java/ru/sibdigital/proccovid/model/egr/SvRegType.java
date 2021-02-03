package ru.sibdigital.proccovid.model.egr;

public enum SvRegType{
    NALOG((short) 1),
    PF((short) 2),
    FSS((short) 3);

    private final short value;

    private SvRegType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
