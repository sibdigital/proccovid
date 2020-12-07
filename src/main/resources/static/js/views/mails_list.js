const DATE_FORMAT = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s");

function mails(reqUrl) {
    return {
        autowidth: true,
        autoheight: true,
        rows: [
            {
                view: 'datatable',
                id: 'mails_table',
                select: 'row',
                navigation: true,
                resizeColumn: true,
                pager: 'Pager',
                datafetch: 25,
                columns: [
                    // { id: "data0", header: "name", adjust: true },
                    // { id: "data1", header: "count", adjust: true },
                    { id: "name", header: "Наименование", adjust: true },
                    { id: "cnt", header: "Количество", adjust: true },
                ],
                scheme: {
                    // $init: function (obj) {
                    //     obj.time_Create = obj.timeCreate.replace("T", " ") //dateUtil.toDateFormat(obj.timeCreate);
                    // },
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
                },
                url: reqUrl,
                datatype: 'json',
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

function getMinDate(date, inc = 1) {
    return webix.Date.add(new Date(date), inc, 'day');
}

function getDate(date, type) {
    if (type === 'start') {
        return new Date(date.getFullYear(), date.getMonth(), 1);
    } else if (type === 'end') {
        return new Date(date.getFullYear(), date.getMonth() + 1, 0);
    }
}

webix.i18n.setLocale("ru-RU");

webix.ready(function() {
    layout = webix.ui({
        container: 'chart-dep',
        width: document.body.clientWidth,
        height: window.innerHeight, //document.body.clientHeight,
        rows: [
            {
                view: 'toolbar',
                height: 40,
                cols: [
                    {
                        view: 'label',
                        label: `<span style="font-size: 1.5rem">${APPLICATION_NAME}. Количество отправленных сообщений.</span>`,
                    }
                ]
            },
            {
                view: 'toolbar',
                cols: [
                    {
                        value: 1, view: 'segmented', id:'tabbar', value: 'listView', multiview: true,
                        width: 300,
                        optionWidth: 150, padding: 10,
                        options: [
                            { value: 'Все', id: 'all'},
                            { value: 'Отправленные', id: 'sent'},
                        ],
                        on: {
                            onChange:function(id){
                                let params = '';
                                let status = 2;
                                let reqUrl = '';
                                const startDate = $$('date_start').getValue();
                                const endDate = $$('date_end').getValue();
                                const timestamp = 'T00:00:00.000+0800'; //+8 gmt

                                const getDate = (date) => {
                                    return [date.getFullYear(), date.getMonth() + 1, date.getDate()].join('-');
                                };

                                // params += '?dateStart=' + getDate(new Date(startDate)) + timestamp;
                                // params += '&dateEnd=' + getDate(new Date(endDate)) + timestamp;
                                params += '?dateStart=' + new Date(startDate).getTime();
                                params += '&dateEnd=' + new Date(endDate).getTime();

                                switch(id) {
                                    case 'all':
                                        reqUrl = 'all';
                                        break
                                    case 'sent':
                                        reqUrl = 'sent/' + status;
                                        break
                                }
                                let view = mails(reqUrl + params);


                                webix.ui({
                                    id: 'root',
                                    rows: [
                                        view
                                    ]
                                }, $$('root'))
                            }
                        }
                    },
                    {
                        view: 'datepicker',
                        id: 'date_start',
                        width: 250,
                        value: getDate(new Date(), 'start'),
                        label: 'Начальная дата', labelWidth: 130,
                        on: {
                            onChange() {
                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()]);
                                if (new Date($$('date_end').getValue()).getTime() < new Date(this.getValue()).getTime()) {
                                    $$('date_end').setValue(new Date(this.getValue()));
                                }
                                $$("date_end").getPopup().getBody().config.minDate = getMinDate(this.getValue(), 0);
                                $$('date_end').refresh();
                            }
                        }
                    },
                    {
                        view: 'datepicker',
                        id: 'date_end',
                        width: 250,
                        value: getDate(new Date(), 'end'),
                        label: 'Конечная дата', labelWidth: 130,
                        suggest: {
                            view: "suggest", type: "calendar", body: {
                                view: "calendar",
                                height: 260,
                                icons: true,
                                minDate: getMinDate(new Date()),
                            }
                        },
                        on: {
                            onChange() {
                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                            }
                        }
                    },
                    // {
                    //     id: 'testpicker',
                    //     view: 'daterangepicker',
                    //     name: 'default',
                    //     width: 500,
                    //     label: 'Default',
                    //     value: {
                    //         start: new Date(),
                    //         end: webix.Date.add(new Date(), 1, "month")
                    //     }
                    // },
                ]
            },
            {
                id: 'root'
            }
        ]
    })
    webix.event(window, "resize", function(event){
        layout.define("width",document.body.clientWidth);
        layout.define("height",window.innerHeight);
        layout.resize();
    });
    $$('tabbar').setValue('all');
})
