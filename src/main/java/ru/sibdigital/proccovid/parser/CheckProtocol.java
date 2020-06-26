package ru.sibdigital.proccovid.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.dto.PostFormDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class CheckProtocol {
    private PostFormDto postFormDto;
    private List<Integer> personsEmptyRowsInExcel;
    private List<Integer> addressesEmptyRowsInExcel;
    private List<Map<String,String>> checkedDeparts;
    private String globalMessage = "OK";
    private boolean success = true;
    private Map<String, Map<String, Integer>> statistic = new HashMap<>(3);

    public CheckProtocol(PostFormDto postFormDto) {
        this.postFormDto = postFormDto;
    }

    public String getErrors() {
        StringBuilder mapAsString = new StringBuilder();
        statistic.entrySet().stream().forEach(stringMapEntry -> {
            mapAsString.append(stringMapEntry.getKey());
            mapAsString.append(": ");
            stringMapEntry.getValue().entrySet().stream().forEach(stringIntegerEntry -> {
                mapAsString.append(stringIntegerEntry.getKey());
                mapAsString.append("=");
                mapAsString.append(stringIntegerEntry.getValue());
                mapAsString.append(", ");
            });
            mapAsString.replace(mapAsString.length()-2, mapAsString.length(),"");
            mapAsString.append("\n");
        });
        mapAsString.append("department: size:");
        mapAsString.append(checkedDeparts.size());
        return mapAsString.toString();
    }
}
