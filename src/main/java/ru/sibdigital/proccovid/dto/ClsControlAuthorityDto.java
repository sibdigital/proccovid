package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.ClsControlAuthorityParent;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsControlAuthorityDto {

    private Long id;
    private ClsControlAuthorityParent controlAuthorityParent;
    private String name;
    private String shortName;
    private Integer weight;
    private Boolean deleted;
}
