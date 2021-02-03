package ru.sibdigital.proccovid.dto;

import ru.sibdigital.proccovid.model.RegEgrul;
import ru.sibdigital.proccovid.model.RegEgrulOkved;
import ru.sibdigital.proccovid.model.RegFilial;
import ru.sibdigital.proccovid.model.egr.*;

import java.util.Set;

public class EgrulContainer {
    private RegEgrul regEgrul;
    private Set<RegEgrulOkved> regEgrulOkved;
    private Set<RegFilial> regFilials;
    private Set<SvStatus> svStatuses;
    private Set<SvRecordEgr> svRecords;
    private Set<SvOrg> svOrgs;
    private Set<SvReg> svRegs;
    private Opf opf;

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

    public Set<SvOrg> getSvOrgs() {
        return svOrgs;
    }

    public void setSvOrgs(Set<SvOrg> svOrgs) {
        this.svOrgs = svOrgs;
    }

    public Set<SvReg> getSvRegs() {
        return svRegs;
    }

    public void setSvRegs(Set<SvReg> svRegs) {
        this.svRegs = svRegs;
    }

    public Opf getOpf() {
        return opf;
    }

    public void setOpf(Opf opf) {
        this.opf = opf;
    }
}
