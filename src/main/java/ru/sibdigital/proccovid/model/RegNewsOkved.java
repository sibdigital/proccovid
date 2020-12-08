package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_news_okved", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegNewsOkved {


    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_NEWS_OKVED_GEN", sequenceName = "reg_news_okved_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_NEWS_OKVED_GEN")
    private Integer id;
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}


    @OneToOne
    @JoinColumn(name = "id_news", referencedColumnName = "id")
    private ClsNews news;
    public ClsNews getNews() {return news;}
    public void setNews(ClsNews news) {this.news = news;}


    @OneToOne
    @JoinColumn(name = "id_okved", referencedColumnName = "id")
    private Okved okved;
    public Okved getOkved() {return okved;}
    public void setOkved(Okved okved) {this.okved = okved;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegNewsOkved newsOkved = (RegNewsOkved) o;
        return Objects.equals(id, newsOkved.id) &&
                Objects.equals(news, newsOkved.news) &&
                Objects.equals(okved, newsOkved.okved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, news, okved);
    }
}
