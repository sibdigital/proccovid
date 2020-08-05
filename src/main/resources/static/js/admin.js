webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

const principals = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'datatable',
                        id: 'principals_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                            // {id: "pass", header: "Пароль", template: "#password#", adjust: true},
                            {id: "email", header: "Email", template: "#organization.email#", adjust: true},
                            {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                            {id: "orgName", header: " Наименование организации/ИП", template: "#organization.name#", adjust: true},
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
                        },
                        url: 'cls_principals'
                    },
                    {
                        view: 'pager',
                        id: 'Pager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    }
                ]
            },
            {
                view: 'checkbox',
                label: 'Выбрать всех',
                labelWidth: 'auto',
                on: {
                    onChange(newVal, oldVal) {
                        if (newVal === 1) {
                            $$('button_send').enable()
                        } else {
                            $$('button_send').disable()
                        }
                    }
                }
            },
            {
                view: 'button',
                id: 'button_send',
                value: 'Отправить приглашение',
                disabled: true,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email', 'type=123').then(function (data) {
                        webix.message(data.text());
                    });
                }
            }
        ]
    }
}

const templates = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'datatable',
                        id: 'templates_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "key", header: "Код", template: "#key#", adjust: true},
                            {id: "value", header: "Шаблон", template: "#value#", adjust: true},
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
                        },
                        url: 'cls_templates'
                    },
                    {
                        view: 'pager',
                        id: 'Pager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    }
                ]
            }
        ]
    }
}

const typeRequests = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'datatable',
                        id: 'type_requests_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        // pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "activityKind", header: "Наименование", template: "#activityKind#", width: 1000},
                            // {id: "shortName", header: "Краткое наименование", template: "#shortName#", width: 300},
                            // {id: "prescription", header: "Prescription", template: "#prescription#", adjust: true},
                            // {id: "prescriptionLink", header: "PrescriptionLink", template: "#prescriptionLink#", adjust: true},
                            // {id: "settings", header: "Настройки", template: "#settings#", adjust: true},
                            // {id: "statusRegistration", header: "Статус регистрации", template: "#statusRegistration#", adjust: true},
                            // {id: "statusVisible", header: "Статус видимости", template: "#statusVisible#", adjust: true},
                            // {id: "beginVisible", header: "Дата начала видимости", template: "#beginVisible#", adjust: true},
                            // {id: "endVisible", header: "Дата конца видимости", template: "#endVisible#", adjust: true},
                            {id: "sortWeight", header: "Вес сортировки", template: "#sortWeight#", adjust: true},
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
                            onItemDblClick: function (id) {

                                let data = $$('type_requests_table').getItem(id);
                                if (data.department) {
                                    data.departmentId = data.department.id;
                                }

                                let window = webix.ui({
                                    view: 'window',
                                    id: 'window',
                                    head: 'Редактирование типа заявки (id: ' + data.id + ').',
                                    close: true,
                                    width: 1000,
                                    height: 800,
                                    position: 'center',
                                    modal: true,
                                    body: typeRequestForm,
                                    on: {
                                        'onShow': function () {
                                        }
                                    }
                                });

                                $$('typeRequestForm').parse(data);

                                $$('departments').getList().add({ id: '', value: '' });

                                $$('prescription').setValue(data.prescription);
                                $$('settings').setValue(data.settings);

                                if (data.beginRegistration) {
                                    $$('beginRegistration').setValue(new Date(data.beginRegistration));
                                }
                                if (data.endRegistration) {
                                    $$('endRegistration').setValue(new Date(data.endRegistration));
                                }
                                if (data.beginVisible) {
                                    $$('beginVisible').setValue(new Date(data.beginVisible));
                                }
                                if (data.endVisible) {
                                    $$('endVisible').setValue(new Date(data.endVisible));
                                }

                                window.show();
                            }
                        },
                        url: 'cls_type_requests'
                    },
                    {
                        cols: [
                            // {
                            //     view: 'pager',
                            //     id: 'Pager',
                            //     height: 38,
                            //     size: 25,
                            //     group: 5,
                            //     template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            // },
                            {},
                            {},
                            {},
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Добавить',
                                click: function () {

                                    let window = webix.ui({
                                        view: 'window',
                                        id: 'window',
                                        head: 'Добавление типа заявки',
                                        close: true,
                                        width: 1000,
                                        height: 800,
                                        position: 'center',
                                        modal: true,
                                        body: typeRequestForm,
                                        on: {
                                            'onShow': function () {
                                            }
                                        }
                                    });

                                    $$('departments').getList().add({ id: '', value: '' });

                                    window.show();
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

const typeRequestForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'typeRequestForm',
                elements: [
                    { view: 'text', label: 'Наименование', labelPosition: 'top', name: 'activityKind', required: true, validate: webix.rules.isNotEmpty },
                    { view: 'text', label: 'Краткое наименование', labelPosition: 'top', name: 'shortName', required: true, validate: webix.rules.isNotEmpty },
                    {
                        view: 'combo',
                        id: 'departments',
                        name: 'departmentId',
                        label: 'Министерство, к которому по умолчанию будут направляться заявки',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        options: 'cls_departments'
                    },
                    { view: 'label', label: 'Предписание' },
                    {
                        view: 'nic-editor',
                        id: 'prescription',
                        height: 200,
                        cdn: false,
                        config: {
                            iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                        }
                    },
                    // { view: 'text', label: 'PrescriptionLink', labelPosition: 'top', name: 'prescriptionLink' },
                    { view: 'label', label: 'Дополнительные настройки' },
                    {
                        view: 'ace-editor',
                        id: 'settings',
                        theme: 'github',
                        mode: 'json',
                        height: 150,
                        cdn: false
                    },
                    {
                        cols: [
                            {
                                view: 'checkbox',
                                label: 'Тип заявки доступен для подачи',
                                labelPosition: 'top',
                                name: 'statusRegistration',
                                on: {
                                    onAfterRender() {
                                        if (this.getValue() === 0) {
                                            $$('beginRegistration').disable();
                                            $$('endRegistration').disable();
                                        } else {
                                            $$('beginRegistration').enable();
                                            $$('endRegistration').enable();
                                        }
                                    },
                                    onChange(newVal, oldVal) {
                                        if (newVal === 0) {
                                            $$('beginRegistration').disable();
                                            $$('endRegistration').disable();
                                        } else {
                                            $$('beginRegistration').enable();
                                            $$('endRegistration').enable();
                                        }
                                    }
                                }
                            },
                            { view: 'datepicker', label: 'Дата начала подачи', labelPosition: 'top', name: 'beginRegistration', timepicker: true, id: 'beginRegistration'},
                            { view: 'datepicker', label: 'Дата конца подачи', labelPosition: 'top', name: 'endRegistration', timepicker: true, id: 'endRegistration'},
                        ]
                    },
                    {
                        cols: [
                            {
                                view: 'checkbox',
                                label: 'Тип заявки виден для подачи',
                                labelPosition: 'top',
                                name: 'statusVisible',
                                on: {
                                    onAfterRender() {
                                        if (this.getValue() === 0) {
                                            $$('beginVisible').disable();
                                            $$('endVisible').disable();
                                        } else {
                                            $$('beginVisible').enable();
                                            $$('endVisible').enable();
                                        }
                                    },
                                    onChange(newVal, oldVal) {
                                        if (newVal === 0) {
                                            $$('beginVisible').disable();
                                            $$('endVisible').disable();
                                        } else {
                                            $$('beginVisible').enable();
                                            $$('endVisible').enable();
                                        }
                                    }
                                }
                            },
                            { view: 'datepicker', label: 'Дата начала видимости', labelPosition: 'top', name: 'beginVisible', timepicker: true, id: 'beginVisible'},
                            { view: 'datepicker', label: 'Дата конца видимости', labelPosition: 'top', name: 'endVisible', timepicker: true, id: 'endVisible'},
                        ]
                    },
                    { view: 'text', label: 'Вес сортировки', labelPosition: 'top', name: 'sortWeight', required: true, validate: webix.rules.isNumber },
                    {
                        view: 'button',
                        css: 'webix_primary',
                        value: 'Сохранить',
                        click: function () {
                            if ($$('typeRequestForm').validate()) {
                                let params = $$('typeRequestForm').getValues();
                                params.prescription = $$('prescription').getValue();
                                params.settings = $$('settings').getValue();

                                webix.ajax().headers({
                                    'Content-Type': 'application/json'
                                }).post('/save_cls_type_request',
                                    JSON.stringify(params)
                                ).then(function (data) {
                                    if (data.text() === 'Тип заявки сохранен') {
                                        webix.message({text: data.text(), type: 'success'});

                                        $$('window').close();

                                        const typeRequestTable = $$('type_requests_table');
                                        const url = typeRequestTable.data.url;
                                        typeRequestTable.clearAll();
                                        typeRequestTable.load(url);
                                    } else {
                                        webix.message({text: data.text(), type: 'error'});
                                    }
                                })
                            } else {
                                webix.message({text: 'Не заполнены обязательные поля', type: 'error'});
                            }
                        }
                    }
                ]
            }
        ]
    }
}

webix.ready(function() {
    let layout = webix.ui({
        rows: [
            {
                view: 'toolbar',
                autoheight: true,
                rows: [
                    {
                        responsive: 't1',
                        css: 'webix_dark',
                        cols: [
                            {
                                view: 'label',
                                minWidth: 400,
                                label: '<span style="font-size: 1.0rem">Личный кабинет администратора</span>',
                            },
                            {},
                            {
                                view: 'label',
                                label: '<a href="/logout" title="Выйти">Выйти</a>',
                                align: 'right'
                            }
                        ]
                    }
                ]
            },
            {
                cols: [
                    {
                        view: 'sidebar',
                        css: 'webix_dark',
                        data: [
                            { id: "TypeRequests", value: 'Типы заявок' },
                            { id: "Principals", value: 'Пользователи' },
                            { id: "Templates", value: 'Шаблоны сообщений' },
                        ],
                        on: {
                            onAfterSelect: function(id) {
                                let view;
                                switch (id) {
                                    case 'Principals': {
                                        view = principals;
                                        break;
                                    }
                                    case 'Templates': {
                                        view = templates;
                                        break;
                                    }
                                    case 'TypeRequests': {
                                        view = typeRequests;
                                        break;
                                    }
                                }
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        view
                                    ]
                                }, $$('content'))
                            }
                        }
                    },
                    {
                        id: 'content'
                    }
                ],
            }
        ]
    })

    webix.event(window, "resize", function (event) {
        layout.define("width",document.body.clientWidth);
        layout.resize();
    });
})
