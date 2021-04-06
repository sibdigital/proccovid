function showTypeViolations() {
    webix.ui({
        id: 'content',
        rows: [
            typeViolations
        ]
    }, $$('content'));
}

const typeViolations = {
    view: 'scrollview',
    scroll: 'xy',
    id: "prescriptionsId",
    body: {
        type: 'space',
        rows: [
            {
                view: 'datatable',
                id: 'type_violations_table',
                select: 'row',
                navigation: true,
                resizeColumn: true,
                // pager: 'Pager',
                datafetch: 25,
                columns: [
                    {id: "nameTypeViolation", header: "Наименование", template: "#name#", fillspace: true},
                ],
                on: {
                    onBeforeLoad: function () {
                        this.showOverlay("Загружаю...");
                    },
                    onAfterLoad: function () {
                        this.hideOverlay();
                        if (!this.count()) {
                            this.showOverlay("Отсутствуют данные")
                        }
                    },
                    onLoadError: function () {
                        this.hideOverlay();
                    },
                    onItemClick: function (id) {
                        let data = $$('type_violations_table').getItem(id);

                        setTimeout(function () {
                            showTypeViolationForm();
                            $$('typeViolationForm').parse(data);
                        }, 100);
                    }
                },
                url: 'type_violations'
            },
            {
                cols: [
                    {
                        // view: 'pager',
                        // id: 'Pager',
                        // height: 38,
                        // size: 25,
                        // group: 5,
                        // template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    },
                    {
                        view: 'button',
                        align: 'right',
                        minWidth: 220,
                        maxWidth: 350,
                        css: 'webix_primary',
                        value: 'Добавить',
                        click: function () {
                            showTypeViolationForm();
                        }
                    }
                ]
            }
        ]
    }
}

function showTypeViolationForm() {
    webix.ui({
        id: 'content',
        rows: [
            typeViolationForm
        ]
    }, $$('content'));
}

const typeViolationForm = {
    view: 'scrollview',
    scroll: 'xy',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'typeViolationForm',
                elements: [
                    {
                        view: 'text',
                        label: 'Наименование',
                        labelPosition: 'top',
                        name: 'name',
                        required: true,
                        validate: webix.rules.isNotEmpty,
                        invalidMessage: 'Поле не может быть пустым',
                    },
                    {
                        view: 'textarea',
                        label: 'Описание',
                        labelPosition: 'top',
                        name: 'description',
                        // required: true,
                        // validate: webix.rules.isNotEmpty,
                        // invalidMessage: 'Поле не может быть пустым',
                    },
                    {
                        cols: [
                            {},
                            {
                                view: 'button',
                                align: 'right',
                                css: 'webix_primary',
                                value: 'Сохранить',
                                maxWidth: 300,
                                click: function () {
                                    if ($$('typeViolationForm').validate()) {
                                        let params = $$('typeViolationForm').getValues();

                                        webix.ajax().headers({
                                            'Content-Type': 'application/json'
                                        }).post('save_type_violation',
                                            JSON.stringify(params)
                                        ).then(function (data) {
                                            if (data.text() === 'Вид нарушения сохранен') {
                                                webix.message({text: data.text(), type: 'success'});
                                                showTypeViolations();
                                            } else {
                                                webix.message({text: data.text(), type: 'error'});
                                            }
                                        })
                                    } else {
                                        webix.message({text: 'Не заполнены обязательные поля', type: 'error'});
                                    }
                                }
                            },
                            {
                                view: 'button',
                                align: 'right',
                                css: 'webix_primary',
                                value: 'Отмена',
                                maxWidth: 300,
                                click: function () {
                                    showTypeViolations();
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}
