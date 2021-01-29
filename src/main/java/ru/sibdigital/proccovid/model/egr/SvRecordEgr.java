package ru.sibdigital.proccovid.model.egr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.sibdigital.proccovid.model.RegEgrip;
import ru.sibdigital.proccovid.model.RegEgrul;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "sv_record_egr", schema = "egr")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SvRecordEgr {

    private Long id;
    private RegEgrul egrul;
    private RegEgrip egrip;
    private ReferenceBook spvz;
    private BigInteger recordId;
    private Date recordDate;
    private Boolean isValid;
    private String data;

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "SV_RECORD_EGR_GEN", sequenceName = "sv_record_egr_id_seq", allocationSize = 1, schema = "egr")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SV_RECORD_EGR_GEN")
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
    @JoinColumn(name = "id_spvz", referencedColumnName = "id")
    public ReferenceBook getSpvz() {
        return spvz;
    }
    public void setSpvz(ReferenceBook spvz) {
        this.spvz = spvz;
    }

    @Basic
    @Column(name = "is_valid")
    public Boolean getIsValid(){
        return isValid;
    };
    public void setIsValid(Boolean isValid){
        this.isValid = isValid;
    }

    @Basic
    @Column(name = "record_id")
    public BigInteger getRecordId() {
        return recordId;
    }
    public void setRecordId(BigInteger recordId) {
        this.recordId = recordId;
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
    @Column(name = "data", nullable = true, columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SvRecordEgr that = (SvRecordEgr) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(egrul, that.egrul) &&
                Objects.equals(egrip, that.egrip) &&
                Objects.equals(spvz, that.spvz) &&
                Objects.equals(recordId, that.recordId) &&
                Objects.equals(recordDate, that.recordDate) &&
                Objects.equals(isValid, that.isValid) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, egrul, egrip, spvz, recordId, recordDate, isValid, data);
    }
}
