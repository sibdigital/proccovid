package ru.sibdigital.proccovid.dto;

import ru.sibdigital.proccovid.model.RegEgrul;
import ru.sibdigital.proccovid.model.RegEgrulOkved;
import ru.sibdigital.proccovid.model.RegFilial;

import java.util.Set;

public class EgrulContainer {
    private RegEgrul regEgrul;
    private Set<RegEgrulOkved> regEgrulOkved;
    private Set<RegFilial> regFilials;

    public EgrulContainer(RegEgrul regEgrul){
        this.setRegEgrul(regEgrul);
    }

    public RegEgrul getRegEgrul() {
        return regEgrul;
    }

    public void setRegEgrul(RegEgrul regEgrul) {
        this.regEgrul = regEgrul;
    }

    public Set<RegEgrulOkved> getRegEgrulOkved() {
        return regEgrulOkved;
    }

    public void setRegEgrulOkved(Set<RegEgrulOkved> regEgrulOkved) {
        this.regEgrulOkved = regEgrulOkved;
    }

    public Set<RegFilial> getRegFilials() {
        return regFilials;
    }

    public void setRegFilials(Set<RegFilial> regFilials) {
        this.regFilials = regFilials;
    }
}
