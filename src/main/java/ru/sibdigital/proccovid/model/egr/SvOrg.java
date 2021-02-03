package ru.sibdigital.proccovid.model.egr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sv_org", schema = "egr")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SvOrg {
    private Long id;
    private Short typeOrg;
    private String code;
    private String name;
    private String adr;

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "SV_ORG_GEN", sequenceName = "sv_org_id_seq", allocationSize = 1, schema = "egr")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SV_ORG_GEN")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "adr")
    public String getAdr() {
        return adr;
    }
    public void setAdr(String adr) {
        this.adr = adr;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SvOrg svOrg = (SvOrg) o;
        return Objects.equals(id, svOrg.id) &&
                Objects.equals(typeOrg, svOrg.typeOrg) &&
                Objects.equals(code, svOrg.code) &&
                Objects.equals(name, svOrg.name) &&
                Objects.equals(adr, svOrg.adr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeOrg, code, name, adr);
    }
}

