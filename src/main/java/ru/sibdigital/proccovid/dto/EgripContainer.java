package ru.sibdigital.proccovid.dto;

import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgripOkved;
import ru.sibdigital.proccovid.model.RegEgrul;
import ru.sibdigital.proccovid.model.egr.SvRecordEgr;
import ru.sibdigital.proccovid.model.egr.SvStatus;

import java.util.Set;

public class EgripContainer {
    private RegEgrip regEgrip;
    private Set<RegEgripOkved> regEgripOkved;
    private Set<SvStatus> svStatuses;
    private Set<SvRecordEgr> svRecords;

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

    public Set<SvStatus> getSvStatuses() {
        return svStatuses;
    }

    public void setSvStatuses(Set<SvStatus> svStatuses) {
        this.svStatuses = svStatuses;
    }

    public Set<SvRecordEgr> getSvRecords() {
        return svRecords;
    }

    public void setSvRecords(Set<SvRecordEgr> svRecords) {
        this.svRecords = svRecords;
    }
}
