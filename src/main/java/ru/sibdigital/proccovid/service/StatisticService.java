package ru.sibdigital.proccovid.service;

import java.util.List;
import java.util.Map;


public interface StatisticService {


    Map getTotalStatistic();

    List<Map<String, Object>> getDepartmentRequestStatistic(Integer idTypeRequest);
    List<Map<String, Object>> getDepartmentRequestStatistic();

    List<Map<String, Object>> getActualRequestStatisticForEeachOrganization();
    List<Map<String, Object>> getActualRequestStatisticForEeachDepartment();

    List<Map<String, Object>> getActualRequestNumberWorkerStatisticForEeachOrganization();
    List<Map<String, Object>> getActualRequestNumberWorkerStatisticForEeachDepartment();


    Map getTotalDachaStatistic();
    Map getTotalStatistic(Integer idTypeRequest);

    List<Map<String, Object>> getNearestDaysDachaRequestStatistic();
}
