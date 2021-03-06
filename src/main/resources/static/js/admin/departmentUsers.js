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

                                setTimeout(function () {
                                    showDepartmentUserForm()

                                    if (data.idDepartment) {
                                        data.departmentId = data.idDepartment.id;
                                    }

                                    if (data.district) {
                                        data.districtId = data.district.id;
                                    }

                                    $$('departmentUserForm').parse(data);
                                    fillRoleList($$('user_role_table'), data.id)

                                    $$('newPassword').define('label', 'Новый пароль');
                                    $$('newPassword').refresh();
                                }, 100);
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
                            {
                                view: 'button',
                                align: 'right',
                                maxWidth: 300,
                                css: 'webix_primary',
                                value: 'Добавить',
                                click: function () {
                                    showDepartmentUserForm();
                                    fillRoleList($$('user_role_table'), -1)
                                    $$('newPassword').define('label', 'Пароль');
                                    $$('newPassword').define('required', true);
                                    $$('newPassword').refresh();
                                },
                            },
                            {
                                view: 'button',
                                align: 'right',
                                id: 'upload_department_users',
                                value: 'Загрузить',
                                maxWidth: 150,
                                click: function() {
                                    window.open('upload_department_users');
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

function showDepartmentUserForm() {
    const mainDepUserForm = {
        id: 'mainDepUserTab',
        view: 'scrollview',
        scroll: 'y',
        autowidth: true,
        autoheight: true,
        body: {
            // type: 'space',
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
                        {
                            view: 'combo',
                            name: 'districtId',
                            label: 'Район',
                            labelPosition: 'top',
                            options: 'cls_districts',
                            //required: true,
                            //validate: webix.rules.isNotEmpty
                        },
                        { view: 'text', label: 'Фамилия', labelPosition: 'top', name: 'lastname', required: true, validate: webix.rules.isNotEmpty },
                        { view: 'text', label: 'Имя', labelPosition: 'top', name: 'firstname', required: true, validate: webix.rules.isNotEmpty },
                        { view: 'text', label: 'Отчество', labelPosition: 'top', name: 'patronymic' },
                        { view: 'text', label: 'Адрес электронной почты', labelPosition: 'top', name: 'email', required: true, validate: webix.rules.isEmail },
                        { view: 'text', label: 'Логин', labelPosition: 'top', name: 'login', required: true, validate: webix.rules.isNotEmpty },
                        { view: 'text', id: 'newPassword', type: 'password', label: 'Новый пароль', labelPosition: 'top', name: 'newPassword', attributes: { autocomplete: 'new-password' } },
                        { view: 'checkbox', label: 'Администратор', labelPosition: 'top', name: 'admin' },

                        {}
                    ]
                }
            ]
        }
    }

    const roleDepUserForm = {
        id: 'roleDepUserTab',
        view: 'scrollview',
        scroll: 'y',
        body: {
            rows: [
                {
                    view: 'datatable', id: 'user_role_table',
                    columns: [
                        {
                            id: 'status',
                            name: 'status',
                            header: '',
                            template: '{common.checkbox()}',
                            editor: 'checkbox',
                            value: true
                        },
                        {id: 'name', header: 'Роль', adjust: true, fillspace: true},
                    ],
                    on: {
                        onCheck: function (row, column, state) {
                        },
                    },
                },
            ]
        }
    }

    const departmentUserForm = {
        // view: 'scrollview',
        // scroll: 'y',
        // body: {
        //     type: 'space',
            rows: [
                {
                    // id: 'departmentUserForm',
                    view: 'form',
                    rows: [
                        {
                            view: 'segmented',
                            id: 'departmentUserTabs',
                            multiview: true,
                            borderless: true,
                            value: 'mainDepUserTab',
                            optionWidth: 200,
                            options: [
                                {
                                    id: 'mainDepUserTab',
                                    value: 'Основное',
                                },
                                {
                                    id: 'roleDepUserTab',
                                    value: 'Роли',
                                },
                            ],
                        },
                        {
                            // id: 'tabview',
                            view:"multiview",
                            animate: false,
                            cells: [
                                mainDepUserForm,
                                roleDepUserForm
                            ]
                        },
                        {
                            cols: [
                                {},
                                {
                                    view: 'button',
                                    css: 'webix_primary',
                                    value: 'Сохранить',
                                    maxWidth: 300,
                                    click: function () {
                                        if ($$('departmentUserForm').validate()) {
                                            let params = $$('departmentUserForm').getValues();
                                            params.userRoles = $$('user_role_table').serialize();

                                            webix.ajax().headers({
                                                'Content-Type': 'application/json'
                                            }).post('save_cls_user',
                                                JSON.stringify(params)
                                            ).then(function (data) {
                                                if (data.text() === 'Пользователь сохранен') {
                                                    webix.message({text: data.text(), type: 'success'});
                                                    showDepartmentUsers();
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
                                        showDepartmentUsers();
                                    }
                                }
                            ]
                        },
                    ]
                }
            ]
        // }
    }


    webix.ui({
        id: 'content',
        rows: [
            departmentUserForm
        ]
    }, $$('content'))
}

function showDepartmentUsers() {
    webix.ui({
        id: 'content',
        rows: [
            departmentUsers
        ]
    }, $$('content'))
}


function fillRoleList(dtable, idDepUser) {
    dtable.clearAll();
    var xhr = webix.ajax().sync().get('user_roles/' + idDepUser);
    var jsonResponse = JSON.parse(xhr.responseText);
    dtable.parse(jsonResponse);
}

function saveRoles() {

}