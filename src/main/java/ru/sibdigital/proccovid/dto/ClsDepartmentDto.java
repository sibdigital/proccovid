package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.ClsDepartmentContact;
import ru.sibdigital.proccovid.model.Okved;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsDepartmentDto {

    private Long id;
    private String name;
    private String fullName;
    private String description;
    private Boolean deleted;
    private List<Okved> okveds;
    private List<ClsDepartmentContactDto> contacts;
    private Boolean okvedsChanged;
    private Boolean contactsChanged;
}
