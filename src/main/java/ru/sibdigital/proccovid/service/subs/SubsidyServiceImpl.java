package ru.sibdigital.proccovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.DocRequestPrs;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.ClsSubsidyRequestStatus;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSpecification;
import ru.sibdigital.proccovid.repository.specification.DocRequestSubsidySearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestSubsidySpecification;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRepo;
import ru.sibdigital.proccovid.repository.subs.ClsSubsidyRequestStatusRepo;
import ru.sibdigital.proccovid.repository.subs.DocRequestSubsidyRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Service
public class SubsidyServiceImpl implements SubsidyService {
    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    TpSubsidyOkvedRepo tpSubsidyOkvedRepo;

    @Autowired
    ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Autowired
    ClsSubsidyRequestStatusRepo clsSubsidyRequestStatusRepo;


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

    @Override
    public Page<DocRequestSubsidy> getRequestsByCriteria(DocRequestSubsidySearchCriteria criteria, int page, int size) {
        DocRequestSubsidySpecification specification = new DocRequestSubsidySpecification();
        specification.setSearchCriteria(criteria);
        Page<DocRequestSubsidy> docRequestSubsidyPage = docRequestSubsidyRepo.findAll(specification, PageRequest.of(page, size, Sort.by("timeSend")));
        return docRequestSubsidyPage;
    }

    @Override
    public List<String> getClsSubsidyRequestStatusShort() {
        List<String> res = clsSubsidyRequestStatusRepo.getClsSubsidyRequestStatusShort();
        return res;
    }

    @Override
    public List<ClsSubsidy> getClsSubsidyShort() {
        return StreamSupport.stream(clsSubsidyRepo.findAll(Sort.by(Sort.Direction.DESC, "id")).spliterator(), false)
                .collect(Collectors.toList());
    }
}
