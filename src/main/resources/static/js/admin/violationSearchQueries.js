const violationSearchQueries = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
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
                                            reloadViolationSearchQueries()
                                        }
                                    }
                                ]
                            },
                        ]
                    },
                    {
                        view: 'datatable',
                        id: 'violation_search_queries_table',
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
                                const item = $$('violation_search_queries_table').getItem(id);

                                setTimeout(function () {
                                    showViolationSearchQueryForm();

                                    webix.ajax().get('violation_search_query', {id: item.id})
                                        .then(function (data) {
                                            data = data.json();
                                            $$('violationSearchQueryForm').parse(data);
                                        });
                                }, 100);
                            }
                        },
                        url: 'violation_search_queries'
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

function showViolationSearchQueryForm() {
    const violationSearchQueryForm = {
        view: 'form',
        id: 'violationSearchQueryForm',
        elements: [
            {
                view: 'text',
                name: 'innOrg',
                id: 'innOrg',
                label: 'ИНН',
                labelPosition: 'top',
            },
            {
                view: 'text',
                name: 'nameOrg',
                id: 'nameOrg',
                label: 'Наименование организации',
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
                view: 'datepicker',
                name: 'beginDateRegOrg',
                id: 'beginDateRegOrg',
                label: 'Дата регистрации с',
                labelPosition: 'top',
            },
            {
                view: 'datepicker',
                name: 'endDateRegOrg',
                id: 'endDateRegOrg',
                label: 'Дата регистрации по',
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
                            showViolationSearchQueries();
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
                        violationSearchQueryForm
                    ]
                }
            }
        ]
    }, $$('content'))
}

function showViolationSearchQueries() {
    webix.ui({
        id: 'content',
        rows: [
            violationSearchQueries
        ]
    }, $$('content'))
}

function reloadViolationSearchQueries() {
    $$('violation_search_queries_table').clearAll();

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

    $$('violation_search_queries_table').load(function() {
        return webix.ajax().get('violation_search_queries', params);
    });
}
