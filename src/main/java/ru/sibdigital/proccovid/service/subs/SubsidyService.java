package ru.sibdigital.proccovid.service.subs;

import ru.sibdigital.proccovid.dto.subs.ClsSubsidyDto;

public interface SubsidyService {
    //для работы с ClsSubsidy, TpRequiredSubsidyFile TpSubsidyFile TpSubsidyOkved  ClsSubsidyRequestStatus
    void saveSubsidy(ClsSubsidyDto subsidyDto);
}
