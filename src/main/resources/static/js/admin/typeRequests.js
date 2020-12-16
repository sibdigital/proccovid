const typeRequests = {
    view: 'scrollview',
    scroll: 'xy',
    id: "typeRequestsId",
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

                                loadTypeRequestFormInContent()

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
                                href: "/type_request",
                                click: function () {
                                    loadTypeRequestFormInContent()

                                    $$('departments').getList().add({ id: '', value: '' });
                                }
                                /*let window = webix.ui({
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

                                window.show();*/

                            }
                        ]
                    }
                ]
            }
        ]
    }
}
//Загрузка формы в контент сайта
function loadTypeRequestFormInContent(){
    webix.ui({
        id: 'content',
        rows: [
            typeRequestForm
        ]
    }, $$('content'))

    $$("tabs").addOption('settings', 'Дополнительные настройки', true);
    $$("tabs").addOption('prescription', 'Предписание', true,0);
}
//fix for paste into nic-editor pane
webix.html.addStyle(".myClass p{margin-top: 0px !important;line-height: 16px !important;}");

const typeRequestForm = {
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
                id: 'typeRequestForm',
                elements: [
                    { view: 'text', labelWidth:190,label: 'Наименование',  name: 'activityKind', required: true, validate: webix.rules.isNotEmpty },
                    { view: 'text', labelWidth:190,label: 'Краткое наименование', name: 'shortName', required: true, validate: webix.rules.isNotEmpty },
                    {
                        view: 'combo',
                        id: 'departments',
                        name: 'departmentId',
                        label: 'Подразделение, к которому по умолчанию будут направляться заявки',
                        labelWidth:500,
                        invalidMessage: 'Поле не может быть пустым',
                        options: 'cls_departments'
                    },
                    {
                        view: "tabbar",
                        id: "tabs",
                        multiview: true,
                        borderless:true,
                        width: 350,
                        options: []
                    },
                    {
                        id:"views",
                        animate:false,
                        minHeight: 300,
                        cells: [
                            {
                                view: 'nic-editor',
                                id: 'prescription',
                                css: "myClass",
                                cdn: false,
                                config: {
                                    iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                                }
                            },
                            {
                                view: 'ace-editor',
                                id: 'settings',
                                theme: 'github',
                                mode: 'json',
                                cdn: false
                            }
                        ]
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
                    { view: 'text', label: 'Вес сортировки',labelWidth:190, name: 'sortWeight', required: true, validate: webix.rules.isNumber }, //
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
                                                //$$('window').close();
                                                //const typeRequestTable = $$('type_requests_table');
                                                //const url = typeRequestTable.data.url;
                                                //typeRequestTable.clearAll();
                                                //typeRequestTable.load(url);
                                                //webix.ui(typeRequests, $$('show_layout'));
                                                webix.ui({
                                                    id: 'content',
                                                    rows: [
                                                        typeRequests
                                                    ]
                                                }, $$('content'))
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
                                maxWidth: 300   ,
                                click: function (){
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            typeRequests
                                        ]
                                    }, $$('content'))
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}
