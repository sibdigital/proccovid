package ru.sibdigital.proccovid.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class ClsMailingListOkved {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "CLS_MAIL_OKVED_GEN", sequenceName = "cls_mailing_list_okved_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLS_MAIL_OKVED_GEN")
    private Integer id;
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}


    @OneToOne
    @JoinColumn(name = "id_mailing", referencedColumnName = "id")
    private ClsMailingList clsMailingList;
    public ClsMailingList getClsMailingList() {return clsMailingList;}
    public void setClsMailingList(ClsMailingList clsMailingList) {this.clsMailingList = clsMailingList;}


    @OneToOne
    @JoinColumn(name = "id_okved", referencedColumnName = "id")
    private Okved okved;
    public Okved getOkved() {return okved;}
    public void setOkved(Okved okved) {this.okved = okved;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClsMailingListOkved clsMailingListOkved = (ClsMailingListOkved) o;
        return Objects.equals(id, clsMailingListOkved.id) &&
                Objects.equals(clsMailingList, clsMailingListOkved.clsMailingList) &&
                Objects.equals(okved, clsMailingListOkved.okved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clsMailingList, okved);
    }
}