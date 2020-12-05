package ru.sibdigital.proccovid.dto;

import ru.sibdigital.proccovid.dto.egrul.EGRUL;

public class EgrulResponse {

    private EGRUL.СвЮЛ data;

    public EGRUL.СвЮЛ getData() {
        return data;
    }

    public void setData(EGRUL.СвЮЛ data) {
        this.data = data;
    }
}
