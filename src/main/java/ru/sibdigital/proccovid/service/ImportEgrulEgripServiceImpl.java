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
import ru.sibdigital.proccovid.dto.egrul.EGRUL;
import ru.sibdigital.proccovid.dto.egrul.СвОКВЭДТип;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
    private MigrationService migrationService;

    private Map<String, Okved> okvedsMap = new HashMap<>();

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

    public void importData(boolean isEgrul, boolean isEgrip) {
        fillOkveds();
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
        for (EGRUL.СвЮЛ свЮЛ : egrul.getСвЮЛ()) {
            //egrulLogger.info("ИНН: " + свЮЛ.getИНН());

            EGRUL.СвЮЛ.СвОКВЭД свОКВЭД = свЮЛ.getСвОКВЭД();
            RegEgrul newRegEgrul = new RegEgrul();
            newRegEgrul.setLoadDate(new Timestamp(System.currentTimeMillis()));
            newRegEgrul.setInn(свЮЛ.getИНН());
            Date dateActual = new Date(свЮЛ.getДатаВып().toGregorianCalendar().getTimeInMillis());
            newRegEgrul.setDateActual(dateActual);
            try {
                свЮЛ.setСвОКВЭД(null);
                newRegEgrul.setData(mapper.writeValueAsString(свЮЛ));
            } catch (JsonProcessingException e) {
                egrulLogger.error("Не удалось преобразовать данные к JSON для ИНН " + свЮЛ.getИНН());
                e.printStackTrace();
            }
            newRegEgrul.setIdMigration(migration.getId());

            EgrulContainer ec = new EgrulContainer(newRegEgrul);

            if (свОКВЭД != null) {
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
                        egrulLogger.error("ОКВЭД" + свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + "версии " + ver + " не найден для ИНН " + свЮЛ.getИНН());
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
                            egrulLogger.error("ОКВЭД" + свОКВЭДТип.getКодОКВЭД() + "версии " + version + " не найден для ИНН " + свЮЛ.getИНН());
                        }
                    }

                }

                ec.setRegEgrulOkved(regEgrulOkveds);
            }
            containerList.add(ec);
        }
        return containerList;
    }

    private void saveEgruls(List<EgrulContainer> list){

        Map<String, RegEgrul> earlier = findSavedEarlierEgrul(list);
        List<Long> deletedOkveds = new ArrayList<>();
        List<EgrulContainer> updatedData = new ArrayList<>();

        if (!earlier.isEmpty()) {
            for (EgrulContainer ec : list) {
                RegEgrul r = ec.getRegEgrul();
                RegEgrul earl = earlier.get(r.getInn());
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
        }

        final List<RegEgrul> rel = updatedData.stream().map(c -> c.getRegEgrul()).collect(Collectors.toList());
        regEgrulRepo.saveAll(rel);

        final List<Set<RegEgrulOkved>> reos = updatedData.stream().map(c -> c.getRegEgrulOkved()).collect(Collectors.toList());
        Set<RegEgrulOkved> granula = new HashSet<>();
        int count = 1;
        for (Set<RegEgrulOkved> reo : reos) {
            if (reo != null) {
                granula.addAll(reo);
                count ++;
                //regEgrulOkvedRepo.saveAll(reo);
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

    private Map<String, RegEgrul> findSavedEarlierEgrul(List<EgrulContainer> list){
        final List<String> inns = list.stream().map(m -> m.getRegEgrul().getInn()).collect(Collectors.toList());
        final List<RegEgrul> rel = regEgrulRepo.findAllByInnList(inns);
        Map<String, RegEgrul> result = new HashMap<>();
        rel.stream().forEach(r -> {
            result.put(r.getInn(), r);
        });
        return result;
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

        Map<String, RegEgrip> earlier = findSavedEarlierEgrips(list);
        List<Long> deletedOkveds = new ArrayList<>();
        List<EgripContainer> updatedData = new ArrayList<>();

        if (!earlier.isEmpty()) {
            for (EgripContainer ec : list) {
                RegEgrip r = ec.getRegEgrip();
                RegEgrip earl = earlier.get(r.getInn());
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
        }

        final List<RegEgrip> rel = updatedData.stream().map(c -> c.getRegEgrip()).collect(Collectors.toList());
        regEgripRepo.saveAll(rel);

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

    private Map<String, RegEgrip> findSavedEarlierEgrips(List<EgripContainer> list){
        final List<String> inns = list.stream().map(m -> m.getRegEgrip().getInn()).collect(Collectors.toList());
        final List<RegEgrip> rel = regEgripRepo.findAllByInnList(inns);
        Map<String, RegEgrip> result = new HashMap<>();
        rel.stream().forEach(r -> {
            result.put(r.getInn(), r);
        });
        return result;
    }

    private  List<EgripContainer> parseEgripData(EGRIP egrip, ClsMigration migration) {
        List<EgripContainer> containerList = new ArrayList<>();
        for (EGRIP.СвИП свИП : egrip.getСвИП()) {
            //egripLogger.info("ИНН: " + свИП.getИННФЛ());

            EGRIP.СвИП.СвОКВЭД свОКВЭД = свИП.getСвОКВЭД();
            RegEgrip newRegEgrip = new RegEgrip();
            newRegEgrip.setLoadDate(new Timestamp(System.currentTimeMillis()));
            newRegEgrip.setInn(свИП.getИННФЛ());
            Date dateActual = new Date(свИП.getДатаВып().toGregorianCalendar().getTimeInMillis());
            newRegEgrip.setDateActual(dateActual);
            try {
                свИП.setСвОКВЭД(null);
                newRegEgrip.setData(mapper.writeValueAsString(свИП));
            } catch (JsonProcessingException e) {
                egripLogger.error("Не удалось преобразовать данные к JSON для ИНН " + свИП.getИННФЛ());
                e.printStackTrace();
            }
            newRegEgrip.setIdMigration(migration.getId());
            EgripContainer ec = new EgripContainer(newRegEgrip);

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

}
