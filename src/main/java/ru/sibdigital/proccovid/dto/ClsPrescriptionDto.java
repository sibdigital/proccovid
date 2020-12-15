package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.AdditionalFields;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsPrescriptionDto {

    private Long id;
    private String name;
    private String description;
    private Long typeRequestId;
    private List<RegPrescriptionTextDto> prescriptionTexts;

    private AdditionalFields additionalFields;
}
