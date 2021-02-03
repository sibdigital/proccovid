package ru.sibdigital.proccovid.model.egr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "opf", schema = "egr")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Opf {
    private Long id;
    private String spr;
    private String code;
    private String fullName;

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "OPF_GEN", sequenceName = "opf_id_seq", allocationSize = 1, schema = "egr")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OPF_GEN")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "spr")
    public String getSpr() {
        return spr;
    }
    public void setSpr(String spr) {
        this.spr = spr;
    }

    @Basic
    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "full_name")
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Opf svOrg = (Opf) o;
        return Objects.equals(id, svOrg.id) &&
                Objects.equals(spr, svOrg.spr) &&
                Objects.equals(code, svOrg.code) &&
                Objects.equals(fullName, svOrg.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, spr, code, fullName);
    }
}

