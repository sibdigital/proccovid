package ru.sibdigital.proccovid.dto;

import java.util.ArrayList;
import java.util.List;

public class CheckedReviewStatusDto {
    private Long id;
    private Long checked;
    private String value;
    private Long reviewStatus;

    public CheckedReviewStatusDto(){}

    public CheckedReviewStatusDto(Long checked, String value, Long reviewStatus) {
        this.checked = checked;
        this.value = value;
        this.reviewStatus = reviewStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChecked() {
        return checked;
    }

    public void setChecked(Long checked) {
        this.checked = checked;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Long reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public final static List<CheckedReviewStatusDto> getInitList(){
        List<CheckedReviewStatusDto> list =  List.of(
            new CheckedReviewStatusDto(0L, "На рассмотрении", 0L),
            new CheckedReviewStatusDto(0L, "Одобрена", 1L),
            new CheckedReviewStatusDto(0L, "Отклонена", 2L),
            new CheckedReviewStatusDto(0L, "Обновлена", 3L),
            new CheckedReviewStatusDto(0L, "Принята", 4L)
        );
        return list;
    }

}