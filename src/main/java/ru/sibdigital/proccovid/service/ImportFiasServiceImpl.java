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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import ru.sibdigital.proccovid.utils.StaxStreamProcessor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ImportFiasServiceImpl implements ImportFiasService {

    private final static Logger fiasLogger = LoggerFactory.getLogger("fiasLogger");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Value("${fias.import.directory}")
    private String fiasPath;

    public String importData(File file) {
        try {
            importFiasData(file);
            return ("Загрузка окончена.");
        }
        catch (Exception e) {
            fiasLogger.info("Не удалось загрузить файл.");
            e.printStackTrace();
            return ("Ошибка. "+ e.getMessage());
        }
    }

    /**
     * Метод импорта данных ФИАС
     */

    private void importFiasData(File file) {
        fiasLogger.info("Импорт ФИАС начат");

        try {
            processZipFiasFile(file);
        }
        catch (Exception e ) {
            System.out.println(e);
        }


        fiasLogger.info("Импорт ФИАС окончен");
    }

    /**
     * Поиск имени таблицы в БД по имени файла и имени узла
     */
    private String findTableName(String nodeName, String filename) {
        String tableName = nodeName.toLowerCase();

        String subFileName = filename.substring(filename.indexOf("as_")+3, filename.indexOf("_2")).toLowerCase();
        switch (tableName) {
            case ("item"):
                if (subFileName.equals("address_objects_division")) {
                    tableName = "addr_obj_division_item";
                }
                else {
                    tableName = subFileName + "_item";
                }
                break;
            case ("param"):
                subFileName = subFileName.substring(0, subFileName.indexOf("_params")).toLowerCase();
                if (subFileName.equals("address_objects")) {
                    tableName = "addr_obj_param";
                }
                else {
                    tableName = subFileName + "_param";
                }
                break;
            case ("object"):
                if (subFileName.equals("address_objects")) {
                    tableName = "addr_object";
                }
                else {
                    subFileName = subFileName.substring(0, subFileName.indexOf("_obj")).toLowerCase();
                    tableName = subFileName + "_object";
                }
                break;
        }
        tableName = "fias." + tableName;
        return tableName;
    }

    /**
     * Метод получения имени ключа таблицы
     */
    private String getPrimaryKeyName(String tableName) {
        String primaryKey;
        switch (tableName) {
            case ("fias.change_history_item"):
                primaryKey = "changeid";
                break;
            case ("fias.objectlevel"):
                primaryKey = "level";
                break;
            case ("fias.reestr_object"):
                primaryKey = "objectid";
                break;
            default:
                primaryKey =  "id";
                break;
        }
        return primaryKey;
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

    /**
     * Получаем все имена колонок таблицы
     */
    private List<String> getTableColumnNames(String tablename) {
        Query query = entityManager.createNativeQuery(
                "select column_name from information_schema.columns where table_name = ?;");
        tablename = tablename.substring(5);
        query.setParameter(1, tablename);
        List<String> list = query.getResultList();
        return list;
    }

    /**
     * Метод генерации SQL query Insert
     */
    private String generateInsertQuery(Node node, String tableName, String primaryKeyName, List<String> columnNames) {
        String query = "";

        if (node.hasAttributes()) {

            String queryTemplate = "INSERT INTO %s(%s) VALUES(%s) ON CONFLICT(%s) DO UPDATE SET %s;"; // Через параметры нельзя: DO UPDATE SET ?5; - недопустимо
            NamedNodeMap nodeMap = node.getAttributes();

            String queryParams = "";
            String queryValues = "";
            String queryUpsert = "";

            for (int k = 0; k < nodeMap.getLength(); k++) {
                Node attribute = nodeMap.item(k);
                String key = attribute.getNodeName().toLowerCase();
                String value = attribute.getNodeValue();
                if (columnNames.contains(key)) {
                    queryParams += "\"" + key + "\",";
                    queryValues += "'" + value + "',";
                    queryUpsert += (key.equals("desc") ? "\"desc\"" : key) + " = EXCLUDED." + key + ",";
                }
                else {
                    fiasLogger.error("Не найдена колонка " + key + " в таблице " + tableName);
                }
            }

            // Удаляем последние (лишние) запятые
            queryParams = queryParams.substring(0, queryParams.length() - 1);
            queryValues = queryValues.substring(0, queryValues.length() - 1);
            queryUpsert = queryUpsert.substring(0, queryUpsert.length() - 1);

            query = String.format(queryTemplate, tableName, queryParams, queryValues, primaryKeyName, queryUpsert);
        }

        return query;
    }

    private void processZipFiasFile(File file) {
        ZipFile zipFile = getZipFile(file);
        if (zipFile != null) {
            try {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry zipEntry = entries.nextElement();
                    String zipEntryName = zipEntry.getName().toLowerCase();

                    fiasLogger.info("Обход файлов. Папка/файл: " + zipEntryName);

                    if (zipEntryName.length() > 5 && zipEntryName.substring(zipEntryName.length()-4).equals(".xml")) { // Проверка на формат xml
                        InputStream is = zipFile.getInputStream(zipEntry);
                        String filename = zipEntryName;

                        // Находим наименование файла
                        if (zipEntryName.lastIndexOf('/') > 0) {
                            filename = zipEntryName.substring(zipEntryName.lastIndexOf('/'));
                        }

                        // Обработка xml файла
                        processFiasFileByDOM(is, filename);
                    }
                }
            } catch (IOException e) {
                fiasLogger.error("Не удалось прочитать xml-файл из zip-файла");
                e.printStackTrace();
            }
            catch (ParserConfigurationException e) {
                fiasLogger.error("Не удалось распарсить xml-файл");
                e.printStackTrace();
            }
            catch (SAXException e) {
                fiasLogger.error("Не удалось распарсить xml-файл");
                e.printStackTrace();
            }
            finally {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Transactional
    int createAndExecuteInserts(Node root, String tableName, String primaryKeyName, List<String> finalColumnNames, int k) {

        Object obj = transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                int j = k;
                while (j < (k+50000) && j <root.getChildNodes().getLength()){
//                for (int j = 0; j < root.getChildNodes().getLength(); j++) {
                    Node node = root.getChildNodes().item(j);
                    String insertQuery = generateInsertQuery(node, tableName, primaryKeyName, finalColumnNames);
                    Query query = entityManager.createNativeQuery(insertQuery);
                    query.executeUpdate();
                    j++;
                }
                return j;
            }
        });


        return (Integer) obj;
    }

    /**
     * Метод обработки по узлам Xml-файла с помощью DOM
     */
    private void processFiasFileByDOM(InputStream is, String filename) throws ParserConfigurationException, IOException, SAXException {
        fiasLogger.info("Обработка файла " + filename);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();

        for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
            Node root = doc.getChildNodes().item(i);
            if (root.getChildNodes().getLength() > 0) {
                Node firstNode = root.getChildNodes().item(0);

                // Найдем по первому элементу наим-е таблицы, имя primary key, имена колонок таблицы
                String nodeName = firstNode.getNodeName();
                String tableName = findTableName(nodeName, filename);
                String primaryKeyName = getPrimaryKeyName(tableName);

                List<String> columnNames = getTableColumnNames(tableName);

                fiasLogger.info("Создание и выполнение inserts. Начало");
                int k = 0;
                while (k < root.getChildNodes().getLength()) {
                    k = createAndExecuteInserts(root, tableName, primaryKeyName, columnNames, k);
                }
                fiasLogger.info("Создание и выполнение inserts. Конец");

            }
        }
        fiasLogger.info("Обработка файла " + filename + " закончена");
    }




    ////////////////////////////// ЗАГРУЗКА ПОЛНАЯ //////////////////////////
    public void importFullData(){
        fiasLogger.info("Импорт ФИАС начат из " + fiasPath);
        try {
            List<Path> dirs = Files.walk(Paths.get(fiasPath))
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            for (Path dir : dirs) {
                importFiasRegionData(dir);
            }

        }
        catch (IOException e) {
            fiasLogger.error("Не удалось прочитать файлы из каталога " + fiasPath);
        }


        fiasLogger.info("Импорт ФИАС окончен");
    }

    private void importFiasRegionData(Path dir) {
        try {
            List<Path> files = Files.walk(dir, 1)
                    .filter(Files::isReadable)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".XML"))
                    .collect(Collectors.toList());

            if (files.size() > 0) {
                Path addrObjectFilePath = null;
                Path admHierarchyFilePath = null;

                for (Path filepath : files) {
                    if (filepath.getFileName().toString().startsWith("AS_ADDR_OBJ_2")) {
                        addrObjectFilePath = filepath;
                    } else if (filepath.getFileName().toString().startsWith("AS_ADM_HIERARCHY_2")) {
                        admHierarchyFilePath = filepath;
                    }
                }
                if (addrObjectFilePath != null && addrObjectFilePath != null) {
                    fiasLogger.info(dir.toString());
                    loadRegionData(addrObjectFilePath, admHierarchyFilePath);
                }
            }

        } catch (IOException e) {
            fiasLogger.error("Не удалось прочитать файлы из каталога " + dir);
        }

    }

    private void loadRegionData(Path addrObjectFilePath, Path admHierarchyFilePath) {
        Set<String> addrObjectSet = loadAddrObjectFile(addrObjectFilePath);
        loadAdmHierarchyItem(admHierarchyFilePath, addrObjectSet);
    }

    ///////////// ЗАГРУЗКА addr_obj ////////////////////
    private Set<String> loadAddrObjectFile(Path addrObjectFilePath) {
        Set<String> addrObjectSet = new HashSet<>();
        try {
            StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(addrObjectFilePath));

            // Исх. данные
            fiasLogger.info("Создание и выполнение inserts AddrObject. Начало");
            int k = 0;
            XMLStreamReader reader = processor.getReader();
            if (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && "ADDRESSOBJECTS".equals(reader.getLocalName())) {
                    while (reader.hasNext()) {
                        addrObjectSet = (Set<String>) createAndExecuteAddrObjectInserts(reader, addrObjectSet);
                    }
                }
            }

            fiasLogger.info("Создание и выполнение inserts AddrObject. Конец");
        }
        catch (IOException | XMLStreamException e) {
            fiasLogger.error("Не удалось прочитать файл " + addrObjectFilePath);
        }

        return addrObjectSet;
    }

    @Transactional
    Object createAndExecuteAddrObjectInserts(XMLStreamReader reader, Set<String> addrObjectSet) {
        Object obj = transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                String tableName = "fias.addr_object";
                Set<String> set = addrObjectSet;
                Set<String> excludedAttributes = getExcludedAttributes();
                Set<String> excludedLevels = getExcludedLevels();

                int j = 0;
                try {
                    while (reader.hasNext() && j < 50000) {
                        reader.next();
                        if (reader.getEventType() == XMLEvent.START_ELEMENT && "OBJECT".equals(reader.getLocalName())) {

                            boolean loadFlag = true;
                            int i = 0;
                            String queryParams = "";
                            String queryValues = "";

                            String attrbValueOBJECTID = "";

                            while (loadFlag && i < reader.getAttributeCount()) {
                                String attributeName = reader.getAttributeLocalName(i);
                                String value = reader.getAttributeValue(i);
                                switch (attributeName) {
                                    case "ISACTIVE":
                                        if (!value.equals("1")) {
                                            loadFlag = false;
                                        }
                                        break;
                                    case "ISACTUAL":
                                        if (!value.equals("1")) {
                                            loadFlag = false;
                                        }
                                        break;
                                    case "LEVEL":
                                        if (excludedLevels.contains(value)) {
                                            loadFlag = false;
                                        }
                                        break;
                                    case "OBJECTID":
                                        attrbValueOBJECTID = value;
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
                            if (loadFlag) {
                                set.add(attrbValueOBJECTID);
                                queryParams = queryParams.substring(0, queryParams.length() - 1);
                                queryValues = queryValues.substring(0, queryValues.length() - 1);

                                String insertQuery = String.format("INSERT INTO %s(%s) VALUES(%s);", tableName, queryParams, queryValues);
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

    ///////////// ЗАГРУЗКА adm_hierarchy_item ////////////////////
    private void loadAdmHierarchyItem(Path admHierarchyFilePath, Set<String> addrObjectSet){
        try {
            StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(admHierarchyFilePath));

            // Исх. данные
            fiasLogger.info("Создание и выполнение inserts AdmHierarchyItem. Начало");
            int k = 0;
            XMLStreamReader reader = processor.getReader();
            if (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && "ITEMS".equals(reader.getLocalName())) {
                    while (reader.hasNext()) {
                        createAndExecuteAdmHierarchyItemInserts(reader, addrObjectSet);
                    }
                }
            }

            fiasLogger.info("Создание и выполнение inserts AdmHierarchyItem. Конец");
        }
        catch (IOException | XMLStreamException e) {
            fiasLogger.error("Не удалось прочитать файл " + admHierarchyFilePath);
        }
    }

    @Transactional
    void createAndExecuteAdmHierarchyItemInserts(XMLStreamReader reader, Set<String> addrObjectSet) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                String tableName = "fias.adm_hierarchy_item";
                Set<String> excludedAttributes = getExcludedAttributes();

                int j = 0;
                try {
                    while (reader.hasNext() && j < 1000) {
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

                                String insertQuery = String.format("INSERT INTO %s(%s) VALUES(%s);", tableName, queryParams, queryValues);
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
        set.add("objectguid".toUpperCase());
        set.add("changeid".toUpperCase());
        set.add("previd".toUpperCase());
        set.add("nextid".toUpperCase());
        set.add("updatedate".toUpperCase());
        set.add("startdate".toUpperCase());
        set.add("enddate".toUpperCase());
        set.add("isactual".toUpperCase());
        set.add("isactive".toUpperCase());
        set.add("createdate".toUpperCase());

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

    private void processFiasFileByStax(InputStream is, String filename) throws ParserConfigurationException, IOException, SAXException {
        fiasLogger.info("Обработка файла " + filename);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();

        for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
            Node root = doc.getChildNodes().item(i);
            if (root.getChildNodes().getLength() > 0) {
                Node firstNode = root.getChildNodes().item(0);

                // Найдем по первому элементу наим-е таблицы, имя primary key, имена колонок таблицы
                String nodeName = firstNode.getNodeName();
                String tableName = findTableName(nodeName, filename);
                String primaryKeyName = getPrimaryKeyName(tableName);

                List<String> columnNames = getTableColumnNames(tableName);

                fiasLogger.info("Создание и выполнение inserts. Начало");
                int k = 0;
                while (k < root.getChildNodes().getLength()) {
                    k = createAndExecuteInserts(root, tableName, primaryKeyName, columnNames, k);
                }
                fiasLogger.info("Создание и выполнение inserts. Конец");

            }
        }
        fiasLogger.info("Обработка файла " + filename + " закончена");
    }

}
