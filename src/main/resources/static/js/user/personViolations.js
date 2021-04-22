webix.Date.startOnMonday = true;

const person_form_data = [
    {id: 'search_fio', css: 'input_fio', name: 'ФИО'},
    {id: 'search_passportData', css: 'input_passport_data', name: 'Паспортные данные'},
    {id: 'search_numberFile', css: 'input_deal_number_person', name: 'Номер дела'},
    {id: 'search_district', css: 'select_district', name: 'Район'},
]

const personViolations = {
    rows: [
        {
            height: 1,
            width: 1,
            rows: [
                {
                    cols: [
                        {
                            view: 'text',
                            css: 'input_fio',
                            id: 'search_fio',
                            label: 'Ввести',
                            labelPosition: 'top',
                            placeholder: 'ФИО',
                            hidden: true,
                            maxWidth: 300,
                            on: {
                                onChange: () => {
                                    let value = $$('search_fio').getValue();
                                    value !== "" ? $('#search_fio').addClass('filter-data') : $('#search_fio').removeClass('filter-data');
                                }
                            }
                        },
                        {
                            view: 'text',
                            css: 'input_passport_data',
                            id: 'search_passportData',
                            label: 'Ввести',
                            labelPosition: 'top',
                            placeholder: 'Паспортные данные',
                            hidden: true,
                            maxWidth: 300,
                            on: {
                                onChange: () => {
                                    let value = $$('search_passportData').getValue();
                                    value !== "" ? $('#search_passportData').addClass('filter-data') : $('#search_passportData').removeClass('filter-data');
                                }
                            }
                        },
                    ]
                },
                {
                    cols: [
                        {
                            view: 'text',
                            css: 'input_deal_number_person',
                            id: 'search_numberFile',
                            label: 'Ввести',
                            labelPosition: 'top',
                            placeholder: 'Номер дела',
                            hidden: true,
                            width: 300,
                            on: {
                                onChange: () => {
                                    let value = $$('search_numberFile').getValue();
                                    value !== "" ? $('#search_numberFile').addClass('filter-data') : $('#search_numberFile').removeClass('filter-data');
                                }
                            }
                        },
                        {
                            view: 'richselect',
                            css: 'select_district',
                            id: 'search_district',
                            label: 'Выбрать',
                            labelPosition: 'top',
                            placeholder: 'Район',
                            maxWidth: 300,
                            hidden: true,
                            options: 'cls_districts',
                            on: {
                                onChange: () => {
                                    let value = $$('search_district').getValue();
                                    value !== "" ? $('#search_district').addClass('filter-data') : $('#search_district').removeClass('filter-data');
                                }
                            }
                        },
                        {
                            view: 'button',
                            id: 'search_button',
                            maxWidth: 300,
                            hidden: true,
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
            cols: [
                {
                    borderless: true,
                    height: 55,
                    template: () => {
                        let result = get_group_filter_btns(person_form_data, reloadPersonViolations);
                        return result;
                    }
                },
            ]
        },
        {
            margin: 10,
            rows: [

                {
                    view: 'datatable',
                    id: 'person_violations_table',
                    select: 'row',
                    scrollX: false,
                    navigation: true,
                    resizeColumn: true,
                    pager: 'Pager',
                    datafetch: 25,
                    columns: [
                        {id: "fullName", minWidth: 300, header: "ФИО", template: "#fullName#", fillspace: true},
                        {id: "district", width: 200, header: "Район", template: "#nameDistrict#"},
                        {id: "deal_number", width: 200, header: "Номер дела", template: "#numberFile#"},
                        {id: "deal_number", width: 200, header: "Дата дела", template: "#dateFile#"},
                        {
                            id: "regAddress",
                            width: 250,
                            header: "Адрес регистрации",
                            template: "#registrationAddress#",
                            fillspace: true
                        },
                        {id: "nameTypeViolation", width: 250, header: "Вид нарушения", template: "#nameTypeViolation#"},
                        {
                            id: "date_reg_person", width: 200, header: "Дата регистрации", template: (obj) => {
                                return obj.dateRegPerson || ""
                            }
                        },
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
                            $$('person_violations_counter').setValue("Количество найденных нарушений: " + this.count())
                        },
                        onLoadError: function () {
                            this.hideOverlay();
                        },
                        onItemDblClick: function (id) {
                            let item = $$('person_violations_table').getItem(id);

                            setTimeout(function () {
                                showPersonViolationForm(id);
                                $$('btnsPanel').hide();
                                showBtnBack(personViolations, 'person_violations_table');

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
                            template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}',
                            minWidth: 300,
                            width: 300
                        },
                        {
                            view: 'label',
                            id: 'person_violations_counter',
                            minWidth: 300,
                            width: 300,
                        },
                        {},
                        {
                            view: 'button',
                            align: 'right',
                            minWidth: 220,
                            maxWidth: 350,
                            css: 'webix_primary',
                            value: 'Добавить',
                            click: function () {
                                showPersonViolationForm();

                                $$('idDistrict').setValue(ID_DISTRICT);
                            },
                        }
                    ]
                }
            ]
        }
    ]
}

function showPersonViolationForm(id) {
    const personViolationForm = {
        rows: [
            {
                view: 'toolbar',
                elements: [
                    {view: 'label', css: {"padding-left": "10px"}, label: 'Просмотр нарушения (id: ' + id + ').'},
                ],
            },
            {
                view: 'form',
                id: 'personViolationForm',
                elements: [
                    view_section('Реквизиты нарушителя'),
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'lastname',
                                id: 'lastname',
                                label: 'Фамилия',
                                labelWidth: 85,
                                width: 528,
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'text',
                                name: 'firstname',
                                id: 'firstname',
                                label: 'Имя',
                                labelWidth: 135,
                                labelPosition: 'right',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'text',
                                name: 'patronymic',
                                id: 'patronymic',
                                label: 'Отчество',
                                // required: true,
                                // validate: webix.rules.isNumber,
                                // invalidMessage: 'Поле не может быть пустым',
                            },
                        ]
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'datepicker',
                                name: 'birthday',
                                id: 'birthday',
                                label: 'Дата рождения',
                                labelWidth: 130,
                                editable: true,
                                width: 528,
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'text',
                                name: 'placeBirth',
                                id: 'placeBirth',
                                label: 'Место рождения',
                                labelWidth: 135,
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                        ]
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'textarea',
                                name: 'registrationAddress',
                                height: 90,
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
                                view: 'textarea',
                                name: 'passportData',
                                height: 90,
                                id: 'passportData',
                                label: 'Паспортные данные',
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                        ]
                    },
                    {
                        view: 'text',
                        name: 'placeWork',
                        id: 'placeWork',
                        label: 'Место работы',
                        labelWidth: 105,
                        // required: true,
                        // validate: webix.rules.isNotEmpty,
                        // invalidMessage: 'Поле не может быть пустым',
                    },
                    view_section('Реквизиты нарушения'),
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'datepicker',
                                name: 'dateRegPerson',
                                id: 'dateRegPerson',
                                label: 'Дата регистрации',
                                editable: true,
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
                                view: 'richselect',
                                name: 'idDistrict',
                                id: 'idDistrict',
                                label: 'Район',
                                labelPosition: 'top',
                                options: 'cls_districts',
                                required: true,
                                validate: webix.rules.isNotEmpty
                            },
                        ]
                    },
                    {
                        view: 'template',
                        type: 'section',
                        template: 'Реквизиты судебного решения'
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'numberFile',
                                id: 'numberFile',
                                label: 'Номер дела',
                                labelPosition: 'top',
                                required: true,
                                invalidMessage: 'Поле не может быть пустым, и длина номера не должна превышать 100 симв.',
                                validate: function (val) {
                                    if (val) {
                                        if (val.length > 100 || val.length == 0) {
                                            return false;
                                        }
                                    } else  {
                                        return false;
                                    }
                                    return true;
                                }
                            },
                            {
                                view: 'datepicker',
                                name: 'dateFile',
                                id: 'dateFile',
                                label: 'Дата',
                                editable: true,
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            }
                        ]
                    },
                    {
                        id: 'btnsPanel',
                        margin: 10,
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

                                        webix.ajax().headers({'Content-Type': 'application/json'}).post('save_person_violation', JSON.stringify(params))
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
                                    hideBtnBack()
                                }
                            }
                        ]
                    }
                ],
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
        css: {"margin-top": "10px !important"},
        rows: [
            personViolations
        ]
    }, $$('content'))
}

async function reloadPersonViolations() {
    $$('person_violations_table').clearAll();

    const fio = $$('search_fio').getValue();
    const numberFile = $$('search_numberFile').getValue();
    const passportData = $$('search_passportData').getValue();
    const idDistrict = $$('search_district').getValue();


    let url = 'person_violations';
    let paramsString = '';
    let params = [
        {name: 'fio', value: fio},
        {name: 'nf', value: numberFile},
        {name: 'pd', value: passportData},
        {name: 'd', value: idDistrict}
    ];

    params.forEach(e => {
        if (e.value != '') {
            paramsString += paramsString == '' ? '?' : '&';
            paramsString += e.name + '=' + e.value;
        }
    })

    await $$('person_violations_table').load(url + paramsString);
    paramsString !== '' && $$('person_violations_counter').setValue('Количество найденных нарушений: ' + $$('person_violations_table').count())
}
