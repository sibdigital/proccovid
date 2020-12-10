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
        type: 'space',
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