package ru.sibdigital.proccovid.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "cls_user", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClsUser implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "DEP_USR_SEQ_GEN", sequenceName = "dep_user_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEP_USR_SEQ_GEN")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ClsDepartment idDepartment;
    private String lastname;
    private String firstname;
    private String patronymic;
    private String login;
    private String password;
    private Boolean isAdmin;
    private String email;

    @OneToOne
    @JoinColumn(name = "id_district", referencedColumnName = "id")
    private ClsDistrict district;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClsDepartment getIdDepartment() {
        return idDepartment;
    }

    public void setIdDepartment(ClsDepartment idDepartment) {
        this.idDepartment = idDepartment;
    }

    @Basic
    @Column(name = "lastname", nullable = false, length = 100)
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    @Basic
    @Column(name = "firstname", nullable = false, length = 100)
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    @Basic
    @Column(name = "patronymic", nullable = true, length = 100)
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Basic
    @Column(name = "login", nullable = false, length = 100)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 100)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "is_admin")
    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ClsDistrict getDistrict() {
        return district;
    }

    public void setDistrict(ClsDistrict district) {
        this.district = district;
    }

    public String getFullName() {
        String fullName = "";
        fullName += this.lastname != null ? this.lastname : "";
        fullName += this.firstname != null ? ' ' + this.firstname : "";
        fullName += this.patronymic != null ? ' ' + this.patronymic : "";
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsUser clsUser = (ClsUser) o;
        return Objects.equals(id, clsUser.id) &&
                Objects.equals(lastname, clsUser.lastname) &&
                Objects.equals(firstname, clsUser.firstname) &&
                Objects.equals(patronymic, clsUser.patronymic) &&
                Objects.equals(login, clsUser.login) &&
                Objects.equals(password, clsUser.password);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(id, lastname, firstname, patronymic, login, password);
        return Objects.hash(login, password);
    }
}
