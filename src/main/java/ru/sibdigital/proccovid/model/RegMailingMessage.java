package ru.sibdigital.proccovid.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reg_mailing_message", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegMailingMessage {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "REG_MAILING_MESSAGE_SEQ_GEN", sequenceName = "reg_mailing_message_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_MAILING_MESSAGE_SEQ_GEN")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "id_mailing", referencedColumnName = "id")
    private ClsMailingList clsMailingList;
    public ClsMailingList getClsMailingList() {
        return clsMailingList;
    }
    public void setClsMailingList(ClsMailingList clsMailingList) {
        this.clsMailingList = clsMailingList;
    }

    @Basic
    @Column(name = "message")
    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "subject")
    private String subject;
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Basic
    @Column(name = "sending_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendingTime;
    public Date getSendingTime() {
        return sendingTime;
    }
    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    @Basic
    @Column(name = "status")
    private Short status;
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
        RegMailingMessage that = (RegMailingMessage) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(clsMailingList, that.clsMailingList) &&
                Objects.equals(message, that.message) &&
                Objects.equals(sendingTime, that.sendingTime) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clsMailingList, message, sendingTime, status);
    }
}