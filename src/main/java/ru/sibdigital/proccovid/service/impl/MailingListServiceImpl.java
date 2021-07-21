package ru.sibdigital.proccovid.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsMailingListDto;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsMailingListOkvedRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsMailingListRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegMailingListFollowerRepo;
import ru.sibdigital.proccovid.service.MailingListService;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class MailingListServiceImpl implements MailingListService {

    @Autowired
    private ClsMailingListRepo clsMailingListRepo;
    @Autowired
    private ClsMailingListOkvedRepo clsMailingListOkvedRepo;
    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;
    @Autowired
    private RegMailingListFollowerRepo regMailingListFollowerRepo;
    @Autowired
    private OkvedRepo okvedRepo;
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

        if (clsMailingList.getUserVisibility() == false) {
            clearFollowers(clsMailingList);
            addFollowersOnInn(clsMailingListDto, clsMailingList);
            addFollowersOnOkved(clsMailingListDto, clsMailingList, listOkveds);
        }

        return clsMailingList;
    }

    private void clearFollowers(ClsMailingList clsMailingList){
        List<RegMailingListFollower> oldFollowers = regMailingListFollowerRepo.findAllByMailingList(clsMailingList);
        regMailingListFollowerRepo.deleteAll(oldFollowers);
    }

    private List<RegMailingListFollower> addFollowersOnInn(ClsMailingListDto clsMailingListDto, ClsMailingList clsMailingList){
        String inns = clsMailingListDto.getFollowerInns();
        String[] innsArray = Arrays.stream(inns.split(";")).map(s -> s.trim()).toArray(String[]::new);
        final List<ClsOrganization> clsOrganizationByInnArray = clsOrganizationRepo.getClsOrganizationByInnArray(innsArray, false, true);

        final List<RegMailingListFollower> followers = toFollowers(clsMailingList, clsOrganizationByInnArray);
        regMailingListFollowerRepo.saveAll(followers);

        return followers;
    }

    private List<RegMailingListFollower> addFollowersOnOkved(ClsMailingListDto clsMailingListDto, ClsMailingList clsMailingList,
                                                             List<Okved> listOkveds){
//        final List<Okved> okvedsAndChildren = listOkveds.stream()
//                .flatMap(o -> okvedRepo.getChildrenOkvedsByPath(o.getPath()).stream())
//                .distinct()
//                .collect(Collectors.toList());
//        final UUID[] uuids = okvedsAndChildren.stream().map(o -> o.getId()).collect(Collectors.toList()).toArray(UUID[]::new);
        final UUID[] uuids = listOkveds.stream().map(ctr -> ctr.getId()).collect(Collectors.toList()).toArray(UUID[]::new);

        final List<ClsOrganization> clsOrganizationByUuidArray = clsOrganizationRepo
                .getClsOrganizationByOkvedArray(uuids, false, true)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        final List<RegMailingListFollower> followers = toFollowers(clsMailingList, clsOrganizationByUuidArray);
        regMailingListFollowerRepo.saveAll(followers);

        return followers;
    }

    private List<RegMailingListFollower> toFollowers(ClsMailingList clsMailingList, List<ClsOrganization> clsOrganizations){
        return  clsOrganizations.stream()
                .map(org -> RegMailingListFollower.builder()
                        .mailingList(clsMailingList)
                        .organization(org)
                        .principal(org.getPrincipal())
                        .activationDate(new Timestamp(System.currentTimeMillis()))
                        .build())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<ClsMailingList> getClsMailingList() {
        return StreamSupport.stream(clsMailingListRepo.findAllByOrderByIdAsc().spliterator(), false)
                .collect(Collectors.toList());
    }
}
