const violations = {
    rows: [
        {
            view: 'toolbar',
            rows: [
                {
                    cols:[
                        {
                            view: 'search',
                            id: 'search',
                            maxWidth: 300,
                            minWidth: 100,
                            tooltip: 'После ввода значения нажмите Enter',
                            placeholder: "Введите ИНН",
                            on: {
                                onEnter: function () {
                                    reload();
                                }
                            }
                        },
                    ]
                }
            ]
        },
        {
            view: 'datatable',
            id: 'violations_table',
            select: 'row',
            navigation: true,
            resizeColumn: true,
            pager: 'Pager',
            datafetch: 25,
            columns: [
                {id: "nameOrg", header: "Наименование организации", template: "#nameOrg#", fillspace: true},
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
                    let item = $$('violations_table').getItem(id);
                    console.log(item)
                    setTimeout(function () {
                        showViolationForm();

                        $$('violationForm').parse(item);
                        $$('nameOrg').define('readonly', true);
                        $$('nameOrg').refresh();
                        $$('opfOrg').define('readonly', true);
                        $$('opfOrg').refresh();
                        $$('innOrg').define('readonly', true);
                        $$('innOrg').refresh();
                        if (item.ogrnOrg) {
                            $$('ogrnOrg').define('readonly', true);
                            $$('ogrnOrg').refresh();
                        } else {
                            $$('ogrnOrg').hide();
                        }
                        if (item.kppOrg) {
                            $$('kppOrg').define('readonly', true);
                            $$('kppOrg').refresh();
                        } else {
                            $$('kppOrg').hide();
                        }
                        $$('dateRegOrg').define('readonly', true);
                        $$('dateRegOrg').refresh();
                        $$('idTypeViolation').define('readonly', true);
                        $$('idTypeViolation').refresh();
                    }, 100);
                }
            },
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
                        showOrganizationSearchForm()
                    },
                }
            ]
        }
    ]
}

function showOrganizationSearchForm() {
    const organizationSearchForm = {
        view: 'form',
        id: 'organizationSearchForm',
        elements: [
            {
                view: 'search',
                id: 'searchInn',
                label: 'ИНН',
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
                                console.log(item)
                                setTimeout(function () {
                                    showViolationForm();

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
                    organizations.push({id: result.id + '', value: template, idEgrul: result.id, name: result.name, inn: result.inn, ogrn: result.ogrn, kpp: result.kpp});
                    const filials = result.filials.map(filial => {
                        const template = '<div style="line-height: 1em; margin-bottom: 0.5em">' + result.shortName + '</div>' +
                            '<div style="font-size: 0.85em; line-height: 1em">ИНН: ' + filial.inn + '</div>' +
                            '<div style="font-size: 0.85em; line-height: 1em">Юридический адрес: ' + filial.jurAddress + '</div>';
                        return {id: filial.id + '_' + filial.filialId, idEgrul: result.id, idFilial: filial.filialId, value: template, name: filial.name, inn: filial.inn, ogrn: filial.ogrn, kpp: filial.kpp}
                    })
                    organizations = organizations.concat(filials);
                } else {
                    organizations.push({id: result.id + '', data: result});
                }
                $$('organizations').parse(organizations);
            } else {
                organizations = result.map(egrip => {
                    const template = '<div style="line-height: 1em; margin-bottom: 0.5em">' + egrip.name + '</div>' +
                    '<div style="font-size: 0.85em; line-height: 1em">ИНН: ' + egrip.inn + '</div>' +
                    '<div style="font-size: 0.85em; line-height: 1em">Юридический адрес: ' + egrip.jurAddress + '</div>';
                    return {id: egrip.id, value: template, idEgrip: egrip.id, name: egrip.name, inn: egrip.inn, ogrn: egrip.ogrn}
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

function showViolationForm() {
    const violationForm = {
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
            {
                view: 'text',
                name: 'nameOrg',
                id: 'nameOrg',
                label: 'Наименование организации/ИП',
                labelPosition: 'top',
                required: true,
                validate: webix.rules.isNotEmpty,
                invalidMessage: 'Поле не может быть пустым',
            },
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
                validate: webix.rules.isNumber,
                invalidMessage: 'Поле не может быть пустым',
            },
            {
                view: 'text',
                name: 'ogrnOrg',
                id: 'ogrnOrg',
                label: 'ОГРН',
                labelPosition: 'top',
                required: true,
                validate: webix.rules.isNotEmpty,
                invalidMessage: 'Поле не может быть пустым',
            },
            {
                view: 'text',
                name: 'kppOrg',
                id: 'kppOrg',
                label: 'КПП',
                labelPosition: 'top',
            },
            {
                view: 'datepicker',
                name: 'dateRegOrg',
                id: 'dateRegOrg',
                label: 'Дата регистрации',
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
                            if ($$('violationForm').validate()) {
                                $$('save_button').disable();

                                let params = $$('violationForm').getValues();

                                webix.ajax().headers({'Content-Type': 'application/json'}).post('/save_violation', JSON.stringify(params))
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
                        }
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
        rows: [
            violations
        ]
    }, $$('content'))
}

function reload() {
    $$('violations_table').clearAll();

    const url = 'violations';
    let params = '';

    const inn = $$('search').getValue();
    if (inn != '') {
        params = '?inn=' + inn;
    }

    $$('violations_table').load(url + params);
}
