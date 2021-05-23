package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.repository.document.DocDachaPersonRepo;
import ru.sibdigital.proccovid.repository.document.DocDachaRepo;
import ru.sibdigital.proccovid.repository.document.DocPersonRepo;
import ru.sibdigital.proccovid.repository.document.DocRequestRepo;

import java.util.*;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    DocPersonRepo docPersonRepo;

    @Autowired
    DocDachaPersonRepo docDachaPersonRepo;

    @Autowired
    DocRequestRepo docRequestRepo;

    @Autowired
    DocDachaRepo docDachaRepo;

    @Override
    public Map<String, Object> getTotalStatistic(){
        Map<String, Object> statistic = new HashMap(5);

        Map<String, Object> peopleStatistic = new HashMap<>(3);
        peopleStatistic.put("accepted",docPersonRepo.getTotalApprovedPeopleByReviewStatus(1));
        peopleStatistic.put("declined",docPersonRepo.getTotalApprovedPeopleByReviewStatus(2));
        peopleStatistic.put("awaiting",docPersonRepo.getTotalApprovedPeopleByReviewStatus(0));


        statistic.put("peopleStatistic", peopleStatistic);
        statistic.put("forEachDayStatistic", docRequestRepo.getStatisticForEachDay());
        return statistic;
    }

    @Override
    public Map getTotalStatistic(Integer idTypeRequest){
        Map<String, Object> statistic = new HashMap(5);

        Map<String, Object> peopleStatistic = new HashMap<>(3);
        peopleStatistic.put("accepted",docPersonRepo.getTotalApprovedPeopleByReviewStatus(1, idTypeRequest));
        peopleStatistic.put("declined",docPersonRepo.getTotalApprovedPeopleByReviewStatus(2, idTypeRequest));
        peopleStatistic.put("awaiting",docPersonRepo.getTotalApprovedPeopleByReviewStatus(0, idTypeRequest));


        statistic.put("peopleStatistic", peopleStatistic);
        statistic.put("forEachDayStatistic", docRequestRepo.getStatisticForEachDay(idTypeRequest));
        return statistic;
    }

    @Override
    public List<Map<String, Object>> getDepartmentRequestStatistic(){
        List<Map<String, Object>> rawStatistic = docRequestRepo.getRequestStatisticForEeachDepartment();
        /*statistic.put("totalPeople", docPersonRepo.getTotalPeople());
        statistic.put("totalApprovedPeople", docPersonRepo.getTotalApprovedPeopleByReviewStatus());*/
        return rawStatistic;
    }

    @Override
    public Map<String, Object> getActualRequestStatisticForEeachOrganization() {
        Map<String, Object> rawStatistic = docRequestRepo.getActualRequestStatisticForEeachOrganization();
        return rawStatistic;
    }

    @Override
    public List<Map<String, Object>> getActualRequestStatisticForEeachDepartment() {
        List<Map<String, Object>> rawStatistic = docRequestRepo.getActualRequestStatisticForEeachDepartment();
        return rawStatistic;
    }

    @Override
    public List<Map<String, Object>> getDepartmentRequestStatistic(Integer idTypeRequest){
        List<Map<String, Object>> rawStatistic = docRequestRepo.getRequestStatisticForEeachDepartment(idTypeRequest);
        /*statistic.put("totalPeople", docPersonRepo.getTotalPeople());
        statistic.put("totalApprovedPeople", docPersonRepo.getTotalApprovedPeopleByReviewStatus());*/
        return rawStatistic;
    }

    @Override
    public Map<String, Object> getActualRequestNumberWorkerStatisticForEeachOrganization() {
        Map<String, Object> rawStatistic = docRequestRepo.getActualNumberWorkerForEachOrganization();
        return rawStatistic;
    }

    @Override
    public Map<String, Object> getActualRequestNumberWorkerStatisticForEeachDepartment() {
        Map<String, Object> rawStatistic = docRequestRepo.getActualNumberWorkerForEachDepartment();
        return rawStatistic;
    }

    @Override
    public List<Map<String, Object>> getNumberOfSubscribersStatisticForEachMailing() {
        List<Map<String, Object>> rawStatistic = docRequestRepo.getNumberOfSubscribersForEachMailing();
        return rawStatistic;
    }

    @Override
    public Integer getCountOfSubscribers() {
        Integer rawStatistic = docRequestRepo.getCountOfSubscribers();
        return rawStatistic;
    }

    @Override
    public List<Map<String, Object>> getNumberOfMailSentForEachMailing(String dateStart, String dateEnd) {
        List<Map<String, Object>> rawStatistic = docRequestRepo.getNumberOfMailSentForEachMailing(new Date(Long.parseLong(dateStart)), new Date(Long.parseLong(dateEnd)));
        return rawStatistic;
    }

    @Override
    public List<Map<String, Object>> getNumberOfMailSentForEachMailing(Integer status,
                                                                                 String dateStart,
                                                                                 String dateEnd) {
        List<Map<String, Object>> rawStatistic = docRequestRepo.getNumberOfMailSentForEachMailing(status, new Date(Long.parseLong(dateStart)), new Date(Long.parseLong(dateEnd)));
        return rawStatistic;
    }

    @Override
    public Map getTotalDachaStatistic() {

        Map<String, Object> statistic = new HashMap(8);


        Map<String, Object> peopleStatistic = new HashMap<>(3);
        //peopleStatistic.put("accepted",docDachaPersonRepo.getTotalApprovedPeopleByReviewStatus(1));
        //peopleStatistic.put("declined",docDachaPersonRepo.getTotalApprovedPeopleByReviewStatus(2));
        peopleStatistic.put("awaiting",docDachaPersonRepo.getTotalApprovedPeopleByReviewStatus(0));




        statistic.put("peopleStatistic", peopleStatistic);
        statistic.put("requestStatistic", docDachaRepo.getCountByReviewStatus());
        statistic.put("forEachDayStatistic", docDachaRepo.getStatisticForEachDay());
       // statistic.put("timeCreateStatistic", docDachaRepo.getTotalStatisticByTimeCreate());
        statistic.put("validDateStatistic", docDachaRepo.getTotalStatisticByValidDate());
        return statistic;
    }

    @Override
    public List<Map<String, Object>> getNearestDaysDachaRequestStatistic() {
        return docDachaRepo.getStatisticForNearestDays();
    }


}
