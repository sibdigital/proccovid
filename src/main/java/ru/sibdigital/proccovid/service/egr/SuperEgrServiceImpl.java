package ru.sibdigital.proccovid.service.egr;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.dto.EgripContainer;
import ru.sibdigital.proccovid.dto.EgrulContainer;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.model.egr.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.egr.*;
import ru.sibdigital.proccovid.service.MigrationService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@Service
public class SuperEgrServiceImpl implements SuperEgrService{

    @Value("${egrip.import.directory}")
    protected String egripPath;

    @Value("${egrul.import.directory}")
    protected String egrulPath;

    @Value("${substringForFullFiles}")
    protected String substringForFullFiles;

    @Value("${egr.validate.delete}")
    protected Boolean deleteFiles;

    @Autowired
    protected RegEgripRepo regEgripRepo;

    @Autowired
    protected RegEgrulRepo regEgrulRepo;

    @Autowired
    protected RegEgripOkvedRepo regEgripOkvedRepo;

    @Autowired
    protected RegEgrulOkvedRepo regEgrulOkvedRepo;

    @Autowired
    protected RegFilialRepo regFilialRepo;

    @Autowired
    protected SvRegRepo svRegRepo;

    @Autowired
    protected SvOrgRepo svOrgRepo;

    @Autowired
    protected SvStatusRepo svStatusRepo;

    @Autowired
    protected MigrationService migrationService;

    @Autowired
    protected OkvedRepo okvedRepo;

    @Autowired
    protected OpfRepo opfRepo;

    @Autowired
    protected SvRecordEgrRepo svRecordEgrRepo;

    @Autowired
    protected ReferenceBookRepo referenceBookRepo;

    ////////////////////////////////////////// FILE OPERATIONS /////////////////////////////////////////////////////////

    protected static Unmarshaller getUnmarshaller(Class clazz) {
        Unmarshaller unmarshaller = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return unmarshaller;
    }

    protected static ZipFile getZipFile(File file) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile;
    }

    protected static Comparator<File> compareByFileName = new Comparator<File>() {
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

    protected Collection<File> getZipFilesFromDirectory(String path, Logger logger) {
        Collection<File> zipFiles = null;

        try {
            zipFiles = FileUtils.listFiles(new File(path),
                    new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
            logger.info("Всего файлов " + zipFiles.size());
        } catch (Exception e) {
            logger.error("Не удалось получить доступ к " + path);
            e.printStackTrace();
        }

        return zipFiles;
    }

    protected List<File> getFullZipFiles(Collection<File> zipFiles) {
        List<File> zipFullFiles = zipFiles.stream()
                .filter(s -> s.getName().toLowerCase().contains(substringForFullFiles))
                .collect(Collectors.toList());

        Collections.sort(zipFullFiles, compareByFileName);

        return zipFullFiles;
    }

    protected List<File> getUpdateZipFiles(Collection<File> zipFiles) {
        List<File> zipUpdateFiles = zipFiles.stream()
                .filter(s -> ! s.getName().toLowerCase().contains(substringForFullFiles))
                .collect(Collectors.toList());

        Collections.sort(zipUpdateFiles, compareByFileName);

        return zipUpdateFiles;
    }

    protected void deleteFile(File file) {
        if (deleteFiles) {
            file.delete();
        }
    }

    protected void renameFile(File file, Logger logger) {
        boolean success = migrationService.renameFile(file);
        if (!success) {
            logger.error("Не удалось переименовать (пометить, что загрузка прошла с ошибками) файл "+ file.getName());
        }
    }

    protected static Timestamp XMLGregorianCalendarToTimestamp(XMLGregorianCalendar xmlGregorianCalendar) {
        return new Timestamp(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());
    }

    /////////////////////////////////////////////// MIGRATIONS ////////////////////////////////////////////////////////

    protected ClsMigration getEgrulMigrationByFile(File zipFile) {
        return migrationService.getClsMigration(zipFile, ModelTypes.EGRUL_LOAD.getValue());
    }

    protected ClsMigration getEgripMigrationByFile(File zipFile) {
        return migrationService.getClsMigration(zipFile, ModelTypes.EGRIP_LOAD.getValue());
    }

    protected Boolean checkIfMigrationHasGoneSuccessfullyBefore(ClsMigration migration) {
        Boolean hasGoneSuccessfully = false;
        if (migration != null && migration.getStatus() == StatusLoadTypes.SUCCESSFULLY_LOADED.getValue()) {
            hasGoneSuccessfully = true;
        }
        return hasGoneSuccessfully;
    }

    protected ClsMigration addOrResetEgrulMigration(ClsMigration migration, File file) {

        if (migration == null) {
            migration = migrationService.addRecord(file, ModelTypes.EGRUL_LOAD.getValue(), StatusLoadTypes.LOAD_START.getValue(), "");
        } else {
            migration = migrationService.changeRecord(migration, file, ModelTypes.EGRUL_LOAD.getValue(), StatusLoadTypes.LOAD_START.getValue(), "");
        }

        return migration;
    }

    protected ClsMigration addOrResetEgripMigration(ClsMigration migration, File file) {

        if (migration == null) {
            migration = migrationService.addRecord(file, ModelTypes.EGRIP_LOAD.getValue(), StatusLoadTypes.LOAD_START.getValue(), "");
        } else {
            migration = migrationService.changeRecord(migration, file, ModelTypes.EGRIP_LOAD.getValue(), StatusLoadTypes.LOAD_START.getValue(), "");
        }

        return migration;
    }

    protected Boolean checkMigrationStatusIsLoadStart(ClsMigration migration) {
        Boolean isLoadStart = false;
        if (migration != null && migration.getStatus() == StatusLoadTypes.LOAD_START.getValue()) {
            isLoadStart = true;
        }

        return isLoadStart;
    }

    protected void changeMigrationStatusToSuccessfully(ClsMigration migration){
        migrationService.changeMigrationStatus(migration, StatusLoadTypes.SUCCESSFULLY_LOADED.getValue(), "");
    }

    protected void markMigrationAsCompletedWithError(ClsMigration migration, Exception e){
        migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), e.getMessage());
    }

    protected void markMigrationAsCompletedWithError(ClsMigration migration, String errorMessage){
        migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), errorMessage);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected Map<Long, RegEgrul> findSavedEarlierEgrul(List<EgrulContainer> list){
        final List<Long> iogrns = list.stream().map(m -> m.getRegEgrul().getIogrn()).collect(Collectors.toList());
        final List<RegEgrul> rel = regEgrulRepo.findAllByIogrnList(iogrns);
        Map<Long, RegEgrul> result = new HashMap<>();
        rel.stream().forEach(r -> {
            result.put(r.getIogrn(), r);
        });
        return result;
    }

    protected Map<Long, RegEgrip> findSavedEarlierEgrips(List<EgripContainer> list){
        final List<Long> iogrns = list.stream().map(m -> m.getRegEgrip().getIogrn()).collect(Collectors.toList());
        final List<RegEgrip> rel = regEgripRepo.findAllByIogrnList(iogrns);
        Map<Long, RegEgrip> result = new HashMap<>();
        rel.stream().forEach(r -> {
            result.put(r.getIogrn(), r);
        });
        return result;
    }

    protected Map<String, ReferenceBook> getMapReferenceBookByType(Short type) {
        Map<String, ReferenceBook> map  = new HashMap<>();
        List<ReferenceBook> list = referenceBookRepo.findAllByType(type);
        for (ReferenceBook book: list) {
            String key = book.getCode();
            map.put(key, book);
        }

        return map;
    }

    protected Map<String, Okved> getMapOkveds(){
        Map<String, Okved> okvedsMap = new HashMap<>();
        final List<Okved> okveds = okvedRepo.findAll();
        for (Okved okved: okveds) {
            String key = okved.getKindCode() + okved.getVersion();
            okvedsMap.put(key, okved);
        }

        return okvedsMap;
    }

    protected ReferenceBook createSpvz(String code, String name) {
        ReferenceBook spvz = ReferenceBook.builder()
                .code(code)
                .name(name)
                .type(ReferenceBookTypes.SPVZ.getValue())
                .status(EgrReferenceBookStatuses.ANOTHER.getValue())
                .build();
        referenceBookRepo.save(spvz);

        return spvz;
    }

    protected ReferenceBook createSulst(String code, String name) {
        ReferenceBook sulst = ReferenceBook.builder()
                .code(code)
                .name(name)
                .type(ReferenceBookTypes.SULST.getValue())
                .status(EgrReferenceBookStatuses.ANOTHER.getValue())
                .build();
        referenceBookRepo.save(sulst);

        return sulst;
    }

    protected ReferenceBook createSipst(String code, String name) {
        ReferenceBook sipst = ReferenceBook.builder()
                .code(code)
                .name(name)
                .type(ReferenceBookTypes.SIPST.getValue())
                .status(EgrReferenceBookStatuses.ANOTHER.getValue())
                .build();
        referenceBookRepo.save(sipst);

        return sipst;
    }

    protected Opf createOpf(String spr, String code, String fullName) {
        Opf opf = Opf.builder()
                .spr(spr)
                .code(code)
                .fullName(fullName)
                .build();
        return opf;
    }

    protected SvOrg createSvOrg(Short type, String code, String name, String adr) {
        SvOrg svOrg = new SvOrg();
        svOrg.setTypeOrg(type);
        svOrg.setCode(code);
        svOrg.setName(name);
        svOrg.setAdr(adr);

        return svOrg;
    }

    ///////////////////////////////////// SAVE ///////////////////////////////////////////////////////////
    protected void saveRegEgrulOkveds(List<EgrulContainer> list) {
        final List<Set<RegEgrulOkved>> reos = list.stream().map(c -> c.getRegEgrulOkved()).collect(Collectors.toList());
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

    protected void saveEgrulSvStatuses(List<EgrulContainer> list) {
        final List<Set<SvStatus>> sss = list.stream().map(c -> c.getSvStatuses()).collect(Collectors.toList());
        saveSvStatuses(sss);
    }

    protected void saveEgripSvStatuses(List<EgripContainer> list) {
        final List<Set<SvStatus>> sss = list.stream().map(c -> c.getSvStatuses()).collect(Collectors.toList());
        saveSvStatuses(sss);
    }

    protected void saveSvStatuses(List<Set<SvStatus>> sss) {
        Set<SvStatus> granula = new HashSet<>();
        int count = 1;
        for (Set<SvStatus> ss : sss) {
            if (ss != null) {
                granula.addAll(ss);
                count ++;
            }
            if (count % 10 == 0 && !granula.isEmpty()){
                svStatusRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            svStatusRepo.saveAll(granula);
        }
    }

    protected void saveOpf(List<EgrulContainer> list) {
        final List<Opf> os = list.stream().map(c -> c.getOpf()).filter(opf -> (opf.getId() != null)).collect(Collectors.toList());
        if (! os.isEmpty()) {
            opfRepo.saveAll(os);
        }
    }

    protected void saveSvFilials(Set<RegFilial> updatedFilials) {
        regFilialRepo.saveAll(updatedFilials);
    }

    protected void saveEgrulSvRecords(List<EgrulContainer> updatedData) {
        final List<Set<SvRecordEgr>> set = updatedData.stream().map(c -> c.getSvRecords()).collect(Collectors.toList());
        saveSvRecords(set);
    }

    protected void saveEgripSvRecords(List<EgripContainer> updatedData) {
        final List<Set<SvRecordEgr>> set = updatedData.stream().map(c -> c.getSvRecords()).collect(Collectors.toList());
        saveSvRecords(set);
    }

    protected void saveSvRecords(List<Set<SvRecordEgr>> set){
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

}
