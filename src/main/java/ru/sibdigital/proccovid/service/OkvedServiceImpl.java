package ru.sibdigital.proccovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.OkvedDto;
import ru.sibdigital.proccovid.model.Okved;
import ru.sibdigital.proccovid.model.Statuses;
import ru.sibdigital.proccovid.model.Types;
import ru.sibdigital.proccovid.repository.OkvedRepo;
import ru.sibdigital.proccovid.utils.ExcelParser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    public Okved changeOkved(OkvedDto okvedDto) {
        Okved okved = okvedRepo.findOkvedById(okvedDto.getId());
        okved.setKindName(okvedDto.getKindName().trim());
        okved.setKindCode(okvedDto.getKindCode().trim());
        okved.setDescription(okvedDto.getDescription());
        okved.setStatus(okvedDto.getStatus());
        okved.setPath(okvedDto.getVersion() + "." + okvedDto.getKindCode().trim());
        okvedRepo.save(okved);
        okvedRepo.setTsVectorsById(okved.getId());

        return okved;
    }

    public Okved createOkved(OkvedDto okvedDto) {
        Okved okved = new Okved();
        okved.setKindName(okvedDto.getKindName().trim());
        okved.setKindCode(okvedDto.getKindCode().trim());
        okved.setDescription(okvedDto.getDescription());
        okved.setStatus(okvedDto.getStatus());
        okved.setVersion(okvedDto.getVersion());
        okved.setPath(okvedDto.getVersion() + "." + okvedDto.getKindCode().trim());
        okvedRepo.save(okved);
        okvedRepo.setTsVectorsById(okved.getId());

        return okved;
    }

    public String deleteOkved(UUID id) {
        Okved okved = okvedRepo.findOkvedById(id);
        okvedRepo.delete(okved);
        return "";
    }


    @Transactional
    public String processFile(MultipartFile multipartFile, String version) {
        try {
            // парсинг файла
            List<OkvedDto> dtos = ExcelParser.parseFile(multipartFile.getInputStream());

            // сброс статуса на 0
//            okvedRepo.resetStatus();

            // импорт данных в базу
            List<Okved> models = new ArrayList<>();
            for (OkvedDto dto: dtos) {
                Okved newOkved = convertToOkved(dto);
                newOkved.setVersion(version);
                newOkved.setPath(version + "." + newOkved.getKindCode());
                Okved oldOkved = this.okvedRepo.findByPath(newOkved.getPath());
                if (oldOkved == null) {
                    models.add(newOkved);
                }
//                else {
//                    if (!oldOkved.equals(newOkved)) {
//                        newOkved.setId(oldOkved.getId());
//                        models.add(newOkved);
//                    }
//                }
            }
            okvedRepo.saveAll(models);

            // создание tsvectors
            okvedRepo.setTsVectors();
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка! Не удалось обработать файл.";
        }
        return "Файл обработан.";
    }

    private Okved convertToOkved(OkvedDto okvedDto) {
        Okved okved = new Okved();

        String[] parts = okvedDto.getCode().split("\\.");
        switch (parts.length) {
            case 1:
                okved.setClassCode(parts[0]);
                okved.setTypeCode(Types.CLASS.getValue());
                break;
            case 2:
                okved.setClassCode(parts[0]);
                okved.setSubclassCode(String.valueOf(parts[1].charAt(0)));
                okved.setTypeCode(Types.SUBCLASS.getValue());
                if (parts[1].length() == 2) {
                    okved.setGroupCode(parts[1]);
                    okved.setTypeCode(Types.GROUP.getValue());
                }
                break;
            case 3:
                okved.setClassCode(parts[0]);
                okved.setSubclassCode(String.valueOf(parts[1].charAt(0)));
                okved.setGroupCode(parts[1]);
                okved.setSubgroupCode(String.valueOf(parts[2].charAt(0)));
                okved.setTypeCode(Types.SUBGROUP.getValue());
                if (parts[2].length() == 2) {
                    okved.setTypeCode(Types.KIND.getValue());
                }
                break;
            default:
                break;
        }

        okved.setKindCode(okvedDto.getCode());
        okved.setPath(okvedDto.getCode());
        okved.setStatus(Statuses.ACTIVE.getValue());
        okved.setKindName(okvedDto.getName());
        okved.setDescription(okvedDto.getDescription());
        return okved;
    }

    @Override
    public List<Okved> findOkvedsBySearchText(String text) {
        return okvedRepo.findBySearchText(text);
    }

    public Okved findOkvedById(UUID id) {
        return okvedRepo.findOkvedById(id);
    }

    public Okved findOkvedByPathCode(String path) {
        return okvedRepo.findByPath(path);
    }
}
