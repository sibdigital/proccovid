package ru.sibdigital.proccovid.service;

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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ImportFiasServiceImpl implements ImportFiasService {

    private final static Logger fiasLogger = LoggerFactory.getLogger("fiasLogger");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

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
