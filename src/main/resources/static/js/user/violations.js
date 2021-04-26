webix.Date.startOnMonday = true;

const organization_form_data = [
    {id: 'search_inn', css: 'input_inn', name: 'ИНН'},
    {id: 'search_name', css: 'input_name', name: 'Наименование организации'},
    {id: 'search_numberFile', css: 'input_deal_number', name: 'Номер дела'},
    {id: 'picker_date', css: 'picker_date', name: 'Дата регистрации'},
    {id: 'search_district', css: 'select_district', name: 'Район'},
]

const violations = {
    rows: [
        {
            height: 1,
            width: 1,
            rows: [
                {
                    cols: [
                        {
                            view: 'text',
                            css: 'input_inn',
                            id: 'search_inn',
                            hidden: true,
                            // minWidth: 100,
                            label: 'Ввести',
                            labelPosition: 'top',
                            labelWidth: 100,
                            width: 250,
                            placeholder: "ИНН",
                            on: {
                                onChange: () => {
                                    let inn = $$('search_inn').getValue();
                                    inn !== "" ? $('#search_inn').addClass('filter-data') : $('#search_inn').removeClass('filter-data');
                                }
                            }
                        },
                        {
                            view: 'text',
                            css: 'input_name',
                            id: 'search_name',
                            hidden: true,
                            label: 'Ввести',
                            labelPosition: 'top',
                            labelWidth: 230,
                            width: 300,
                            placeholder: "Наименование организации",
                            on: {
                                onChange: () => {
                                    let name = $$('search_name').getValue();
                                    name !== "" ? $('#search_name').addClass('filter-data') : $('#search_name').removeClass('filter-data');
                                }
                            }
                        },
                    ]
                },
                {
                    cols: [
                        {
                            rows: [
                                {
                                    view: 'text',
                                    css: 'input_deal_number',
                                    id: 'search_numberFile',
                                    hidden: true,
                                    label: 'Ввести',
                                    labelWidth: 100,
                                    labelPosition: 'top',
                                    width: 250,
                                    placeholder: "Номер дела",
                                    on: {
                                        onChange: () => {
                                            let deal_number = $$('search_numberFile').getValue();
                                            deal_number !== "" ? $('#search_numberFile').addClass('filter-data') : $('#search_numberFile').removeClass('filter-data');
                                        }
                                    }
                                },
                            ]
                        },
                        {
                            css: 'picker_date',
                            id: 'picker_date',
                            hidden: true,
                            rows: [
                                {
                                    view: 'datepicker',
                                    css: 'picker_date_start',
                                    id: 'search_beginDateRegOrg',
                                    label: 'С',
                                    editable: true,
                                    labelWidth: 30,
                                    width: 250,
                                    on: {
                                        onChange: () => {
                                            let begin_date_reg_org = $$('search_beginDateRegOrg').getValue();
                                            begin_date_reg_org !== null ? $('#picker_date').addClass('filter-data') : $('#picker_date').removeClass('filter-data');
                                        }
                                    }
                                },
                                {
                                    view: 'datepicker',
                                    css: 'picker_date_end',
                                    id: 'search_endDateRegOrg',
                                    label: 'По',
                                    editable: true,
                                    labelWidth: 30,
                                    width: 250,
                                    on: {
                                        onChange: () => {
                                            let end_date_reg_org = $$('search_endDateRegOrg').getValue();
                                            end_date_reg_org !== null ? $('#picker_date').addClass('filter-data') : $('#picker_date').removeClass('filter-data');
                                        }
                                    }
                                },
                            ]
                        },
                        {
                            rows: [
                                {
                                    view: 'richselect',
                                    css: 'select_district',
                                    id: 'search_district',
                                    hidden: true,
                                    label: 'Выбрать',
                                    labelPosition: 'top',
                                    placeholder: 'Район',
                                    maxWidth: 300,
                                    options: 'cls_districts',
                                    on: {
                                        onChange: () => {
                                            let district = $$('search_district').getValue();
                                            district !== "" ? $('#search_district').addClass('filter-data') : $('#search_district').removeClass('filter-data');
                                        }
                                    }
                                },
                            ]
                        },
                        {
                            view: 'button',
                            id: 'search_button',
                            css: 'webix_primary',
                            value: 'Найти',
                            hidden: true,
                            maxWidth: 300,
                            click: function () {
                                reloadViolations()
                            }
                        }
                    ]
                },
            ]
        },
        {
            cols: [
                {
                    borderless: true,
                    height: 55,
                    template: () => {
                        let result = get_group_filter_btns(organization_form_data, reloadViolations);
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
                    id: 'violations_table',
                    select: 'row',
                    scrollX: false,
                    navigation: true,
                    fixedRowHeight: false,
                    rowLineHeight: 28,
                    resizeColumn: true,
                    pager: 'Pager',
                    datafetch: 25,
                    columns: [
                        {
                            id: "nameOrg",
                            width: 350,
                            minWidth: 300,
                            header: "Организация",
                            template: "#nameOrg#",
                            fillspace: true
                        },
                        {id: "inn", width: 150, header: "ИНН", template: "#innOrg#"},
                        {id: "district", width: 200, header: "Район", template: "#nameDistrict#"},
                        {id: "deal_number", width: 200, header: "Номер дела", template: "#numberFile#"},
                        {id: "deal_date", width: 200, header: "Дата дела", template: "#dateFile#"},
                        {
                            id: "nameTypeViolation",
                            width: 300,
                            minWidth: 200,
                            header: "Вид нарушения",
                            template: "#nameTypeViolation#"
                        },
                        {
                            id: "date_reg_org", width: 200, header: "Дата регистрации", template: (obj) => {
                                return obj.dateRegOrg || ""
                            }
                        },
                    ],
                    // scheme: {
                    //     $init: function (obj) {
                    //          obj.time_Create = obj.timeCreate.replace("T", " ")
                    //     },
                    // },
                    on: {
                        onBeforeLoad: function () {
                            this.showOverlay("Загружаю...");
                        },
                        onAfterLoad: function () {
                            this.hideOverlay();
                            if (!this.count()) {
                                this.showOverlay("Отсутствуют данные")
                            }
                            $$('violations_counter').setValue('Количество найденных нарушений: ' + this.count())
                        },
                        onLoadError: function () {
                            this.hideOverlay();
                        },
                        onItemDblClick: function (id) {
                            let item = $$('violations_table').getItem(id);
                            getViolationForm(item.id);
                        },
                        'data->onStoreUpdated': function () {
                            this.adjustRowHeight(null, true);
                        },
                    },
                    // onClick: {
                    //     'data->onStoreUpdated': function() {
                    //         this.adjustRowHeight(null, true);
                    //     },
                    // },
                    url: 'violations'
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
                            id: 'violations_counter',
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
                                showOrganizationSearchForm()
                            },
                        }
                    ]
                }
            ]
        }
    ]
}

function getViolationForm(idViolation) {
    setTimeout(function () {
        showViolationForm(idViolation);
        $$('btnsPanel').hide();
        showBtnBack(violations, 'violations_table');

        webix.ajax().get('violation', {id: idViolation})
            .then(function (data) {
                data = data.json();
                $$('violationForm').parse(data);
                $$('nameOrg').define('readonly', true);
                $$('nameOrg').refresh();
                $$('opfOrg').define('readonly', true);
                $$('opfOrg').refresh();
                $$('innOrg').define('readonly', true);
                $$('innOrg').refresh();
                if (data.ogrnOrg) {
                    $$('ogrnOrg').define('readonly', true);
                    $$('ogrnOrg').refresh();
                } else {
                    $$('ogrnOrg').hide();
                }
                if (data.kppOrg) {
                    $$('kppOrg').define('readonly', true);
                    $$('kppOrg').refresh();
                } else {
                    $$('kppOrg').hide();
                }
                $$('dateRegOrg').define('readonly', true);
                $$('dateRegOrg').refresh();
                $$('idTypeViolation').define('readonly', true);
                $$('idTypeViolation').refresh();
            });
    }, 100);
}

function showOrganizationSearchForm() {
    const organizationSearchForm = {
        view: 'form',
        id: 'organizationSearchForm',
        elements: [
            {
                view: 'search',
                id: 'searchInn',
                label: 'Введите ИНН организации',
                labelPosition: 'top',
                required: true,
                validate: webix.rules.isNumber,
                invalidMessage: 'Поле не может быть пустым',
                on: {
                    onSearchIconClick: findOrganizations,
                    onEnter: findOrganizations,
                }
            },
            {
                id: 'foundOrganizations',
                hidden: true,
                rows: [
                    {
                        view: 'list',
                        id: 'organizations',
                        template: '#value#',
                        type: {
                            height: 65
                        },
                        on: {
                            onItemClick: function (id) {
                                let item = $$('organizations').getItem(id);

                                setTimeout(function () {
                                    showViolationForm();

                                    $$('idDistrict').setValue(ID_DISTRICT);
                                    $$('idEgrul').setValue(item.idEgrul);
                                    $$('idEgrip').setValue(item.idEgrip);
                                    $$('idFilial').setValue(item.idFilial);
                                    $$('nameOrg').setValue(item.name);
                                    $$('nameOrg').define('readonly', true);
                                    $$('nameOrg').refresh();
                                    $$('innOrg').setValue(item.inn);
                                    $$('innOrg').define('readonly', true);
                                    $$('innOrg').refresh();
                                    if (item.ogrn) {
                                        $$('ogrnOrg').setValue(item.ogrn);
                                        $$('ogrnOrg').define('readonly', true);
                                        $$('ogrnOrg').refresh();
                                    } else {
                                        $$('ogrnOrg').hide();
                                    }
                                    if (item.kpp) {
                                        $$('kppOrg').setValue(item.kpp);
                                        $$('kppOrg').define('readonly', true);
                                        $$('kppOrg').refresh();
                                    } else {
                                        $$('kppOrg').hide();
                                    }
                                }, 100);
                            }
                        }
                    },
                ]
            },
            {
                id: 'notFoundOrganizations',
                hidden: true,
                rows: [
                    {
                        view: 'label',
                        label: 'Организация не найдена',
                        align: 'center',
                    },
                    {
                        view: 'button',
                        align: 'left',
                        css: 'webix_primary',
                        value: 'Продолжить',
                        maxWidth: 300,
                        click: function () {
                            const inn = $$('searchInn').getValue();

                            showViolationForm();

                            $$('idDistrict').setValue(ID_DISTRICT);
                            $$('innOrg').setValue(inn);
                            $$('innOrg').define('readonly', true);
                            $$('innOrg').refresh();
                        },
                    }
                ]
            }
        ]
    }

    webix.ui({
        id: 'content',
        rows: [
            organizationSearchForm
        ]
    }, $$('content'))
}

function findOrganizations() {
    const inn = $$('searchInn').getValue();
    if (inn === '') {
        $$('searchInn').focus();
        webix.alert("ИНН не введен", 'error');
        return;
    } else if (isNaN(inn)) {
        $$('searchInn').focus();
        webix.alert("ИНН не соответствует формату", 'error');
        return;
    }

    let type = '';
    if (inn.length === 10) {
        type = 'egrul';
    } else if (inn.length === 12) {
        type = 'egrip';
    }
    if (type === '') {
        $$('searchInn').focus();
        webix.alert("ИНН не соответствует формату", 'error');
        return;
    }

    webix.ajax(type + '?inn=' + inn).then(function (data) {
        const response = data.json();
        if (response.finded == true) {
            $$('notFoundOrganizations').hide();
            $$('foundOrganizations').show();
            $$('organizations').clearAll();
            let organizations = [];
            const result = response.data;
            if (type === 'egrul') {
                if (result.filials && result.filials.length > 0) {
                    const template = '<div style="line-height: 1em; margin-bottom: 0.5em">' + result.shortName + '</div>' +
                        '<div style="font-size: 0.85em; line-height: 1em">ИНН: ' + result.inn + '</div>' +
                        '<div style="font-size: 0.85em; line-height: 1em">Юридический адрес: ' + result.jurAddress + '</div>';
                    organizations.push({
                        id: result.id + '',
                        value: template,
                        idEgrul: result.id,
                        name: result.name,
                        inn: result.inn,
                        ogrn: result.ogrn,
                        kpp: result.kpp
                    });
                    const filials = result.filials.map(filial => {
                        const template = '<div style="line-height: 1em; margin-bottom: 0.5em">' + result.shortName + '</div>' +
                            '<div style="font-size: 0.85em; line-height: 1em">ИНН: ' + filial.inn + '</div>' +
                            '<div style="font-size: 0.85em; line-height: 1em">Юридический адрес: ' + filial.jurAddress + '</div>';
                        return {
                            id: filial.id + '_' + filial.filialId,
                            idEgrul: result.id,
                            idFilial: filial.filialId,
                            value: template,
                            name: filial.name,
                            inn: filial.inn,
                            ogrn: filial.ogrn,
                            kpp: filial.kpp
                        }
                    })
                    organizations = organizations.concat(filials);
                } else {
                    const template = '<div style="line-height: 1em; margin-bottom: 0.5em">' + result.shortName + '</div>' +
                        '<div style="font-size: 0.85em; line-height: 1em">ИНН: ' + result.inn + '</div>' +
                        '<div style="font-size: 0.85em; line-height: 1em">Юридический адрес: ' + result.jurAddress + '</div>';
                    organizations.push({
                        id: result.id + '',
                        value: template,
                        idEgrul: result.id,
                        name: result.name,
                        inn: result.inn,
                        ogrn: result.ogrn,
                        kpp: result.kpp
                    });
                }
                $$('organizations').parse(organizations);
            } else {
                organizations = result.map(egrip => {
                    const template = '<div style="line-height: 1em; margin-bottom: 0.5em">' + egrip.name + '</div>' +
                        '<div style="font-size: 0.85em; line-height: 1em">ИНН: ' + egrip.inn + '</div>' +
                        '<div style="font-size: 0.85em; line-height: 1em">Юридический адрес: ' + egrip.jurAddress + '</div>';
                    return {
                        id: egrip.id,
                        value: template,
                        idEgrip: egrip.id,
                        name: egrip.name,
                        inn: egrip.inn,
                        ogrn: egrip.ogrn
                    }
                })
                $$('organizations').parse(organizations);
            }
        } else {
            $$('foundOrganizations').hide();
            $$('notFoundOrganizations').show();
        }
    }).catch(function (reason) {
        console.log(reason);
        webix.alert("Не удалось получить данные", 'error');
    });
}

function showViolationForm(id) {
    const violationForm = {
        rows: [
            {
                view: 'toolbar',
                elements: [
                    {view: 'label', css: {"padding-left": "10px"}, label: 'Просмотр нарушения (id: ' + id + ').'},
                ],
            },
            {
                view: 'form',
                id: 'violationForm',
                elements: [
                    {
                        view: 'text',
                        id: 'idEgrul',
                        name: 'idEgrul',
                        hidden: true,
                    },
                    {
                        view: 'text',
                        id: 'idEgrip',
                        name: 'idEgrip',
                        hidden: true,
                    },
                    {
                        view: 'text',
                        id: 'idFilial',
                        name: 'idFilial',
                        hidden: true,
                    },
                    view_section('Реквизиты нарушителя'),
                    {
                        margin: 10,
                        rows: [
                            {
                                margin: 10,
                                cols: [
                                    {
                                        view: 'textarea',
                                        name: 'nameOrg',
                                        id: 'nameOrg',
                                        height: 130,
                                        label: 'Наименование организации/ИП',
                                        labelPosition: 'top',
                                        required: true,
                                        validate: webix.rules.isNotEmpty,
                                        invalidMessage: 'Поле не может быть пустым',
                                    },
                                    {
                                        margin: 10,
                                        rows: [
                                            {
                                                view: 'text',
                                                name: 'opfOrg',
                                                id: 'opfOrg',
                                                label: 'Организационно-правовая форма',
                                                labelPosition: 'top',
                                                required: true,
                                                validate: webix.rules.isNotEmpty,
                                                invalidMessage: 'Поле не может быть пустым',
                                            },
                                            {
                                                view: 'text',
                                                name: 'innOrg',
                                                id: 'innOrg',
                                                label: 'ИНН',
                                                labelPosition: 'top',
                                                required: true,
                                                validate: function (val) {
                                                    if (!val || isNaN(val) || !(val.length == 10 || val.length == 12)) {
                                                        return false;
                                                    }
                                                    return true;
                                                },
                                                invalidMessage: 'ИНН не соответствует формату',
                                            },
                                        ]
                                    },
                                ]
                            },
                        ]
                    },
                    view_section('Реквизиты нарушения'),
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'datepicker',
                                name: 'dateRegOrg',
                                id: 'dateRegOrg',
                                label: 'Дата регистрации',
                                editable: true,
                                labelPosition: 'top',
                                // required: true,
                                // validate: webix.rules.isNotEmpty,
                                // invalidMessage: 'Поле не может быть пустым',
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
                        ]
                    },
                    {
                        view: 'text',
                        name: 'ogrnOrg',
                        id: 'ogrnOrg',
                        label: 'ОГРН',
                        labelPosition: 'top',
                        // required: true,
                        hidden: true,
                        invalidMessage: 'ОГРН не соответствует формату',
                        validate: function (val) {
                            if (!val) {
                                return false;
                            }
                            if (isNaN(val)) {
                                return false;
                            }
                            if (val.length != 15) {
                                return false;
                            }
                            return true;
                        }
                    },
                    {
                        view: 'text',
                        name: 'kppOrg',
                        id: 'kppOrg',
                        label: 'КПП',
                        hidden: true,
                        labelPosition: 'top',
                        invalidMessage: 'КПП не соответствует формату',
                        validate: function (val) {
                            if (val) {
                                if (isNaN(val)) {
                                    return false;
                                }
                                if (val.length != 9) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    },
                    view_section('Реквизиты судебного решения'),
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
                                invalidMessage: 'Поле не может быть пустым и длина номера не должна превышать 100 симв.',
                                validate: function (val) {
                                    if (val) {
                                        if (val.length > 100 || val.length == 0) {
                                            return false;
                                        }
                                    } else {
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
                                    if ($$('violationForm').validate()) {
                                        $$('save_button').disable();

                                        let params = $$('violationForm').getValues();

                                        webix.ajax().headers({'Content-Type': 'application/json'}).post('save_violation', JSON.stringify(params))
                                            .then(function (data) {
                                                if (data.text() === 'Нарушение сохранено') {
                                                    webix.message({text: data.text(), type: 'success'});
                                                    showViolations();
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
                                    showViolations();
                                    hideBtnBack()
                                }
                            }
                        ]
                    }
                ]
            }
        ]

    }
    webix.ui({
        id: 'content',
        rows: [
            violationForm
        ]
    }, $$('content'))
}

function showViolations() {
    webix.ui({
        id: 'content',
        css: {"margin-top": "10px !important"},
        rows: [
            violations
        ]
    }, $$('content'))
}

async function reloadViolations() {
    $$('violations_table').clearAll();

    const inn = $$('search_inn').getValue();
    const nameOrg = $$('search_name').getValue();
    const numberFile = $$('search_numberFile').getValue();
    const beginDateRegOrg = $$('search_beginDateRegOrg').getValue();
    const endDateRegOrg = $$('search_endDateRegOrg').getValue();
    const idDistrict = $$('search_district').getValue();

    const format = webix.Date.dateToStr('%Y-%m-%d');

    let url = 'violations';
    let paramsString = '';
    let params = [
        {name: 'inn', value: inn},
        {name: 'name', value: nameOrg},
        {name: 'nf', value: numberFile},
        {name: 'bdr', value: format(beginDateRegOrg)},
        {name: 'edr', value: format(endDateRegOrg)},
        {name: 'd', value: idDistrict}
    ];

    params.forEach(e => {
        if (e.value != '') {
            paramsString += paramsString == '' ? '?' : '&';
            paramsString += e.name + '=' + e.value;
        }
    })

    await $$('violations_table').load(url + paramsString);
    paramsString !== '' && $$('violations_counter').setValue('Количество найденных нарушений: ' + $$('violations_table').count());
}

