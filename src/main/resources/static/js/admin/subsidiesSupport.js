
// const DATE_FORMAT = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

// function getSubsidies(param_url, status, view_item_another_page) {
//     return {
//         autowidth: true,
//         autoheight: true,
//         rows: [
//             {
//                 view: 'datatable',
//                 id: 'requests_table',
//                 select: 'row',
//                 navigation: true,
//                 resizeColumn: true,
//                 pager: 'Pager',
//                 datafetch: 25,
//                 columns: [
//                     {
//                         id: "orgName",
//                         header: "Организация/ИП",
//                         template: "#organization.name#",
//                         adjust: true
//                     },
//                     {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
//                     {id: "ogrn", header: "ОГРН", template: "#organization.ogrn#", adjust: true},
//                     // {id: "typeRequest", header: "Вид деятельности", template: "#typeRequest.activityKind#", adjust: true},
//                     // {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
//                     {id: "subsidy", header: "Субсидия", template: "#subsidyRequestStatus.subsidy.shortName#", adjust: true},
//                     {id: "time_send", header: "Дата подачи", adjust: true, format: DATE_FORMAT },
//                     // {id: "personSlrySaveCnt", header: "Числ. с сохр. зп", adjust: true},
//                     // {id: "personOfficeCnt", header: "Числ. работающих", adjust: true},
//                     // {id: "personRemoteCnt", header: "Числе. удал. режим", adjust: true},
//                 ],
//                 scheme: {
//                     $init: function (obj) {
//                         obj.time_Create = obj.timeCreate.replace("T", " ") //dateUtil.toDateFormat(obj.timeCreate);
//                     },
//                 },
//                 on: {
//                     onBeforeLoad: function () {
//                         this.showOverlay("Загружаю...");
//                     },
//                     onAfterLoad: function () {
//                         this.hideOverlay();
//                         if (!this.count()) {
//                             this.showOverlay("Отсутствуют данные")
//                         }
//                     },
//                     onLoadError: function () {
//                         this.hideOverlay();
//                     },
//                     onItemDblClick: function (id) {
//                         this.hideOverlay();
//                         let data = $$('requests_table').getItem(id);
//
//                         if (!view_item_another_page) {
//                             showRequestForm(data.id);
//                         } else {
//                             window.open('request/view?id=' + data.id);
//                         }
//                     }
//                 },
//                 url: param_url
//             },
//             {
//                 view: 'pager',
//                 id: 'Pager',
//                 height: 38,
//                 size: 25,
//                 group: 5,
//                 template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
//             }
//         ]
//     }
// }

const subsidiesSupport = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        value: 'subsidies',

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
                                        view: 'richselect',
                                        id: 'subsidy_type',
                                        // width: 450,
                                        fillspace: true,
                                        css: 'smallText',
                                        placeholder: 'Все виды субсидии',
                                        options: 'cls_subsidy_short',
                                        on: {
                                            onChange() {
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'subsidy_request_status',
                                        width: 450,
                                        css: 'smallText',
                                        placeholder: 'Все статусы',
                                        options: 'cls_subsidy_request_status_short',
                                        on: {
                                            onChange() {
                                            }
                                        }
                                    },
                                    {
                                        view: 'search',
                                        id: 'search_inn',
                                        width: 450,
                                        maxWidth: 450,
                                        minWidth: 100,
                                        tooltip: 'после ввода значения нажмите "Найти"',
                                        placeholder: "Поиск по ИНН и названию",
                                    },
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

                                ],
                            },
                        ]
                    },
                    {
                        borderless: true,
                        height: 55,
                        css: {"display":"flex", "align-items":"center"},
                        template: () => {
                            const data = [
                                {id: 'datePickers', css: 'datePickers', name: 'Дата и время поиска'},
                            ]


                            let result = get_group_filter_btns(data, reloadSubsidiesSearchQueries, clearSubsidiesSearchQueries);
                            return result;
                        }
                    },
                    {
                        view: 'datatable',
                        id: 'subsidies_table',
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {
                                id: "orgName",
                                header: "Организация/ИП",
                                template: "#organization.name#",
                                adjust: true
                            },
                            {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                            {id: "ogrn", header: "ОГРН", template: "#organization.ogrn#", adjust: true},
                            {id: "subsidy", header: "Субсидия", template: "#subsidy.shortName#", adjust: true},
                            {id: "timeSend", header: "Дата подачи", template: '#timeSend#', adjust: true, format: webix.Date.dateToStr("%d.%m.%Y %H:%i:%s") },
                        ],
                        scheme: {
                            $init: function (obj) {
                                obj.time_Create = obj.timeCreate.replace("T", " ") //dateUtil.toDateFormat(obj.timeCreate);
                            },
                        },
                        on: {
                            onBeforeLoad: function () {
                                this.showOverlay("Загружаю...");
                            },
                            onAfterLoad: function () {
                                this.hideOverlay();
                                const listData = [];
                                if (!this.count()) {
                                    this.showOverlay("Отсутствуют данные")
                                } else {
                                    // for (let i = 0; i < this.count(); i++) {
                                    //     listData.push(this.getItem());
                                    // }
                                }
                                console.dir({ listDataTable: this.serialize() });
                            },
                            onLoadError: function () {
                                this.hideOverlay();
                            },
                            onItemDblClick: function (id) {
                                this.hideOverlay();
                                let data = $$('subsidies_table').getItem(id);

                                window.open('request_subsidy/view?id=' + data.id);
                            }
                        },
                        url: 'list_subsidy/1'
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
            }
        ]
    }
}

function clearSubsidiesSearchQueries() {
    $$('search_beginSearchTime').setValue('');
    $$('search_endSearchTime').setValue('');
    $$('search_inn').setValue('');
    $$('subsidy_type').setValue('');
    $$('subsidy_request_status').setValue('');
    reloadSubsidiesSearchQueries();
}

function reloadSubsidiesSearchQueries() {
    $$('subsidies_table').clearAll();

    const params = {};
    const beginSearchTime = $$('search_beginSearchTime').getValue();
    if (beginSearchTime != null) {
        params.bst = webix.i18n.timeFormatDate(beginSearchTime);
    }
    const endSearchTime = $$('search_endSearchTime').getValue();
    if (endSearchTime != null) {
        params.est = webix.i18n.timeFormatDate(endSearchTime);
    }

    const inn = $$('search_inn').getValue();
    if (inn) {
        params.inn = inn;
    }

    const subsidy_type = $$('subsidy_type').getValue();
    const subsidy_request_status = $$('subsidy_request_status').getValue();
    console.dir({ subsidy_request_status, subsidy_type });

    $$('subsidies_table').load(function () {
        return webix.ajax().get('list_subsidy/1', params);
    });
}
