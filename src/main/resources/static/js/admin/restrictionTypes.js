
const restrictionTypes = {
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
                        view: 'datatable',
                        id: 'restriction_types_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        // pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "name", header: "Наименование", template: "#name#", width: 1000},
                        ],
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
                                let data = $$('restriction_types_table').getItem(id);

                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        restrictionTypeForm
                                    ]
                                }, $$('content'));

                                $$('restrictionTypeForm').parse(data);
                            }
                        },
                        url: 'cls_restriction_types'
                    },
                    {
                        cols: [
                            // {
                            //     view: 'pager',
                            //     id: 'Pager',
                            //     height: 38,
                            //     size: 25,
                            //     group: 5,
                            //     template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            // },
                            {},
                            {},
                            {},
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Добавить',
                                click: function () {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            restrictionTypeForm
                                        ]
                                    }, $$('content'));
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

const restrictionTypeForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'restrictionTypeForm',
                elements: [
                    { view: 'text', label: 'Наименование', labelPosition: 'top', name: 'name', required: true, validate: webix.rules.isNotEmpty },
                    {
                        cols: [
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Сохранить',
                                maxWidth: 300,
                                click: function () {
                                    if ($$('restrictionTypeForm').validate()) {
                                        let params = $$('restrictionTypeForm').getValues();

                                        webix.ajax().headers({
                                            'Content-Type': 'application/json'
                                        }).post('/save_cls_restriction_type',
                                            params).then(function (data) {
                                            if (data.text() === 'Тип ограничения сохранен') {
                                                webix.message({text: data.text(), type: 'success'});

                                                webix.ui({
                                                    id: 'content',
                                                    rows: [
                                                        restrictionTypes
                                                    ]
                                                }, $$('content'));
                                            } else {
                                                webix.message({text: data.text(), type: 'error'});
                                            }
                                        })
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
                                    webix.ui({
                                        id: 'content',
                                        rows: [typeRequests]
                                    }, $$('content'))
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}