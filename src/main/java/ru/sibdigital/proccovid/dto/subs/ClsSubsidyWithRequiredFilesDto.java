package ru.sibdigital.proccovid.dto.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsSubsidyWithRequiredFilesDto {
    ClsSubsidyDto clsSubsidy;
    TpRequiredSubsidyFileDto[] tpRequiredSubsidyFiles;

}
