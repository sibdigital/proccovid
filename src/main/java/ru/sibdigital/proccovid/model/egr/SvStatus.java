package ru.sibdigital.proccovid.model.egr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgrul;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "sv_status", schema = "egr")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SvStatus {
    private Long id;
    private RegEgrul egrul;
    private RegEgrip egrip;
    private ReferenceBook sulst;
    private Date exclDecDate;
    private String exclDecNum;
    private Date publDate;
    private String journalNum;
    private String grn;
    private Date recordDate;
    private String grnCorr;
    private Date recordDateCorr;

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "SV_STATUS_GEN", sequenceName = "sv_status_id_seq", allocationSize = 1, schema = "egr")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SV_STATUS_GEN")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "id_egrul", referencedColumnName = "id")
    public RegEgrul getEgrul() {
        return egrul;
    }
    public void setEgrul(RegEgrul egrul) {
        this.egrul = egrul;
    }

    @ManyToOne
    @JoinColumn(name = "id_egrip", referencedColumnName = "id")
    public RegEgrip getEgrip() {
        return egrip;
    }
    public void setEgrip(RegEgrip egrip) {
        this.egrip = egrip;
    }

    @ManyToOne
    @JoinColumn(name = "id_sulst", referencedColumnName = "id")
    public ReferenceBook getSulst() {
        return sulst;
    }
    public void setSulst(ReferenceBook sulst) {
        this.sulst = sulst;
    }

    public Date getExclDecDate() {
        return exclDecDate;
    }

    public void setExclDecDate(Date exclDecDate) {
        this.exclDecDate = exclDecDate;
    }

    public String getExclDecNum() {
        return exclDecNum;
    }

    public void setExclDecNum(String exclDecNum) {
        this.exclDecNum = exclDecNum;
    }

    public Date getPublDate() {
        return publDate;
    }

    public void setPublDate(Date publDate) {
        this.publDate = publDate;
    }

    public String getJournalNum() {
        return journalNum;
    }

    public void setJournalNum(String journalNum) {
        this.journalNum = journalNum;
    }

    public String getGrn() {
        return grn;
    }

    public void setGrn(String grn) {
        this.grn = grn;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getGrnCorr() {
        return grnCorr;
    }

    public void setGrnCorr(String grnCorr) {
        this.grnCorr = grnCorr;
    }

    public Date getRecordDateCorr() {
        return recordDateCorr;
    }

    public void setRecordDateCorr(Date recordDateCorr) {
        this.recordDateCorr = recordDateCorr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SvStatus svStatus = (SvStatus) o;
        return Objects.equals(id, svStatus.id) &&
                Objects.equals(egrul, svStatus.egrul) &&
                Objects.equals(egrip, svStatus.egrip) &&
                Objects.equals(sulst, svStatus.sulst) &&
                Objects.equals(exclDecDate, svStatus.exclDecDate) &&
                Objects.equals(exclDecNum, svStatus.exclDecNum) &&
                Objects.equals(publDate, svStatus.publDate) &&
                Objects.equals(journalNum, svStatus.journalNum) &&
                Objects.equals(grn, svStatus.grn) &&
                Objects.equals(recordDate, svStatus.recordDate) &&
                Objects.equals(grnCorr, svStatus.grnCorr) &&
                Objects.equals(recordDateCorr, svStatus.recordDateCorr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, egrul, egrip, sulst, exclDecDate, exclDecNum, publDate, journalNum, grn, recordDate, grnCorr, recordDateCorr);
    }
}
