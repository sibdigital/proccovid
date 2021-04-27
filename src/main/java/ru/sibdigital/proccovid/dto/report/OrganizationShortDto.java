package ru.sibdigital.proccovid.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationShortDto {
    private Long id;
    private String name;
    private String shortName;
    private String inn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationShortDto that = (OrganizationShortDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(shortName, that.shortName) && Objects.equals(inn, that.inn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, shortName, inn);
    }
}
