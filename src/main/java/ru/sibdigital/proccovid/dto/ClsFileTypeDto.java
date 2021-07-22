package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsFileTypeDto {
    private Long id;
    private String name;
    private String shortName;
    private Boolean isDeleted;
    private Timestamp timeCreate;
    private String code;
}
