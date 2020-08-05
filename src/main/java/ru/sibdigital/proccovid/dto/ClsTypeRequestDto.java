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
public class ClsTypeRequestDto {

    private Long id;
    private String activityKind;
    private String shortName;
    private Long departmentId;
    private String prescription;
    private String prescriptionLink;
    private String settings;
    private Integer statusRegistration;
    private Timestamp beginRegistration;
    private Timestamp endRegistration;
    private Integer statusVisible;
    private Timestamp beginVisible;
    private Timestamp endVisible;
    private Integer sortWeight;

}
