package ru.sibdigital.proccovid.service;

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
import ru.sibdigital.proccovid.utils.StaxStreamProcessor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Value("${fiasZip.import.directory}")
    private String fiasZipPath;


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
    public void importZipFiasData() {
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
            loadFiasFiles(zipFiles);
        }
    }

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

    private void loadFiasFiles(Collection<File> Files) {
        for (File file : Files) {
            ZipFile zipFile = getZipFile(file);
            if (zipFile != null) {

                List<? extends ZipEntry> zipEntriesList = zipFile.stream()
                       .filter(ze -> !ze.isDirectory())
                        .filter(ze -> ze.getName().toLowerCase().endsWith(".xml"))
                       .collect(Collectors.toList());
                final Map<String, Map<String, ZipEntry>> mapForLoad = createMapForLoad(zipEntriesList);

                mapForLoad.forEach((k, v) -> processRegionData(k, v, zipFile));
            }
        }
        fiasLogger.info("Импорт ФИАС окончен");
    }

    private void processRegionData(String k, Map<String, ZipEntry> map, ZipFile zipFile)  {
        fiasLogger.info("Directory: " + k);
        try {
            ZipEntry aoe = map.get(ADDR_OBJ_SUBSTRING);
            Set<String> addrObjectSet = loadAddrObjectFile(zipFile.getInputStream(aoe));

            ZipEntry ahe = map.get(ADM_HIERARCHY_SUBSTRING);
            loadAdmHierarchyItem(zipFile.getInputStream(ahe), addrObjectSet);
        } catch (IOException e) {
            fiasLogger.error("Не удалось прочитать xml файл");
        }

    }

    private Set<String> loadAddrObjectFile(InputStream addrObjectInputStream) {
        fiasLogger.info("Inserts AddrObject. Начало");

        Set<String> addrObjectSet = new HashSet<>();
        try {
            StaxStreamProcessor processor = new StaxStreamProcessor(addrObjectInputStream);
            XMLStreamReader reader = processor.getReader();

            if (reader.hasNext()) {
                if (reader.next() == XMLEvent.START_ELEMENT) {
                    while (reader.hasNext()) {
                        addrObjectSet = (Set<String>) createAndExecuteAddrObjectInserts(reader, addrObjectSet);
                    }
                }
            }
        }
        catch (XMLStreamException e) {
            fiasLogger.error("Не удалось прочитать xml файл с ADDR_OBJECT");
        }

        fiasLogger.info("Inserts AddrObject. Конец");
        return addrObjectSet;
    }

    private void loadAdmHierarchyItem(InputStream admHierarchyInputStream, Set<String> addrObjectSet){
        try {
            StaxStreamProcessor processor = new StaxStreamProcessor(admHierarchyInputStream);

            fiasLogger.info("Inserts AdmHierarchyItem. Начало");
            XMLStreamReader reader = processor.getReader();
            if (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    while (reader.hasNext()) {
                        createAndExecuteAdmHierarchyItemInserts(reader, addrObjectSet);
                    }
                }
            }
            fiasLogger.info("Inserts AdmHierarchyItem. Конец");
        }
        catch ( XMLStreamException e) {
            fiasLogger.error("Не удалось прочитать файл c ADM_HIERARCHY");
        }
    }

    @Transactional
    Object createAndExecuteAddrObjectInserts(XMLStreamReader reader, Set<String> addrObjectSet) {
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

                                switch (attributeName) {
                                    case "isactive":
                                        if (!value.equals("1")) {
                                            loadFlag = false;
                                        }
                                        break;
                                    case "isactual":
                                        if (!value.equals("1")) {
                                            loadFlag = false;
                                        }
                                        break;
                                    case "level":
                                        if (excludedLevels.contains(value)) {
                                            loadFlag = false;
                                        }
                                        break;
                                    case "objectid":
                                        attrbValueOBJECTID = value;
                                        break;
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
                }

                return set;

            }});

        return obj;
    }

    @Transactional
    void createAndExecuteAdmHierarchyItemInserts(XMLStreamReader reader, Set<String> addrObjectSet) {
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
                }
            }});
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
}
