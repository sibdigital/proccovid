package ru.sibdigital.proccovid.dto;

import ru.sibdigital.proccovid.dto.egrul040601.Файл;

import java.util.List;

public class EgrulNodes {
    private Файл.Документ.СвЮЛ.СвОКВЭД  свОКВЭД;
    private Файл.Документ.СвЮЛ.СвПодразд свПодразд;
    private Файл.Документ.СвЮЛ.СвРегОрг свРегОрг;
    private Файл.Документ.СвЮЛ.СвРегПФ  свРегПФ;
    private Файл.Документ.СвЮЛ.СвРегФСС свРегФСС;
    private List<Файл.Документ.СвЮЛ.СвСтатус> свСтатус;
    private List<Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ> свЗапЕГРЮЛ;
    private String спрОПФ;
    private String кодОПФ;
    private String полнНаимОПФ;

    public EgrulNodes(Файл.Документ.СвЮЛ свЮЛ) {
        this.setСвОКВЭД(свЮЛ.getСвОКВЭД());
        this.setСвПодразд(свЮЛ.getСвПодразд());
        this.setСвРегОрг(свЮЛ.getСвРегОрг());
        this.setСвРегПФ(свЮЛ.getСвРегПФ());
        this.setСвРегФСС(свЮЛ.getСвРегФСС());
        this.setСвСтатус(свЮЛ.getСвСтатус());
        this.setСвЗапЕГРЮЛ(свЮЛ.getСвЗапЕГРЮЛ());
        this.setСпрОПФ(свЮЛ.getСпрОПФ());
        this.setКодОПФ(свЮЛ.getКодОПФ());
        this.setПолнНаимОПФ(свЮЛ.getПолнНаимОПФ());
    }

    public Файл.Документ.СвЮЛ.СвОКВЭД getСвОКВЭД() {
        return свОКВЭД;
    }
    public void setСвОКВЭД(Файл.Документ.СвЮЛ.СвОКВЭД свОКВЭД) {
        this.свОКВЭД = свОКВЭД;
    }

    public Файл.Документ.СвЮЛ.СвПодразд getСвПодразд() {
        return свПодразд;
    }
    public void setСвПодразд(Файл.Документ.СвЮЛ.СвПодразд свПодразд) {
        this.свПодразд = свПодразд;
    }

    public Файл.Документ.СвЮЛ.СвРегОрг getСвРегОрг() {
        return свРегОрг;
    }
    public void setСвРегОрг(Файл.Документ.СвЮЛ.СвРегОрг свРегОрг) {
        this.свРегОрг = свРегОрг;
    }

    public Файл.Документ.СвЮЛ.СвРегПФ getСвРегПФ() {
        return свРегПФ;
    }
    public void setСвРегПФ(Файл.Документ.СвЮЛ.СвРегПФ свРегПФ) {
        this.свРегПФ = свРегПФ;
    }

    public Файл.Документ.СвЮЛ.СвРегФСС getСвРегФСС() {
        return свРегФСС;
    }
    public void setСвРегФСС(Файл.Документ.СвЮЛ.СвРегФСС свРегФСС) {
        this.свРегФСС = свРегФСС;
    }

    public List<Файл.Документ.СвЮЛ.СвСтатус> getСвСтатус() {
        return свСтатус;
    }
    public void setСвСтатус(List<Файл.Документ.СвЮЛ.СвСтатус> свСтатус) {
        this.свСтатус = свСтатус;
    }

    public List<Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ> getСвЗапЕГРЮЛ() {
        return свЗапЕГРЮЛ;
    }
    public void setСвЗапЕГРЮЛ(List<Файл.Документ.СвЮЛ.СвЗапЕГРЮЛ> свЗапЕГРЮЛ) {
        this.свЗапЕГРЮЛ = свЗапЕГРЮЛ;
    }

    public String getСпрОПФ() {
        return спрОПФ;
    }
    public void setСпрОПФ(String спрОПФ) {
        this.спрОПФ = спрОПФ;
    }

    public String getКодОПФ() {
        return кодОПФ;
    }
    public void setКодОПФ(String кодОПФ) {
        this.кодОПФ = кодОПФ;
    }

    public String getПолнНаимОПФ() {
        return полнНаимОПФ;
    }
    public void setПолнНаимОПФ(String полнНаимОПФ) {
        this.полнНаимОПФ = полнНаимОПФ;
    }
}
