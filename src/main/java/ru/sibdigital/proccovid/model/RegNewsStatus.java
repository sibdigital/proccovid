package ru.sibdigital.proccovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reg_news_status", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegNewsStatus {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_NEWS_STATUS_GEN", sequenceName = "reg_news_status_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_NEWS_STATUS_GEN")
    private Integer id;
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}


    @OneToOne
    @JoinColumn(name = "id_news", referencedColumnName = "id")
    private ClsNews news;
    public ClsNews getNews() {return news;}
    public void setNews(ClsNews news) {this.news = news;}


    @Basic
    @Column(name = "status_review")
    private Long statusReview;
    public Long getStatusReview() {return statusReview;}
    public void setStatusReview(Long statusReview) {this.statusReview = statusReview;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegNewsStatus newsOkved = (RegNewsStatus) o;
        return Objects.equals(id, newsOkved.id) &&
                Objects.equals(news, newsOkved.news) &&
                Objects.equals(statusReview, newsOkved.statusReview);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, news, statusReview);
    }
}
