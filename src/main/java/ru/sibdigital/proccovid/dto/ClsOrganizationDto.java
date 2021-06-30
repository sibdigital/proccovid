package ru.sibdigital.proccovid.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsOrganizationDto {
    private Long id;
    private String name;
    private String shortName;
    private String inn;
    private String ogrn;
    private String kpp;
    private String addressJur;
    private String okvedAdd;
    private String okved;
    private String email;
    private String phone;
    private Integer statusImport;
    private Timestamp timeImport;
    private Integer idTypeRequest;
    private Integer idTypeOrganization;
    private Integer typeTaxReporting;
    private Boolean deleted;
    private Timestamp timeCreate;
    private Boolean activated;
    private Boolean consentDataProcessing;
    private Boolean isActualized;
    private Timestamp timeActualization;
    private Long idPrincipal;
}
