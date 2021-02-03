package ru.sibdigital.proccovid.service.egr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.proccovid.dto.EgrulNodes;
import ru.sibdigital.proccovid.dto.EgrulContainer;
import ru.sibdigital.proccovid.dto.egrul040601.*;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.model.egr.SvRegType;
import ru.sibdigital.proccovid.model.egr.*;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@Service
public class ImportEgrulServiceImpl extends SuperEgrServiceImpl implements ImportEgrulService{

    private final static Logger egrulLogger = LoggerFactory.getLogger("egrulLogger");

    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, Okved> okvedsMap = new HashMap<>();
    private Map<String, ReferenceBook> sulstMap  = new HashMap<>();
    private Map<String, ReferenceBook> spvzMap  = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void fillMaps() {
        okvedsMap = getMapOkveds();
        sulstMap = getMapReferenceBookByType(ReferenceBookTypes.SULST.getValue());
        spvzMap = getMapReferenceBookByType(ReferenceBookTypes.SPVZ.getValue());
    }

    public void importEgrulData() {
        egrulLogger.info("Импорт ЕГРЮЛ начат из " + egrulPath);

        fillMaps();
        Collection<File> zipFiles = getZipFilesFromDirectory(egrulPath, egrulLogger);

        // Загрузка ПОЛНОГО zip
        List<File> zipFullFiles = getFullZipFiles(zipFiles);
        egrulLogger.info("Всего файлов ПОЛНОГО " + zipFullFiles.size());

        if (zipFullFiles != null && !zipFullFiles.isEmpty()) {
            loadEGRULFiles(zipFullFiles);
        }

        // Загрузка ОБНОВЛЕНИЙ
        List<File> zipUpdateFiles = getUpdateZipFiles(zipFiles);
        egrulLogger.info("Всего файлов ОБНОВЛЕНИЙ " + zipUpdateFiles.size());

        if (zipUpdateFiles != null && !zipUpdateFiles.isEmpty()) {
            loadEGRULFiles(zipUpdateFiles);
        }

        egrulLogger.info("Импорт ЕГРЮЛ окончен");
    }

    private void loadEGRULFiles(List<File> zipFiles) {
        for (File zipFile : zipFiles) {
            ClsMigration migration = getEgrulMigrationByFile(zipFile);
            if (!checkIfMigrationHasGoneSuccessfullyBefore(migration)) {
                // Добавить запись об обработке файла
                migration = addOrResetEgrulMigration(migration, zipFile);

                // Обработать данные zip
                processEgrulFile(zipFile, migration);

                // Изменить запись о статусе обработки файла и удалить файл
                if (checkMigrationStatusIsLoadStart(migration)) {
                    changeMigrationStatusToSuccessfully(migration);
                    deleteFile(zipFile);
                } else {
                    renameFile(zipFile, egrulLogger);
                }
            }
        }
    }

    private void processEgrulFile(File file, ClsMigration migration) {
        egrulLogger.info("Обработка файла " + file.getName() + " " + new Date());

        ZipFile zipFile = getZipFile(file);
        if (zipFile != null) {
            Unmarshaller unmarshaller = getUnmarshaller(Файл.class);
            if (unmarshaller != null) {
                try {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = entries.nextElement();
                        saveEgrulData(zipFile, zipEntry, unmarshaller, migration);
                    }
                }
                catch (Exception e) {
                    egrulLogger.error(e.getMessage());
                    markMigrationAsCompletedWithError(migration, e);
                    e.printStackTrace();
                }
                finally {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        markMigrationAsCompletedWithError(migration, e);
                        e.printStackTrace();
                    }
                }
            } else {
                egrulLogger.error("Не удалось создать демаршаллизатор");
                markMigrationAsCompletedWithError(migration, "Не удалось создать демаршаллизатор");
            }
        } else {
            egrulLogger.error("Не удалось прочитать zip-файл");
            markMigrationAsCompletedWithError(migration, "Не удалось прочитать zip-файл");
        }
        egrulLogger.info("Окончание обработки файла " + file.getName() + " " + new Date());
    }

    @Transactional
    boolean saveEgrulData(ZipFile zipFile, ZipEntry zipEntry, Unmarshaller unmarshaller, ClsMigration migration) {
        egrulLogger.info("Обработка xml файла " + zipEntry.getName());
        boolean result = false;

        InputStream is = null;
        try {
            is = zipFile.getInputStream(zipEntry);
            Файл egrul = (Файл) unmarshaller.unmarshal(is);

            List<EgrulContainer> list = parseEgrulData(egrul, migration);
            saveEgruls(list);

            result = true;
        } catch (IOException e) {
            egrulLogger.error("Не удалось прочитать xml-файл из zip-файла");
            markMigrationAsCompletedWithError(migration, "Не удалось прочитать xml-файл из zip-файла");
            e.printStackTrace();
        } catch (JAXBException e) {
            egrulLogger.error("Не удалось демаршализовать xml-файл из zip-файла");
            markMigrationAsCompletedWithError(migration, "Не удалось демаршализовать xml-файл из zip-файла");
            e.printStackTrace();
        }
        return result;
    }

    private List<EgrulContainer> parseEgrulData(Файл egrul, ClsMigration migration) {
        List<EgrulContainer> containerList = new ArrayList<>();
        for (Файл.Документ документ : egrul.getДокумент()) {
            Файл.Документ.СвЮЛ свЮЛ = документ.getСвЮЛ();

            EgrulNodes egrulNodes = new EgrulNodes(свЮЛ);
            RegEgrul newRegEgrul = createRegEgrul(свЮЛ, migration);
            EgrulContainer ec = createEgrulContainer(newRegEgrul, egrulNodes, свЮЛ);
            newRegEgrul.setActiveStatus(getActiveStatus(свЮЛ, ec.getSvStatuses()));

            try {
                newRegEgrul.setData(getDataWNullNodes(свЮЛ));
            } catch (JsonProcessingException e) {
                egrulLogger.error("Не удалось преобразовать данные к JSON для ОГРН " + свЮЛ.getОГРН());
                e.printStackTrace();
            }

            containerList.add(ec);
        }
        return containerList;
    }

    private void saveEgruls(List<EgrulContainer> list){
        saveOpf(list);

        List<EgrulContainer> updatedData = getContainersToUpdate(list);
        List<Long> deletedNodes = updatedData.stream()
                .map(ec -> ec.getRegEgrul().getId())
                .filter(id -> (id != null))
                .collect(Collectors.toList());

        if (!deletedNodes.isEmpty()) {
            regEgrulOkvedRepo.deleteRegEgrulOkveds(deletedNodes);
            svRegRepo.deleteSvRegsByIdEgruls(deletedNodes);
            svStatusRepo.deleteSvStatusesByIdEgruls(deletedNodes);
            svRecordEgrRepo.deleteSvRecordEgrsByIdEgruls(deletedNodes);
        }

        final List<RegEgrul> rel = updatedData.stream().map(c -> c.getRegEgrul()).collect(Collectors.toList());
        regEgrulRepo.saveAll(rel);

        saveRegEgrulOkveds(updatedData);
        saveSvOrgAndReg(updatedData);
        saveEgrulSvStatuses(updatedData);
        saveEgrulSvRecords(updatedData);

        Set<RegFilial> updatedFilials = getUpdatedFilials(updatedData);
        saveSvFilials(updatedFilials);
    }

    ////////////////////////////////////////////////// PARSE DATA //////////////////////////////////////////////////////

    private Set<RegEgrulOkved> parseEgrulOkveds(Файл.Документ.СвЮЛ.СвОКВЭД свОКВЭД, RegEgrul newRegEgrul){
        Set<RegEgrulOkved> regEgrulOkveds = new HashSet<>();
        if (свОКВЭД != null) {
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
                    egrulLogger.error("ОКВЭД" + свОКВЭД.getСвОКВЭДОсн().getКодОКВЭД() + "версии " + ver + " не найден для ОГРН " + newRegEgrul.getOgrn());
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
                        egrulLogger.error("ОКВЭД" + свОКВЭДТип.getКодОКВЭД() + "версии " + version + " не найден для ОГРН " + newRegEgrul.getOgrn());
                    }
                }

            }
        }
        return regEgrulOkveds;
    }

    private Set<SvStatus> parseEgrulStatuses(List<Файл.Документ.СвЮЛ.СвСтатус> статусList, RegEgrul newRegEgrul) {
        Set<SvStatus> statusSet = new HashSet<>();
        for (Файл.Документ.СвЮЛ.СвСтатус статус : статусList) {
            Short orgDosSv = null;
            String orgDosSvGrn = null, orgDosSvGrnCorr = null,
                    exclDecNum = null, journalNum = null, grnCorr = null;
            Date orgDosSvRecordDate = null, orgDosSvRecordDateCorr = null,
                    exclDecDate = null, publDate = null, recordDateCorr = null;

            Файл.Документ.СвЮЛ.СвСтатус.СвСтатус1 свСтатус1 = статус.getСвСтатус();
            ReferenceBook sulst = null;
            if (свСтатус1 != null) {
                sulst = sulstMap.get(свСтатус1.getКодСтатусЮЛ());
                if (sulst == null) {
                    sulst = createSulst(свСтатус1);
                    sulstMap.put(sulst.getCode(), sulst);
                }
            }

            if (статус.getОгрДосСв() != null) {
                orgDosSv = Short.parseShort(статус.getОгрДосСв().getОгрДосСв());
                orgDosSvGrn = статус.getОгрДосСв().getГРНДата().getГРН();
                orgDosSvRecordDate = XMLGregorianCalendarToTimestamp(статус.getОгрДосСв().getГРНДата().getДатаЗаписи());
                if (статус.getОгрДосСв().getГРНДатаИспр() != null) {
                    orgDosSvGrnCorr = статус.getОгрДосСв().getГРНДатаИспр().getГРН();
                    orgDosSvRecordDateCorr = XMLGregorianCalendarToTimestamp(статус.getОгрДосСв().getГРНДатаИспр().getДатаЗаписи());
                }
            }

            if (статус.getСвРешИсклЮЛ() != null) {
                exclDecDate = XMLGregorianCalendarToTimestamp(статус.getСвРешИсклЮЛ().getДатаРеш());
                exclDecNum = статус.getСвРешИсклЮЛ().getНомерРеш();
                publDate = XMLGregorianCalendarToTimestamp(статус.getСвРешИсклЮЛ().getДатаПубликации());
                journalNum = статус.getСвРешИсклЮЛ().getНомерЖурнала();
            }

            if (статус.getГРНДатаИспр() != null) {
                grnCorr = статус.getГРНДатаИспр().getГРН();
                recordDateCorr = XMLGregorianCalendarToTimestamp(статус.getГРНДатаИспр().getДатаЗаписи());
            }

            SvStatus svStatus = SvStatus.builder()
                .egrul(newRegEgrul)
                .referenceBook(sulst)
                .orgDosSv(orgDosSv)
                .orgDosSvRecordDate(orgDosSvRecordDate)
                .orgDosSvGrn(orgDosSvGrn)
                .orgDosSvRecordDateCorr(orgDosSvRecordDateCorr)
                .orgDosSvGrnCorr(orgDosSvGrnCorr)
                .exclDecDate(exclDecDate)
                .exclDecNum(exclDecNum)
                .publDate(publDate)
                .journalNum(journalNum)
                .grn(статус.getГРНДата().getГРН())
                .recordDate(XMLGregorianCalendarToTimestamp(статус.getГРНДата().getДатаЗаписи()))
                .grnCorr(grnCorr)
                .recordDateCorr(recordDateCorr)
                .build();

            statusSet.add(svStatus);
        }

        return statusSet;
    }

    private Set<SvRecordEgr> parseSvRecords(List<Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ> свЗапЕГРЮЛList, RegEgrul newRegEgrul) throws JsonProcessingException {
        Set<SvRecordEgr> svRecords = new HashSet<>();
        for (Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ свЗапЕГРЮЛ : свЗапЕГРЮЛList) {

            SvRecordEgr svRecordEgr = createSvRecordEgr(свЗапЕГРЮЛ);
            svRecordEgr.setEgrul(newRegEgrul);

            свЗапЕГРЮЛ.setВидЗап(null);
            свЗапЕГРЮЛ.setИдЗап(null);
            свЗапЕГРЮЛ.setДатаЗап(null);

            svRecordEgr.setData(mapper.writeValueAsString(свЗапЕГРЮЛ));

            svRecords.add(svRecordEgr);
        }

        return svRecords;
    }

    private Set<SvReg> parseRegs(RegEgrul newRegEgrul, EgrulNodes egrulNodes) {
        Set<SvReg> svRegs = new HashSet<>();
        Файл.Документ.СвЮЛ.СвРегОрг регОрг = egrulNodes.getСвРегОрг();
        if (регОрг != null) {
            SvOrg svOrg = getSvOrg(регОрг);
            SvReg svReg = createSvReg(регОрг, newRegEgrul, svOrg);
            svRegs.add(svReg);
        }

        Файл.Документ.СвЮЛ.СвРегПФ свРегПФ = egrulNodes.getСвРегПФ();
        if (свРегПФ != null) {
            SvOrg svOrg = getSvOrg(свРегПФ);
            SvReg svReg = createSvReg(свРегПФ, newRegEgrul, svOrg);
            svRegs.add(svReg);
        }

        Файл.Документ.СвЮЛ.СвРегФСС свРегФСС = egrulNodes.getСвРегФСС();
        if (свРегФСС != null) {
            SvOrg svOrg = getSvOrg(свРегФСС);
            SvReg svReg = createSvReg(свРегФСС, newRegEgrul, svOrg);
            svRegs.add(svReg);
        }

        return svRegs;
    }

    private Set<RegFilial> parseFilials(Файл.Документ.СвЮЛ.СвПодразд свПодразд, RegEgrul regEgrul) throws JsonProcessingException{
        Set<RegFilial> filials = new HashSet<>();
        if ( свПодразд != null) {
            List<Файл.Документ.СвЮЛ.СвПодразд.СвФилиал> свФилиалList = свПодразд.getСвФилиал();
            for (Файл.Документ.СвЮЛ.СвПодразд.СвФилиал свФилиал : свФилиалList) {
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
                    address = getKladrAddress(свФилиал.getАдрМНРФ());
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

            List<Файл.Документ.СвЮЛ.СвПодразд.СвПредстав> свПредставList = свПодразд.getСвПредстав();
            for (Файл.Документ.СвЮЛ.СвПодразд.СвПредстав свПредстав : свПредставList) {
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
                    address = getKladrAddress(свПредстав.getАдрМНРФ());
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

        return filials;
    }

    //////////////////////////////////////////// GET VALUES, FILTER, FIND ELEMENTS /////////////////////////////////////
    private SvOrg getSvOrg(Object obj) {
        Short type = null;
        String code = null, name = null, adr = null;
        if (obj instanceof Файл.Документ.СвЮЛ.СвРегОрг) {
            Файл.Документ.СвЮЛ.СвРегОрг свРегОрг = (Файл.Документ.СвЮЛ.СвРегОрг) obj;
            type = SvRegType.NALOG.getValue();
            code = свРегОрг.getКодНО();
            name = свРегОрг.getНаимНО();
            adr = свРегОрг.getАдрРО();
        } else if (obj instanceof Файл.Документ.СвЮЛ.СвРегПФ) {
            Файл.Документ.СвЮЛ.СвРегПФ свРегПФ = (Файл.Документ.СвЮЛ.СвРегПФ) obj;
            Файл.Документ.СвЮЛ.СвРегПФ.СвОргПФ свОргПФ = свРегПФ.getСвОргПФ();
            type = SvRegType.PF.getValue();
            code = свОргПФ.getКодПФ();
            name = свОргПФ.getНаимПФ();
        } else if (obj instanceof Файл.Документ.СвЮЛ.СвРегФСС) {
            Файл.Документ.СвЮЛ.СвРегФСС свРегФСС = (Файл.Документ.СвЮЛ.СвРегФСС) obj;
            Файл.Документ.СвЮЛ.СвРегФСС.СвОргФСС свОргФСС = свРегФСС.getСвОргФСС();
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

    private Opf getOpf(String spr, String code, String fullName) {
        Opf opf = opfRepo.findOpfBySprAndCodeAndFullName(spr, code, fullName);
        if (opf == null) {
            opf = createOpf(spr, code, fullName);
        }
        return opf;
    }

    private Integer getActiveStatus(Файл.Документ.СвЮЛ свЮЛ, Set<SvStatus> svStatuses) {
        Integer activeStatus = EgrActiveStatus.ACTIVE.getValue();

        if (!checkСвЮЛIsValid(svStatuses)) {
            activeStatus = EgrActiveStatus.NOT_VALID.getValue();
        }

        if (checkСвЮЛIsCeased(свЮЛ)) {
            activeStatus = EgrActiveStatus.CEASED.getValue();
        }

        return activeStatus;
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

    private List<EgrulContainer> getContainersToUpdate(List<EgrulContainer> list) {
        Map<Long, RegEgrul> earlier = findSavedEarlierEgrul(list);
        List<EgrulContainer> updatedData = new ArrayList<>();

        if (!earlier.isEmpty()) {
            for (EgrulContainer ec : list) {
                RegEgrul r = ec.getRegEgrul();
                RegEgrul earl = earlier.get(r.getIogrn());
                if (earl != null) {
                    // Производить замену, только если СвЮЛ.ДатаВып больше date_actual записи таблицы
                    if (r.getDateActual().after(earl.getDateActual())) {
                        updatedData.add(ec);
                        r.setId(earl.getId());
                    }
                } else {
                    updatedData.add(ec);
                }
            }
        } else {
            updatedData = list;
        }

        return updatedData;
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

    private String getDataWNullNodes(Файл.Документ.СвЮЛ свЮЛ) throws JsonProcessingException {
        свЮЛ.setСвРегОрг(null);
        свЮЛ.setСвРегПФ(null);
        свЮЛ.setСвРегФСС(null);

        свЮЛ.setСвОКВЭД(null);
        свЮЛ.setСвСтатус(null);

        свЮЛ.setСпрОПФ(null);
        свЮЛ.setКодОПФ(null);
        свЮЛ.setПолнНаимОПФ(null);

//        свЮЛ.setСвПодразд(null); -- при парсинге Подразделений теперь зануляется


        return mapper.writeValueAsString(свЮЛ);
    }

    //////////////////////////////////////////////// CREATE ELEMENT ////////////////////////////////////////////////////
    private EgrulContainer createEgrulContainer(RegEgrul newRegEgrul, EgrulNodes egrulNodes, Файл.Документ.СвЮЛ свЮЛ) {
        EgrulContainer ec = new EgrulContainer(newRegEgrul);

        // ОКВЭДы
        Файл.Документ.СвЮЛ.СвОКВЭД свОКВЭД = egrulNodes.getСвОКВЭД();
        Set<RegEgrulOkved> regEgrulOkveds = parseEgrulOkveds(свОКВЭД, newRegEgrul);
        ec.setRegEgrulOkved(regEgrulOkveds);

        // Филиалы
        Файл.Документ.СвЮЛ.СвПодразд свПодразд = egrulNodes.getСвПодразд();
        try {
            Set<RegFilial> regFilials = parseFilials(свПодразд, newRegEgrul);
            ec.setRegFilials(regFilials);
            свЮЛ.setСвПодразд(null);
        } catch (JsonProcessingException e) {
            egrulLogger.error("Не удалось преобразовать данные филиала к JSON для ОГРН " + newRegEgrul.getOgrn());
            e.printStackTrace();
        }

        // Сведения о регистрации в Налоговой, ПФ, ФСС
        Set<SvReg> svRegs = parseRegs(newRegEgrul, egrulNodes);
        Set<SvOrg> svOrgs = svRegs.stream().map(svReg -> svReg.getSvOrg()).filter(svOrg -> (svOrg.getId() == null)).collect(Collectors.toSet());
        ec.setSvOrgs(svOrgs);
        ec.setSvRegs(svRegs);

        // Статус
        List<Файл.Документ.СвЮЛ.СвСтатус> статусList = egrulNodes.getСвСтатус();
        Set<SvStatus> statusSet = parseEgrulStatuses(статусList, newRegEgrul);
        ec.setSvStatuses(statusSet);

        // СвЗапЕГРЮЛ
        List<Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ> свЗапЕГРЮЛList = egrulNodes.getСвЗапЕГРЮЛ();
        try {
            Set<SvRecordEgr> sres = parseSvRecords(свЗапЕГРЮЛList, newRegEgrul);
            ec.setSvRecords(sres);
        } catch (JsonProcessingException e) {
            egrulLogger.error("Не удалось преобразовать данные филиала к JSON для ОГРН " + newRegEgrul.getOgrn());
            e.printStackTrace();
        }

        // СпрОПФ, КодОПФ, ПолнНаимОПФ
        Opf opf = getOpf(egrulNodes.getСпрОПФ(), egrulNodes.getКодОПФ(), egrulNodes.getПолнНаимОПФ());
        ec.setOpf(opf);
        newRegEgrul.setOpf(opf);

        return ec;
    }

    private RegEgrul createRegEgrul(Файл.Документ.СвЮЛ свЮЛ, ClsMigration migration) {
        RegEgrul newRegEgrul = new RegEgrul();
        newRegEgrul.setLoadDate(new Timestamp(System.currentTimeMillis()));
        newRegEgrul.setInn(свЮЛ.getИНН());
        newRegEgrul.setKpp(свЮЛ.getКПП());
        newRegEgrul.setIdMigration(migration.getId());

        String ogrn = свЮЛ.getОГРН();
        newRegEgrul.setOgrn(ogrn);

        Date dateActual = new Date(свЮЛ.getДатаВып().toGregorianCalendar().getTimeInMillis());
        newRegEgrul.setDateActual(dateActual);

        try {
            newRegEgrul.setIogrn((ogrn != null) ? Long.parseLong(ogrn): null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newRegEgrul.setFullName(свЮЛ.getСвНаимЮЛ().getНаимЮЛПолн());
        newRegEgrul.setShortName(свЮЛ.getСвНаимЮЛ().getНаимЮЛСокр());

        return newRegEgrul;
    }

    private SvRecordEgr createSvRecordEgr(Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ свЗапЕГРЮЛ) {
        Boolean isValid = checkIsValidSvRecord(свЗапЕГРЮЛ);
        ReferenceBook spvz = getOrCreateSpvz(свЗапЕГРЮЛ.getВидЗап());

        SvRecordEgr svRecordEgr = SvRecordEgr.builder()
                .recordId(свЗапЕГРЮЛ.getИдЗап())
                .recordDate(XMLGregorianCalendarToTimestamp(свЗапЕГРЮЛ.getДатаЗап()))
                .isValid(isValid)
                .spvz(spvz)
                .build();

        return svRecordEgr;
    }

    private ReferenceBook createSulst(Файл.Документ.СвЮЛ.СвСтатус.СвСтатус1 свСтатус1) {
        String sulstCode = свСтатус1.getКодСтатусЮЛ();
        String sulstName = свСтатус1.getНаимСтатусЮЛ();
        ReferenceBook sulst = createSulst(sulstCode, sulstName);

        return sulst;
    }

    private SvReg createSvReg(Object obj, RegEgrul egrul, SvOrg svOrg) {
        SvReg svReg = new SvReg();
        if (obj instanceof Файл.Документ.СвЮЛ.СвРегОрг) {
            Файл.Документ.СвЮЛ.СвРегОрг свРегОрг = (Файл.Документ.СвЮЛ.СвРегОрг) obj;
            XMLGregorianCalendar recordDateXML = свРегОрг.getГРНДата().getДатаЗаписи();
            Timestamp recordDate = XMLGregorianCalendarToTimestamp(recordDateXML);

            svReg.setEgrul(egrul);
            svReg.setTypeOrg(SvRegType.NALOG.getValue());
            svReg.setSvOrg(svOrg);
            svReg.setGrn(свРегОрг.getГРНДата().getГРН());
            svReg.setRecordDate(recordDate);

        } else if (obj instanceof Файл.Документ.СвЮЛ.СвРегПФ) {
            Файл.Документ.СвЮЛ.СвРегПФ свРегПФ = (Файл.Документ.СвЮЛ.СвРегПФ) obj;
            Файл.Документ.СвЮЛ.СвРегПФ.СвОргПФ свОргПФ = свРегПФ.getСвОргПФ();

            XMLGregorianCalendar recordDateXML = свРегПФ.getГРНДата().getДатаЗаписи();
            Timestamp recordDate = XMLGregorianCalendarToTimestamp(recordDateXML);

            Timestamp recordDateCorr = null;
            String grnCorr = null;
            if (свРегПФ.getГРНДатаИспр() != null) {
                XMLGregorianCalendar recordDateCorrXML = свРегПФ.getГРНДатаИспр().getДатаЗаписи();
                recordDateCorr = XMLGregorianCalendarToTimestamp(recordDateCorrXML);
                grnCorr = свРегПФ.getГРНДатаИспр().getГРН();
            }

            svReg.setEgrul(egrul);
            svReg.setTypeOrg(SvRegType.PF.getValue());
            svReg.setSvOrg(svOrg);
            svReg.setGrn(свРегПФ.getГРНДата().getГРН());
            svReg.setRecordDate(recordDate);
            svReg.setGrnCorr(grnCorr);
            svReg.setRecordDateCorr(recordDateCorr);

        } else if (obj instanceof Файл.Документ.СвЮЛ.СвРегФСС) {
            Файл.Документ.СвЮЛ.СвРегФСС свРегФСС = (Файл.Документ.СвЮЛ.СвРегФСС) obj;
            Файл.Документ.СвЮЛ.СвРегФСС.СвОргФСС свОргФСС = свРегФСС.getСвОргФСС();

            XMLGregorianCalendar recordDateXML = свРегФСС.getГРНДата().getДатаЗаписи();
            Timestamp recordDate = XMLGregorianCalendarToTimestamp(recordDateXML);

            Timestamp recordDateCorr = null;
            String grnCorr = null;
            if (свРегФСС.getГРНДатаИспр() != null) {
                XMLGregorianCalendar recordDateCorrXML = свРегФСС.getГРНДатаИспр().getДатаЗаписи();
                recordDateCorr = XMLGregorianCalendarToTimestamp(recordDateCorrXML);
                grnCorr = свРегФСС.getГРНДатаИспр().getГРН();
            }

            svReg.setEgrul(egrul);
            svReg.setTypeOrg(SvRegType.FSS.getValue());
            svReg.setSvOrg(svOrg);
            svReg.setGrn(свРегФСС.getГРНДата().getГРН());
            svReg.setRecordDate(recordDate);
            svReg.setGrnCorr(grnCorr);
            svReg.setRecordDateCorr(recordDateCorr);
        }

        return svReg;
    }

    ////////////////////////////////////////////////// SAVE DATA ///////////////////////////////////////////////////////

    private void saveSvOrgAndReg(List<EgrulContainer> list) {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean checkСвЮЛIsValid(Set<SvStatus> svStatuses) {
        final List<String> unactiveStatuses = Arrays.asList("701", "702", "801");

        boolean isValid = true;
        for (SvStatus svStatus : svStatuses) {
            if (unactiveStatuses.contains(svStatus.getReferenceBook().getCode())) {
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean checkСвЮЛIsCeased(Файл.Документ.СвЮЛ свЮЛ) {
        boolean isCeased = false;
        Файл.Документ.СвЮЛ.СвПрекрЮЛ свПрекрЮЛ = свЮЛ.getСвПрекрЮЛ();
        if (свПрекрЮЛ != null) {
            isCeased = true;
        }
        return isCeased;
    }

    private Boolean checkIsValidSvRecord(Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ свЗапЕГРЮЛ) {
        Boolean isValid = true;

        Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ.СвСтатусЗап свСтатусЗап = свЗапЕГРЮЛ.getСвСтатусЗап();
        if (свСтатусЗап != null && свСтатусЗап.getГРНДатаНед() != null) {
            isValid = false;
        }

        return isValid;
    }

    private String getKladrAddress(АдрРФЕГРЮЛТип адр) {
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

}

