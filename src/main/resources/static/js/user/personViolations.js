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
                                showPersonViolationForm(item.id);
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
                                    }).finally(() => {
                                    webix.ajax().headers({'Content-Type': 'application/json'}).post('view_person_violation', JSON.stringify($$('personViolationForm').getValues()));

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
    const personViolationForm = person_violations_form(id);

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
