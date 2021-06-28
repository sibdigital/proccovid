package ru.sibdigital.proccovid.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.proccovid.dto.CheckedReviewStatusDto;
import ru.sibdigital.proccovid.dto.ClsNewsDto;
import ru.sibdigital.proccovid.dto.KeyValue;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.classifier.ClsNewsRepo;
import ru.sibdigital.proccovid.repository.classifier.ClsOrganizationRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegNewsFileRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegNewsOkvedRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegNewsOrganizationRepo;
import ru.sibdigital.proccovid.repository.regisrty.RegNewsStatusRepo;
import ru.sibdigital.proccovid.service.NewsService;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class NewsServiceImpl implements NewsService {

    @Value("${upload.path}/news")
    String uploadingDir;

    @Autowired
    private ClsNewsRepo clsNewsRepo;

    @Autowired
    private RegNewsOrganizationRepo regNewsOrganizationRepo;

    @Autowired
    private RegNewsStatusRepo regNewsStatusRepo;

    @Autowired
    private RegNewsOkvedRepo regNewsOkvedRepo;

    @Autowired
    private RegNewsFileRepo regNewsFileRepo;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;


    public ClsNews saveNews(ClsNewsDto clsNewsDto) {
        ClsNews clsNews = null;
        try {
            Date startTime = new Date(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(clsNewsDto.getStartTime()).getTime());
            Date endTime = new Date(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(clsNewsDto.getEndTime()).getTime());

            if (clsNewsDto.getId() != null) {
                clsNews = clsNewsRepo.findById(clsNewsDto.getId()).orElse(null);
            }

            if (Objects.nonNull(clsNews)) {
                clsNews.setHeading(clsNewsDto.getHeading());
                clsNews.setMessage(clsNewsDto.getMessage());
                clsNews.setStartTime(startTime);
                clsNews.setEndTime(endTime);
            }
            else {
                clsNews = ClsNews.builder()
                        .id(clsNewsDto.getId())
                        .heading(clsNewsDto.getHeading())
                        .message(clsNewsDto.getMessage())
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

                clsNewsRepo.save(clsNews);
                clsNews.setHashId("" + clsNews.getId() + encode(System.currentTimeMillis() - Long.parseLong("1577808000000")));
            }

            clsNewsRepo.save(clsNews);

            saveRegNewsOkved(clsNews, clsNewsDto);
            saveRegNewsOrganization(clsNews, clsNewsDto);
            saveRegNewsStatus(clsNews, clsNewsDto);

        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

        return clsNews;
    }

    public void saveRegNewsOkved(ClsNews clsNews, ClsNewsDto clsNewsDto){
        List<RegNewsOkved> list = regNewsOkvedRepo.findClsNewsOkvedByNews(clsNews);
        regNewsOkvedRepo.deleteAll(list);

        List<Okved> listOkveds = clsNewsDto.getOkveds();
        for (Okved okved : listOkveds) {
            RegNewsOkved rno = RegNewsOkved.builder()
                    .news(clsNews)
                    .okved(okved)
                    .build();
            regNewsOkvedRepo.save(rno);
        }
    }

    public void saveRegNewsOrganization(ClsNews clsNews, ClsNewsDto clsNewsDto){
        List<RegNewsOrganization> list1 = regNewsOrganizationRepo.findRegNewsOrganizationByNews(clsNews);
        regNewsOrganizationRepo.deleteAll(list1);

        List<KeyValue> listInn = clsNewsDto.getInnList();
        List<RegNewsOrganization> regNewsOrganizationList = new ArrayList<>();
        for (KeyValue inn : listInn) {
            List<ClsOrganization> organizationList = clsOrganizationRepo.findAllByInn(inn.getValue());
            for (ClsOrganization organization : organizationList) {
                RegNewsOrganization rnorg =  RegNewsOrganization.builder()
                        .news(clsNews)
                        .organization(organization)
                        .build();
                regNewsOrganizationList.add(rnorg);
            }
        }
        if (regNewsOrganizationList != null) {
            regNewsOrganizationRepo.saveAll(regNewsOrganizationList);
        }
    }

    public void saveRegNewsStatus(ClsNews clsNews, ClsNewsDto clsNewsDto){
        List<RegNewsStatus> list3 = regNewsStatusRepo.findRegNewsStatusByNews(clsNews);
        regNewsStatusRepo.deleteAll(list3);

        List<CheckedReviewStatusDto> crsList = clsNewsDto.getStatuses();
        for (CheckedReviewStatusDto status : crsList){
            if (status.getChecked() == 1) {
                RegNewsStatus rns = RegNewsStatus.builder()
                        .news(clsNews)
                        .statusReview(status.getReviewStatus())
                        .build();
                regNewsStatusRepo.save(rns);
            }
        }
    }

    private static String encode(Long num) {
        String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int    BASE     = ALPHABET.length();

        StringBuilder sb = new StringBuilder();
        while ( num > 0 ) {
            sb.append(ALPHABET.charAt((int) (num % BASE)) );
            num /= BASE;
        }
        return sb.reverse().toString();
    }

    @Override
    public RegNewsFile saveRegNewsFile(MultipartFile file, Long idNews) {
        ClsNews clsNews = clsNewsRepo.findById(idNews).orElse(null);

        RegNewsFile regNewsFile = construct(file, clsNews);
        if (regNewsFile != null) {
            regNewsFileRepo.save(regNewsFile);
        }
        return regNewsFile;
    }

    private RegNewsFile construct(MultipartFile multipartFile, ClsNews clsNews) {
        RegNewsFile rnf = null;
        try {
            final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();

            File directory = new File(absolutePath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            final String filename = clsNews.getId().toString() + "n_" + UUID.randomUUID();
            final String originalFilename = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            File file = new File(String.format("%s/%s%s", absolutePath, filename, extension));
            multipartFile.transferTo(file);

            final String fileHash = getFileHash(file);
            final long size = Files.size(file.toPath());

            final List<RegNewsFile> files = new ArrayList<>();

            if (!files.isEmpty()) {
                rnf = files.get(0);
            } else {
                rnf = RegNewsFile.builder()
                        .news(clsNews)
                        .attachmentPath(String.format("%s/%s", uploadingDir, filename))
                        .fileName(filename)
                        .originalFileName(originalFilename)
                        .isDeleted(false)
                        .fileExtension(extension)
                        .fileSize(size)
                        .hash(fileHash)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .build();
            }
        } catch (IOException ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return rnf;
    }

    private String getFileHash(File file) {
        String result = "NOT";
        try {
            final byte[] bytes = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
            result = DatatypeConverter.printHexBinary(hash);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    @Override
    public boolean deleteRegNewsFile(Long id) {
        try {
            RegNewsFile regNewsFile = regNewsFileRepo.getOne(id);
            regNewsFile.setDeleted(true);
            regNewsFileRepo.save(regNewsFile);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public Map<String, List> getNewsTables(Long id_news) {
        Map<String, List> map = new HashMap<>();

        List<CheckedReviewStatusDto> listStatuses = getListStatusesByNews(id_news);

        map.put("okveds", regNewsOkvedRepo.findClsNewsOkvedByNews_Id(id_news));
        map.put("inn", regNewsOrganizationRepo.findInnByNews(id_news));
        map.put("files", regNewsFileRepo.findRegNewsFileByNews_IdAndIsDeleted(id_news, false));
        map.put("statuses", listStatuses);

        return map;
    }

    private List<CheckedReviewStatusDto> getListStatusesByNews(Long id_news){
        List<CheckedReviewStatusDto> initList = CheckedReviewStatusDto.getInitList();

        if (id_news != (long) -1) {
            Long checkedValue = (long) 1;
            List<RegNewsStatus> list = regNewsStatusRepo.findRegNewsStatusByNews_Id(id_news);
            for (RegNewsStatus regNewsStatus : list) {
                int rowsId = regNewsStatus.getStatusReview().intValue();
                initList.get(rowsId).setChecked(checkedValue);
            }
        }

        return initList;
    }
}
