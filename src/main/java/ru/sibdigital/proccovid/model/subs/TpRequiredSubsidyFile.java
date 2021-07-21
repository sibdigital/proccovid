package ru.sibdigital.proccovid.model.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.ClsFileType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tp_required_subsidy_file", schema = "subs")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TpRequiredSubsidyFile {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "tp_required_subsidy_file_id_seq", sequenceName = "tp_required_subsidy_file_id_seq",
            allocationSize = 1, schema = "subs"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tp_required_subsidy_file_id_seq")
    private Long id;
    private Boolean isDeleted;
    private Boolean isRequired;
    private Timestamp timeCreate;
    private String comment;
    private int weight;

    @OneToOne
    @JoinColumn(name = "id_file_type", referencedColumnName = "id")
    private ClsFileType clsFileType;

    @OneToOne
    @JoinColumn(name = "id_subsidy", referencedColumnName = "id")
    private ClsSubsidy clsSubsidy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "is_deleted")
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Basic
    @Column(name = "is_required")
    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    @Basic
    @Column(name = "time_create")
    public Timestamp getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Timestamp timeCreate) {
        this.timeCreate = timeCreate;
    }

    @Basic
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Basic
    @Column(name = "weight")
    public int getWeight() { return weight; }

    public void setWeight(int weight) { this.weight = weight; }

    public ClsFileType getClsFileType() { return clsFileType; }

    public void setClsFileType(ClsFileType clsFileType) { this.clsFileType = clsFileType; }

    public ClsSubsidy getClsSubsidy() { return clsSubsidy; }

    public void setClsSubsidy(ClsSubsidy clsSubsidy) { this.clsSubsidy = clsSubsidy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TpRequiredSubsidyFile that = (TpRequiredSubsidyFile) o;
        return id == that.id && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(isRequired, that.isRequired) && Objects.equals(timeCreate, that.timeCreate) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDeleted, isRequired, timeCreate, comment);
    }
}
