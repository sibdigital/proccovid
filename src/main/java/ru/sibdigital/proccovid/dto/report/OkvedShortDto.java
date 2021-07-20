package ru.sibdigital.proccovid.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OkvedShortDto {
    private UUID id;
    private String kindCode;
    private String kindName;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKindCode() {
        return kindCode;
    }

    public void setKindCode(String kindCode) {
        this.kindCode = kindCode;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OkvedShortDto that = (OkvedShortDto) o;
        return Objects.equals(id, that.id) && Objects.equals(kindCode, that.kindCode) && Objects.equals(kindName, that.kindName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kindCode, kindName);
    }
}
