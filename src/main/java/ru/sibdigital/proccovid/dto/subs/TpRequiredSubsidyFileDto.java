package ru.sibdigital.proccovid.dto.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.ClsFileType;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;

import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TpRequiredSubsidyFileDto {
    private Long id;
    private Boolean isDeleted;
    private Boolean required;
    private Timestamp timeCreate;
    private String comment;
    private int weight;
    private ClsFileType clsFileType;
    private ClsSubsidy clsSubsidy;
}
