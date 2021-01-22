package ru.sibdigital.proccovid.model.egr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sulst", schema = "egr")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Sulst {
    private Long id;
    private String code;
    private String name;

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "SULST_GEN", sequenceName = "sulst_id_seq", allocationSize = 1, schema = "egr")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SULST_GEN")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sulst sulst = (Sulst) o;
        return Objects.equals(id, sulst.id) &&
                Objects.equals(code, sulst.code) &&
                Objects.equals(name, sulst.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name);
    }
}
