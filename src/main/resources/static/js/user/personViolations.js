const personViolations = {
    rows: [
        {
            view: 'toolbar',
            rows: [
                {
                    cols:[
                        {
                            view: 'text',
                            id: 'search_lastname',
                            label: 'Фамилия',
                            labelWidth: 100,
                            maxWidth: 300,
                        },
                        {
                            view: 'text',
                            id: 'search_firstname',
                            label: 'Имя',
                            labelWidth: 100,
                            maxWidth: 300,
                        },
                        {
                            view: 'text',
                            id: 'search_patronymic',
                            label: 'Отчество',
                            labelWidth: 100,
                            maxWidth: 300,
                        },
                        {
                            view: 'text',
                            id: 'search_passportData',
                            label: 'Паспортные данные',
                            labelWidth: 200,
                            // width: 300,
                        },
                    ]
                },
                {
                    cols: [
                        {
                            view: 'text',
                            id: 'search_numberFile',
                            label: 'Номер дела',
                            labelWidth: 100,
                            width: 300,
                        },
                        {},
                        {
                            view: 'button',
                            id: 'search_button',
                            minWidth: 220,
                            maxWidth: 350,
                            css: 'webix_primary',
                            value: 'Найти',
                            click: function () {
                                reloadPersonViolations();
                            },
                        }
                    ]
                }
            ]
        },
        {
            view: 'datatable',
            id: 'person_violations_table',
            select: 'row',
            navigation: true,
            resizeColumn: true,
            pager: 'Pager',
            datafetch: 25,
            columns: [
                {id: "fullName", header: "ФИО", template: "#fullName#", fillspace: true},
                {id: "regAddress", header: "Адрес регистрации", template: "#registrationAddress#", fillspace: true},
                {id: "nameTypeViolation", header: "Вид нарушения", template: "#nameTypeViolation#"},
                {id: "time_Create", header: "Дата добавления", format: DATE_FORMAT},
            ],
            scheme: {
                $init: function (obj) {
                    obj.time_Create = obj.timeCreate.replace("T", " ")
                },
            },
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
                    let item = $$('person_violations_table').getItem(id);

                    setTimeout(function () {
                        showPersonViolationForm();

                        webix.ajax().get('person_violation', {id: item.id})
                            .then(function (data) {
                                data = data.json();
                                $$('personViolationForm').parse(data);
                                $$('lastname').define('readonly', true);
                                $$('lastname').refresh();
                                $$('firstname').define('readonly', true);
                                $$('firstname').refresh();
                                $$('patronymic').define('readonly', true);
                                $$('patronymic').refresh();
                                $$('birthday').define('readonly', true);
                                $$('birthday').refresh();
                                $$('placeBirth').define('readonly', true);
                                $$('placeBirth').refresh();
                            });
                    }, 100);
                }
            },
            url: 'person_violations'
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
                    minWidth: 220,
                    maxWidth: 350,
                    css: 'webix_primary',
                    value: 'Добавить',
                    click: function () {
                        showPersonViolationForm();
                    },
                }
            ]
        }
    ]
}

function showPersonViolationForm() {
    const personViolationForm = {
        view: 'form',
        id: 'personViolationForm',
        elements: [
            {
                cols: [
                    {
                        view: 'text',
                        name: 'lastname',
                        id: 'lastname',
                        label: 'Фамилия',
                        labelPosition: 'top',
                        required: true,
                        validate: webix.rules.isNotEmpty,
                        invalidMessage: 'Поле не может быть пустым',
                    },
                    {
                        view: 'text',
                        name: 'firstname',
                        id: 'firstname',
                        label: 'Имя',
                        labelPosition: 'top',
                        required: true,
                        validate: webix.rules.isNotEmpty,
                        invalidMessage: 'Поле не может быть пустым',
                    },
                    {
                        view: 'text',
                        name: 'patronymic',
                        id: 'patronymic',
                        label: 'Отчество',
                        labelPosition: 'top',
                        // required: true,
                        // validate: webix.rules.isNumber,
                        // invalidMessage: 'Поле не может быть пустым',
                    },
                ]
            },
            {
                cols: [
                    {
                        view: 'datepicker',
                        name: 'birthday',
                        id: 'birthday',
                        label: 'Дата рождения',
                        labelPosition: 'top',
                        maxWidth: 200,
                        required: true,
                        validate: webix.rules.isNotEmpty,
                        invalidMessage: 'Поле не может быть пустым',
                    },
                    {
                        view: 'text',
                        name: 'placeBirth',
                        id: 'placeBirth',
                        label: 'Место рождения',
                        labelPosition: 'top',
                        required: true,
                        validate: webix.rules.isNotEmpty,
                        invalidMessage: 'Поле не может быть пустым',
                    },
                ]
            },
            {
                view: 'text',
                name: 'registrationAddress',
                id: 'registrationAddress',
                label: 'Адрес регистрации',
                labelPosition: 'top',
                required: true,
                validate: webix.rules.isNotEmpty,
                invalidMessage: 'Поле не может быть пустым',
            },
            // {
            //     view: 'text',
            //     name: 'residenceAddress',
            //     id: 'residenceAddress',
            //     label: 'Адрес проживания',
            //     labelPosition: 'top',
            //     required: true,
            //     validate: webix.rules.isNotEmpty,
            //     invalidMessage: 'Поле не может быть пустым',
            // },
            {
                view: 'text',
                name: 'passportData',
                id: 'passportData',
                label: 'Паспортные данные',
                labelPosition: 'top',
                required: true,
                validate: webix.rules.isNotEmpty,
                invalidMessage: 'Поле не может быть пустым',
            },
            {
                view: 'text',
                name: 'placeWork',
                id: 'placeWork',
                label: 'Место работы',
                labelPosition: 'top',
                // required: true,
                // validate: webix.rules.isNotEmpty,
                // invalidMessage: 'Поле не может быть пустым',
            },
            {
                view: 'richselect',
                id: 'idTypeViolation',
                name: 'idTypeViolation',
                label: 'Вид нарушения',
                labelPosition: 'top',
                required: true,
                validate: webix.rules.isNotEmpty,
                options: 'type_violations',
                invalidMessage: 'Поле не может быть пустым',
            },
            {
                view: 'template',
                type: 'section',
                template: 'Реквизиты судебного решения'
            },
            {
                cols: [
                    {
                        view: 'text',
                        name: 'numberFile',
                        id: 'numberFile',
                        label: 'Номер дела',
                        labelPosition: 'top',
                        // required: true,
                        // validate: webix.rules.isNotEmpty,
                        // invalidMessage: 'Поле не может быть пустым',
                    },
                    {
                        view: 'datepicker',
                        name: 'dateFile',
                        id: 'dateFile',
                        label: 'Дата',
                        labelPosition: 'top',
                        // required: true,
                        // validate: webix.rules.isNotEmpty,
                        // invalidMessage: 'Поле не может быть пустым',
                    }
                ]
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        id: 'save_button',
                        align: 'right',
                        css: 'webix_primary',
                        value: 'Сохранить',
                        maxWidth: 300,
                        click: function () {
                            if ($$('personViolationForm').validate()) {
                                $$('save_button').disable();

                                let params = $$('personViolationForm').getValues();

                                webix.ajax().headers({'Content-Type': 'application/json'}).post('/save_person_violation', JSON.stringify(params))
                                    .then(function (data) {
                                        if (data.text() === 'Нарушение сохранено') {
                                            webix.message({text: data.text(), type: 'success'});
                                            showPersonViolations();
                                        } else {
                                            webix.message({text: data.text(), type: 'error'});
                                        }
                                    });
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
                            showPersonViolations();
                        }
                    }
                ]
            }
        ]
    }

    webix.ui({
        id: 'content',
        rows: [
            personViolationForm
        ]
    }, $$('content'))
}

function showPersonViolations() {
    webix.ui({
        id: 'content',
        rows: [
            personViolations
        ]
    }, $$('content'))
}

function reloadPersonViolations() {
    $$('person_violations_table').clearAll();

    const params = {};
    const lastname = $$('search_lastname').getValue();
    if (lastname != '') {
        params.l = lastname;
    }
    const firstname = $$('search_firstname').getValue();
    if (firstname != '') {
        params.f = firstname;
    }
    const patronymic = $$('search_patronymic').getValue();
    if (patronymic != '') {
        params.p = patronymic;
    }
    const numberFile = $$('search_numberFile').getValue();
    if (numberFile != '') {
        params.nf = numberFile;
    }
    const passportData = $$('search_passportData').getValue();
    if (passportData != '') {
        params.pd = passportData;
    }

    $$('person_violations_table').load(function() {
        return webix.ajax().get('person_violations', params);
    });
}
