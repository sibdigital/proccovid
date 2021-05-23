package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cls_mailing_list", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsMailingList {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_MAIL_GEN", sequenceName = "cls_mailing_list_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_MAIL_GEN")
    private Long id;
    private String name;
    private String description;
    private Short status;
    private Boolean isUserVisibility;
    private Boolean isForPrincipal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false)
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

    @Basic
    @Column(name = "status", nullable = false)
    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsMailingList that = (ClsMailingList) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(isUserVisibility, that.isUserVisibility) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Basic
    @Column(name = "is_user_visibility", nullable = false)
    public Boolean getUserVisibility() {
        return isUserVisibility;
    }

    public void setUserVisibility(Boolean userVisibility) {
        isUserVisibility = userVisibility;
    }

    @Basic
    @Column(name = "is_for_principal", nullable = false)
    public Boolean getForPrincipal() {
        return isForPrincipal;
    }

    public void setForPrincipal(Boolean forPrincipal) {
        isForPrincipal = forPrincipal;
    }
}