package ru.sibdigital.proccovid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.dto.EgripContainer;
import ru.sibdigital.proccovid.dto.EgrulContainer;
import ru.sibdigital.proccovid.dto.egrip.EGRIP;
import ru.sibdigital.proccovid.dto.egrip.ГРНИПДатаТип;
import ru.sibdigital.proccovid.dto.egrul.*;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.model.egr.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.egr.ReferenceBookRepo;
import ru.sibdigital.proccovid.repository.egr.SvRecordEgrRepo;
import ru.sibdigital.proccovid.repository.egr.SvStatusRepo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ImportEgrulEgripServiceImpl implements ImportEgrulEgripService {

    private final static Logger egrulLogger = LoggerFactory.getLogger("egrulLogger"); // Создание егрюл и оквэдов
    private final static Logger egripLogger = LoggerFactory.getLogger("egripLogger");

    private final static Logger egrulFilesLogger = LoggerFactory.getLogger("egrulDirLogger"); // По каким файлам прошлись
    private final static Logger egripFilesLogger = LoggerFactory.getLogger("egripDirLogger");

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${egrul.import.directory}")
    private String egrulPath;

    @Value("${egrip.import.directory}")
    private String egripPath;

    @Value("${substringForFullFiles}")
    private String substringForFullFiles;

    @Value("${egr.validate.delete}")
    private Boolean deleteFiles;

    @Autowired
    private RegEgrulRepo regEgrulRepo;

    @Autowired
    private RegEgripRepo regEgripRepo;

    @Autowired
    private OkvedRepo okvedRepo;

    @Autowired
    private RegEgrulOkvedRepo regEgrulOkvedRepo;

    @Autowired
    private RegEgripOkvedRepo regEgripOkvedRepo;

    @Autowired
    private RegFilialRepo regFilialRepo;

    @Autowired
    private MigrationService migrationService;

    @Autowired
    private ReferenceBookRepo referenceBookRepo;

    @Autowired
    private SvStatusRepo svStatusRepo;

    @Autowired
    private SvRecordEgrRepo svRecordEgrRepo;

    private Map<String, Okved> okvedsMap = new HashMap<>();

    private Map<String, ReferenceBook> sulstMap  = new HashMap<>();
    private Map<String, ReferenceBook> sipstMap  = new HashMap<>();
    private Map<String, ReferenceBook> spvzMap  = new HashMap<>();

    private static Unmarshaller getUnmarshaller(Class clazz) {
        Unmarshaller unmarshaller = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return unmarshaller;
    }

    private static ZipFile getZipFile(File file) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile;
    }

    private static Timestamp XMLGregorianCalendarToTimestamp(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar != null) {
            return new Timestamp(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());
        } else {
            return null;
        }
    }

    public void importData(boolean isEgrul, boolean isEgrip) {
        fillOkveds();
        fillReferenceBooks();
        if (isEgrul) {
            importEgrulData();
        }
        if (isEgrip) {
            importEgripData();
        }
    }

    private void fillOkveds(){
        final List<Okved> okveds = okvedRepo.findAll();
        for (Okved okved: okveds) {
            String key = okved.getKindCode() + okved.getVersion();
            okvedsMap.put(key, okved);
        }
    }

    private void fillReferenceBooks(){
        sulstMap = getMapReferenceBookByType(ReferenceBookTypes.SULST.getValue());
        sipstMap = getMapReferenceBookByType(ReferenceBookTypes.SIPST.getValue());
        spvzMap  = getMapReferenceBookByType(ReferenceBookTypes.SPVZ.getValue());
    }

    private Map<String, ReferenceBook> getMapReferenceBookByType(Short type) {
        Map<String, ReferenceBook> map  = new HashMap<>();
        List<ReferenceBook> list = referenceBookRepo.findAllByType(type);
        for (ReferenceBook book: list) {
            String key = book.getCode();
            map.put(key, book);
        }

        return map;
    }

    private static Comparator<File> compareByFileName = new Comparator<File>() {
        @Override
        public int compare(File file1, File file2) {
            String filename1 = file1.getName().toLowerCase();
            String filename2 = file2.getName().toLowerCase();

            String subFilename1 = filename1.substring(filename1.lastIndexOf("_") +1);
            String subFilename2 = filename2.substring(filename1.lastIndexOf("_") +1);
            Integer num1 = Integer.parseInt(subFilename1.substring(0, subFilename1.indexOf(".zip")));
            Integer num2 = Integer.parseInt(subFilename2.substring(0, subFilename2.indexOf(".zip")));
            return num1.compareTo(num2);
        }
    };

    ////////////////////////////// ЕГРЮЛ /////////////////////////////
    /**
     * Метод импорта данных ЕГРЮЛ
     */
    private void importEgrulData() {
        egrulLogger.info("Импорт ЕГРЮЛ начат из " + egrulPath);
        egrulFilesLogger.info("Импорт ЕГРЮЛ начат из " + egrulPath);

        Collection<File> zipFiles = null;
        try {
            zipFiles = FileUtils.listFiles(new File(egrulPath),
                    new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
            egrulLogger.info("Всего файлов " + zipFiles.size());
            egrulFilesLogger.info("Всего файлов " + zipFiles.size());
        } catch (Exception e) {
            egrulFilesLogger.error("Не удалось получить доступ к " + egrulPath);
            e.printStackTrace();
        }

        // Загрузка ПОЛНОГО zip
        List<File> zipFullFiles = zipFiles.stream()
                .filter(s -> s.getName().toLowerCase().contains(substringForFullFiles))
                .collect(Collectors.toList());

        Collections.sort(zipFullFiles, compareByFileName);
        egrulLogger.info("Всего файлов ПОЛНОГО " + zipFullFiles.size());
        egrulFilesLogger.info("Всего файлов ПОЛНОГО " + zipFullFiles.size());

        if (zipFullFiles != null && !zipFullFiles.isEmpty()) {
            loadEGRULFiles(zipFullFiles);
        }


        // Загрузка ОБНОВЛЕНИЙ
        List<File> zipUpdateFiles = zipFiles.stream()
                .filter(s -> ! s.getName().toLowerCase().contains(substringForFullFiles))
                .collect(Collectors.toList());

        Collections.sort(zipUpdateFiles, compareByFileName);
        egrulLogger.info("Всего файлов ОБНОВЛЕНИЙ " + zipUpdateFiles.size());
        egrulFilesLogger.info("Всего файлов ОБНОВЛЕНИЙ " + zipUpdateFiles.size());

        if (zipUpdateFiles != null && !zipUpdateFiles.isEmpty()) {
            loadEGRULFiles(zipUpdateFiles);
        }

        egrulLogger.info("Импорт ЕГРЮЛ окончен");
        egrulFilesLogger.info("Импорт ЕГРЮЛ окончен");
    }

    private void loadEGRULFiles(List<File> zipFiles) {
        for (File zipFile : zipFiles) {
            ClsMigration migration = migrationService.getClsMigration(zipFile, ModelTypes.EGRUL_LOAD.getValue());
            if (migration == null || migration.getStatus() != StatusLoadTypes.SUCCESSFULLY_LOADED.getValue()) {
                // Добавить запись об обработке файла
                migration = migrationService.addMigrationRecord(migration, zipFile, ModelTypes.EGRUL_LOAD.getValue(), StatusLoadTypes.LOAD_START.getValue(), "");

                // Обработать данные zip
                processEgrulFile(zipFile, migration);

                // Изменить запись о статусе обработки файла
                if (migration.getStatus() == StatusLoadTypes.LOAD_START.getValue()) {
                    migrationService.changeMigrationStatus(migration, StatusLoadTypes.SUCCESSFULLY_LOADED.getValue(), "");

                    if (deleteFiles) {
                        zipFile.delete();
                    }
                } else { // загрузка файла прошла с ошибками. Переименовать файл.
                    boolean success = migrationService.renameFile(zipFile);
                        if (!success) {
                            egrulLogger.error("Не удалось переименовать (пометить, что загрузка прошла с ошибками) файл "+ zipFile.getName());
                        }
                }
            }
            else if (migration.getStatus() == StatusLoadTypes.SUCCESSFULLY_LOADED.getValue()) {
                egrulFilesLogger.error(zipFile.getName() + " уже был обработан.");
                if (deleteFiles) {
                    zipFile.delete();
                }
            }
        }
    }

    private void processEgrulFile(File file, ClsMigration migration) {
        egrulFilesLogger.info("Обработка файла " + file.getName() + " " + new Date());

        ZipFile zipFile = getZipFile(file);
        if (zipFile != null) {
            Unmarshaller unmarshaller = getUnmarshaller(EGRUL.class);
            if (unmarshaller != null) {
                try {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = entries.nextElement();

                        saveEgrulData(file, zipFile, zipEntry, unmarshaller, migration);
                    }
                } finally {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                egrulFilesLogger.error("Не удалось создать демаршаллизатор");
                migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось создать демаршаллизатор");
            }
        } else {
            egrulFilesLogger.error("Не удалось прочитать zip-файл");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать zip-файл");
        }
        egrulFilesLogger.info("Окончание обработки файла " + file.getName() + " " + new Date());
    }

    @Transactional
    boolean saveEgrulData(File file, ZipFile zipFile, ZipEntry zipEntry, Unmarshaller unmarshaller, ClsMigration migration) {
        egrulFilesLogger.info("Обработка xml файла " + zipEntry.getName());
        egrulLogger.info("Обработка xml файла " + zipEntry.getName());
        boolean result = false;

        InputStream is = null;
        try {
            is = zipFile.getInputStream(zipEntry);
            EGRUL egrul = (EGRUL) unmarshaller.unmarshal(is);

            List<EgrulContainer> list = parseEgrulData(egrul, migration);
            saveEgruls(list);

            result = true;
        } catch (IOException e) {
            egrulFilesLogger.error("Не удалось прочитать xml-файл из zip-файла");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать xml-файл из zip-файла");
            e.printStackTrace();
        } catch (JAXBException e) {
            egrulFilesLogger.error("Не удалось демаршализовать xml-файл из zip-файла");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось демаршализовать xml-файл из zip-файла");
            e.printStackTrace();
        }
        return result;
    }

    private List<EgrulContainer> parseEgrulData(EGRUL egrul, ClsMigration migration) {

        List<EgrulContainer> containerList = new ArrayList<>();
        Set<RegFilial> filials = new HashSet<>();
        Set<SvStatus> svStatuses = new HashSet<>();
        Set<SvRecordEgr> svRecords = new HashSet<>();
        for (EGRUL.СвЮЛ свЮЛ : egrul.getСвЮЛ()) {
            //egrulLogger.info("ИНН: " + свЮЛ.getИНН());
            EGRUL.СвЮЛ.СвОКВЭД свОКВЭД = свЮЛ.getСвОКВЭД();
//            Date dateActual = new Date(свЮЛ.getДатаВып().toGregorianCalendar().getTimeInMillis());
//            String ogrn = свЮЛ.getОГРН();
//
//            RegEgrul newRegEgrul = new RegEgrul();
//            newRegEgrul.setLoadDate(new Timestamp(System.currentTimeMillis()));
//            newRegEgrul.setInn(свЮЛ.getИНН());
//            newRegEgrul.setKpp(свЮЛ.getКПП());
//            newRegEgrul.setOgrn(ogrn);
//            newRegEgrul.setIogrn((ogrn != null && ogrn.isBlank() == false) ? Long.parseLong(ogrn) : null);
//            newRegEgrul.setDateActual(dateActual);

            RegEgrul newRegEgrul = construct(свЮЛ);
            newRegEgrul.setIdMigration(migration.getId());

            svStatuses = parseSvStatuses(свЮЛ, newRegEgrul);
            newRegEgrul.setActiveStatus(getActiveStatus(свЮЛ, svStatuses));
            свЮЛ.setСвСтатус(null);

            try {
                filials = parseFilials(свЮЛ, newRegEgrul);
                свЮЛ.setСвПодразд(null);
            } catch (JsonProcessingException e) {
                egrulLogger.error("Не удалось преобразовать данные филиала к JSON для ОГРН " + свЮЛ.getОГРН());
                e.printStackTrace();
            }

            try {
                svRecords = parseSvRecords(свЮЛ, newRegEgrul);
                свЮЛ.setСвПодразд(null);
            } catch (JsonProcessingException e) {
                egrulLogger.error("Не удалось преобразовать данные филиала к JSON для ОГРН " + свЮЛ.getОГРН());
                e.printStackTrace();
            }

            try {
                свЮЛ.setСвОКВЭД(null);
                свЮЛ.setСвСтатус(null);
                свЮЛ.setСвЗапЕГРЮЛ(null);
                newRegEgrul.setData(mapper.writeValueAsString(свЮЛ));
            } catch (JsonProcessingException e) {
                egrulLogger.error("Не удалось преобразовать данные к JSON для ОГРН " + свЮЛ.getОГРН());
                e.printStackTrace();
            }

            EgrulContainer ec = new EgrulContainer(newRegEgrul);
            ec.setRegFilials(filials);
            ec.setSvStatuses(svStatuses);
            ec.setSvRecords(svRecords);

            if (свОКВЭД != null) {
                Set<RegEgrulOkved> regEgrulOkveds = getRegEgrulOkveds(свОКВЭД, newRegEgrul);
                ec.setRegEgrulOkved(regEgrulOkveds);
            }
            containerList.add(ec);
        }
        return containerList;
    }

    private RegEgrul construct(EGRUL.СвЮЛ свЮЛ) {
        String ogrn = свЮЛ.getОГРН();
        Date dateActual = new Date(свЮЛ.getДатаВып().toGregorianCalendar().getTimeInMillis());

        RegEgrul newRegEgrul = new RegEgrul();
        newRegEgrul.setLoadDate(new Timestamp(System.currentTimeMillis()));
        newRegEgrul.setInn(свЮЛ.getИНН());
        newRegEgrul.setKpp(свЮЛ.getКПП());
        newRegEgrul.setOgrn(ogrn);
        newRegEgrul.setIogrn((ogrn != null && ogrn.isBlank() == false) ? Long.parseLong(ogrn) : null);
        newRegEgrul.setDateActual(dateActual);

        return newRegEgrul;
    }

    private Set<RegEgrulOkved> getRegEgrulOkveds(EGRUL.СвЮЛ.СвОКВЭД свОКВЭД, RegEgrul newRegEgrul) {
        Set<RegEgrulOkved> regEgrulOkveds = new HashSet<>();

        if (свОКВЭД.getСвОКВЭДОсн() != null) {
            String ver = свОКВЭД.getСвОКВЭДОсн().getПрВерсОКВЭД() != null ? свОКВЭД.getСвОКВЭДОсн().getПрВерсОКВЭД() : "2001";
            String okey = свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + ver;
            final Okved okved = okvedsMap.get(okey);
            if (okved != null) {
                RegEgrulOkved reo = new RegEgrulOkved();
                reo.setMain(true);
                reo.setRegEgrul(newRegEgrul);
                reo.setIdOkved(okved.getIdSerial());
                regEgrulOkveds.add(reo);
            } else {
                egrulLogger.error("ОКВЭД" + свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + "версии " + ver + " не найден для ИНН " + newRegEgrul.getInn());
            }
        }
        if (свОКВЭД.getСвОКВЭДДоп() != null) {
            for (СвОКВЭДТип свОКВЭДТип : свОКВЭД.getСвОКВЭДДоп()) {
                String version = свОКВЭДТип.getПрВерсОКВЭД() != null ? свОКВЭДТип.getПрВерсОКВЭД() : "2001";
                String dokey = свОКВЭДТип.getКодОКВЭД() + version;
                final Okved dokved = okvedsMap.get(dokey);

                if (dokved != null) {
                    RegEgrulOkved dreo = new RegEgrulOkved();
                    dreo.setMain(false);
                    dreo.setRegEgrul(newRegEgrul);
                    dreo.setIdOkved(dokved.getIdSerial());
                    regEgrulOkveds.add(dreo);
                } else {
                    egrulLogger.error("ОКВЭД" + свОКВЭДТип.getКодОКВЭД() + "версии " + version + " не найден для ИНН " + newRegEgrul.getInn());
                }
            }

        }

        return regEgrulOkveds;
    }

    private Integer getActiveStatus(EGRUL.СвЮЛ свЮЛ,  Set<SvStatus> svStatuses) {
        Integer activeStatus = EgrActiveStatus.ACTIVE.getValue();

        if (!checkСвЮЛIsValid(svStatuses)) {
            activeStatus = EgrActiveStatus.NOT_VALID.getValue();
        }

        if (checkСвЮЛIsCeased(свЮЛ)) {
            activeStatus = EgrActiveStatus.CEASED.getValue();
        }

        return activeStatus;
    }

    private boolean checkСвЮЛIsValid(Set<SvStatus> svStatuses) {
        final List<String> unactiveStatuses = Arrays.asList("701", "702", "801");

//        long count = svStatuses.stream().map(svStatus -> svStatus.getReferenceBook().getCode())
//                .filter(c -> unactiveStatuses.contains(c))
//                .count();

        boolean isValid = true;
        for (SvStatus svStatus : svStatuses) {
            if (unactiveStatuses.contains(svStatus.getReferenceBook().getCode())) {
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean checkСвЮЛIsCeased(EGRUL.СвЮЛ свЮЛ) {
        boolean isCeased = false;
        EGRUL.СвЮЛ.СвПрекрЮЛ свПрекрЮЛ = свЮЛ.getСвПрекрЮЛ();
        if (свПрекрЮЛ != null) {
            isCeased = true;
        }
        return isCeased;
    }

    private Set<SvStatus> parseSvStatuses(EGRUL.СвЮЛ свЮЛ, RegEgrul regEgrul) {
        Set<SvStatus> statuses = new HashSet<>();
        List<EGRUL.СвЮЛ.СвСтатус> свСтатусList = свЮЛ.getСвСтатус();
        if (свСтатусList != null) {
            for (EGRUL.СвЮЛ.СвСтатус свСтатус : свСтатусList) {
                SvStatus svStatus = SvStatus.builder()
                                    .egrul(regEgrul)
                                    .build();

                if (свСтатус.getСвРешИсклЮЛ() != null) {
                    try {
                        svStatus.setExclDecDate(XMLGregorianCalendarToTimestamp(свСтатус.getСвРешИсклЮЛ().getДатаРеш()));
                        svStatus.setExclDecNum(свСтатус.getСвРешИсклЮЛ().getНомерРеш());
                        svStatus.setPublDate(XMLGregorianCalendarToTimestamp(свСтатус.getСвРешИсклЮЛ().getДатаПубликации()));
                        svStatus.setJournalNum(свСтатус.getСвРешИсклЮЛ().getНомерЖурнала());
                    } catch (Exception e) {
                        egrulLogger.error("Не удалось распарсить свРешИсклЮЛ" + свЮЛ.getОГРН());
                    }
                }

                EGRUL.СвЮЛ.СвСтатус.СвСтатус1 свСтатус1 = свСтатус.getСвСтатус();
                if (свСтатус1 != null) {
                    ReferenceBook sulst = sulstMap.get(свСтатус1.getКодСтатусЮЛ());
                    if (sulst == null) {
                        sulst = createSulst(свСтатус1);
                    }
                    svStatus.setReferenceBook(sulst);
                }

                ГРНДатаТип grnDate = свСтатус.getГРНДата();
                if (grnDate != null) {
                    svStatus.setGrn(grnDate.getГРН());
                    svStatus.setRecordDate(XMLGregorianCalendarToTimestamp(grnDate.getДатаЗаписи()));
                }

                ГРНДатаТип grnDateCorr = свСтатус.getГРНДатаИспр();
                if (grnDateCorr != null) {
                    svStatus.setGrnCorr(grnDateCorr.getГРН());
                    svStatus.setRecordDateCorr(XMLGregorianCalendarToTimestamp(grnDateCorr.getДатаЗаписи()));
                }

                statuses.add(svStatus);
            }
        }

        return statuses;
    }

    private ReferenceBook createSulst(EGRUL.СвЮЛ.СвСтатус.СвСтатус1 свСтатус1) {
        ReferenceBook sulst = ReferenceBook.builder()
                        .code(свСтатус1.getКодСтатусЮЛ())
                        .name(свСтатус1.getНаимСтатусЮЛ())
                        .type(ReferenceBookTypes.SULST.getValue())
                        .status(EgrReferenceBookStatuses.ANOTHER.getValue())
                        .build();
        referenceBookRepo.save(sulst);
        sulstMap.put(sulst.getCode(), sulst);

        return sulst;
    }

    private Set<RegFilial> parseFilials(EGRUL.СвЮЛ свЮЛ, RegEgrul regEgrul) throws JsonProcessingException {
        Set<RegFilial> filials = new HashSet<>();

        if (свЮЛ.getСвПодразд() != null ) {
            List<EGRUL.СвЮЛ.СвПодразд.СвФилиал> свФилиалList = свЮЛ.getСвПодразд().getСвФилиал();
            if (свФилиалList != null) {
                for (EGRUL.СвЮЛ.СвПодразд.СвФилиал свФилиал : свФилиалList) {
                    RegFilial regFilial = RegFilial.builder()
                            .egrul(regEgrul)
                            .inn(regEgrul.getInn())
                            .type(EgrFilialTypes.FILIAL.getValue())
                            .activeStatus(EgrActiveStatus.ACTIVE.getValue())
                            .build();

                    String fullName = "";
                    if (свФилиал.getСвНаим() != null) {
                        fullName = свФилиал.getСвНаим().getНаимПолн();
                        regFilial.setFullName(fullName);
                    }

                    if (свФилиал.getСвУчетНОФилиал() != null) {
                        regFilial.setKpp(свФилиал.getСвУчетНОФилиал().getКПП());
                    }

                    String address = "";
                    String kladrCode = null;
                    if (свФилиал.getАдрМНРФ() != null) {
                        address = getКladrAddress(свФилиал.getАдрМНРФ());
                        kladrCode = свФилиал.getАдрМНРФ().getКодАдрКладр();
                    }

                    if (свФилиал.getАдрМНИн() != null) {
                        address = getForeignAddress(свФилиал.getАдрМНИн());
                    }

                    String filialAddressName = (((fullName == null || fullName.equals("")) ? "" : (fullName + " ")) + ((address == null) ? "" : address));


                    regFilial.setAddress(filialAddressName);
                    regFilial.setKladrCode(kladrCode);

                    regFilial.setData(mapper.writeValueAsString(свФилиал));
                    filials.add(regFilial);
                }

                List<EGRUL.СвЮЛ.СвПодразд.СвПредстав> свПредставList = свЮЛ.getСвПодразд().getСвПредстав();
                for (EGRUL.СвЮЛ.СвПодразд.СвПредстав свПредстав : свПредставList) {
                    RegFilial regFilial = RegFilial.builder()
                            .egrul(regEgrul)
                            .inn(regEgrul.getInn())
                            .type(EgrFilialTypes.REPRESENTATION.getValue())
                            .activeStatus(EgrActiveStatus.ACTIVE.getValue())
                            .build();

                    String fullName = "";
                    if (свПредстав.getСвНаим() != null) {
                        fullName = свПредстав.getСвНаим().getНаимПолн();
                        regFilial.setFullName(fullName);
                    }

                    if (свПредстав.getСвУчетНОПредстав() != null) {
                        regFilial.setKpp(свПредстав.getСвУчетНОПредстав().getКПП());
                    }

                    String address = "";
                    String kladrCode = null;
                    if (свПредстав.getАдрМНРФ() != null) {
                        address = getКladrAddress(свПредстав.getАдрМНРФ());
                        kladrCode = свПредстав.getАдрМНРФ().getКодАдрКладр();
                    }

                    if (свПредстав.getАдрМНИн() != null) {
                        address = getForeignAddress(свПредстав.getАдрМНИн());
                    }

                    String filialAddressName = (((fullName == null || fullName.equals("")) ? "" : (fullName + " ")) + ((address == null) ? "" : address));

                    regFilial.setAddress(filialAddressName);
                    regFilial.setKladrCode(kladrCode);

                    regFilial.setData(mapper.writeValueAsString(свПредстав));
                    filials.add(regFilial);
                }
            }
        }

        return filials;
    }

    private Set<SvRecordEgr> parseSvRecords(EGRUL.СвЮЛ свЮЛ, RegEgrul regEgrul) throws JsonProcessingException {
        Set<SvRecordEgr> svRecords = new HashSet<>();
        List<EGRUL.СвЮЛ.СвЗапЕГРЮЛ> свЗапЕГРЮЛList = свЮЛ.getСвЗапЕГРЮЛ();
        if (свЗапЕГРЮЛList!= null ) {
            for (EGRUL.СвЮЛ.СвЗапЕГРЮЛ свЗапЕГРЮЛ : свЗапЕГРЮЛList) {
                SvRecordEgr svRecordEgr = SvRecordEgr.builder()
                                        .egrul(regEgrul)
                                        .recordId(свЗапЕГРЮЛ.getИдЗап())
                                        .recordDate(XMLGregorianCalendarToTimestamp(свЗапЕГРЮЛ.getДатаЗап()))
                                        .build();

                svRecordEgr.setIsValid(isValidSvRecord(свЗапЕГРЮЛ));

                ВидЗапТип видЗап =  свЗапЕГРЮЛ.getВидЗап();
                if (видЗап != null) {
                    ReferenceBook spvz = spvzMap.get(видЗап.getКодСПВЗ());
                    if (spvz == null) {
                        spvz = createSpvz(видЗап.getКодСПВЗ(), видЗап.getНаимВидЗап());
                    }
                    svRecordEgr.setSpvz(spvz);
                }

                свЗапЕГРЮЛ.setВидЗап(null);
                свЗапЕГРЮЛ.setИдЗап(null);
                свЗапЕГРЮЛ.setИдЗап(null);

                svRecordEgr.setData(mapper.writeValueAsString(свЗапЕГРЮЛ));

                svRecords.add(svRecordEgr);
            }
        }

        return svRecords;
    }

    private void saveEgruls(List<EgrulContainer> list){

        Map<Long, RegEgrul> earlier = findSavedEarlierEgrul(list);
        List<Long> deletedOkveds = new ArrayList<>();
        List<EgrulContainer> updatedData = new ArrayList<>();

        if (!earlier.isEmpty()) {
            for (EgrulContainer ec : list) {
                RegEgrul r = ec.getRegEgrul();
                RegEgrul earl = earlier.get(r.getIogrn());
                if (earl !=null) {
                    // Производить замену, только если СвЮЛ.ДатаВып больше date_actual записи таблицы
                    if (r.getDateActual().after(earl.getDateActual())) {
                        updatedData.add(ec);
                        r.setId(earl.getId());
                        deletedOkveds.add(earl.getId());
                    }
                }
                else {
                    updatedData.add(ec);
                }
            }
        }
        else {
            updatedData = list;
        }

        if (!deletedOkveds.isEmpty()) {
            regEgrulOkvedRepo.deleteRegEgrulOkveds(deletedOkveds);
//            regFilialRepo.deleteRegFilials(deletedOkveds);
            svStatusRepo.deleteSvStatuses(deletedOkveds);
            svRecordEgrRepo.deleteSvRecordEgrsByIdEgruls(deletedOkveds);
        }

        final List<RegEgrul> rel = updatedData.stream().map(c -> c.getRegEgrul()).collect(Collectors.toList());
        regEgrulRepo.saveAll(rel);

        saveRegEgrulOkveds(updatedData);
//        saveSvFilials(updatedData);
        saveSvStatuses(updatedData);
        saveSvRecords(updatedData);

        Set<RegFilial> updatedFilials = getUpdatedFilials(updatedData);
        saveSvFilials(updatedFilials);
    }

    private void saveRegEgrulOkveds(List<EgrulContainer> updatedData) {
        final List<Set<RegEgrulOkved>> reos = updatedData.stream().map(c -> c.getRegEgrulOkved()).collect(Collectors.toList());
        Set<RegEgrulOkved> granula = new HashSet<>();
        int count = 1;
        for (Set<RegEgrulOkved> reo : reos) {
            if (reo != null) {
                granula.addAll(reo);
                count ++;
            }
            if (count % 10 == 0 && !granula.isEmpty()){
                regEgrulOkvedRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            regEgrulOkvedRepo.saveAll(granula);
        }
    }

    private void saveSvFilials(List<EgrulContainer> updatedData) {
        final List<Set<RegFilial>> rfs = updatedData.stream().map(c -> c.getRegFilials()).collect(Collectors.toList());
        Set<RegFilial> granula = new HashSet<>();
        int cnt = 1;
        for (Set<RegFilial> rf : rfs) {
            if (rf != null) {
                granula.addAll(rf);
                cnt ++;
            }
            if (cnt % 10 == 0 && !granula.isEmpty()){
                regFilialRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            regFilialRepo.saveAll(granula);
        }
    }

    private void saveSvFilials(Set<RegFilial> updatedFilials) {
        regFilialRepo.saveAll(updatedFilials);
    }

    private Set<RegFilial> getUpdatedFilials(List<EgrulContainer> updatedData) {
        Set<RegFilial> updatedFilials = new HashSet<>();
        for (EgrulContainer container : updatedData) {
            Long egrulId = container.getRegEgrul().getId();
            if (egrulId != null) {
                List<RegFilial> prevFilials = regFilialRepo.findAllByEgrul_Id(egrulId).orElse(null);
                Set<String> prevActiveFilialsAddresses = new HashSet<>();
                if (prevFilials != null) {
                    prevActiveFilialsAddresses = prevFilials.stream()
                            .filter(f -> (f.getActiveStatus() == EgrActiveStatus.ACTIVE.getValue()))
                            .map(f -> f.getAddress())
                            .collect(Collectors.toSet());
                }
                Set<String> filialsNameAddresses = container.getRegFilials().stream()
                        .map(f -> f.getAddress())
                        .collect(Collectors.toSet()); // из распарсиных xml

                // Новые филиалы, чьих хэшкодов нет в базе
//                Set<Integer> finalPrevActiveFilialsHashes = prevActiveFilialsHashes;
                if (prevActiveFilialsAddresses != null) {
                    if (filialsNameAddresses != null) {
                        Set<String> addressesOfNewFilials = new HashSet<>(filialsNameAddresses);
                        addressesOfNewFilials.removeAll(prevActiveFilialsAddresses);

                        Set<RegFilial> newFilials = container.getRegFilials().stream().filter(f -> addressesOfNewFilials.contains(f.getAddress())).collect(Collectors.toSet());
                        updatedFilials.addAll(newFilials);
                    }
                } else {
                    updatedFilials.addAll(container.getRegFilials());
                }

                // Филиалы, чьи хэшкоды есть в базе, но нет в новом списке
                if (filialsNameAddresses != null && prevFilials != null) {
                    Set<String> addressesOfNotActiveAnymoreFilials = new HashSet<>(prevActiveFilialsAddresses);
                    addressesOfNotActiveAnymoreFilials.removeAll(filialsNameAddresses);

                    Set<RegFilial> notActiveAnymoreFilials = prevFilials.stream().filter(f -> addressesOfNotActiveAnymoreFilials.contains(f.getAddress())).collect(Collectors.toSet());
                    notActiveAnymoreFilials.forEach(f -> f.setActiveStatus(EgrActiveStatus.CEASED.getValue()));
                    updatedFilials.addAll(notActiveAnymoreFilials);
                }
            } else {
                updatedFilials.addAll(container.getRegFilials());
            }
        }

        return updatedFilials;
    }

    private void saveSvStatuses(List<EgrulContainer> updatedData) {
        final List<Set<SvStatus>> svs = updatedData.stream().map(c -> c.getSvStatuses()).collect(Collectors.toList());
        Set<SvStatus> granula = new HashSet<>();
        int cnt = 1;
        for (Set<SvStatus> sv : svs) {
            if (sv != null) {
                granula.addAll(sv);
                cnt ++;
            }
            if (cnt % 10 == 0 && !granula.isEmpty()){
                svStatusRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            svStatusRepo.saveAll(granula);
        }
    }

    private void saveSvRecords(List<EgrulContainer> updatedData) {
        final List<Set<SvRecordEgr>> set = updatedData.stream().map(c -> c.getSvRecords()).collect(Collectors.toList());
        Set<SvRecordEgr> granula = new HashSet<>();
        int cnt = 1;
        for (Set<SvRecordEgr> sre : set) {
            if (sre != null) {
                granula.addAll(sre);
                cnt ++;
            }
            if (cnt % 10 == 0 && !granula.isEmpty()){
                svRecordEgrRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            svRecordEgrRepo.saveAll(granula);
        }
    }

    private Map<Long, RegEgrul> findSavedEarlierEgrul(List<EgrulContainer> list){
        final List<Long> iogrns = list.stream().map(m -> m.getRegEgrul().getIogrn()).collect(Collectors.toList());
        final List<RegEgrul> rel = regEgrulRepo.findAllByIogrnList(iogrns);
        Map<Long, RegEgrul> result = new HashMap<>();
        rel.stream().forEach(r -> {
            result.put(r.getIogrn(), r);
        });
        return result;
    }

    private Boolean isValidSvRecord(EGRUL.СвЮЛ.СвЗапЕГРЮЛ свЗапЕГРЮЛ) {
        Boolean isValid = true;

        EGRUL.СвЮЛ.СвЗапЕГРЮЛ.СвСтатусЗап свСтатусЗап = свЗапЕГРЮЛ.getСвСтатусЗап();
        if (свСтатусЗап != null && свСтатусЗап.getГРНДатаНед() != null) {
            isValid = false;
        }

        return isValid;
    }

    ////////////////////////////// ЕГРИП /////////////////////////////

    /**
     * Метод импорта данных ЕГРИП
     */
    private void importEgripData() {
        egripLogger.info("Импорт ЕГРИП начат");
        egripFilesLogger.info("Импорт ЕГРИП начат");

        Collection<File> zipFiles = null;
        try {
            zipFiles = FileUtils.listFiles(new File(egripPath),
                    new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
        } catch (Exception e) {
            egripFilesLogger.error("Не удалось получить доступ к " + egripPath);
            e.printStackTrace();
        }

        // Загрузка ПОЛНОГО zip
        List<File> zipFullFiles = zipFiles.stream()
                .filter(s -> s.getName().toLowerCase().contains(substringForFullFiles))
                .collect(Collectors.toList());

        Collections.sort(zipFullFiles, compareByFileName);
        loadEGRIPFiles(zipFullFiles);

        // Загрузка ОБНОВЛЕНИЙ
        List<File> zipUpdateFiles = zipFiles.stream()
                .filter(s -> ! s.getName().toLowerCase().contains(substringForFullFiles))
                .collect(Collectors.toList());

        Collections.sort(zipUpdateFiles, compareByFileName);
        loadEGRIPFiles(zipUpdateFiles);

        egripLogger.info("Импорт ЕГРИП окончен");
        egripFilesLogger.info("Импорт ЕГРИП окончен");
    }

    private void loadEGRIPFiles(List<File> zipFiles) {
        for (File zipFile : zipFiles) {
            ClsMigration migration = migrationService.getClsMigration(zipFile, ModelTypes.EGRIP_LOAD.getValue());
            if (migration == null || migration.getStatus() != StatusLoadTypes.SUCCESSFULLY_LOADED.getValue()) {
                // Добавить запись об обработке файла
                migration = migrationService.addMigrationRecord(migration, zipFile, ModelTypes.EGRIP_LOAD.getValue(), StatusLoadTypes.LOAD_START.getValue(), "");

                // Обработать данные zip
                processEgripFile(zipFile, migration);

                // Изменить запись о статусе обработки файла
                if (migration.getStatus() == StatusLoadTypes.LOAD_START.getValue()) {
                    migrationService.changeMigrationStatus(migration, StatusLoadTypes.SUCCESSFULLY_LOADED.getValue(), "");

                    if (deleteFiles) {
                        zipFile.delete();
                    }
                } else { // загрузка файла прошла с ошибками. Переименовать файл.
                    boolean success = migrationService.renameFile(zipFile);
                    if (!success) {
                        egripLogger.error("Не удалось переименовать (пометить, что загрузка прошла с ошибками) файл "+ zipFile.getName());
                    }
                }
            }
            else if (migration.getStatus() != StatusLoadTypes.SUCCESSFULLY_LOADED.getValue()) {
                egripFilesLogger.error(zipFile.getName() + " уже был обработан.");
                if (deleteFiles) {
                    zipFile.delete();
                }
            }
        }
    }

    private void processEgripFile(File file, ClsMigration migration) {
        egripFilesLogger.info("Обработка файла " + file.getName() + " " + new Date());

        ZipFile zipFile = getZipFile(file);
        if (zipFile != null) {
            Unmarshaller unmarshaller = getUnmarshaller(EGRIP.class);
            if (unmarshaller != null) {
                try {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = entries.nextElement();
                        saveEgripData(file, zipFile, zipEntry, unmarshaller, migration);
                    }
                } finally {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                egripFilesLogger.error("Не удалось создать демаршаллизатор");
                migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось создать демаршаллизатор");

            }
        } else {
            egripFilesLogger.error("Не удалось прочитать zip-файл");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать zip-файл");

        }
        egripFilesLogger.info("Окончание обработки файла " + file.getName() + " " + new Date());

    }

    @Transactional
    void saveEgripData(File file, ZipFile zipFile, ZipEntry zipEntry, Unmarshaller unmarshaller, ClsMigration migration) {
        egripFilesLogger.info("Обработка xml файла " + zipEntry.getName());
        egripLogger.info("Обработка xml файла " + zipEntry.getName());
        InputStream is = null;
        try {
            is = zipFile.getInputStream(zipEntry);
            EGRIP egrip = (EGRIP) unmarshaller.unmarshal(is);

            List<EgripContainer> list = parseEgripData(egrip, migration);
            saveEgrips(list);

        }  catch (IOException e) {
            egripFilesLogger.error("Не удалось прочитать xml-файл из zip-файла");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать xml-файл из zip-файла");
            e.printStackTrace();
        } catch (JAXBException e) {
            egripFilesLogger.error("Не удалось демаршализовать xml-файл из zip-файла");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось демаршализовать xml-файл из zip-файла");
            e.printStackTrace();
        }

    }

    private void saveEgrips(List<EgripContainer> list){

        Map<Long, RegEgrip> earlier = findSavedEarlierEgrips(list);
        List<Long> deletedOkveds = new ArrayList<>();
        List<EgripContainer> updatedData = new ArrayList<>();

        if (!earlier.isEmpty()) {
            for (EgripContainer ec : list) {
                RegEgrip r = ec.getRegEgrip();
                RegEgrip earl = earlier.get(r.getIogrn());
                if (earl != null) {
                    if (r.getDateActual().after(earl.getDateActual())) {
                        updatedData.add(ec);
                        r.setId(earl.getId());
                        deletedOkveds.add(earl.getId());
                    }
                }
                else {
                    updatedData.add(ec);
                }
            }
        }
        else {
            updatedData = list;
        }

        if (!deletedOkveds.isEmpty()) {
            regEgripOkvedRepo.deleteRegEgrulOkveds(deletedOkveds);
            svStatusRepo.deleteSvStatuses(deletedOkveds);
            svRecordEgrRepo.deleteSvRecordEgrsByIdEgrips(deletedOkveds);
        }

        final List<RegEgrip> rel = updatedData.stream().map(c -> c.getRegEgrip()).collect(Collectors.toList());
        regEgripRepo.saveAll(rel);

        saveRegEgripOkveds(updatedData);
        saveEgripSvStatuses(updatedData);
        saveEgripSvRecords(updatedData);
    }

    private void saveRegEgripOkveds(List<EgripContainer> updatedData) {
        final List<Set<RegEgripOkved>> reos = updatedData.stream().map(c -> c.getRegEgripOkved()).collect(Collectors.toList());
        Set<RegEgripOkved> granula = new HashSet<>();
        int count = 1;
        for (Set<RegEgripOkved> reo : reos) {
            if (reo != null) {
                granula.addAll(reo);
                count ++;
                //regEgrulOkvedRepo.saveAll(reo);
            }
            if (count % 10 == 0 && !granula.isEmpty()){
                regEgripOkvedRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            regEgripOkvedRepo.saveAll(granula);
        }
    }

    private void saveEgripSvStatuses(List<EgripContainer> updatedData) {
        final List<Set<SvStatus>> svs = updatedData.stream().map(c -> c.getSvStatuses()).collect(Collectors.toList());
        Set<SvStatus> granula = new HashSet<>();
        int cnt = 1;
        for (Set<SvStatus> sv : svs) {
            if (sv != null) {
                granula.addAll(sv);
                cnt ++;
            }
            if (cnt % 10 == 0 && !granula.isEmpty()){
                svStatusRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            svStatusRepo.saveAll(granula);
        }
    }

    private void saveEgripSvRecords(List<EgripContainer> updatedData) {
        final List<Set<SvRecordEgr>> set = updatedData.stream().map(c -> c.getSvRecords()).collect(Collectors.toList());
        Set<SvRecordEgr> granula = new HashSet<>();
        int cnt = 1;
        for (Set<SvRecordEgr> sre : set) {
            if (sre != null) {
                granula.addAll(sre);
                cnt ++;
            }
            if (cnt % 10 == 0 && !granula.isEmpty()){
                svRecordEgrRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            svRecordEgrRepo.saveAll(granula);
        }
    }

    private Map<Long, RegEgrip> findSavedEarlierEgrips(List<EgripContainer> list){
        final List<Long> iogrns = list.stream().map(m -> m.getRegEgrip().getIogrn()).collect(Collectors.toList());
        final List<RegEgrip> rel = regEgripRepo.findAllByIogrnList(iogrns);
        Map<Long, RegEgrip> result = new HashMap<>();
        rel.stream().forEach(r -> {
            result.put(r.getIogrn(), r);
        });
        return result;
    }

    private  List<EgripContainer> parseEgripData(EGRIP egrip, ClsMigration migration) {
        List<EgripContainer> containerList = new ArrayList<>();
        Set<SvStatus> svStatuses = new HashSet<>();
        Set<SvRecordEgr> svRecords = new HashSet<>();
        for (EGRIP.СвИП свИП : egrip.getСвИП()) {
            //egripLogger.info("ИНН: " + свИП.getИННФЛ());

            EGRIP.СвИП.СвОКВЭД свОКВЭД = свИП.getСвОКВЭД();
            RegEgrip newRegEgrip = new RegEgrip();
            newRegEgrip.setLoadDate(new Timestamp(System.currentTimeMillis()));
            newRegEgrip.setInn(свИП.getИННФЛ());

            String ogrn = свИП.getОГРНИП();
            newRegEgrip.setOgrn(ogrn);
            newRegEgrip.setIogrn((ogrn != null) ? Long.parseLong(ogrn) : null);
            newRegEgrip.setTypeEgrip(Short.valueOf(свИП.getКодВидИП()));

            Date dateActual = new Date(свИП.getДатаВып().toGregorianCalendar().getTimeInMillis());
            newRegEgrip.setDateActual(dateActual);

            svStatuses = parseSvStatuses(свИП, newRegEgrip);

//            newRegEgrip.setActiveStatus(getActiveStatus(свИП, svStatuses));

            try {
                svRecords = parseSvRecords(свИП, newRegEgrip);
            } catch (JsonProcessingException e) {
                egrulLogger.error("Не удалось преобразовать данные филиала к JSON для ОГРН " + свИП.getОГРНИП());
                e.printStackTrace();
            }

            try {
                свИП.setСвОКВЭД(null);
                свИП.setСвСтатус(null);
                свИП.setСвЗапЕГРИП(null);
                newRegEgrip.setData(mapper.writeValueAsString(свИП));
            } catch (JsonProcessingException e) {
                egripLogger.error("Не удалось преобразовать данные к JSON для ИНН " + свИП.getИННФЛ());
                e.printStackTrace();
            }

            newRegEgrip.setIdMigration(migration.getId());

            Integer activeStatus = getActiveStatus(свИП, svStatuses, svRecords);
            newRegEgrip.setActiveStatus(activeStatus);

            EgripContainer ec = new EgripContainer(newRegEgrip);
            ec.setSvStatuses(svStatuses);
            ec.setSvRecords(svRecords);

            if (свОКВЭД != null) {
                Set<RegEgripOkved> regEgripOkveds = new HashSet<>();

                if (свОКВЭД.getСвОКВЭДОсн() != null) {
                    String version = свОКВЭД.getСвОКВЭДОсн().getПрВерсОКВЭД() != null ? свОКВЭД.getСвОКВЭДОсн().getПрВерсОКВЭД() : "2001";
                    String okey = свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + version;
                    final Okved okved = okvedsMap.get(okey);
                    if (okved != null) {
                        RegEgripOkved reo = new RegEgripOkved();
                        reo.setMain(true);
                        reo.setRegEgrip(newRegEgrip);
                        reo.setIdOkved(okved.getIdSerial());
                        regEgripOkveds.add(reo);
                    } else {
                        egripLogger.error("ОКВЭД " +свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + "версии " + version + " не найден для ИНН " +  свИП.getИННФЛ());
                    }
                }

                if (свОКВЭД.getСвОКВЭДДоп() != null) {
                    for (ru.sibdigital.proccovid.dto.egrip.СвОКВЭДТип свОКВЭДТип : свОКВЭД.getСвОКВЭДДоп()) {
                        String version = свОКВЭДТип.getПрВерсОКВЭД() != null ? свОКВЭДТип.getПрВерсОКВЭД() : "2001";
                        String dokey = свОКВЭДТип.getКодОКВЭД() + version;
                        final Okved dokved = okvedsMap.get(dokey);
                        if (dokved != null) {
                            RegEgripOkved dreo = new RegEgripOkved();
                            dreo.setMain(false);
                            dreo.setRegEgrip(newRegEgrip);
                            dreo.setIdOkved(dokved.getIdSerial());
                            regEgripOkveds.add(dreo);
                        } else {
                            egripLogger.error("ОКВЭД " + свОКВЭДТип.getКодОКВЭД() + "версии " + version + "не найден для ИНН " + свИП.getИННФЛ());
                        }
                    }
                }
                ec.setRegEgripOkved(regEgripOkveds);
            }
            containerList.add(ec);
        }
        return containerList;
    }

    private Set<SvStatus> parseSvStatuses(EGRIP.СвИП свИП, RegEgrip regEgrip) {
        Set<SvStatus> statuses = new HashSet<>();
        EGRIP.СвИП.СвСтатус свСтатус = свИП.getСвСтатус();
        if (свСтатус != null) {
            SvStatus svStatus = SvStatus.builder()
                    .egrip(regEgrip)
                    .build();

            EGRIP.СвИП.СвСтатус.СвСтатус1 свСтатус1 = свСтатус.getСвСтатус();
            if (свСтатус1 != null) {
                ReferenceBook sipst = sipstMap.get(свСтатус1.getКодСтатус());
                if (sipst == null) {
                    sipst = createSipst(свСтатус1);
                }
                svStatus.setReferenceBook(sipst);
            }

            ГРНИПДатаТип grnDate = свСтатус.getГРНИПДата();
            if (grnDate != null) {
                svStatus.setGrn(grnDate.getГРНИП());
                svStatus.setRecordDate(XMLGregorianCalendarToTimestamp(grnDate.getДатаЗаписи()));
            }
            statuses.add(svStatus);
        }

        return statuses;
    }

    private Set<SvRecordEgr> parseSvRecords(EGRIP.СвИП свИП, RegEgrip regEgrip) throws JsonProcessingException {
        Set<SvRecordEgr> svRecords = new HashSet<>();
        List<EGRIP.СвИП.СвЗапЕГРИП> свЗапЕГРИПList = свИП.getСвЗапЕГРИП();
        if (свЗапЕГРИПList!= null ) {
            for (EGRIP.СвИП.СвЗапЕГРИП свЗапЕГРИП : свЗапЕГРИПList) {
                SvRecordEgr svRecordEgr = SvRecordEgr.builder()
                        .egrip(regEgrip)
                        .recordId(свЗапЕГРИП.getИдЗап())
                        .recordDate(XMLGregorianCalendarToTimestamp(свЗапЕГРИП.getДатаЗап()))
                        .build();

                svRecordEgr.setIsValid(isValidSvRecord(свЗапЕГРИП));

                ru.sibdigital.proccovid.dto.egrip.ВидЗапТип видЗап =  свЗапЕГРИП.getВидЗап();
                if (видЗап != null) {
                    ReferenceBook spvz = spvzMap.get(видЗап.getКодСПВЗ());
                    if (spvz == null) {
                        spvz = createSpvz(видЗап.getКодСПВЗ(), видЗап.getНаимВидЗап());
                    }
                    svRecordEgr.setSpvz(spvz);
                }

                свЗапЕГРИП.setВидЗап(null);
                свЗапЕГРИП.setИдЗап(null);
                свЗапЕГРИП.setДатаЗап(null);

                svRecordEgr.setData(mapper.writeValueAsString(свЗапЕГРИП));

                svRecords.add(svRecordEgr);
            }
        }

        return svRecords;
    }

    private ReferenceBook createSipst(EGRIP.СвИП.СвСтатус.СвСтатус1 свСтатус1) {
        ReferenceBook sipst = ReferenceBook.builder()
                .code(свСтатус1.getКодСтатус())
                .name(свСтатус1.getНаимСтатус())
                .type(ReferenceBookTypes.SIPST.getValue())
                .status(EgrReferenceBookStatuses.ANOTHER.getValue())
                .build();
        referenceBookRepo.save(sipst);
        sipstMap.put(sipst.getCode(), sipst);

        return sipst;
    }

    private Integer getActiveStatus(EGRIP.СвИП свИП, Set<SvStatus> svStatuses, Set<SvRecordEgr> svRecords) {
        Integer activeStatus = EgrActiveStatus.ACTIVE.getValue();

        if (checkСвИПIsCeased(свИП)) {
            activeStatus = EgrActiveStatus.CEASED.getValue();
        }

        if (activeStatus == EgrActiveStatus.ACTIVE.getValue()) {
            if (!checkСвИПIsValid(svStatuses)) {
                activeStatus = EgrActiveStatus.NOT_VALID.getValue();
            }
        }

        if (activeStatus == EgrActiveStatus.ACTIVE.getValue()) {
            if (!checkСвИПIsActiveBySvRecord(svRecords)) {
                activeStatus = EgrActiveStatus.NOT_ACTIVE_BY_SV_RECORD.getValue();
            }
        }

        return activeStatus;
    }

    private boolean checkСвИПIsCeased(EGRIP.СвИП свИП) {
        boolean isCeased = false;
        EGRIP.СвИП.СвПрекращ свПрекрИП = свИП.getСвПрекращ();
        if (свПрекрИП != null) {
            isCeased = true;
        }
        return isCeased;
    }

    private boolean checkСвИПIsValid(Set<SvStatus> svStatuses) {

        boolean isValid = true;
        for (SvStatus svStatus : svStatuses) {
            String code = svStatus.getReferenceBook().getCode();
            Integer icode = Integer.valueOf(code);
            if (icode > 200) {
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean checkСвИПIsActiveBySvRecord(Set<SvRecordEgr> svRecords) {
        boolean isActive = true;
        for (SvRecordEgr record : svRecords) {
            if (record.getIsValid() && record.getSpvz().getStatus() == EgrReferenceBookStatuses.ORGANIZATION_NOT_ACTIVE.getValue()) {
                isActive = false;
            }
        }

        return isActive;
    }

    private Boolean isValidSvRecord(EGRIP.СвИП.СвЗапЕГРИП свЗапЕГРИП) {
        Boolean isValid = true;

        EGRIP.СвИП.СвЗапЕГРИП.СвСтатусЗап свСтатусЗап = свЗапЕГРИП.getСвСтатусЗап();
        if (свСтатусЗап != null && свСтатусЗап.getГРНИПДатаНед() != null) {
            isValid = false;
        }

        return isValid;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////

    private String getКladrAddress(АдрРФЕГРЮЛТип адр) {
        String address = "";
        address += (адр.getКодРегион() != null) ? адр.getКодРегион() : "";

        РегионТип регион = адр.getРегион();
        if (регион != null) {
            address += " " + регион.getТипРегион() + " " + регион.getНаимРегион();
        }

        РайонТип район = адр.getРайон();
        if (район != null) {
            address += " " + район.getТипРайон() + " " + район.getНаимРайон();
        }

        НаселПунктТип населПунктТип = адр.getНаселПункт();
        if (населПунктТип != null) {
            address += " " + населПунктТип.getТипНаселПункт() + " " + населПунктТип.getНаимНаселПункт();
        }

        ГородТип город = адр.getГород();
        if (город != null) {
            address += " " + город.getТипГород() + " " + город.getНаимГород();
        }

        УлицаТип улица = адр.getУлица();
        if (улица != null) {
            address += " " + улица.getТипУлица() + " " + улица.getНаимУлица();
        }

        address += (адр.getКорпус() != null) ? " КОРПУС " + адр.getКорпус() : "";
        address += (адр.getДом() != null) ? " ДОМ " + адр.getДом() : "";
        address += (адр.getКварт() != null) ? " КВАРТИРА " + адр.getКварт() : "";

        return address;
    }

    private String getForeignAddress(АдрИнЕГРЮЛТип адр) {
        String address = "";
        address += (адр.getАдрИн() != null) ? адр.getАдрИн() : "";

        return address;
    }

    private ReferenceBook createSpvz(String code, String name) {
        ReferenceBook spvz = ReferenceBook.builder()
                .code(code)
                .name(name)
                .type(ReferenceBookTypes.SPVZ.getValue())
                .status(EgrReferenceBookStatuses.ANOTHER.getValue())
                .build();
        referenceBookRepo.save(spvz);
        spvzMap.put(spvz.getCode(), spvz);

        return spvz;
    }
}
