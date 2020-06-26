package ru.sibdigital.proccovid.service;

import java.util.List;
import java.util.Map;


public interface StatisticService {


    Map getTotalStatistic();

    List<Map<String, Object>> getDepartmentRequestStatistic(Integer idTypeRequest);
    List<Map<String, Object>> getDepartmentRequestStatistic();

    Map getTotalDachaStatistic();
    Map getTotalStatistic(Integer idTypeRequest);

    List<Map<String, Object>> getNearestDaysDachaRequestStatistic();
}
