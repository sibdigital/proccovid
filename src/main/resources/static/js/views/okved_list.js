function saveOkved() {
    let params = $$('okvedForm').getValues();
    webix.ajax().headers({
        'Content-Type': 'application/json'
    }).post('save_okved',
        JSON.stringify(params)
    ).then(function (data) {
        if (data.text() === 'ОКВЭД сохранен') {
            webix.message({text: data.text(), type: 'success'});

            $$('window').close();
            $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()]);
        } else {
            webix.message({text: data.text(), type: 'error'});
        }
    })
}

function deleteOkved() {
    let params = $$('okvedForm').getValues();
    if (params.version != 'synt') {
        webix.message({text: 'Нельзя удалять ОКВЭДы версии ' + params.version, type: 'error'});
    }
    else {
        webix.confirm('Вы действительно хотите удалить ОКВЭД?')
            .then(
                function () {
                    webix.ajax().headers({
                        'Content-Type': 'application/json'
                    }).post('delete_okved',
                        JSON.stringify(params)
                    ).then(function (data) {
                        if (data.text() === 'ОКВЭД удален') {
                            webix.message({text: data.text(), type: 'success'});
                            $$('window').close();
                            $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()]);
                        } else {
                            webix.message({text: data.text(), type: 'error'});
                        }
                    })
                }
            )
    }
}

const okvedForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'okvedForm',
                rows: [
                    {
                        cols: [
                            {view: 'text', label: 'Код', labelPosition: 'top', name: 'kindCode'},
                            {view: 'text', label: 'Версия', labelPosition: 'top', name: 'version'},
                        ]
                    },
                    {view: 'text', label: 'Наименование', labelPosition: 'top', name: 'kindName'},
                    {view: 'textarea', label: 'Описание', labelPosition: 'top', name: 'description', autoheight: true},
                    {view: 'radio', label: 'Статус', name: 'status', name: 'status', options: [
                            {value: 'Работа разрешена', id: 1},
                            {value: 'Работа приостановлена', id: 0},
                        ]},
                    {cols: [
                            {view: 'button', value: 'Сохранить', click: saveOkved},
                            {view: 'button', value: 'Удалить', click: deleteOkved}
                        ]}

                ]
            }
        ]
    }
}

function okvedslist(param_url, version, view_item_another_page) {
    return {
        autowidth: true,
        autoheight: true,
        rows: [
            {
                view: 'datatable',
                id: 'okveds_table',
                select: 'row',
                navigation: true,
                resizeColumn: true,
                pager: 'Pager',
                datafetch: 25,
                columns: [
                    {id: "kindCode", header: "Код", adjust: true},
                    {id: "version", header: "Версия", adjust: true},
                    {id: "kindName", header: "Наименование", adjust: true, fillspace: true},
                    {id: "status", header: "Статус", template: function (obj) {
                        if (obj.status == 1) {
                            return 'Работа разрешена';
                        }
                        else {
                            return 'Работа приостановлена';
                        }
                        }, adjust: true}
                ],
                scheme: {
                    $init: function (obj) {},
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
                        let data = $$('okveds_table').getItem(id);
                        let window = webix.ui({
                            view: 'window',
                            id: 'window',
                            head: 'Редактирование ОКВЭДа (' + data.kindCode + ' ' + data.kindName + ').',
                            close: true,
                            width: 1000,
                            height: 800,
                            position: 'center',
                            modal: true,
                            body: okvedForm
                        });

                        $$('okvedForm').parse(data);

                        var $object = $$('okvedForm').elements;
                        if (data.version == '2001' || data.version == '2014') {
                            for (var key in $object){
                                if (key != "status") {
                                    var $el = $object[key];
                                    $el.define("readonly", "true")
                                    $el.refresh()
                                }
                            }
                        }
                        else  {
                            var $el = $object["version"];
                            $el.define("readonly", "true")
                            $el.refresh()
                        }

                        window.show();
                    }
                },
                url: param_url
            },
            {
                view: 'pager',
                id: 'Pager',
                height: 38,
                size: 25,
                group: 7,
                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
            }
        ]
    }
}

function okvedslist_chooseOkved(param_url, version, view_item_another_page) {
    return {
        autowidth: true,
        autoheight: true,
        rows: [
            {
                view: 'datatable',
                id: 'okveds_table',
                select: 'row',
                navigation: true,
                resizeColumn: true,
                pager: 'Pager',
                datafetch: 25,
                tooltip: {
                    template: "<span class='webix_light'>Для добавления ОКВЭДа дважды щелкните по нему</span>"
                },
                columns: [
                    {id: "kindCode", header: "Код", adjust: true},
                    {id: "version", header: "Версия", adjust: true},
                    {id: "kindName", header: "Наименование", adjust: true, fillspace: true},
                    {id: "status", header: "Статус", template: function (obj) {
                            if (obj.status == 1) {
                                return 'Работа разрешена';
                            }
                            else {
                                return 'Работа приостановлена';
                            }
                        }, adjust: true}
                ],
                scheme: {
                    $init: function (obj) {},
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
                        let data = $$('okveds_table').getItem(id);
                        $$('linked_okved_table').add(data);
                    }
                },
                url: param_url
            },
            {
                view: 'pager',
                id: 'Pager',
                height: 38,
                size: 25,
                group: 7,
                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
            }
        ]
    }
}


