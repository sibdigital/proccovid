package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cls_type_violation", schema = "public")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsTypeViolation {

    @Id
    @SequenceGenerator(name = "CLS_TYPE_VIOLATION_SEQ_GEN", sequenceName = "cls_type_violation_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_TYPE_VIOLATION_SEQ_GEN")
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsTypeViolation that = (ClsTypeViolation) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
