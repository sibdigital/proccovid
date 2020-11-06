package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.repository.OkvedRepo;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OkvedServiceImpl implements OkvedService {

    @Autowired
    private OkvedRepo okvedRepo;

    public List<Okved> getOkveds() {
        return StreamSupport.stream(okvedRepo.findAll(Sort.by(Sort.Direction.ASC, "kindCode")).spliterator(), false)
                .collect(Collectors.toList());
    }
}
