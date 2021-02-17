//define(['views/showform'], function(showform) {
// define(function() {
//
//     webix.i18n.setLocale("ru-RU");
//
    const DATE_FORMAT = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")
//
//     return function(param_url, status) {
    function requests(param_url, status, view_item_another_page) {
        return {
            autowidth: true,
            autoheight: true,
            rows: [
                {
                    view: 'datatable',
                    id: 'requests_table',
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
                        {id: "typeRequest", header: "Вид деятельности", template: "#typeRequest.activityKind#", adjust: true},
                        {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                        {id: "time_Create", header: "Дата подачи", adjust: true, format: DATE_FORMAT },
                        {id: "personSlrySaveCnt", header: "Числ. с сохр. зп", adjust: true},
                        {id: "personOfficeCnt", header: "Числ. работающих", adjust: true},
                        {id: "personRemoteCnt", header: "Числе. удал. режим", adjust: true},
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
                            if (!this.count()) {
                                this.showOverlay("Отсутствуют данные")
                            }
                        },
                        onLoadError: function () {
                            this.hideOverlay();
                        },
                        onItemDblClick: function (id) {
                            this.hideOverlay();
                            let data = $$('requests_table').getItem(id);

                            if (!view_item_another_page) {
                                showRequestForm(data.id);
                            } else {
                                window.open('request/view?id=' + data.id);
                            }
                        }
                    },
                    url: param_url
                },
                {
                    view: 'pager',
                    id: 'Pager',
                    height: 38,
                    size: 25,
                    group: 5,
                    template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                }
            ]
        }
    }
// })