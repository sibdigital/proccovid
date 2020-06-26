package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.AdditionalAttributes;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostFormDto {
    private Long organizationId;
    
    private Long departmentId;

    private String organizationName;
    private String  organizationShortName;
    private String  organizationInn;
    private String  organizationOgrn;
    private String  organizationAddressJur;
    private String organizationOkvedAdd;
    private String  organizationOkved;
    private String  organizationEmail;
    private String  organizationPhone;

    private List<FactAddressDto> addressFact;

    private List<PersonDto> persons;

    private Long personOfficeCnt;
    private Long personRemoteCnt;
    private Long personSlrySaveCnt;
    private Long personOfficeFactCnt;

//    private MultipartFile attachmentMultiPart;
    private String attachment;
    private String attachmentFilename;

    private Boolean isAgree;
    private Boolean isProtect;
    private String reqBasis;
    
    
    
    //Checker section
    private String departmentIdStatus = "OK";
    private String organizationNameStatus = "OK";
    private String  organizationShortNameStatus = "OK";
    private String  organizationInnStatus = "OK";
    private String  organizationOgrnStatus = "OK";
    private String  organizationAddressJurStatus = "OK";
    private String organizationOkvedAddStatus = "OK";
    private String  organizationOkvedStatus = "OK";
    private String  organizationEmailStatus = "OK";
    private String  organizationPhoneStatus = "OK";

    private String personOfficeCntStatus = "OK";
    private String personRemoteCntStatus = "OK";
    private String personSlrySaveCntStatus = "OK";
    private String personOfficeFactCntStatus = "OK";
    private String attachmentMultiPartStatus = "OK";
    private String isAgreeStatus = "OK";
    private String isProtectStatus = "OK";
    private String reqBasisStatus = "OK";

    private String addressFactStatus = "OK";
    private String personsStatus = "OK";

    private AdditionalAttributes additionalAttributes;

}
