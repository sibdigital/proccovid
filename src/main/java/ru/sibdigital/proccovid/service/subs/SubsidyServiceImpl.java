package ru.sibdigital.proccovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.subs.ClsSubsidyDto;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.TpSubsidyOkved;
import ru.sibdigital.proccovid.repository.classifier.ClsDepartmentRepo;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRepo;
import ru.sibdigital.proccovid.repository.subs.TpSubsidyOkvedRepo;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class SubsidyServiceImpl implements SubsidyService {
    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    TpSubsidyOkvedRepo tpSubsidyOkvedRepo;

    @Autowired
    ClsDepartmentRepo clsDepartmentRepo;

    @Override
    public void saveSubsidy(ClsSubsidyDto subsidyDto) {
        ClsSubsidy subsidy;
        ClsDepartment department = null;
        if (subsidyDto.getDepartmentId() != null) {
            department = clsDepartmentRepo.findById(subsidyDto.getDepartmentId()).orElse(null);
        }
        if (subsidyDto.getId() != null) {
            subsidy = clsSubsidyRepo.findById(subsidyDto.getId()).orElse(null);
            subsidy.setName(subsidyDto.getName());
            subsidy.setShortName(subsidyDto.getShortName());
            subsidy.setDepartment(department);
        } else {
            subsidy = ClsSubsidy.builder()
                        .name(subsidyDto.getName())
                        .shortName(subsidyDto.getShortName())
                        .isDeleted(false)
                        .department(department)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .build();
        }
        clsSubsidyRepo.save(subsidy);

        List<Okved> newOkvedList = subsidyDto.getOkveds();

        List<TpSubsidyOkved> oldList = tpSubsidyOkvedRepo.findAllBySubsidyAndIsDeleted(subsidy, false);
        List<TpSubsidyOkved> listToDelete = oldList.stream()
                                            .filter(ctr -> !newOkvedList.contains(ctr.getOkved()))
                                            .collect(Collectors.toList());
        listToDelete.forEach(ctr -> ctr.setDeleted(true));
        tpSubsidyOkvedRepo.saveAll(listToDelete);

        List<Okved> oldOkvedList = oldList.stream().map(ctr -> ctr.getOkved()).collect(Collectors.toList());
        List<TpSubsidyOkved> list = newOkvedList.stream()
                                    .filter(ctr -> !oldOkvedList.contains(ctr))
                                    .map(ctr -> TpSubsidyOkved.builder()
                                                    .subsidy(subsidy)
                                                    .okved(ctr)
                                                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                                                    .isDeleted(false)
                                                    .build())
                                    .collect(Collectors.toList());
        tpSubsidyOkvedRepo.saveAll(list);
    }
}
