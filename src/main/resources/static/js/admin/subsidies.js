const subsidyForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'subsidyFormId',
                autoheight: true,
                rows: [
                    {view: 'text', label: 'Наименование', labelPosition: 'top', name: 'name', required: true, invalidMessage: 'Поле не может быть пустым',},
                    {view: 'text', label: 'Краткое наим.', labelPosition: 'top', name: 'shortName', required: true, invalidMessage: 'Поле не может быть пустым',},
                    {
                        view: 'richselect',
                        id: 'departmentId',
                        label: 'Ответственное подразделение',
                        labelPosition: 'top',
                        invalidMessage: 'Поле не может быть пустым',
                        options: 'cls_departments_short'
                    },
                    {
                        cols: [
                            {},
                            {
                                view: 'button',
                                align: 'right',
                                label: "Подбор ОКВЭДов",
                                width: 200,
                                css: "webix_primary",
                                click: () => {
                                    let data = $$('okved_table').serialize();

                                    let window = webix.ui({
                                        view: 'window',
                                        id: 'windowCLO',
                                        close: true,
                                        head: 'Подбор ОКВЭДов для субсидии',
                                        width: 1000,
                                        height: 800,
                                        position: 'center',
                                        modal: true,
                                        body: okvedSelector('okved_selector_id', 'linked_okved_table', 'okved_table'),
                                        on: {
                                            'onHide': function() {
                                                window.destructor();
                                            }
                                        }

                                    });
                                    $$('linked_okved_table').parse(data);

                                    window.show();
                                }
                            }
                        ]
                    },
                    {
                        view: 'datatable',
                        id: 'okved_table',
                        label: 'ОКВЭДы',
                        labelPosition: 'top',
                        minHeight: 200,
                        select: 'row',
                        editable: true,
                        pager: 'Pager',
                        columns: [
                            {
                                id: 'kindCode',
                                header: 'Код',
                                sort: 'text'
                            },
                            {
                                id: 'version',
                                header: 'Версия',
                                sort: 'text'
                            },
                            {
                                id: 'kindName',
                                header: 'ОКВЭД',
                                sort: 'text',
                                fillspace: true,
                            },
                        ],
                        data: [],
                    },
                    {
                        view: 'pager',
                        id: 'Pager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    },
                    {
                        cols: [
                            {
                                view: "button",
                                label: "Удалить",
                                type: "icon",
                                width: 200,
                                icon: "fas fa-trash",
                                css: "button_red",
                                click: () => {
                                    webix.confirm('Вы действительно хотите удалить субсидию?')
                                        .then(
                                            function () {
                                                let params = $$('subsidyFormId').getValues();
                                                webix.ajax()
                                                    .headers({'Content-type': 'application/json'})
                                                    .post('delete_subsidy', JSON.stringify(params))
                                                    .then((answer) => {
                                                        if (answer.json()) {
                                                            webix.message("Субсидия удалена", "success");
                                                            webix.ui({
                                                                id: 'content',
                                                                rows: [
                                                                    subsidies
                                                                ]
                                                            }, $$('content'))
                                                        } else {
                                                            webix.message("Не удалось удалить субсидию", "error");
                                                        }
                                                    })
                                            });
                                }
                            },
                            {},
                            {
                                view: "button",
                                label: "Сохранить",
                                width: 170,
                                css: "webix_primary",
                                click: () => {
                                    if ($$('subsidyFormId').validate()) {
                                        let params = $$('subsidyFormId').getValues();
                                        params.departmentId = $$('departmentId').getValue();

                                        let okveds = $$('okved_table').serialize();
                                        params.okveds = okveds;

                                        webix.ajax()
                                            .headers({'Content-type': 'application/json'})
                                            .post('save_subsidy', JSON.stringify(params))
                                            .then((answer) => {
                                                if (answer.json()) {
                                                    webix.message("Субсидия сохранена", "success");
                                                    webix.ui({
                                                        id: 'content',
                                                        rows: [
                                                            subsidies
                                                        ]
                                                    }, $$('content'))
                                                } else {
                                                    webix.message("Не удалось сохранить субсидию", "error");
                                                }
                                            })
                                    }
                                }
                            },
                            {
                                view: 'button',
                                align: 'right',
                                maxWidth: 200,
                                css: 'webix_secondary',
                                value: 'Отмена',
                                click: function () {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            subsidies
                                        ]
                                    }, $$('content'))
                                }
                            },
                        ]
                    }
                ]
            }
        ]
    }
}


const subsidies = {
    view: 'scrollview',
    id: 'subsidiesId',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        id: 'subsidies_table',
                        view: 'datatable',
                        select: 'row',
                        multiselect: true,
                        resizeColumn:true,
                        pager: 'Pager',
                        readonly: true,
                        columns: [
                            { id: 'name', header: 'Наименование', adjust: true, sort: "string", fillspace: true},
                            { id: 'departmentName',
                              header: 'Ответственное подразделение',
                              template: function (obj) {
                                if (obj.department != null) {
                                    return obj.department.name;
                                } else  {
                                    return "";
                                }
                              },
                              adjust: true,
                              sort: "text",
                              fillspace: true
                            },
                        ],
                        on: {
                            onItemDblClick: function (id) {
                                item = this.getItem(id);
                                var xhr = webix.ajax().sync().get('subsidy/' + item.id);
                                var jsonResponse = JSON.parse(xhr.responseText);
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        webix.copy(subsidyForm)
                                    ]
                                }, $$("content"));
                                $$('subsidyFormId').parse(jsonResponse);
                                if (jsonResponse.department) {
                                    $$('departmentId').setValue(jsonResponse.department.id)
                                }

                                $$('subsidyFormId').load(
                                    function (){
                                        var xhr = webix.ajax().sync().get('subsidy_okveds/' + item.id);
                                        var jsonResponse = JSON.parse(xhr.responseText);
                                        for (var k in jsonResponse) {
                                            var row = jsonResponse[k];
                                            $$('okved_table').add(row);
                                        }
                                    });

                            }
                        },
                        data: [],
                        url: 'subsidies',
                    },
                    {
                        cols: [
                            {
                                view: "pager",
                                id: "Pager",
                                animate: {
                                    subtype: "out"
                                },
                                height: 38,
                                size: 25,
                                group: 5,
                                template: "{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}"
                            },
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                align: 'right',
                                value: 'Добавить',
                                width: 250,
                                click: function () {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            subsidyForm
                                        ]
                                    }, $$('content'))
                                }
                            }
                        ]
                    }]
            }
        ]
    }
}
