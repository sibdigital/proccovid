package ru.sibdigital.proccovid.model;

import java.util.List;

public class ClsTypeRequestSettings {

    List<Field> fields;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public static class Field {

        protected UI ui;
        protected int pos;

        public UI getUi() {
            return ui;
        }

        public void setUi(UI ui) {
            this.ui = ui;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public static class UI {

            protected String view;
            protected String template;

            public String getView() {
                return view;
            }

            public void setView(String view) {
                this.view = view;
            }

            public String getTemplate() {
                return template;
            }

            public void setTemplate(String template) {
                this.template = template;
            }
        }
    }
}
