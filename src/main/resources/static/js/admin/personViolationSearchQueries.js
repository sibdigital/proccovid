const personViolationSearchQueries = {
    body: {
        type: 'space',
        rows: [
            {
                height: 1,
                css: {"margin-top": "0px !important"},
                rows: [
                    {
                        cols: [
                            {},
                            {
                                id: 'filterGroup',
                                rows: [
                                    {
                                        id: 'datePickers',
                                        css: 'datePickers',
                                        hidden: true,
                                        rows: [
                                            {
                                                view: 'datepicker',
                                                id: 'search_beginSearchTime',
                                                label: 'C',
                                                timepicker: true,
                                                labelWidth: 30,
                                                width: 250,
                                                on: {
                                                    onChange: () => {
                                                        let value = $$('search_beginSearchTime').getValue();
                                                        value !== "" ? $('#datePickers').addClass('filter-data') : $('#datePickers').removeClass('filter-data');
                                                    }
                                                }
                                            },
                                            {
                                                view: 'datepicker',
                                                id: 'search_endSearchTime',
                                                label: 'По',
                                                timepicker: true,
                                                labelWidth: 30,
                                                width: 250,
                                                on: {
                                                    onChange: () => {
                                                        let value = $$('search_endSearchTime').getValue();
                                                        value !== "" ? $('#datePickers').addClass('filter-data') : $('#datePickers').removeClass('filter-data');
                                                    }
                                                }
                                            },
                                        ]
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'search_user',
                                        css: 'search_user_css',
                                        label: 'Выбрать',
                                        hidden: true,
                                        labelPosition: 'top',
                                        maxWidth: 300,
                                        scheme: {
                                            $init: function (o) {
                                                // o.$value = o.fullName;
                                                console.log(o)
                                            },
                                        },
                                        on: {
                                            onChange: () => {
                                                let value = $$('search_user').getValue();
                                                value !== "" ? $('#search_user').addClass('filter-data') : $('#search_user').removeClass('filter-data');
                                            }
                                        },
                                        options: 'all_cls_users',
                                    },
                                ]
                            }
                        ]
                    },
                ]
            },
            {
                rows: [
                    {
                        borderless: true,
                        height: 55,
                        css: {"display": "flex", "align-items": "center"},
                        template: () => {
                            const data = [
                                {id: 'datePickers', css: 'datePickers', name: 'Дата и время поиска'},
                                {id: 'search_user', css: 'search_user_css', name: 'Пользователь'}
                            ]

                            let result = get_group_filter_btns(data, reloadPersonViolationSearchQueries);
                            return result;
                        }
                    },
                    {
                        view: 'datatable',
                        id: 'person_violation_search_queries_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "name", header: "Пользователь", template: "#nameUser#", adjust: true, fillspace: true},
                            {
                                id: "numberFound",
                                header: "Количество найденных",
                                template: (obj) => {
                                    return obj.numberFound ?? 'Просмотр'
                                },
                                minWidth: 200,
                                width: 300,
                            },
                            {id: "time_Create", header: "Дата и время поиска", adjust: true, format: dateFormat},
                        ],
                        scheme: {
                            $init: function (obj) {
                                obj.time_Create = obj.timeCreate ? obj.timeCreate.replace("T", " ") : "";
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
                            onItemDblClick: function (id) {
                                const item = $$('person_violation_search_queries_table').getItem(id);

                                setTimeout(function () {
                                    showPersonViolationSearchQueryForm();

                                    webix.ajax().get('person_violation_search_query', {id: item.id})
                                        .then(function (data) {
                                            data = data.json();
                                            let date = xml_format(data.timeCreate.replace("T", " "));
                                            data.timeCreate = date;
                                            $$('personViolationSearchQueryForm').parse(data);
                                            data.idViolation !== null ? $$('idViolation').setValue("<a target='_blank' href='violation/view?id_person_violation=" + data.idViolation + "'>Действие: осуществлен просмотр нарушения № " + data.idViolation + "</a>")
                                                : $$('idViolation').setValue("Действие: осуществлен поиск");

                                        });
                                }, 100);
                            }
                        },
                        url: 'person_violation_search_queries'
                    },
                    {
                        view: 'pager',
                        id: 'Pager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    },
                ]
            },
        ]
    }
}


function showPersonViolationSearchQueryForm() {
    const personViolationSearchQueryForm = {
        view: 'form',
        id: 'personViolationSearchQueryForm',
        elements: [
            view_section('Параметры поиска'),
            {
                margin: 20,
                rows: [
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'nameUser',
                                id: 'nameUser',
                                label: 'Пользователь',
                                labelWidth: 110,
                            },
                            {
                                view: 'datepicker',
                                name: 'timeCreate',
                                id: 'timeCreate',
                                label: 'Дата и время поиска',
                                timepicker: true,
                                labelWidth: 160,
                            },
                        ]
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'lastname',
                                id: 'lastname',
                                label: 'Фамилия',
                                labelWidth: 80,
                            },
                            {
                                view: 'text',
                                name: 'firstname',
                                id: 'firstname',
                                label: 'Имя',
                                labelWidth: 50,
                            },
                            {
                                view: 'text',
                                name: 'patronymic',
                                id: 'patronymic',
                                label: 'Отчество',
                                labelWidth: 80,
                            },
                        ]
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'passportData',
                                id: 'passportData',
                                label: 'Паспортные данные',
                                labelWidth: 160,
                            },
                            {
                                view: 'text',
                                name: 'numberFile',
                                id: 'numberFile',
                                label: 'Номер дела',
                                labelWidth: 100,
                            },
                            {
                                view: 'text',
                                name: 'districtName',
                                id: 'districtName',
                                label: 'Район',
                                labelWidth: 60,
                            },
                        ]
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'numberFound',
                                id: 'numberFound',
                                width: 420,
                                label: 'По заданным параметрам поиска найдено:',
                                labelWidth: 320,
                            },
                            {
                                view: 'label',
                                name: 'idViolation',
                                id: 'idViolation',
                            },
                        ]
                    },
                    {
                        cols: [
                            {},
                            {
                                view: 'button',
                                align: 'right',
                                css: 'webix_primary',
                                value: 'Выход',
                                maxWidth: 300,
                                click: function () {
                                    showPersonViolationSearchQueries();
                                }
                            }
                        ]
                    },
                ]
            },
            {}
        ]
    }

    webix.ui({
        id: 'content',
        rows: [
            {
                view: 'scrollview',
                scroll: 'xy',
                id: 'show_layout',
                autowidth: true,
                autoheight: true,
                body: {
                    type: 'space',
                    rows: [
                        personViolationSearchQueryForm
                    ]
                }
            }
        ]
    }, $$('content'))
}

function showPersonViolationSearchQueries() {
    webix.ui({
        id: 'content',
        rows: [
            personViolationSearchQueries
        ]
    }, $$('content'))
}

function reloadPersonViolationSearchQueries() {
    $$('person_violation_search_queries_table').clearAll();

    const params = {};
    const beginSearchTime = $$('search_beginSearchTime').getValue();
    if (beginSearchTime != null) {
        params.bst = webix.i18n.timeFormatDate(beginSearchTime);
    }
    const endSearchTime = $$('search_endSearchTime').getValue();
    if (endSearchTime != null) {
        params.est = webix.i18n.timeFormatDate(endSearchTime);
    }
    const idUser = $$('search_user').getValue();
    if (idUser) {
        params.u = idUser;
    }

    $$('person_violation_search_queries_table').load(function () {
        return webix.ajax().get('person_violation_search_queries', params);
    });
}
