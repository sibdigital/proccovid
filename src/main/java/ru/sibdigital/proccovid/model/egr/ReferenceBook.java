package ru.sibdigital.proccovid.model.egr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reference_book", schema = "egr")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ReferenceBook {
    private Long id;
    private String code;
    private String name;
    private Short  type; // СИЛСТ, СЮЛСТ, СПВЗ

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REFERENCE_BOOK_GEN", sequenceName = "reference_book_id_seq", allocationSize = 1, schema = "egr")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REFERENCE_BOOK_GEN")
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

    @Basic
    @Column(name = "type")
    public Short getType() {
        return type;
    }
    public void setType(Short type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceBook sulst = (ReferenceBook) o;
        return Objects.equals(id, sulst.id) &&
                Objects.equals(code, sulst.code) &&
                Objects.equals(name, sulst.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name);
    }
}
