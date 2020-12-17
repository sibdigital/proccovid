package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.ClsDepartment;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsDepartmentContactDto {
    private Long id;
    private Integer type;
    private String contactValue;
    private String description;
    private ClsDepartment department;
}
