package ru.sibdigital.proccovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.subs.ClsSubsidyDto;
import ru.sibdigital.proccovid.dto.subs.TpRequiredSubsidyFileDto;
import ru.sibdigital.proccovid.model.ClsDepartment;
import ru.sibdigital.proccovid.model.ClsFileType;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.subs.ClsSubsidy;
import ru.sibdigital.proccovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.proccovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.proccovid.model.subs.TpSubsidyOkved;
import ru.sibdigital.proccovid.repository.ClsFileTypeRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsDepartmentRepo;
import ru.sibdigital.proccovid.repository.specification.DocRequestSubsidySearchCriteria;
import ru.sibdigital.proccovid.repository.specification.DocRequestSubsidySpecification;
import ru.sibdigital.proccovid.repository.subs.*;

import java.sql.Timestamp;
import java.util.List;
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

    @Autowired
    TpRequiredSubsidyFileRepo tpRequiredSubsidyFileRepo;

    @Autowired
    ClsFileTypeRepo clsFileTypeRepo;


    @Override
    public ClsSubsidy saveSubsidy(ClsSubsidyDto subsidyDto) {
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
        return subsidy;
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

    @Override
    public void saveRequiredSubsidyFile(List<TpRequiredSubsidyFileDto> requiredSubsidyFilesDto, ClsSubsidy clsSubsidy) {
        List<TpRequiredSubsidyFileDto> filteredReqFileDtos = requiredSubsidyFilesDto.stream()
                .filter(fileDto -> fileDto.getClsFileType() != null)
                .collect(Collectors.toList());

        filteredReqFileDtos.
                forEach(reqFileDto -> {
                    TpRequiredSubsidyFile fileWithTypeId = tpRequiredSubsidyFileRepo.findByIdSubsidyAndIdFileType(clsSubsidy.getId(), reqFileDto.getClsFileType().getId());
                    TpRequiredSubsidyFile requiredSubsidyFile;
                    if (fileWithTypeId == null) {
                        requiredSubsidyFile = TpRequiredSubsidyFile.builder()
                                .isDeleted(false)
                                .isRequired(reqFileDto.getRequired())
                                .timeCreate(new Timestamp(System.currentTimeMillis()))
                                .comment(reqFileDto.getComment())
                                .weight(reqFileDto.getWeight())
                                .clsFileType(reqFileDto.getClsFileType())
                                .clsSubsidy(clsSubsidy)
                                .build();
                    } else {
                        requiredSubsidyFile = TpRequiredSubsidyFile.builder()
                                .id(fileWithTypeId.getId())
                                .isDeleted(fileWithTypeId.getDeleted())
                                .isRequired(reqFileDto.getRequired())
                                .timeCreate(fileWithTypeId.getTimeCreate())
                                .comment(reqFileDto.getComment())
                                .weight(reqFileDto.getWeight())
                                .clsFileType(reqFileDto.getClsFileType())
                                .clsSubsidy(fileWithTypeId.getClsSubsidy())
                                .build();
                    }
                    tpRequiredSubsidyFileRepo.save(requiredSubsidyFile);
                });

        setDeletedReqFiles(clsSubsidy, filteredReqFileDtos);
    }

    public void setDeletedReqFiles(ClsSubsidy subsidy, List<TpRequiredSubsidyFileDto> filteredReqSubFilesDto) {
        List<TpRequiredSubsidyFile> allReqFiles = tpRequiredSubsidyFileRepo.findAllByIdSubsidy(subsidy.getId());
        // set is deleted if not exists
        allReqFiles.stream()
                .filter(e -> !filteredReqSubFilesDto.stream()
                        .map(b -> b.getClsFileType().getId())
                        .collect(Collectors.toList())
                        .contains(e.getClsFileType().getId())
                ).collect(Collectors.toList())
                .forEach(c -> {
                    c.setDeleted(true);
                    tpRequiredSubsidyFileRepo.save(c);
                });
    }

    @Override
    public List<ClsFileType> getAllWithoutExists(Long[] ids) {
        return clsFileTypeRepo.getAllWithoutExists(ids);
    }

}
