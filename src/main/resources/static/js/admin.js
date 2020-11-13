webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function createOkved() {
    let params = $$('okvedCreateForm').getValues();
    webix.ajax().headers({
        'Content-Type': 'application/json'
    }).post('/save_okved',
        JSON.stringify(params)
    ).then(function (data) {
        if (data.text() === 'ОКВЭД сохранен') {
            webix.message({text: data.text(), type: 'success'});
            $$('window').close();
        } else {
            webix.message({text: data.text(), type: 'error'});
        }
    })
}

function addOkved(){
    let values = $$('form_okved').getValues()
    if(values.okved_richselect == ''){
        webix.message('не заполнены обязательные поля')
        return;
    }

    var name_okved = $$('okved_richselect').getText();
    var path = values.okved_richselect;
    var version = path.substring(0, 4);
    var found_element = $$('okved_table').find(function (obj) {
        return obj.name_okved == name_okved && obj.path == path;
    })

    if (found_element.length == 0) {
        $$('okved_table').add({
            name_okved: name_okved,
            path: path,
            version: version
        }, $$('okved_table').count() + 1)
    }
    else {
        webix.message('Уже добавлен этот ОКВЭД')
        return;
    }
}

function removeOkved() {
    if(!$$("okved_table").getSelectedId()){
        webix.message("Ничего не выбрано!");
        return;
    }
    webix.confirm('Вы действительно хотите удалить выбранный ОКВЭД?')
        .then(
            function () {
                $$("okved_table").remove($$("okved_table").getSelectedId());
            }
        )
}

const departments = {
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
                        id: 'departments_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        // pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "name", header: "Наименование", template: "#name#", adjust: true},
                            {id: "description", header: "Описание", template: "#description#", adjust: true},
                            {
                                id: "deleted", header: "Удален",
                                template: function (obj) {
                                    let text = '';
                                    if (obj.deleted === true) {
                                        text = 'Да'
                                    }
                                    return text;
                                },
                                adjust: true},
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

                                let data = $$('departments_table').getItem(id);

                                let window = webix.ui({
                                    view: 'window',
                                    id: 'window',
                                    head: 'Редактирование подразделения (id: ' + data.id + ').',
                                    close: true,
                                    width: 1000,
                                    height: 800,
                                    position: 'center',
                                    modal: true,
                                    body: departmentForm,
                                    on: {
                                        'onShow': function () {
                                            var xhr = webix.ajax().sync().get('department_okveds/' + data.id);
                                            var jsonResponse = JSON.parse(xhr.responseText);
                                            for (var k in jsonResponse) {
                                                var row = {
                                                    name_okved: jsonResponse[k].value,
                                                    path: jsonResponse[k].id,
                                                    version: jsonResponse[k].id.substring(0, 4)
                                                }
                                                $$('okved_table').add(row);
                                            }
                                            $$('okved_version').setValue('2014');
                                        },
                                        'onHide': function(){
                                            window.destructor();
                                        }
                                    }
                                });

                                $$('departmentForm').parse(data);

                                window.show();
                            }
                        },
                        url: 'cls_departments'
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
                                        head: 'Добавление подразделения',
                                        close: true,
                                        width: 1000,
                                        height: 800,
                                        position: 'center',
                                        modal: true,
                                        body: departmentForm,
                                        on: {
                                            'onShow': function () {
                                            }
                                        }
                                    });

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

const departmentForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'departmentForm',
                elements: [
                    { view: 'text', label: 'Наименование', labelPosition: 'top', name: 'name', required: true, validate: webix.rules.isNotEmpty },
                    { view: 'textarea', label: 'Описание', labelPosition: 'top', name: 'description', required: true, validate: webix.rules.isNotEmpty },
                    {
                        rows: [
                            {
                                view: 'label',
                                label: 'ОКВЭД',
                                align: 'left',
                            },
                            {
                                view: 'datatable', name: 'okved_table', label: '', labelPosition: 'top',
                                height: 200,
                                select: 'row',
                                editable: true,
                                id: 'okved_table',
                                columns: [
                                    {
                                        id: 'name_okved',
                                        header: 'ОКВЭД',
                                        fillspace: true,
                                    },
                                    {
                                        id: 'path',
                                        visible: true,
                                        hidden: true,
                                        fillspace: true,
                                    },
                                    {
                                        id: 'version',
                                        header: 'Версия',
                                        visible: true,
                                        fillspace: true,
                                    },
                                ],
                                data: [],
                            },
                            {
                                view: 'form',
                                id: 'form_okved',
                                elements: [
                                    {
                                        type: 'space',
                                        cols: [
                                            {   view: 'richselect',
                                                name: 'okved_version',
                                                id: 'okved_version',
                                                label: 'Версия',
                                                labelPosition: 'top',
                                                width: 200,
                                                required: true,
                                                options: [
                                                    {id: '2001', value:'2001'},
                                                    {id: '2014', value:'2014'},
                                                    {id: 'synt', value:'Синтетический'},
                                                    ],
                                                on: {
                                                    onChange() {
                                                        $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                                    }
                                                }},
                                            {   view: 'richselect',
                                                name: 'okved_richselect',
                                                id: 'okved_richselect',
                                                label: 'ОКВЭД',
                                                labelPosition: 'top',
                                                fillspace: true,
                                                required: true,
                                                options: {
                                                    url: 'okveds',
                                                    on: {
                                                        onShow: function () {
                                                            let values = $$('form_okved').getValues();
                                                            let version = values.okved_version;
                                                            if (version == '') {
                                                                webix.message('Выберите версию ОКВЭДа');
                                                                return;
                                                            }
                                                            this.getBody().filter(
                                                                function (obj) {
                                                                    if (typeof obj.id == 'string') {
                                                                        return obj.id.substring(0,4) == version;
                                                                    }
                                                                    else
                                                                        return false;
                                                                })
                                                        }
                                                    },
                                                },
                                                on: {
                                                    onChange() {
                                                        $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                                    }
                                                }},
                                        ]
                                    },
                                    {
                                        margin: 5,
                                        cols: [
                                            {view: 'button', value: 'Добавить',  click: addOkved },
                                            {view: 'button', value: 'Удалить',  click: removeOkved},
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    // { view: ''},
                    { view: 'checkbox', label: 'Удален', labelPosition: 'top', name: 'deleted' },
                    {
                        view: 'button',
                        css: 'webix_primary',
                        value: 'Сохранить',
                        click: function () {
                            if ($$('departmentForm').validate()) {
                                let params = $$('departmentForm').getValues();

                                let okveds = []
                                $$('okved_table').data.each(function (obj) {
                                    let okved = {
                                        id: obj.path,
                                        value: obj.name_okved
                                    }
                                    okveds.push(okved);
                                })
                                params.okveds = okveds;

                                webix.ajax().headers({
                                    'Content-Type': 'application/json'
                                }).post('/save_cls_department',
                                    params).then(function (data) {
                                    if (data.text() === 'Подразделение сохранено') {
                                        webix.message({text: data.text(), type: 'success'});

                                        $$('window').close();

                                        const departmentsTable = $$('departments_table');
                                        const url = departmentsTable.data.url;
                                        departmentsTable.clearAll();
                                        departmentsTable.load(url);
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

const departmentUsers = {
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
                        id: 'department_users_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "lasname", header: "Фамилия", template: "#lastname#", adjust: true},
                            {id: "firstname", header: "Имя", template: "#firstname#", adjust: true},
                            {id: "patronymic", header: "Отчество", template: "#patronymic#", adjust: true},
                            {id: "department", header: "Подразделение", template: "#idDepartment.name#", adjust: true},
                            {id: "login", header: "Логин", template: "#login#", adjust: true},
                            {
                                id: "admin", header: "Администратор",
                                template: function (obj) {
                                    let text = '';
                                    if (obj.admin === true) {
                                        text = 'Да'
                                    }
                                    return text;
                                },
                                adjust: true},
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

                                let data = $$('department_users_table').getItem(id);
                                if (data.idDepartment) {
                                    data.departmentId = data.idDepartment.id;
                                }

                                let window = webix.ui({
                                    view: 'window',
                                    id: 'window',
                                    head: 'Редактирование пользователя подразделения (id: ' + data.id + ').',
                                    close: true,
                                    width: 1000,
                                    height: 800,
                                    position: 'center',
                                    modal: true,
                                    body: departmentUserForm,
                                    on: {
                                        'onShow': function () {
                                        }
                                    }
                                });

                                $$('departmentUserForm').parse(data);

                                window.show();

                                $$('newPassword').define('label', 'Новый пароль');
                                $$('newPassword').refresh();
                            }
                        },
                        url: 'cls_users'
                    },
                    {
                        cols: [
                            {
                                view: 'pager',
                                id: 'Pager',
                                height: 38,
                                size: 25,
                                group: 5,
                                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            },
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
                                        head: 'Добавление пользователя подразделения',
                                        close: true,
                                        width: 1000,
                                        height: 800,
                                        position: 'center',
                                        modal: true,
                                        body: departmentUserForm,
                                        on: {
                                            'onShow': function () {
                                            }
                                        }
                                    });

                                    window.show();

                                    $$('newPassword').define('label', 'Пароль');
                                    $$('newPassword').define('required', true);
                                    $$('newPassword').refresh();
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

const departmentUserForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'departmentUserForm',
                elements: [
                    {
                        view: 'combo',
                        name: 'departmentId',
                        label: 'Подразделение',
                        labelPosition: 'top',
                        options: 'cls_departments',
                        required: true,
                        validate: webix.rules.isNotEmpty
                    },
                    { view: 'text', label: 'Фамилия', labelPosition: 'top', name: 'lastname', required: true, validate: webix.rules.isNotEmpty },
                    { view: 'text', label: 'Имя', labelPosition: 'top', name: 'firstname', required: true, validate: webix.rules.isNotEmpty },
                    { view: 'text', label: 'Отчество', labelPosition: 'top', name: 'patronymic' },
                    { view: 'text', label: 'Логин', labelPosition: 'top', name: 'login', required: true, validate: webix.rules.isNotEmpty },
                    { view: 'text', id: 'newPassword', type: 'password', label: 'Новый пароль', labelPosition: 'top', name: 'newPassword', attributes: { autocomplete: 'new-password' } },
                    { view: 'checkbox', label: 'Администратор', labelPosition: 'top', name: 'admin' },
                    {
                        view: 'button',
                        css: 'webix_primary',
                        value: 'Сохранить',
                        click: function () {
                            if ($$('departmentUserForm').validate()) {
                                let params = $$('departmentUserForm').getValues();

                                webix.ajax().headers({
                                    'Content-Type': 'application/json'
                                }).post('/save_cls_user',
                                    JSON.stringify(params)
                                ).then(function (data) {
                                    if (data.text() === 'Пользователь сохранен') {
                                        webix.message({text: data.text(), type: 'success'});

                                        $$('window').close();

                                        const departmentUsersTable = $$('department_users_table');
                                        const url = departmentUsersTable.data.url;
                                        departmentUsersTable.clearAll();
                                        departmentUsersTable.load(url);
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
            },
            {
                view: 'button',
                id: 'button_send_message',
                value: 'Отправить сообщение',
                disabled: false,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email_message', 'type=1402').then(function (data) {
                        webix.message(data.text());
                    });
                }
            },
            {
                view: 'button',
                id: 'button_send_message24',
                value: 'Отправить сообщение (не получившие в последние 24 часа)',
                disabled: false,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email_message24', 'type=1402').then(function (data) {
                        webix.message(data.text());
                    });
                }
            },
            {
                view: 'button',
                id: 'button_send_test_message',
                value: 'Отправить тестовое сообщение',
                disabled: false,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email_test_message', 'type=1402').then(function (data) {
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
/*
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
                                });*/
                                webix.ui(typeRequestForm, $$('typeRequestsId'));
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
                                    webix.ui(typeRequestForm, $$('typeRequestsId'));
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

//fix for paste into nic-editor pane
webix.html.addStyle(".myClass p{margin-top: 0px !important;line-height: 16px !important;}");

const typeRequestForm = {
    view: 'scrollview',
    scroll: 'xy',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
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
                        view:"tabview",
                        id:"tabs",
                        cells: [
                            //{ view: 'label', label: 'Предписание' },
                            {
                                header: "Предписание",
                                body:
                                {
                                    view: 'nic-editor',
                                    id: 'prescription',
                                    height: 450,
                                    css: "myClass",
                                    cdn: false,
                                    config: {
                                        iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                                    }
                                }
                            },
                            // { view: 'text', label: 'PrescriptionLink', labelPosition: 'top', name: 'prescriptionLink' },
                            //{ view: 'label', label: 'Дополнительные настройки' },
                            {
                                header: "Предписание",
                                body:
                                {
                                    view: 'ace-editor',
                                    id: 'settings',
                                    theme: 'github',
                                    mode: 'json',
                                    height: 450,
                                    cdn: false
                                }
                            },
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
                                                setTimeout(function() {
                                                    window.location.reload(true)
                                                }, 500)
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
                                    window.location.reload(true)
                                    //webix.ui(typeRequests, $$('show_layout'));
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

webix.require(['js/views/requests.js', 'js/views/other-requests.js', 'js/views/showform-other.js', 'js/views/okved_list.js']);

const adminRequests = {
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
                        view: 'toolbar',
                        rows: [
                            {
                                view: 'richselect',
                                id: 'department_filter',
                                css: 'smallText',
                                placeholder: 'Все подразделения',
                                options: 'cls_departments',
                                on: {
                                    onChange() {
                                        $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                    }
                                }
                            },
                            {
                                cols: [
                                    {
                                        view: 'segmented', id:'tabbar', value: 'requests', multiview: true,
                                        width: 600,
                                        optionWidth: 150,  align: 'center', padding: 10,
                                        options: [
                                            { value: 'Необработанные', id: 'requests'},
                                            { value: 'Принятые', id: 'accepted'},
                                            { value: 'Отклоненные', id: 'rejected'},
                                            { value: 'Прочие', id: 'other'}
                                        ],
                                        on: {
                                            onAfterRender() {
                                                this.callEvent('onChange', ['requests']);
                                            },
                                            onChange: function (id) {
                                                let status = 0
                                                switch (id) {
                                                    case 'requests':
                                                        status = 0;
                                                        $$('district_filter').setValue('');
                                                        $$('district_filter').hide();
                                                        $$('export_to_xlsx').hide();
                                                        $$('request_type').show();
                                                        $$('actualization_type').show();
                                                        break
                                                    case 'accepted':
                                                        status = 1;
                                                        $$('district_filter').setValue('');
                                                        $$('district_filter').hide();
                                                        $$('export_to_xlsx').hide();
                                                        $$('request_type').show();
                                                        $$('actualization_type').show();
                                                        break
                                                    case 'rejected':
                                                        status = 2;
                                                        $$('district_filter').setValue('');
                                                        $$('district_filter').hide();
                                                        $$('export_to_xlsx').hide();
                                                        $$('request_type').show();
                                                        $$('actualization_type').show();
                                                        break
                                                    case 'other':
                                                        status = 4;
                                                        $$('district_filter').show();
                                                        $$('export_to_xlsx').show();
                                                        $$('request_type').setValue('');
                                                        $$('request_type').hide();
                                                        $$('actualization_type').hide();
                                                        break
                                                }

                                                let departmentId = $$('department_filter').getValue();
                                                if (!departmentId) {
                                                    departmentId = 0;
                                                }

                                                let req_tbl_url = 'list_request/' + departmentId + '/' + status;

                                                let params = '';
                                                let request_type = $$('request_type').getValue();
                                                if (request_type) {
                                                    params = '?id_type_request=' + request_type;
                                                }

                                                let actualization = $$('actualization_type').getValue();
                                                if (actualization) {
                                                    let boolean_actualization = 0;
                                                    if (actualization=='id_true') {
                                                        boolean_actualization = 1;
                                                    }
                                                    params += params == '' ? '?' : '&';
                                                    params += 'is_actualization=' + boolean_actualization;
                                                }

                                                let district = $$('district_filter').getValue();
                                                if (district) {
                                                    params += params == '' ? '?' : '&';
                                                    params += 'id_district=' + district;
                                                }

                                                let search_text = $$('search').getValue();
                                                if (search_text) {
                                                    params += params == '' ? '?' : '&';
                                                    params += 'innOrName=' + search_text;
                                                }

                                                let view = requests(req_tbl_url + params, status, true);
                                                if (status == 4) {
                                                    view = other_requests(req_tbl_url + params);
                                                }

                                                webix.delay(function () {
                                                    webix.ui({
                                                        id: 'subContent',
                                                        rows: [
                                                            view
                                                        ]
                                                    }, $$('subContent'))
                                                })
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'request_type',
                                        width: 450,
                                        css: 'smallText',
                                        placeholder: 'Все типы заявок',
                                        options: 'cls_type_requests_short',
                                        on: {
                                            onChange() {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'district_filter',
                                        width: 250,
                                        css: 'smallText',
                                        placeholder: 'Все районы',
                                        options: 'cls_districts',
                                        hidden: true,
                                        on: {
                                            onChange() {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'actualization_type',
                                        css: 'smallText',
                                        placeholder: 'Все заявки',
                                        hidden: true,
                                        width: 200,
                                        options: [
                                            { value: 'Не актуализированные', id: 'id_false'},
                                            { value: 'Актуализированные', id: 'id_true'}
                                        ],
                                        on: {
                                            onChange() {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {
                                        view: 'search',
                                        id: 'search',
                                        maxWidth: 300,
                                        minWidth: 100,
                                        tooltip: 'после ввода значения нажмите Enter',
                                        placeholder: "Поиск по ИНН и названию",
                                        on: {
                                            onEnter: function () {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {},
                                    {
                                        view: 'button',
                                        align: 'right',
                                        id: 'export_to_xlsx',
                                        value: 'Выгрузить',
                                        width: 140,
                                        hidden: true,
                                        click: function() {
                                            let params = {};
                                            params.id_department = $$('department_filter').getValue();
                                            let status = $$('tabbar').getValue();
                                            switch (status) {
                                                case 'requests':
                                                    status = 0;
                                                    break
                                                case 'accepted':
                                                    status = 1;
                                                    break
                                                case 'rejected':
                                                    status = 2;
                                                    break
                                                case 'other':
                                                    status = 4;
                                                    break
                                            }
                                            params.status = status;
                                            params.id_type_request = $$('request_type').getValue();
                                            params.id_district = $$('district_filter').getValue();
                                            params.is_actualization = $$('actualization_filter').getValue();
                                            params.innOrName = $$('search').getValue();
                                            webix.ajax().response("blob").get('export_to_xlsx', params, function(text, data) {
                                                webix.html.download(data, 'request.xlsx');
                                            });
                                        }
                                    }
                                ],
                            }
                        ]
                    },
                    {
                        id: 'subContent'
                    }
                ]
            }
        ]
    }
}

const statistic = {
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
                        view: 'label',
                        label: "<a href='statistic' target='_blank'>Статистика по заявкам</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='dacha/statistic' target='_blank'>Статистика по заявкам от дачников</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='barber/statistic' target='_blank'>Статистика по заявкам от парикмахерских</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='actualOrganizations/statistic' target='_blank')>Статистика по актуальным заявкам по организациям</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='actualDepartments/statistic' target='_blank'>Статистика по актуальным заявкам по подразделениям</a>"
                    }
                ]
            }
        ]
    }
}

const okveds = {
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
                        view: 'toolbar',
                        rows: [
                            {
                                view: 'search',
                                id: 'search',
                                maxWidth: 300,
                                minWidth: 100,
                                tooltip: 'После ввода значения нажмите Enter',
                                placeholder: "Введите код или наименование из ОКВЭД",
                                on: {
                                    onEnter: function () {
                                        $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                    }
                                }
                            },
                            {
                                cols: [
                                    {
                                        view: 'segmented', id:'tabbar',  multiview: true,
                                        width: 600,
                                        optionWidth: 150,  align: 'left', padding: 10,
                                        options: [
                                            { value: '2001', id: '2001'},
                                            { value: '2014', id: '2014'},
                                            { value: 'Синтетические', id: 'synt'}
                                        ],
                                        on: {
                                            onAfterRender() {
                                                this.callEvent('onChange', ['2001']);
                                            },
                                            onChange: function (id) {
                                                let version = '2001';
                                                switch (id) {
                                                    case '2001':
                                                        version = '2001';
                                                        $$('create_okved').hide();
                                                        break
                                                    case '2014':
                                                        version = '2014';
                                                        $$('create_okved').hide();
                                                        break
                                                    case 'synt':
                                                        version = 'synt';
                                                        $$('create_okved').show();
                                                        break
                                                }

                                                let params = '';
                                                let search_text = $$('search').getValue();
                                                if (search_text) {
                                                    params += params == '' ? '?' : '&';
                                                    params += 'searchText=' + search_text;
                                                }

                                                let view = okvedslist('list_okved/' + version + params, version, true);

                                                webix.delay(function () {
                                                    webix.ui({
                                                        id: 'subContentOkved',
                                                        rows: [
                                                            view
                                                        ]
                                                    }, $$('subContentOkved'))
                                                })
                                            }
                                        }
                                    },
                                    {},
                                    {
                                        view: 'button',
                                        align: 'right',
                                        id: 'create_okved',
                                        value: 'Добавить',
                                        width: 140,
                                        hidden: true,
                                        click: function() {
                                            data = {version: 'synt', status: 1};
                                            let window = webix.ui({
                                                view: 'window',
                                                id: 'window',
                                                head: 'Создание синтетического ОКВЭДа',
                                                close: true,
                                                width: 1000,
                                                height: 800,
                                                position: 'center',
                                                modal: true,
                                                body: okvedCreateForm
                                            });

                                            $$('okvedCreateForm').parse(data);
                                            window.show();
                                        }
                                    }
                                ],
                            }
                        ]
                    },
                    {
                        id: 'subContentOkved'
                    }
                ]
            }
        ]
    }
}

const okvedCreateForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'okvedCreateForm',
                rows: [
                    {
                        cols: [
                            {view: 'text', label: 'Код', labelPosition: 'top', name: 'kindCode'},
                            {view: 'text', label: 'Версия', labelPosition: 'top', name: 'version', readonly: true},
                        ]
                    },
                    {view: 'text', label: 'Наименование', labelPosition: 'top', name: 'kindName'},
                    {view: 'textarea', label: 'Описание', labelPosition: 'top', name: 'description', autoheight: true},
                    {view: 'radio', label: 'Статус', name: 'status', name: 'status', options: [
                            {value: 'Работа разрешена', id: 1},
                            {value: 'Работа приостановлена', id: 0},
                        ]},
                    {cols: [
                            {view: 'button', value: 'Сохранить', click: createOkved},
                        ]}

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
                                label: DEPARTMENT + ' (<a href="/logout" title="Выйти">' + USER_NAME + '</a>)',
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
                        id: 'sidebar',
                        css: 'webix_dark',
                        data: [
                            { id: "Departments", value: 'Подразделения' },
                            { id: "DepartmentUsers", value: 'Пользователи подразделений' },
                            { id: "Requests", value: 'Заявки' },
                            { id: "TypeRequests", value: 'Типы заявок' },
                            { id: "Principals", value: 'Пользователи' },
                            { id: "Templates", value: 'Шаблоны сообщений' },
                            { id: "Statistic", value: 'Статистика' },
                            { id: "Okveds", value: 'ОКВЭДы' },
                        ],
                        on: {
                            onAfterSelect: function(id) {
                                let view;
                                switch (id) {
                                    case 'Departments': {
                                        view = departments;
                                        break;
                                    }
                                    case 'DepartmentUsers': {
                                        view = departmentUsers;
                                        break;
                                    }
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
                                    case 'Requests': {
                                        view = adminRequests;
                                        break;
                                    }
                                    case 'Statistic': {
                                        view = statistic;
                                        break;
                                    }
                                    case 'Okveds': {
                                        view = okveds;
                                        break;
                                    }
                                }
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        view
                                    ]
                                }, $$('content'))

                                if (id === 'Requests') {
                                    $$('department_filter').getList().add({id:'', value:'Все подразделения', $empty: true}, 0);
                                    $$('request_type').getList().add({id:'', value:'Все типы заявок', $empty: true}, 0);
                                    $$('district_filter').getList().add({id:'', value:'Все районы', $empty: true}, 0);
                                    $$('actualization_type').getList().add({id:'', value:'Все заявки', $empty: true}, 0)
                                }
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
        layout.define("height",window.innerHeight);
        layout.resize();
    });

})

