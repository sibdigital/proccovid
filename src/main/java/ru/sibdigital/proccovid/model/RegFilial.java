package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reg_filial", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TypeDefs({
        @TypeDef(name = "JsonbType", typeClass = Jsonb.class)
})
public class RegFilial {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_egrul_pk")
    @SequenceGenerator(name="seq_reg_filial_pk", sequenceName = "seq_reg_egrul_pk", allocationSize=1)
    private Long id;
    @Basic
    @Column(name = "inn", nullable = true, length = 10)
    private String inn;
    @Basic
    @Column(name = "kpp", nullable = true, length = 9)
    private String kpp;
    @Basic
    @Column(name = "full_name")
    private String fullName;
    @Basic
    @Column(name = "data", nullable = true, columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private String data;
    @ManyToOne
    @JoinColumn(name = "id_egrul", referencedColumnName = "id")
    private RegEgrul egrul;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public RegEgrul getEgrul() {
        return egrul;
    }
    public void setEgrul(RegEgrul egrul) {
        this.egrul = egrul;
    }

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
        RegFilial regFilial = (RegFilial) o;
        return Objects.equals(id, regFilial.id) &&
                Objects.equals(inn, regFilial.inn) &&
                Objects.equals(kpp, regFilial.kpp) &&
                Objects.equals(egrul, regFilial.egrul);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inn, kpp, egrul);
    }
}
