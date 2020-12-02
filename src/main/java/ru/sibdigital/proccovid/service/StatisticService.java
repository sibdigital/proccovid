package ru.sibdigital.proccovid.service;

import java.util.List;
import java.util.Map;


public interface StatisticService {


    Map getTotalStatistic();

    List<Map<String, Object>> getDepartmentRequestStatistic(Integer idTypeRequest);
    List<Map<String, Object>> getDepartmentRequestStatistic();

    Map<String, Object> getActualRequestStatisticForEeachOrganization();
    List<Map<String, Object>> getActualRequestStatisticForEeachDepartment();

    Map<String, Object> getActualRequestNumberWorkerStatisticForEeachOrganization();
    Map<String, Object> getActualRequestNumberWorkerStatisticForEeachDepartment();

    List<Map<String, Object>> getNumberOfSubscribersStatisticForEachMailing();
    Integer getCountOfSubscribers();

    List<Map<String, Object>> getNumberOfMailSentForEachMailing(String dateStart, String dateEnd);
    List<Map<String, Object>> getNumberOfMailSentForEachMailing(Integer status, String dateStart, String dateEnd);


    Map getTotalDachaStatistic();
    Map getTotalStatistic(Integer idTypeRequest);

    List<Map<String, Object>> getNearestDaysDachaRequestStatistic();
}
