package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.AdditionalFields;

import java.sql.Timestamp;
import java.util.List;

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
    private String consent;

    private Long restrictionTypeIds;

    private AdditionalFields additionalFields;

    private List<RegTypeRequestPrescriptionDto> regTypeRequestPrescriptions;

}
