function okvedSelector(id_okved_selector, id_okved_selector_table, id_main_okved_table) {
    return {
        view: 'scrollview',
        id: id_okved_selector,
        scroll: 'xy',
        body: {
            type: 'space',
            rows: [
            {
                autowidth: true,
                autoheight: true,
                borderless: true,
                rows: [
                    { template:"Редактируемая таблица ОКВЭДов", type:"section" },
                    {
                        view: 'datatable',
                        id: id_okved_selector_table,
                        pager: 'Pager',
                        datafetch: 25,
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
                                header: 'Наименование',
                                fillspace: true,
                                sort: 'text'
                            },
                            {
                                id: 'btnDelete',
                                header: 'Удалить',
                                template:"{common.trashIcon()}"
                            },
                        ],
                        onClick:{
                            "wxi-trash":function(event, id, node){
                                this.remove(id)
                            }
                        }
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
                            { template:"Подбор ОКВЭДов", type:"section" },
                            {
                                rows: [
                                    {},
                                    {
                                        view: 'button',
                                        css: 'button_blue',
                                        align: 'right',
                                        type: 'icon',
                                        icon: 'fas fa-angle-double-up',
                                        maxWidth: 200,
                                        label: 'Добавить',
                                        click: function () {
                                            let okveds = $$('okvedTreeId').getChecked().toString();
                                            let params = {
                                                okveds: okveds
                                            }
                                            webix.ajax().get('get_children_okveds_by_parents', params).then(function (data) {
                                                let jsonResponse = data.json();
                                                for (var k in jsonResponse) {
                                                    var row = jsonResponse[k];
                                                    if (!$$(id_okved_selector_table).exists(row.id)) {
                                                        $$(id_okved_selector_table).add(row);
                                                    }
                                                }
                                            });
                                        },
                                    },
                                    {}
                                ]
                            }
                        ]
                    },
                    // tree('okvedTreeId'),
                    okvedTreeByParentId('okvedTreeId'),
                    {
                        cols: [
                            {
                                view: 'button',
                                css: 'button_red',
                                align: 'left',
                                type: 'icon',
                                icon: 'fas fa-trash-alt',
                                maxWidth: 200,
                                label: 'Очистить таблицу',
                                click: function () {
                                    $$(id_okved_selector_table).clearAll();
                                },
                            },
                            {},
                            {
                                view: 'button',
                                css: 'webix_secondary',
                                maxWidth: 200,
                                value: 'Отмена',
                                click: function () {
                                    $$('windowCLO').close();
                                },
                            },
                            {
                                view: 'button',
                                css: 'webix_primary',
                                maxWidth: 200,
                                value: 'Сохранить изменения',
                                click: function () {
                                    var data = $$(id_okved_selector_table).serialize();
                                    $$(id_main_okved_table).clearAll();
                                    $$(id_main_okved_table).parse(data);
                                    $$('windowCLO').close();
                                },
                            },
                        ]
                    },
                ]
            }
        ]
    }
    }
}