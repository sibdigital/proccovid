package ru.sibdigital.proccovid.dto.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.Okved;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsSubsidyDto {

    private Long id;
    private String name;
    private String shortName;
    private Long departmentId;
    private List<Okved> okveds;
}
