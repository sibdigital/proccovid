const personViolationSearchQueries = {
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
                                cols: [
                                    {
                                        cols: [
                                            {
                                                view: 'datepicker',
                                                id: 'search_beginSearchTime',
                                                label: 'Дата и время поиска с',
                                                timepicker: true,
                                                labelWidth: 170,
                                                width: 340,
                                            },
                                            {
                                                view: 'datepicker',
                                                id: 'search_endSearchTime',
                                                label: 'по',
                                                timepicker: true,
                                                labelWidth: 30,
                                                width: 200,
                                            },
                                        ]
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'search_user',
                                        label: 'Пользователь',
                                        labelWidth: 150,
                                        maxWidth: 400,
                                        scheme: {
                                            $init: function (o) {
                                                // o.$value = o.fullName;
                                                console.log(o)
                                            },
                                        },
                                        options: 'all_cls_users',
                                    },
                                    {
                                        view: 'button',
                                        id: 'search_button',
                                        css: 'webix_primary',
                                        value: 'Найти',
                                        maxWidth: 300,
                                        click: function () {
                                            reloadPersonViolationSearchQueries()
                                        }
                                    }
                                ]
                            },
                        ]
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
                            onItemClick: function (id) {
                                const item = $$('person_violation_search_queries_table').getItem(id);

                                setTimeout(function () {
                                    showPersonViolationSearchQueryForm();

                                    webix.ajax().get('person_violation_search_query', {id: item.id})
                                        .then(function (data) {
                                            data = data.json();
                                            $$('personViolationSearchQueryForm').parse(data);
                                        });
                                }, 100);
                            }
                        },
                        url: 'person_violation_search_queries'
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
                        ]
                    }
                ]
            }
        ]
    }
}

function showPersonViolationSearchQueryForm() {
    const personViolationSearchQueryForm = {
        view: 'form',
        id: 'personViolationSearchQueryForm',
        elements: [
            {
                view: 'text',
                name: 'lastname',
                id: 'lastname',
                label: 'Фамилия',
                labelPosition: 'top',
            },
            {
                view: 'text',
                name: 'firstname',
                id: 'firstname',
                label: 'Имя',
                labelPosition: 'top',
            },
            {
                view: 'text',
                name: 'patronymic',
                id: 'patronymic',
                label: 'Отчество',
                labelPosition: 'top',
            },
            {
                view: 'text',
                name: 'passportData',
                id: 'passportData',
                label: 'Паспортные данные',
                labelPosition: 'top',
            },
            {
                view: 'text',
                name: 'numberFile',
                id: 'numberFile',
                label: 'Номер дела',
                labelPosition: 'top',
            },
            {
                view: 'text',
                name: 'districtName',
                id: 'districtName',
                label: 'Район',
                labelPosition: 'top',
            },
            {
                view: 'text',
                name: 'numberFound',
                id: 'numberFound',
                label: 'Количество найденных нарушений',
                labelPosition: 'top',
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        align: 'right',
                        css: 'webix_primary',
                        value: 'Отмена',
                        maxWidth: 300,
                        click: function () {
                            showPersonViolationSearchQueries();
                        }
                    }
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

    $$('person_violation_search_queries_table').load(function() {
        return webix.ajax().get('person_violation_search_queries', params);
    });
}
