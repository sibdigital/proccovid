package ru.sibdigital.proccovid.bean;

import ru.sibdigital.proccovid.model.DocRequestPrs;

import java.util.List;

public class DocRequestCollection {

    private Long total_count;
    private Long pos;
    private List<DocRequestPrs> data;

    public Long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Long total_count) {
        this.total_count = total_count;
    }

    public Long getPos() {
        return pos;
    }

    public void setPos(Long pos) {
        this.pos = pos;
    }

    public List<DocRequestPrs> getData() {
        return data;
    }

    public void setData(List<DocRequestPrs> data) {
        this.data = data;
    }
}
