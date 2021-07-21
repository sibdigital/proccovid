package ru.sibdigital.proccovid.service.subs;

import org.springframework.data.domain.Page;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.dto.subs.TpRequiredSubsidyFileDto;
import ru.sibdigital.proccovid.model.ClsFileType;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.ClsSubsidyRequestStatus;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.proccovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestSubsidySearchCriteria;

import java.util.List;
import java.util.Map;

import ru.sibdigital.proccovid.dto.subs.ClsSubsidyDto;

public interface SubsidyService {
    //для работы с ClsSubsidy, TpRequiredSubsidyFile TpSubsidyFile TpSubsidyOkved  ClsSubsidyRequestStatus
    ClsSubsidy saveSubsidy(ClsSubsidyDto subsidyDto);

    Page<DocRequestSubsidy> getRequestsByCriteria(DocRequestSubsidySearchCriteria criteria, int page, int size);

    List<String> getClsSubsidyRequestStatusShort();

    List<ClsSubsidy> getClsSubsidyShort();

    void saveRequiredSubsidyFile(List<TpRequiredSubsidyFileDto> requiredSubsidyFilesDto, ClsSubsidy clsSubsidy);

    List<ClsFileType> getAllWithoutExists(Long[] ids);
}
