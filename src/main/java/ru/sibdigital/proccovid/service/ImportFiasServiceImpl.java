package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import ru.sibdigital.proccovid.model.ClsMigration;
import ru.sibdigital.proccovid.model.ModelTypes;
import ru.sibdigital.proccovid.model.StatusLoadTypes;
import ru.sibdigital.proccovid.repository.ClsMigrationRepo;
import ru.sibdigital.proccovid.repository.fias.AddrObjectRepo;
import ru.sibdigital.proccovid.utils.StaxStreamProcessor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ImportFiasServiceImpl implements ImportFiasService {

    private final static Logger fiasLogger = LoggerFactory.getLogger("fiasLogger");

    private final Integer MAX_NUM_QUERIES = 10000;
    private final String ADDR_OBJ_SUBSTRING = "AS_ADDR_OBJ_2";
    private final String ADM_HIERARCHY_SUBSTRING = "AS_ADM_HIERARCHY_2";
    private final String ADM_HIERARCHY_TABLENAME = "fias.adm_hierarchy_item";
    private final String ADDR_OBJ_TABLENAME = "fias.addr_object";
    private final Integer LOAD_VER_FULL = 0;
    private final Integer LOAD_VER_UPDATE = 1;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private AddrObjectRepo addrObjectRepo;

    @Autowired
    private MigrationService migrationService;

    @Value("${fiasZip.import.directory}")
    private String fiasZipPath;

    @Value("${fias.validate.delete}")
    private Boolean deleteFiles;


    private static ZipFile getZipFile(File file) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile;
    }

    /////////////////////////// ЗАГРУЗКА ZIP ЗАГРУЗКА ADDR_OBJ и ADM_HIERARCHY FULL //////////////////
    public void importZipFullFiasData() {
        fiasLogger.info("Импорт ФИАС начат из " + fiasZipPath);

        Collection<File> zipFiles = null;
        try {
            zipFiles = FileUtils.listFiles(new File(fiasZipPath),
                    new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
        } catch (Exception e) {
            fiasLogger.error("Не удалось получить доступ к " + fiasZipPath);
            e.printStackTrace();
        }

        if (zipFiles != null && !zipFiles.isEmpty()) {
            loadFiasFiles(zipFiles, LOAD_VER_FULL);
        }
    }

    /////////////////////////// ЗАГРУЗКА ZIP ADDR_OBJ и ADM_HIERARCHY UPDATES ////////////////////////

    public void importZipUpdatesFiasData() {
        fiasLogger.info("Импорт обновлений ФИАС начат из " + fiasZipPath);

        Collection<File> zipFiles = null;
        try {
            zipFiles = FileUtils.listFiles(new File(fiasZipPath),
                    new RegexFileFilter("^(.*?)"), DirectoryFileFilter.DIRECTORY);
        } catch (Exception e) {
            fiasLogger.error("Не удалось получить доступ к " + fiasZipPath);
            e.printStackTrace();
        }

        if (zipFiles != null && !zipFiles.isEmpty()) {
            loadFiasFiles(zipFiles, LOAD_VER_UPDATE);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    private Map.Entry<String, ZipEntry> createEntry(ZipEntry zipEntry){
        String filename = zipEntry.getName().toUpperCase();
        Map.Entry<String, ZipEntry>  me = null;
        if (filename.contains(ADDR_OBJ_SUBSTRING)) {
            me = Map.entry(ADDR_OBJ_SUBSTRING, zipEntry);
        } else if (filename.contains(ADM_HIERARCHY_SUBSTRING)) {
            me = Map.entry(ADM_HIERARCHY_SUBSTRING, zipEntry);
        }
        return me;
    }

    private Map<String, Map<String, ZipEntry>> createMapForLoad(List<? extends ZipEntry> zipEntriesList){
        Map<String, Map<String, ZipEntry>> map = new TreeMap<>();
        for (ZipEntry zipEntry: zipEntriesList ) {
            final String zen = zipEntry.getName();
            final int lastIndex = zen.lastIndexOf('/');
            if (lastIndex != -1) {
                final String dir = zen.substring(0, lastIndex);
                Map<String, ZipEntry> zeDir = map.get(dir);
                Map.Entry<String, ZipEntry> me = createEntry(zipEntry);
                if (me != null) {
                    if (zeDir == null) {
                        Map<String, ZipEntry> entr = new HashMap<>();
                        entr.put(me.getKey(), me.getValue());
                        map.put(dir, entr);
                    } else {
                        zeDir.put(me.getKey(), me.getValue());
                    }
                }
            }
        }
        return map;
    }

    private void loadFiasFiles(Collection<File> fileCollection, Integer loadVersion) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH_mm");

        ArrayList<File> files = new ArrayList<File>(fileCollection);
        files.sort(Comparator.comparingLong(File::lastModified));

        for (File file : files) {
            fiasLogger.info("File: " + file.getName());

            ClsMigration migration = migrationService.getClsMigration(file, ModelTypes.FIAS_LOAD.getValue());
            // Если файл еще не обработан
            if (migration == null || migration.getStatus() != StatusLoadTypes.SUCCESSFULLY_LOADED.getValue()) {

                // Добавить запись об обработке файла
                migration = migrationService.addMigrationRecord(migration, file, ModelTypes.FIAS_LOAD.getValue(), StatusLoadTypes.LOAD_START.getValue(), "");

                // Обработать файл
                ZipFile zipFile = getZipFile(file);
                if (zipFile != null) {

                    List<? extends ZipEntry> zipEntriesList = zipFile.stream()
                            .filter(ze -> !ze.isDirectory())
                            .filter(ze -> ze.getName().toLowerCase().endsWith(".xml"))
                            .collect(Collectors.toList());
                    final Map<String, Map<String, ZipEntry>> mapForLoad = createMapForLoad(zipEntriesList);

//                    mapForLoad.forEach((k, v) -> processRegionData(k, v, zipFile, loadVersion, file)); // Нельзя передать migration в lambda
                    for (Map.Entry<String, Map<String, ZipEntry>> entry : mapForLoad.entrySet()){
                        processRegionData(entry.getKey(), entry.getValue(), zipFile, loadVersion, migration);
                    }

                }

                // Изменить запись о статусе обработки файла
                if (migration.getStatus() == StatusLoadTypes.LOAD_START.getValue()) {
                    migrationService.changeMigrationStatus(migration, StatusLoadTypes.SUCCESSFULLY_LOADED.getValue(), "");

                    if (deleteFiles) {
                        try {
                            zipFile.close();
                            file.delete();
                        }
                        catch (Exception e) {
                            fiasLogger.error("Не удалось удалить файл "+ file.getName());
                        }
                    }
                }
                else { // загрузка файла прошла с ошибками. Переименовать файл.
                    try {
                        zipFile.close();
                        String filename = file.getName();
                        String fileTime = sdf.format(file.lastModified());
                        File newFile = new File(file.getParent(), String.format("%s_%s_error%s", getFileNameWithoutExtension(filename), fileTime, getFileExtension(filename)));
                        boolean success = file.renameTo(newFile);
                        if (!success) {
                            fiasLogger.error("Не удалось переименовать (пометить, что загрузка прошла с ошибками) файл "+ file.getName());
                        }
                    }
                    catch (Exception e) {
                        fiasLogger.error("Не удалось переименовать (пометить, что загрузка прошла с ошибками) файл "+ file.getName());
                    }
                }
            }
            else {
                fiasLogger.info(file.getName() + " уже был обработан.");
            }

        }

        fiasLogger.info("Импорт ФИАС окончен");
    }

    private void processRegionData(String k, Map<String, ZipEntry> map, ZipFile zipFile, Integer loadVersion, ClsMigration migration)  {
        fiasLogger.info("Directory: " + k);
        try {
            ZipEntry aoe = map.get(ADDR_OBJ_SUBSTRING);
            Set<String> addrObjectSet = loadAddrObjectFile(zipFile.getInputStream(aoe), migration);

            ZipEntry ahe = map.get(ADM_HIERARCHY_SUBSTRING);
            loadAdmHierarchyItem(zipFile.getInputStream(ahe), addrObjectSet, loadVersion, migration);
        } catch (IOException e) {
            fiasLogger.error("Не удалось прочитать xml файл");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать xml файл");
        }

    }

    private Set<String> loadAddrObjectFile(InputStream addrObjectInputStream, ClsMigration migration) {
        fiasLogger.info("Inserts AddrObject. Начало");

        Set<String> addrObjectSet = new HashSet<>();
        try {
            StaxStreamProcessor processor = new StaxStreamProcessor(addrObjectInputStream);
            XMLStreamReader reader = processor.getReader();

            if (reader.hasNext()) {
                if (reader.next() == XMLEvent.START_ELEMENT) {
                    while (reader.hasNext()) {
                        addrObjectSet = (Set<String>) createAndExecuteAddrObjectInserts(reader, addrObjectSet, migration);
                    }
                }
            }
            reader.close();
        }
        catch (XMLStreamException e) {
            fiasLogger.error("Не удалось прочитать xml файл с ADDR_OBJECT");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать xml файл с ADDR_OBJECT");
        }

        fiasLogger.info("Inserts AddrObject. Конец");
        return addrObjectSet;
    }

    private void loadAdmHierarchyItem(InputStream admHierarchyInputStream, Set<String> addrObjectSet, Integer loadVersion, ClsMigration migration){
        try {
            StaxStreamProcessor processor = new StaxStreamProcessor(admHierarchyInputStream);

            fiasLogger.info("Inserts AdmHierarchyItem. Начало");
            XMLStreamReader reader = processor.getReader();
            if (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    while (reader.hasNext()) {
                        if (loadVersion == LOAD_VER_FULL) {
                            createAndExecuteAdmHierarchyItemInsertsFULL(reader, addrObjectSet, migration);
                        }
                        else if (loadVersion == LOAD_VER_UPDATE) {
                            createAndExecuteAdmHierarchyItemInsertsUPDATE(reader, migration);
                        }
                    }
                }
            }
            reader.close();
            fiasLogger.info("Inserts AdmHierarchyItem. Конец");
        }
        catch ( XMLStreamException e) {
            fiasLogger.error("Не удалось прочитать файл c ADM_HIERARCHY");
            migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать файл c ADM_HIERARCHY");
        }
    }

    @Transactional
    Object createAndExecuteAddrObjectInserts(XMLStreamReader reader, Set<String> addrObjectSet, ClsMigration migration) {
        Object obj = transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                String tableName = ADDR_OBJ_TABLENAME;
                Set<String> set = addrObjectSet;
                Set<String> excludedAttributes = getExcludedAttributes();
                Set<String> excludedLevels = getExcludedLevels();

                int j = 0;
                try {
                    while (reader.hasNext() && j < MAX_NUM_QUERIES) {
                        reader.next();
                        if (reader.getEventType() == XMLEvent.START_ELEMENT) {
                            String queryParams = "";
                            String queryValues = "";
                            String attrbValueOBJECTID = "";

                            boolean loadFlag = true;
                            int i = 0;
                            while (loadFlag && i < reader.getAttributeCount()) {
                                String attributeName = reader.getAttributeLocalName(i).toLowerCase();
                                String value = reader.getAttributeValue(i);

                                if ((attributeName.equals("isactive") || attributeName.equals("isactual")) && !value.equals("1")) {
                                    loadFlag = false;
                                }
                                else if (attributeName.equals("level") && excludedLevels.contains(value)) {
                                    loadFlag = false;
                                }
                                else  if (attributeName.equals("objectid")) {
                                    attrbValueOBJECTID = value;
                                }

                                if (!excludedAttributes.contains(attributeName)) {
                                    queryParams += "\"" + attributeName + "\",";
                                    if (value.contains("\'")) {
                                        value = value.replace("\'", "\"");
                                    }
                                    queryValues += "\'" + value + "\',";
                                }
                                i++;
                            }
                            if (loadFlag) {
                                set.add(attrbValueOBJECTID);
                                queryParams = queryParams.substring(0, queryParams.length() - 1);
                                queryValues = queryValues.substring(0, queryValues.length() - 1);

                                String insertQuery = String.format("INSERT INTO %s(%s) VALUES(%s) ON CONFLICT(id) DO NOTHING;", tableName, queryParams, queryValues);
                                Query query = entityManager.createNativeQuery(insertQuery);
                                query.executeUpdate();
                                j++;
                            }
                        }
                    }
                } catch (XMLStreamException e) {
                    fiasLogger.error("Не удалось прочитать xml");
                    migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать xml файл с ADDR_OBJECT");
                }

                return set;

            }});

        return obj;
    }

    @Transactional
    void createAndExecuteAdmHierarchyItemInsertsFULL(XMLStreamReader reader, Set<String> addrObjectSet, ClsMigration migration) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                String tableName = ADM_HIERARCHY_TABLENAME;
                Set<String> excludedAttributes = getExcludedAttributes();

                int j = 0;
                try {
                    while (reader.hasNext() && j < MAX_NUM_QUERIES) {
                        reader.next();
                        if (reader.getEventType() == XMLEvent.START_ELEMENT && "ITEM".equals(reader.getLocalName())) {
                            boolean loadFlag = true;
                            int i = 0;
                            String queryParams = "";
                            String queryValues = "";

                            boolean inAddrObj = false;

                            while (loadFlag && i < reader.getAttributeCount()) {
                                String attributeName = reader.getAttributeLocalName(i);
                                String value = reader.getAttributeValue(i);
                                switch (attributeName) {
                                    case "ISACTIVE":
                                        if (!value.equals("1")) {
                                            loadFlag = false;
                                        }
                                        break;
                                    case "PARENTOBJID":
                                        if (addrObjectSet.contains(value)) {
                                            inAddrObj = true;
                                        }
                                        break;
                                    case "OBJECTID":
                                        if (addrObjectSet.contains(value)) {
                                            inAddrObj = true;
                                        }
                                        break;
                                }

                                if (!excludedAttributes.contains(attributeName)) {
                                    queryParams += "\"" + attributeName.toLowerCase() + "\",";
                                    if (value.contains("\'")) {
                                        value = value.replace("\'", "\"");
                                    }
                                    queryValues += "\'" + value + "\',";
                                }
                                i++;
                            }
                            if (loadFlag && inAddrObj) {
                                queryParams = queryParams.substring(0, queryParams.length() - 1);
                                queryValues = queryValues.substring(0, queryValues.length() - 1);

                                String insertQuery = String.format("INSERT INTO %s(%s) VALUES(%s) ON CONFLICT(id) DO NOTHING;", tableName, queryParams, queryValues);
                                Query query = entityManager.createNativeQuery(insertQuery);
                                query.executeUpdate();
                                j++;
                            }
                        }
                    }
                } catch (XMLStreamException e) {
                    fiasLogger.error("Не удалось прочитать xml");
                    migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать файл c ADM_HIERARCHY");
                }
            }});
    }

    @Transactional
    void createAndExecuteAdmHierarchyItemInsertsUPDATE(XMLStreamReader reader, ClsMigration migration) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                String tableName = ADM_HIERARCHY_TABLENAME;
                Set<String> excludedAttributes = getExcludedAttributes();

                int j = 0;
                try {
                    while (reader.hasNext() && j < MAX_NUM_QUERIES) {
                        reader.next();
                        if (reader.getEventType() == XMLEvent.START_ELEMENT && "ITEM".equals(reader.getLocalName())) {
                            int i = 0;
                            String queryParams = "";
                            String queryValues = "";

                            boolean loadFlag = true;
                            boolean inAddrObj = false;

                            while (loadFlag && i < reader.getAttributeCount()) {
                                String attributeName = reader.getAttributeLocalName(i).toLowerCase();
                                String value = reader.getAttributeValue(i);

                                if (attributeName.equals("isactive")) {
                                    loadFlag = value.equals("1");
                                }

                                if ((attributeName.equals("parentobjid") || attributeName.equals("objectid")) && isValueInAddrObj(value)) {
                                    inAddrObj = true;
                                }

                                if (!excludedAttributes.contains(attributeName)) {
                                    queryParams += "\"" + attributeName + "\",";
                                    if (value.contains("\'")) {
                                        value = value.replace("\'", "\"");
                                    }
                                    queryValues += "\'" + value + "\',";
                                }
                                i++;
                            }
                            if (loadFlag && inAddrObj) {
                                queryParams = queryParams.substring(0, queryParams.length() - 1); // удаление посл. запятых
                                queryValues = queryValues.substring(0, queryValues.length() - 1);

                                String insertQuery = String.format("INSERT INTO %s(%s) VALUES(%s) ON CONFLICT(id) DO NOTHING;", tableName, queryParams, queryValues);
                                Query query = entityManager.createNativeQuery(insertQuery);
                                query.executeUpdate();
                                j++;
                            }
                        }
                    }
                } catch (XMLStreamException e) {
                    fiasLogger.error("Не удалось прочитать xml");
                    migrationService.changeMigrationStatus(migration, StatusLoadTypes.COMPLETED_WITH_ERRORS.getValue(), "Не удалось прочитать файл c ADM_HIERARCHY");
                }
            }});
    }

    private Boolean isValueInAddrObj(String value) {
        Long objectid = Long.parseLong(value);
        return (addrObjectRepo.findAddrObjectByObjectid(objectid) != null);
    }

    private Set<String> getExcludedAttributes(){
        Set<String> set = new HashSet<>();
        set.add("objectguid");
        set.add("changeid");
        set.add("previd");
        set.add("nextid");
        set.add("updatedate");
        set.add("startdate");
        set.add("enddate");
        set.add("isactual");
        set.add("isactive");
        set.add("createdate");

        return set;
    }

    private Set<String> getExcludedLevels(){
        Set<String> set = new HashSet<>();
        set.add("9");
        set.add("11");
        set.add("12");
        set.add("13");
        set.add("14");
        set.add("15");
        set.add("16");
        set.add("17");

        return set;
    }


    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    private String getFileNameWithoutExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(0, lastIndexOf);
    }


}
