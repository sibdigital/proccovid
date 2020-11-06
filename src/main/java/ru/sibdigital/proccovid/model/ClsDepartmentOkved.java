package ru.sibdigital.proccovid.model;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class ClsDepartmentOkved {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}


    @OneToOne
    @JoinColumn(name = "id_department", referencedColumnName = "id")
    private ClsDepartment department;
    public ClsDepartment getDepartment() {return department;}
    public void setDepartment(ClsDepartment department) {this.department = department;}


    @OneToOne
    @JoinColumn(name = "id_okved", referencedColumnName = "id")
    private Okved okved;
    public Okved getOkved() {return okved;}
    public void setOkved(Okved okved) {this.okved = okved;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsDepartmentOkved departmentOkved = (ClsDepartmentOkved) o;
        return Objects.equals(id, departmentOkved.id) &&
                Objects.equals(department, departmentOkved.department) &&
                Objects.equals(okved, departmentOkved.okved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, department, okved);
    }
}
