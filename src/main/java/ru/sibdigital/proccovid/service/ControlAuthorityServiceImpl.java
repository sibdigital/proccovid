package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsControlAuthorityDto;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.model.ClsControlAuthorityParent;
import ru.sibdigital.proccovid.repository.ClsControlAuthorityParentRepo;
import ru.sibdigital.proccovid.repository.ClsControlAuthorityRepo;
import ru.sibdigital.proccovid.repository.specification.ClsControlAuthoritySearchCriteria;
import ru.sibdigital.proccovid.repository.specification.ClsControlAuthoritySpecification;

import java.util.List;

@Service
@Slf4j
public class ControlAuthorityServiceImpl implements ControlAuthorityService{
    @Autowired
    private ClsControlAuthorityRepo clsControlAuthorityRepo;

    @Autowired
    private ClsControlAuthorityParentRepo clsControlAuthorityParentRepo;

    @Override
    public List<ClsControlAuthorityParent> getControlAuthorityParentsList() {
        List<ClsControlAuthorityParent> clsControlAuthorityParents = clsControlAuthorityParentRepo.findAll();
        return clsControlAuthorityParents;
    }

    @Override
    public Page<ClsControlAuthority> getControlAuthoritiesBySearchCriteria(ClsControlAuthoritySearchCriteria searchCriteria, int page, int size) {
        ClsControlAuthoritySpecification specification = new ClsControlAuthoritySpecification();
        specification.setSearchCriteria(searchCriteria);

        Page<ClsControlAuthority> clsControlAuthorityPage = clsControlAuthorityRepo.findAll(specification, PageRequest.of(page, size, Sort.by("name")));
        return clsControlAuthorityPage;
    }

    @Override
    public ClsControlAuthority saveControlAuthority(ClsControlAuthorityDto clsControlAuthorityDto) {
        ClsControlAuthority clsControlAuthority = null;

        if(clsControlAuthorityDto.getId() == null) {
            clsControlAuthority = clsControlAuthority.builder()
                .id(clsControlAuthorityDto.getId())
                .controlAuthorityParent(clsControlAuthorityDto.getControlAuthorityParent())
                .name(clsControlAuthorityDto.getName())
                .shortName(clsControlAuthorityDto.getShortName())
                .build();
        } else {
            clsControlAuthority = clsControlAuthorityRepo.findById(clsControlAuthorityDto.getId()).orElse(null);
            System.out.println(clsControlAuthority.getControlAuthorityParent().getId());
            clsControlAuthority = clsControlAuthority.builder()
                    .id(clsControlAuthorityDto.getId())
                    .controlAuthorityParent(clsControlAuthorityDto.getControlAuthorityParent())
                    .name(clsControlAuthorityDto.getName())
                    .shortName(clsControlAuthorityDto.getShortName())
                    .build();
        }

        clsControlAuthorityRepo.save(clsControlAuthority);

        return clsControlAuthority;
    }

    @Override
    public boolean deleteControlAuthority(Long id) {
        try {
            clsControlAuthorityRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
}
