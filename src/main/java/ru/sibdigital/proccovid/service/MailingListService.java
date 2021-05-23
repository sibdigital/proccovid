package ru.sibdigital.proccovid.service;

import ru.sibdigital.proccovid.dto.ClsMailingListDto;
import ru.sibdigital.proccovid.model.ClsMailingList;

import java.util.List;

public interface MailingListService {
    List<ClsMailingList> getClsMailingList();
    public ClsMailingList saveClsMailingList(ClsMailingListDto clsMailingListDto);

}
