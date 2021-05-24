package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsMailingListDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.classifier.ClsMailingListOkvedRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsMailingListRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegMailingListFollowerRepo;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class MailingListServiceImpl implements MailingListService{

    @Autowired
    private ClsMailingListRepo clsMailingListRepo;
    @Autowired
    private ClsMailingListOkvedRepo clsMailingListOkvedRepo;
    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;
    @Autowired
    private RegMailingListFollowerRepo regMailingListFollowerRepo;
    @Override
    public ClsMailingList saveClsMailingList(ClsMailingListDto clsMailingListDto) {

        ClsMailingList clsMailingList = ClsMailingList.builder()
                .id(clsMailingListDto.getId())
                .name(clsMailingListDto.getName())
                .description(clsMailingListDto.getDescription())
                .status(clsMailingListDto.getStatus())
                .isUserVisibility(clsMailingListDto.getIsUserVisibility())
                .isForPrincipal(clsMailingListDto.getIsForPrincipal())
                .build();

        clsMailingListRepo.save(clsMailingList);

        List<ClsMailingListOkved> list = clsMailingListOkvedRepo.findClsMailingListOkvedByClsMailingList(clsMailingList);
        clsMailingListOkvedRepo.deleteAll(list);

        List<Okved> listOkveds = clsMailingListDto.getOkveds();
        for (Okved okved : listOkveds) {
            ClsMailingListOkved clsMailingListOkved = new ClsMailingListOkved();
            clsMailingListOkved.setClsMailingList(clsMailingList);
            clsMailingListOkved.setOkved(okved);
            clsMailingListOkvedRepo.save(clsMailingListOkved);
        }

        String inns = clsMailingListDto.getFollowerInns();
        String[] innsArray = Arrays.stream(inns.split(";")).map(s -> s.trim()).toArray(String[]::new);
        final List<ClsOrganization> clsOrganizationByInnArray = clsOrganizationRepo.getClsOrganizationByInnArray(innsArray, false, true);

        final List<RegMailingListFollower> followers = clsOrganizationByInnArray.stream()
                .map(org -> RegMailingListFollower.builder()
                        .mailingList(clsMailingList)
                        .organization(org)
                        .principal(org.getPrincipal())
                        .activationDate(new Timestamp(System.currentTimeMillis()))
                        .build())
                .collect(Collectors.toList());

        regMailingListFollowerRepo.saveAll(followers);

        return clsMailingList;
    }

    @Override
    public List<ClsMailingList> getClsMailingList() {
        return StreamSupport.stream(clsMailingListRepo.findAllByOrderByIdAsc().spliterator(), false)
                .collect(Collectors.toList());
    }
}
