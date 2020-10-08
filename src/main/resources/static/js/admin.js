webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
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
                    { view: 'checkbox', label: 'Удален', labelPosition: 'top', name: 'deleted' },
                    {
                        view: 'button',
                        css: 'webix_primary',
                        value: 'Сохранить',
                        click: function () {
                            if ($$('departmentForm').validate()) {
                                let params = $$('departmentForm').getValues();

                                webix.ajax().headers({
                                    'Content-Type': 'application/json'
                                }).post('/save_cls_department',
                                    JSON.stringify(params)
                                ).then(function (data) {
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
                        label: 'Подразделение, к которому по умолчанию будут направляться заявки',
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

webix.require(['js/views/requests.js', 'js/views/other-requests.js', 'js/views/showform-other.js']);

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
                        label: "<a href='statistic'>Статистика по заявкам</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='dacha/statistic'>Статистика по заявкам от дачников</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='barber/statistic'>Статистика по заявкам от парикмахерских</a>"
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
                        css: 'webix_dark',
                        data: [
                            { id: "Departments", value: 'Подразделения' },
                            { id: "DepartmentUsers", value: 'Пользователи подразделений' },
                            { id: "Requests", value: 'Заявки' },
                            { id: "TypeRequests", value: 'Типы заявок' },
                            { id: "Principals", value: 'Пользователи' },
                            { id: "Templates", value: 'Шаблоны сообщений' },
                            { id: "Statistic", value: 'Статистика' },
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
