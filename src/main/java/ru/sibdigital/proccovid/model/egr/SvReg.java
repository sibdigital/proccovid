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
@Table(name = "sv_reg", schema = "egr")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SvReg {

    private Long id;
    private RegEgrul egrul;
    private RegEgrip egrip;
    private Short typeOrg;
    private String regNum;
    private Date regDate;
    private SvOrg svOrg;
    private String grn;
    private Date recordDate;
    private String grnCorr;
    private Date recordDateCorr;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SV_REG_SEQ_GEN", sequenceName = "sv_reg_id_seq", allocationSize = 1, schema = "egr")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SV_REG_SEQ_GEN")
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

    @Basic
    @Column(name = "type_org", nullable = false)
    public Short getTypeOrg() {
        return typeOrg;
    }
    public void setTypeOrg(Short typeOrg) {
        this.typeOrg = typeOrg;
    }

    @Basic
    @Column(name = "reg_num", nullable = false)
    public String getRegNum() {
        return regNum;
    }
    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    @Basic
    @Column(name = "reg_date", nullable = false)
    public Date getRegDate() {
        return regDate;
    }
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    @ManyToOne
    @JoinColumn(name = "id_sv_org", referencedColumnName = "id")
    public SvOrg getSvOrg() {
        return svOrg;
    }
    public void setSvOrg(SvOrg svOrg) {
        this.svOrg = svOrg;
    }

    @Basic
    @Column(name = "grn")
    public String getGrn() {
        return grn;
    }
    public void setGrn(String grn) {
        this.grn = grn;
    }

    @Basic
    @Column(name = "record_date")
    public Date getRecordDate() {
        return recordDate;
    }
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    @Basic
    @Column(name = "grn_corr")
    public String getGrnCorr() {
        return grnCorr;
    }
    public void setGrnCorr(String grnCorr) {
        this.grnCorr = grnCorr;
    }

    @Basic
    @Column(name = "record_date_corr")
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
        SvReg svReg = (SvReg) o;
        return Objects.equals(id, svReg.id) &&
                Objects.equals(egrul, svReg.egrul) &&
                Objects.equals(egrip, svReg.egrip) &&
                Objects.equals(typeOrg, svReg.typeOrg) &&
                Objects.equals(regNum, svReg.regNum) &&
                Objects.equals(regDate, svReg.regDate) &&
                Objects.equals(svOrg, svReg.svOrg) &&
                Objects.equals(grn, svReg.grn) &&
                Objects.equals(recordDate, svReg.recordDate) &&
                Objects.equals(grnCorr, svReg.grnCorr) &&
                Objects.equals(recordDateCorr, svReg.recordDateCorr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, egrul, egrip, typeOrg, regNum, regDate, svOrg, grn, recordDate, grnCorr, recordDateCorr);
    }
}
