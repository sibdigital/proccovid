const fileTypes = {
    rows: [
        {
            type: 'space',
            rows: [
                {
                    view: 'datatable',
                    id: 'fileTypesDatatable',
                    fixedRowHeight: false,
                    rowLineHeight: 28,
                    pager: 'fileTypesPager',
                    // select: 'row',
                    columns: [
                        { id: 'name', header: 'Название', minWidth: 350, fillspace: 1, sort: 'text' },
                        { id: 'shortName', header: 'Сокращённое название', adjust: 1, sort: 'text' },
                        {
                            id: 'createTime',
                            header: 'Дата создания',
                            width: 160,
                            template: function (obj) {
                                obj.timeCreate = obj.timeCreate.replace("T", " ");
                                var time_Create = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")(obj.timeCreate);
                                return time_Create;
                            },
                        },
                        { id: 'code', header: 'Код', adjust: 1, sort: 'text' },
                        {
                            id: 'addBtn',
                            width: 50,
                            header: '<span title="Добавить документ" style="color: royalblue" class="webix_icon fas fa-plus add-file"></span>',
                            template: '<span title="Удалить документ" class="webix_icon fas fa-trash del-icon"></span>'
                        }
                    ],
                    url: 'file_types',
                    onClick: {
                        "add-file": function () {

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
                            value: 'Добавить'
                        }
                    ]
                }
            ]
        }
    ]
}