package ru.sibdigital.proccovid.model.fias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "addr_object", schema = "fias")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AddrObject {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "ADDR_OBJECT_GEN", sequenceName = "addr_object_id_seq", allocationSize = 1, schema = "fias")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDR_OBJECT_GEN")
    private Long id;
    private Long objectid;
    private Long objectguid;
    private Long changeid;
    private String name;
    private String typename;
    private Short level;
    private Long opertypeid;
    private Long previd;
    private Long nextid;
    private Timestamp updatedate;
    private Timestamp startdate;
    private Timestamp enddate;
    private Short isactual;
    private Short isactive;
    private Timestamp createdate;
    private Long levelid;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "objectid")
    public Long getObjectid() {
        return objectid;
    }
    public void setObjectid(Long objectid) {
        this.objectid = objectid;
    }

    @Basic
    @Column(name = "objectguid")
    public Long getObjectguid() {
        return objectguid;
    }
    public void setObjectguid(Long objectguid) {
        this.objectguid = objectguid;
    }

    @Basic
    @Column(name = "changeid")
    public Long getChangeid() {
        return changeid;
    }
    public void setChangeid(Long changeid) {
        this.changeid = changeid;
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
    @Column(name = "typename")
    public String getTypename() {
        return typename;
    }
    public void setTypename(String typename) {
        this.typename = typename;
    }

    @Basic
    @Column(name = "level")
    public Short getLevel() {
        return level;
    }
    public void setLevel(Short level) {
        this.level = level;
    }

    @Basic
    @Column(name = "opertypeid")
    public Long getOpertypeid() {
        return opertypeid;
    }
    public void setOpertypeid(Long opertypeid) {
        this.opertypeid = opertypeid;
    }

    @Basic
    @Column(name = "previd")
    public Long getPrevid() {
        return previd;
    }
    public void setPrevid(Long previd) {
        this.previd = previd;
    }

    @Basic
    @Column(name = "nextid")
    public Long getNextid() {
        return nextid;
    }
    public void setNextid(Long nextid) {
        this.nextid = nextid;
    }

    @Basic
    @Column(name = "updatedate")
    public Timestamp getUpdatedate() {
        return updatedate;
    }
    public void setUpdatedate(Timestamp updatedate) {
        this.updatedate = updatedate;
    }

    @Basic
    @Column(name = "startdate")
    public Timestamp getStartdate() {
        return startdate;
    }
    public void setStartdate(Timestamp startdate) {
        this.startdate = startdate;
    }

    @Basic
    @Column(name = "enddate")
    public Timestamp getEnddate() {
        return enddate;
    }
    public void setEnddate(Timestamp enddate) {
        this.enddate = enddate;
    }

    @Basic
    @Column(name = "isactual")
    public Short getIsactual() {
        return isactual;
    }
    public void setIsactual(Short isactual) {
        this.isactual = isactual;
    }

    @Basic
    @Column(name = "isactive")
    public Short getIsactive() {
        return isactive;
    }
    public void setIsactive(Short isactive) {
        this.isactive = isactive;
    }

    @Basic
    @Column(name = "createdate")
    public Timestamp getCreatedate() {
        return createdate;
    }
    public void setCreatedate(Timestamp createdate) {
        this.createdate = createdate;
    }

    @Basic
    @Column(name = "levelid")
    public Long getLevelid() {
        return levelid;
    }
    public void setLevelid(Long levelid) {
        this.levelid = levelid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddrObject that = (AddrObject) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(objectguid, that.objectguid) &&
                Objects.equals(changeid, that.changeid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(typename, that.typename) &&
                Objects.equals(level, that.level) &&
                Objects.equals(opertypeid, that.opertypeid) &&
                Objects.equals(previd, that.previd) &&
                Objects.equals(nextid, that.nextid) &&
                Objects.equals(updatedate, that.updatedate) &&
                Objects.equals(startdate, that.startdate) &&
                Objects.equals(enddate, that.enddate) &&
                Objects.equals(isactual, that.isactual) &&
                Objects.equals(isactive, that.isactive) &&
                Objects.equals(createdate, that.createdate) &&
                Objects.equals(levelid, that.levelid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, objectguid, changeid, name, typename, level, opertypeid, previd, nextid, updatedate, startdate, enddate, isactual, isactive, createdate, levelid);
    }
}

