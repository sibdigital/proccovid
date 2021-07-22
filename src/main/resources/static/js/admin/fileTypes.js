const fileTypes = {
    rows: [
        {
            type: 'space',
            rows: [
                {
                    view: 'datatable',
                    id: 'fileTypesDatatable',
                    fixedRowHeight: false,
                    rowLineHeight: 36,
                    pager: 'fileTypesPager',
                    columns: [
                        { id: 'name', header: 'Название', minWidth: 350, fillspace: 1, sort: 'text' },
                        { id: 'shortName', header: 'Сокращённое название', adjust: 1, sort: 'text' },
                        {
                            id: 'createTime',
                            header: 'Дата создания',
                            width: 160,
                            template: function (obj) {
                                // obj.timeCreate = obj.timeCreate.replace("T", " ");
                                return webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")(obj.timeCreate.replace("T", " "));
                            },
                        },
                        { id: 'code', header: 'Код', adjust: 1, sort: 'text' },
                        {
                            id: 'addBtn',
                            width: 50,
                            header: '<span title="Добавить документ" style="color: green" class="webix_icon fas fa-plus add-file"></span>',
                            template: '<span title="Удалить документ" class="webix_icon fas fa-trash del-icon"></span>'
                        }
                    ],
                    url: 'file_types',
                    onClick: {
                        "add-file": function () {
                            loadInContent(fileTypeForm);
                        },
                        "del-icon": function (event, id) {
                            const item = this.getItem(id);

                            webix.ajax()
                                .headers({ 'Content-type':'application/json'} )
                                .post('del_file_type', item)
                                .then((response) => {
                                    const responseJson = response.json();
                                    webix.message(responseJson.message, responseJson.status);
                                    this.remove(id.row);
                                })
                        }
                    },
                    on: {
                        onBeforeLoad: function () {
                            this.showOverlay("Загружаю...");
                        },
                        onAfterLoad: function () {
                            this.hideOverlay();
                            if (!this.count()) {
                                this.showOverlay("Отсутствуют данные");
                            }
                        },
                        onLoadError: function () {
                            this.hideOverlay();
                        },
                        "data->onStoreUpdated": function () {
                            this.adjustRowHeight(null, true);
                        },
                        onItemDblClick: async function (id) {
                            const item = this.getItem(id);
                            await loadInContent(fileTypeForm);
                            $$('fileTypeForm').parse(item);
                        }
                    }
                },
                {
                    cols: [
                        {
                            view: 'pager',
                            id: 'fileTypesPager',
                            height: 38,
                            size: 25,
                            group: 5,
                            template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                        },
                        {},
                        {
                            view: 'button',
                            css: 'webix_primary',
                            width: 200,
                            value: 'Добавить',
                            click: function() {
                                loadInContent(fileTypeForm);
                            }
                        }
                    ]
                }
            ]
        }
    ]
}

const fileTypeForm = {
    type: 'space',
    rows: [
        {
            cols: [
                getBackButton('content', fileTypes),
                {}
            ]
        },
        {
            view: 'form',
            id: 'fileTypeForm',
            elements: [
                {
                    view: 'text',
                    id: 'name',
                    name: 'name',
                    label: 'Название',
                    invalidMessage: 'Поле не должно быть пустым'
                },
                {
                    view: 'text',
                    id: 'shortName',
                    name: 'shortName',
                    label: 'Сокращённое название',
                    invalidMessage: 'Поле не должно быть пустым'
                },
                {
                    view: 'text',
                    id: 'code',
                    name: 'code',
                    label: 'Код',
                },
                {
                    cols: [
                        {},
                        {
                            view: 'button',
                            css: 'webix_primary',
                            value: 'Сохранить',
                            width: 150,
                            click: function () {
                                const form = $$('fileTypeForm');

                                if(form.validate()) {
                                    const fileType = form.getValues();
                                    webix.ajax()
                                        .headers({ 'Content-type':'application/json'} )
                                        .post('save_file_type', fileType)
                                        .then((response) => {
                                            const responseJson = response.json();
                                            if (responseJson.status === 'success') {
                                                loadInContent(fileTypes);
                                            }
                                            webix.message(responseJson.message, responseJson.status);
                                        })
                                }
                            }
                        }
                    ]
                }
            ],
            elementsConfig: {
                labelPosition: 'top',
                // bottomPadding: 12
            },
            rules: {
                name: webix.rules.isNotEmpty,
                shortName: webix.rules.isNotEmpty
            }
        },
        {}
    ]
}