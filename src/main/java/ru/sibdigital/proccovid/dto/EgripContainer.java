package ru.sibdigital.proccovid.dto;

import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgripOkved;
import ru.sibdigital.proccovid.model.RegEgrul;

import java.util.Set;

public class EgripContainer {
    private RegEgrip regEgrip;
    private Set<RegEgripOkved> regEgripOkved;

    public EgripContainer(RegEgrip regEgrip){
        this.setRegEgrip(regEgrip);
    }

    public RegEgrip getRegEgrip() {
        return regEgrip;
    }

    public void setRegEgrip(RegEgrip regEgrip) {
        this.regEgrip = regEgrip;
    }

    public Set<RegEgripOkved> getRegEgripOkved() {
        return regEgripOkved;
    }

    public void setRegEgripOkved(Set<RegEgripOkved> regEgripOkved) {
        this.regEgripOkved = regEgripOkved;
    }
}
