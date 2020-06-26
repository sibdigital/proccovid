function other_requests(param_url) {
    return {
        autowidth: true,
        autoheight: true,
        rows: [
            {
                view: 'datatable',
                id: 'notifications_table',
                select: 'row',
                navigation: true,
                resizeColumn: true,
                pager: 'Pager',
                datafetch: 25,
                columns: [
                    {
                        id: "name",
                        header: "ФИО",
                        template: "#organization.name#",
                        adjust: true
                    },
                    {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                    {id: "typeRequest", header: "Тип заявки", template: "#typeRequest.activityKind#", adjust: true},
                    {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                    {
                        id: "district",
                        header: "Район, котором оказывается услуга",
                        template: "#district.name#",
                        adjust: true
                    },
                    {id: "time_Create", header: "Дата подачи", adjust: true, format: DATE_FORMAT}
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
                    onItemDblClick: function (id) {
                        this.hideOverlay();

                        let data = $$('notifications_table').getItem(id);
                        let queryWin = webix.ui(showform_other(data));

                        webix.extend($$('show_layout'), webix.ProgressBar);

                        queryWin.show();
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
