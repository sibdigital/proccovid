package ru.sibdigital.proccovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.proccovid.repository.DocDachaPersonRepo;
import ru.sibdigital.proccovid.repository.DocDachaRepo;
import ru.sibdigital.proccovid.repository.DocPersonRepo;
import ru.sibdigital.proccovid.repository.DocRequestRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Map<String, Object>> getDepartmentRequestStatistic(Integer idTypeRequest){
        List<Map<String, Object>> rawStatistic = docRequestRepo.getRequestStatisticForEeachDepartment(idTypeRequest);
        /*statistic.put("totalPeople", docPersonRepo.getTotalPeople());
        statistic.put("totalApprovedPeople", docPersonRepo.getTotalApprovedPeopleByReviewStatus());*/
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
