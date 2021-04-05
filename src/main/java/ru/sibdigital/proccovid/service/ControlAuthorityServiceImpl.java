package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.ClsControlAuthorityDto;
import ru.sibdigital.proccovid.model.ClsControlAuthority;
import ru.sibdigital.proccovid.repository.ClsControlAuthorityRepo;

@Service
@Slf4j
public class ControlAuthorityServiceImpl implements ControlAuthorityService{
    @Autowired
    private ClsControlAuthorityRepo clsControlAuthorityRepo;

    @Override
    public ClsControlAuthority saveControlAuthority(ClsControlAuthorityDto clsControlAuthorityDto) {
        ClsControlAuthority clsControlAuthority = null;

        if(clsControlAuthorityDto.getId() == null) {
            clsControlAuthority = clsControlAuthority.builder()
                .id(clsControlAuthorityDto.getId())
                .idParent(clsControlAuthorityDto.getIdParent())
                .name(clsControlAuthorityDto.getName())
                .shortName(clsControlAuthorityDto.getShortName())
                .build();
        } else {
            clsControlAuthority = clsControlAuthorityRepo.findById(clsControlAuthorityDto.getId()).orElse(null);
            clsControlAuthority = clsControlAuthority.builder()
                    .id(clsControlAuthorityDto.getId())
                    .idParent(clsControlAuthorityDto.getIdParent())
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
