package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsDepartmentContactDto {
    private Long id;
    private Long departmentId;
    private Integer type;
    private String contactValue;
    private String description;
}
