package ru.sibdigital.proccovid.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cls_template", schema = "public")
public class ClsTemplate {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "CLS_TEMPLATE_SEQ_GEN", sequenceName = "cls_template_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_TEMPLATE_SEQ_GEN")
    private Long id;
    private String key;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Basic
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsTemplate that = (ClsTemplate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, value);
    }
}
