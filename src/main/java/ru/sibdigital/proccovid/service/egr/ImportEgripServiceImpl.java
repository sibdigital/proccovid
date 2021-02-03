package ru.sibdigital.proccovid.service.egr;

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
import ru.sibdigital.proccovid.dto.EgripNodes;
import ru.sibdigital.proccovid.dto.egrip040501.ВидЗапТип;
import ru.sibdigital.proccovid.dto.egrip040501.СвОКВЭДТип;
import ru.sibdigital.proccovid.dto.egrip040501.Файл;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.model.egr.*;

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
public class ImportEgripServiceImpl extends SuperEgrServiceImpl implements ImportEgripService{

    private final static Logger egripLogger = LoggerFactory.getLogger("egripLogger");

    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, Okved> okvedsMap = new HashMap<>();
    private Map<String, ReferenceBook> sipstMap  = new HashMap<>();
    private Map<String, ReferenceBook> spvzMap  = new HashMap<>();

    private void fillMaps() {
        okvedsMap = getMapOkveds();
        sipstMap = getMapReferenceBookByType(ReferenceBookTypes.SIPST.getValue());
        spvzMap = getMapReferenceBookByType(ReferenceBookTypes.SPVZ.getValue());
    }

    public void importEgripData() {
        egripLogger.info("Импорт ЕГРИП начат из " + egripPath);

        fillMaps();
        Collection<File> zipFiles = getZipFilesFromDirectory(egripPath, egripLogger);

        // Загрузка ПОЛНОГО zip
        List<File> zipFullFiles = getFullZipFiles(zipFiles);
        egripLogger.info("Всего файлов ПОЛНОГО " + zipFullFiles.size());

        if (zipFullFiles != null && !zipFullFiles.isEmpty()) {
            loadEGRIPFiles(zipFullFiles);
        }

        // Загрузка ОБНОВЛЕНИЙ
        List<File> zipUpdateFiles = getUpdateZipFiles(zipFiles);
        egripLogger.info("Всего файлов ОБНОВЛЕНИЙ " + zipUpdateFiles.size());

        if (zipUpdateFiles != null && !zipUpdateFiles.isEmpty()) {
            loadEGRIPFiles(zipUpdateFiles);
        }

        egripLogger.info("Импорт ЕГРИП окончен");
    }

    private void loadEGRIPFiles(List<File> zipFiles) {
        for (File zipFile : zipFiles) {
            ClsMigration migration = getEgripMigrationByFile(zipFile);
            if (!checkIfMigrationHasGoneSuccessfullyBefore(migration)) {
                // Добавить запись об обработке файла
                migration = addOrResetEgripMigration(migration, zipFile);

                // Обработать данные zip
                processEgripFile(zipFile, migration);

                // Изменить запись о статусе обработки файла
                if (checkMigrationStatusIsLoadStart(migration)) {
                    changeMigrationStatusToSuccessfully(migration);
                    deleteFile(zipFile);
                } else {
                    renameFile(zipFile, egripLogger);
                }
            }
        }
    }

    private void processEgripFile(File file, ClsMigration migration) {
        egripLogger.info("Обработка файла " + file.getName() + " " + new Date());

        ZipFile zipFile = getZipFile(file);
        if (zipFile != null) {
            Unmarshaller unmarshaller = getUnmarshaller(Файл.class);
            if (unmarshaller != null) {
                try {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = entries.nextElement();
                        saveEgripData(zipFile, zipEntry, unmarshaller, migration);
                    }
                } catch (Exception e) {
                    egripLogger.error(e.getMessage());
                    markMigrationAsCompletedWithError(migration, e);
                    e.printStackTrace();
                } finally {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        markMigrationAsCompletedWithError(migration, e);
                        e.printStackTrace();
                    }
                }
            } else {
                egripLogger.error("Не удалось создать демаршаллизатор");
                markMigrationAsCompletedWithError(migration, "Не удалось создать демаршаллизатор");
            }
        } else {
            egripLogger.error("Не удалось прочитать zip-файл");
            markMigrationAsCompletedWithError(migration, "Не удалось прочитать zip-файл");
        }
        egripLogger.info("Окончание обработки файла " + file.getName() + " " + new Date());
    }

    @Transactional
    boolean saveEgripData(ZipFile zipFile, ZipEntry zipEntry, Unmarshaller unmarshaller, ClsMigration migration) {
        egripLogger.info("Обработка xml файла " + zipEntry.getName());
        boolean result = false;

        InputStream is = null;
        try {
            is = zipFile.getInputStream(zipEntry);
            Файл egrip = (Файл) unmarshaller.unmarshal(is);

            List<EgripContainer> list = parseEgripData(egrip, migration);
            saveEgrips(list);

            result = true;
        } catch (IOException e) {
            egripLogger.error("Не удалось прочитать xml-файл из zip-файла");
            markMigrationAsCompletedWithError(migration, "Не удалось прочитать xml-файл из zip-файла");
            e.printStackTrace();
        } catch (JAXBException e) {
            egripLogger.error("Не удалось демаршализовать xml-файл из zip-файла");
            markMigrationAsCompletedWithError(migration, "Не удалось демаршализовать xml-файл из zip-файла");
            e.printStackTrace();
        }
        return result;
    }

    private List<EgripContainer> parseEgripData(Файл egrip, ClsMigration migration) {

        List<EgripContainer> containerList = new ArrayList<>();
        for (Файл.Документ документ : egrip.getДокумент()) {
            Файл.Документ.СвИП свИП = документ.getСвИП();

            EgripNodes egripNodes = new EgripNodes(свИП);
            RegEgrip newRegEgrip = createRegEgrip(свИП, migration);
            EgripContainer ec = createEgripContainer(newRegEgrip, egripNodes);

            try {
                newRegEgrip.setData(getDataWNullNodes(свИП));
            } catch (JsonProcessingException e) {
                egripLogger.error("Не удалось преобразовать данные к JSON для ОГРН " + свИП.getОГРНИП());
                e.printStackTrace();
            }
            containerList.add(ec);
        }
        return containerList;
    }

    private void saveEgrips(List<EgripContainer> list){

        List<EgripContainer> updatedData = getContainersToUpdate(list);
        List<Long> deletedNodes = updatedData.stream()
                .map(ec -> ec.getRegEgrip().getId())
                .filter(id -> (id != null))
                .collect(Collectors.toList());

        if (!deletedNodes.isEmpty()) {
            regEgripOkvedRepo.deleteRegEgrulOkveds(deletedNodes);
            svRegRepo.deleteSvRegsByIdEgrips(deletedNodes);
            svRecordEgrRepo.deleteSvRecordEgrsByIdEgrips(deletedNodes);
            svStatusRepo.deleteSvStatusesByIdEgrips(deletedNodes);
        }

        final List<RegEgrip> rel = updatedData.stream().map(c -> c.getRegEgrip()).collect(Collectors.toList());
        regEgripRepo.saveAll(rel);

        saveRegEgripOkveds(updatedData);
        saveSvOrgAndReg(updatedData);
        saveEgripSvStatuses(updatedData);
        saveEgripSvRecords(updatedData);
    }

    ////////////////////////////////////////////////// PARSE DATA //////////////////////////////////////////////////////

    private Set<RegEgripOkved> parseEgripOkveds(Файл.Документ.СвИП.СвОКВЭД свОКВЭД, RegEgrip newRegEgrip){
        Set<RegEgripOkved> regEgripOkveds = new HashSet<>();
        if (свОКВЭД != null) {
            if (свОКВЭД.getСвОКВЭДОсн() != null) {
                String ver = свОКВЭД.getСвОКВЭДОсн().getПрВерсОКВЭД() != null ? свОКВЭД.getСвОКВЭДОсн().getПрВерсОКВЭД() : "2001";
                String okey = свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + ver;
                final Okved okved = okvedsMap.get(okey);
                if (okved != null) {
                    RegEgripOkved reo = new RegEgripOkved();
                    reo.setMain(true);
                    reo.setRegEgrip(newRegEgrip);
                    reo.setIdOkved(okved.getIdSerial());
                    regEgripOkveds.add(reo);
                } else {
                    egripLogger.error("ОКВЭД" + свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + "версии " + ver + " не найден для ОГРН " + newRegEgrip.getOgrn());
                }
            }
            if (свОКВЭД.getСвОКВЭДДоп() != null) {
                for (СвОКВЭДТип свОКВЭДТип : свОКВЭД.getСвОКВЭДДоп()) {
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
                        egripLogger.error("ОКВЭД" + свОКВЭДТип.getКодОКВЭД() + "версии " + version + " не найден для ОГРН " + newRegEgrip.getOgrn());
                    }
                }

            }
        }
        return regEgripOkveds;
    }

    private SvStatus parseEgripStatuses(Файл.Документ.СвИП.СвСтатус статус, RegEgrip newRegEgrip) {
        String exclDecNum = null, journalNum = null, grnCorr = null;
        Date exclDecDate = null, publDate = null, recordDateCorr = null;

        if (статус.getСвРешИсклИП() != null) {
            exclDecDate = XMLGregorianCalendarToTimestamp(статус.getСвРешИсклИП().getДатаРеш());
            exclDecNum = статус.getСвРешИсклИП().getНомерРеш();
            publDate = XMLGregorianCalendarToTimestamp(статус.getСвРешИсклИП().getДатаПубликации());
            journalNum = статус.getСвРешИсклИП().getНомерЖурнала();
        }

        Файл.Документ.СвИП.СвСтатус.СвСтатус1 свСтатус1 = статус.getСвСтатус();
        ReferenceBook sipst = null;
        if (свСтатус1 != null) {
            sipst = sipstMap.get(свСтатус1.getКодСтатус());
            if (sipst == null) {
                sipst = createSipst(свСтатус1);
            }
        }


        SvStatus svStatus = SvStatus.builder()
                .egrip(newRegEgrip)
                .referenceBook(sipst)
                .exclDecDate(exclDecDate)
                .exclDecNum(exclDecNum)
                .publDate(publDate)
                .journalNum(journalNum)
                .grn(статус.getГРНИПДата().getГРНИП())
                .recordDate(XMLGregorianCalendarToTimestamp(статус.getГРНИПДата().getДатаЗаписи()))
                .grnCorr(grnCorr)
                .recordDateCorr(recordDateCorr)
                .build();


        return svStatus;
    }

    private Set<SvRecordEgr> parseSvRecords(List<Файл.Документ.СвИП.СвЗапЕГРИП> свЗапЕГРЮЛList, RegEgrip newRegEgrip) throws JsonProcessingException{
        Set<SvRecordEgr> svRecords = new HashSet<>();
        for (Файл.Документ.СвИП.СвЗапЕГРИП свЗапЕГРИП : свЗапЕГРЮЛList) {
            SvRecordEgr svRecordEgr = createSvRecordEgr(свЗапЕГРИП);
            svRecordEgr.setEgrip(newRegEgrip);

            свЗапЕГРИП.setВидЗап(null);
            свЗапЕГРИП.setИдЗап(null);
            свЗапЕГРИП.setДатаЗап(null);

            svRecordEgr.setData(mapper.writeValueAsString(свЗапЕГРИП));

            svRecords.add(svRecordEgr);
        }

        return svRecords;
    }

//////////////////////////////////////GET VALUES, FILTER, FIND ELEMENTS  ///////////////////////////////////////////////
    private List<EgripContainer> getContainersToUpdate(List<EgripContainer> list) {
    Map<Long, RegEgrip> earlier = findSavedEarlierEgrips(list);
    List<EgripContainer> updatedData = new ArrayList<>();

    if (!earlier.isEmpty()) {
        for (EgripContainer ec : list) {
            RegEgrip r = ec.getRegEgrip();
            RegEgrip earl = earlier.get(r.getIogrn());
            if (earl != null) {
                // Производить замену, только если СвЮЛ.ДатаВып больше date_actual записи таблицы
                if (r.getDateActual().after(earl.getDateActual())) {
                    updatedData.add(ec);
                    r.setId(earl.getId());
                }
            }
            else {
                updatedData.add(ec);
            }
        }
    } else {
        updatedData = list;
    }

    return updatedData;
}

    private SvOrg getSvOrg(Object obj) {
        Short type = null;
        String code = null, name = null, adr = null;
        if (obj instanceof Файл.Документ.СвИП.СвРегОрг) {
            Файл.Документ.СвИП.СвРегОрг свРегОрг = (Файл.Документ.СвИП.СвРегОрг) obj;
            type = SvRegType.NALOG.getValue();
            code = свРегОрг.getКодНО();
            name = свРегОрг.getНаимНО();
            adr = свРегОрг.getАдрРО();
        } else if (obj instanceof Файл.Документ.СвИП.СвРегПФ) {
            Файл.Документ.СвИП.СвРегПФ свРегПФ = (Файл.Документ.СвИП.СвРегПФ) obj;
            Файл.Документ.СвИП.СвРегПФ.СвОргПФ свОргПФ = свРегПФ.getСвОргПФ();
            type = SvRegType.PF.getValue();
            code = свОргПФ.getКодПФ();
            name = свОргПФ.getНаимПФ();
        } else if (obj instanceof Файл.Документ.СвИП.СвРегФСС) {
            Файл.Документ.СвИП.СвРегФСС свРегФСС = (Файл.Документ.СвИП.СвРегФСС) obj;
            Файл.Документ.СвИП.СвРегФСС.СвОргФСС свОргФСС = свРегФСС.getСвОргФСС();
            type = SvRegType.FSS.getValue();
            code = свОргФСС.getКодФСС();
            name = свОргФСС.getНаимФСС();
        }

        SvOrg svOrg = svOrgRepo.findSvOrgByTypeOrgAndCodeAndNameAndAdr(type, code, name, adr);
        if (svOrg == null) {
            svOrg = createSvOrg(type, code, name, adr);
        }

        return svOrg;
    }

    private ReferenceBook getOrCreateSpvz(ВидЗапТип видЗап) {
        ReferenceBook spvz = null;
        if (видЗап != null) {
            spvz = spvzMap.get(видЗап.getКодСПВЗ());
            if (spvz == null) {
                spvz = createSpvz(видЗап.getКодСПВЗ(), видЗап.getНаимВидЗап());
                spvzMap.put(spvz.getCode(), spvz);
            }
        }

        return spvz;
    }

    private String getDataWNullNodes(Файл.Документ.СвИП свИП) throws JsonProcessingException {
        свИП.setСвРегОрг(null);
        свИП.setСвРегПФ(null);
        свИП.setСвРегФСС(null);
        свИП.setСвОКВЭД(null);
        return mapper.writeValueAsString(свИП);
    }

    //////////////////////////////////////////////// CREATE ELEMENT ////////////////////////////////////////////////////
    private RegEgrip createRegEgrip(Файл.Документ.СвИП свИП, ClsMigration migration) {
        RegEgrip newRegEgrip = new RegEgrip();
        newRegEgrip.setLoadDate(new Timestamp(System.currentTimeMillis()));
        newRegEgrip.setInn(свИП.getИННФЛ());
        newRegEgrip.setIdMigration(migration.getId());
        newRegEgrip.setTypeEgrip(Short.valueOf(свИП.getКодВидИП()));

        String ogrn = свИП.getОГРНИП();
        newRegEgrip.setOgrn(ogrn);

        Date dateActual = new Date(свИП.getДатаВып().toGregorianCalendar().getTimeInMillis());
        newRegEgrip.setDateActual(dateActual);

        try {
            newRegEgrip.setIogrn((ogrn != null) ? Long.parseLong(ogrn): null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newRegEgrip;
    }

    private EgripContainer createEgripContainer(RegEgrip newRegEgrip, EgripNodes egripNodes) {
        EgripContainer ec = new EgripContainer(newRegEgrip);

        // ОКВЭДы
        Файл.Документ.СвИП.СвОКВЭД свОКВЭД = egripNodes.getСвОКВЭД();
        Set<RegEgripOkved> regEgripOkveds = parseEgripOkveds(свОКВЭД, newRegEgrip);
        ec.setRegEgripOkved(regEgripOkveds);

        // Сведения о регистрации в Налоговой, ПФ, ФСС
        Set<SvOrg> svOrgs = new HashSet<>();
        Set<SvReg> svRegs = new HashSet<>();
        Файл.Документ.СвИП.СвРегОрг регОрг = egripNodes.getСвРегОрг();
        if (регОрг != null) {
            SvOrg svOrg = getSvOrg(регОрг);
            svOrgs.add(svOrg);
            SvReg svReg = createSvReg(регОрг, newRegEgrip, svOrg);
            svRegs.add(svReg);
        }

        Файл.Документ.СвИП.СвРегПФ свРегПФ = egripNodes.getСвРегПФ();
        if (свРегПФ != null) {
            SvOrg svOrg = getSvOrg(свРегПФ);
            svOrgs.add(svOrg);
            SvReg svReg = createSvReg(свРегПФ, newRegEgrip, svOrg);
            svRegs.add(svReg);
        }

        Файл.Документ.СвИП.СвРегФСС свРегФСС = egripNodes.getСвРегФСС();
        if (свРегФСС != null) {
            SvOrg svOrg = getSvOrg(свРегФСС);
            svOrgs.add(svOrg);
            SvReg svReg = createSvReg(свРегФСС, newRegEgrip, svOrg);
            svRegs.add(svReg);
        }

        ec.setSvOrgs(svOrgs);
        ec.setSvRegs(svRegs);

        // Статус
        Файл.Документ.СвИП.СвСтатус статус = egripNodes.getСвСтатус();
        SvStatus status = parseEgripStatuses(статус, newRegEgrip);
        Set<SvStatus> sss = new HashSet<>(Set.of(status));
        ec.setSvStatuses(sss);


        // СвЗапЕГРИП
        List<Файл.Документ.СвИП.СвЗапЕГРИП> свЗапЕГРИПList = egripNodes.getСвЗапЕГРИП();
        try {
            Set<SvRecordEgr> sres = parseSvRecords(свЗапЕГРИПList, newRegEgrip);
            ec.setSvRecords(sres);
        } catch (JsonProcessingException e) {
            egripLogger.error("Не удалось преобразовать данные филиала к JSON для ОГРН " + newRegEgrip.getOgrn());
            e.printStackTrace();
        }

        return ec;
    }

    private SvRecordEgr createSvRecordEgr(Файл.Документ.СвИП.СвЗапЕГРИП свЗапЕГРИП) {
        Boolean isValid = checkIsValidSvRecord(свЗапЕГРИП);
        ReferenceBook spvz = getOrCreateSpvz(свЗапЕГРИП.getВидЗап());

        SvRecordEgr svRecordEgr = SvRecordEgr.builder()
                .recordId(свЗапЕГРИП.getИдЗап())
                .recordDate(XMLGregorianCalendarToTimestamp(свЗапЕГРИП.getДатаЗап()))
                .isValid(isValid)
                .spvz(spvz)
                .build();
        return svRecordEgr;
    }

    private SvReg createSvReg(Object obj, RegEgrip egrip, SvOrg svOrg) {
        SvReg svReg = new SvReg();
        if (obj instanceof Файл.Документ.СвИП.СвРегОрг) {
            Файл.Документ.СвИП.СвРегОрг свРегОрг = (Файл.Документ.СвИП.СвРегОрг) obj;
            XMLGregorianCalendar recordDateXML = свРегОрг.getГРНИПДата().getДатаЗаписи();
            Timestamp recordDate = XMLGregorianCalendarToTimestamp(recordDateXML);

            svReg.setEgrip(egrip);
            svReg.setTypeOrg(SvRegType.NALOG.getValue());
            svReg.setSvOrg(svOrg);
            svReg.setGrn(свРегОрг.getГРНИПДата().getГРНИП());
            svReg.setRecordDate(recordDate);

        } else if (obj instanceof Файл.Документ.СвИП.СвРегПФ) {
            Файл.Документ.СвИП.СвРегПФ свРегПФ = (Файл.Документ.СвИП.СвРегПФ) obj;
            Файл.Документ.СвИП.СвРегПФ.СвОргПФ свОргПФ = свРегПФ.getСвОргПФ();

            XMLGregorianCalendar recordDateXML = свРегПФ.getГРНИПДата().getДатаЗаписи();
            Timestamp recordDate = XMLGregorianCalendarToTimestamp(recordDateXML);

            Timestamp recordDateCorr = null;
            String grnCorr = null;
            if (свРегПФ.getГРНИПДатаИспр() != null) {
                XMLGregorianCalendar recordDateCorrXML = свРегПФ.getГРНИПДатаИспр().getДатаЗаписи();
                recordDateCorr = XMLGregorianCalendarToTimestamp(recordDateCorrXML);
                grnCorr = свРегПФ.getГРНИПДатаИспр().getГРНИП();
            }

            svReg.setEgrip(egrip);
            svReg.setTypeOrg(SvRegType.PF.getValue());
            svReg.setSvOrg(svOrg);
            svReg.setGrn(свРегПФ.getГРНИПДата().getГРНИП());
            svReg.setRecordDate(recordDate);
            svReg.setGrnCorr(grnCorr);
            svReg.setRecordDateCorr(recordDateCorr);

        } else if (obj instanceof Файл.Документ.СвИП.СвРегФСС) {
            Файл.Документ.СвИП.СвРегФСС свРегФСС = (Файл.Документ.СвИП.СвРегФСС) obj;
            Файл.Документ.СвИП.СвРегФСС.СвОргФСС свОргФСС = свРегФСС.getСвОргФСС();

            XMLGregorianCalendar recordDateXML = свРегФСС.getГРНИПДата().getДатаЗаписи();
            Timestamp recordDate = XMLGregorianCalendarToTimestamp(recordDateXML);

            Timestamp recordDateCorr = null;
            String grnCorr = null;
            if (свРегФСС.getГРНИПДатаИспр() != null) {
                XMLGregorianCalendar recordDateCorrXML = свРегФСС.getГРНИПДатаИспр().getДатаЗаписи();
                recordDateCorr = XMLGregorianCalendarToTimestamp(recordDateCorrXML);
                grnCorr = свРегФСС.getГРНИПДатаИспр().getГРНИП();
            }

            svReg.setEgrip(egrip);
            svReg.setTypeOrg(SvRegType.FSS.getValue());
            svReg.setSvOrg(svOrg);
            svReg.setGrn(свРегФСС.getГРНИПДата().getГРНИП());
            svReg.setRecordDate(recordDate);
            svReg.setGrnCorr(grnCorr);
            svReg.setRecordDateCorr(recordDateCorr);
        }

        return svReg;
    }

    private ReferenceBook createSipst(Файл.Документ.СвИП.СвСтатус.СвСтатус1 свСтатус1) {
        String sipstCode = свСтатус1.getКодСтатус();
        String sipstName = свСтатус1.getНаимСтатус();

        ReferenceBook sipst = createSipst(sipstCode, sipstName);
        sipstMap.put(sipst.getCode(), sipst);

        return sipst;
    }

    ////////////////////////////////////////////////// SAVE DATA ///////////////////////////////////////////////////////

    private void saveRegEgripOkveds(List<EgripContainer> list) {
        final List<Set<RegEgripOkved>> reos = list.stream().map(c -> c.getRegEgripOkved()).collect(Collectors.toList());
        Set<RegEgripOkved> granula = new HashSet<>();
        int count = 1;
        for (Set<RegEgripOkved> reo : reos) {
            if (reo != null) {
                granula.addAll(reo);
                count ++;
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

    private void saveSvOrgAndReg(List<EgripContainer> list) {
        // svOrgs
        final List<Set<SvOrg>> sos = list.stream().map(c -> c.getSvOrgs()).collect(Collectors.toList());
        Set<SvOrg> granula = new HashSet<>();
        int count = 1;
        for (Set<SvOrg> so : sos) {
            if (so != null) {
                granula.addAll(so);
                count ++;
            }
            if (count % 10 == 0 && !granula.isEmpty()){
                svOrgRepo.saveAll(granula);
                granula.clear();
            }
        }
        if (!granula.isEmpty()){
            svOrgRepo.saveAll(granula);
        }

        // svRegs
        final List<Set<SvReg>> srs = list.stream().map(c -> c.getSvRegs()).collect(Collectors.toList());
        Set<SvReg> gran = new HashSet<>();
        int cnt = 1;
        for (Set<SvReg> sr : srs) {
            if (sr != null) {
                gran.addAll(sr);
                cnt ++;
            }
            if (cnt % 10 == 0 && !gran.isEmpty()){
                svRegRepo.saveAll(gran);
                gran.clear();
            }
        }
        if (!gran.isEmpty()){
            svRegRepo.saveAll(gran);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Boolean checkIsValidSvRecord(Файл.Документ.СвИП.СвЗапЕГРИП свЗапЕГРИП) {
        Boolean isValid = true;
        Файл.Документ.СвИП.СвЗапЕГРИП.СвСтатусЗап свСтатусЗап = свЗапЕГРИП.getСвСтатусЗап();

        if (свСтатусЗап != null && свСтатусЗап.getГРНИПДатаНед() != null) {
            isValid = false;
        }

        return isValid;
    }

}

