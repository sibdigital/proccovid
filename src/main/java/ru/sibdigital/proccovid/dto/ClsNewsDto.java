package ru.sibdigital.proccovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.proccovid.model.Okved;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsNewsDto {

    private Long id;
    private String heading;
    private String message;
    private String startTime;
    private String endTime;
    private String hashId;

    public List<CheckedReviewStatusDto> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<CheckedReviewStatusDto> statuses) {
        this.statuses = statuses;
    }

    private List<Okved> okveds;
    private List<KeyValue> innList;
    private List<CheckedReviewStatusDto> statuses;


}