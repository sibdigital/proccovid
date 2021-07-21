/* [FIX] inner html tooltips style */
(function () {
    const _tooltip = webix.ui({view: "tooltip", dx: 20, dy: 0});
    let _moveTimeout;
    webix.event(document, "mousemove", e => {
        clearTimeout(_moveTimeout);
        _tooltip.hide();
        if (e.target.title || e.target.$title) {
            const data = {value: e.target.title || e.target.$title}, pos = webix.html.pos(e);
            if (e.target.title) {
                e.target.$title = e.target.title;
                e.target.removeAttribute("title");//to prevent native title behaviour
            }
            _tooltip.define({template: "{obj.value}", css: ""})
            _moveTimeout = webix.delay(_tooltip.show, _tooltip, [data, pos], webix.TooltipControl.delay);
        }
    }, {capture: true});
}());

const subsidyForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        type: "clean",
        rows: [
            {
                view: 'tabbar',
                id: 'subsidy_tabview',
                multiview: true,
                options: [
                    {
                        value: 'Субсидия', id: 'subsidy'
                    },
                    {
                        value: 'Необходимые документы', id: 'requiredFiles'
                    }
                ]
            },
            {
                animate: false,
                fitBiggest: true,
                cells: [
                    {
                        id: 'subsidy',
                        rows: [
                            {
                                view: 'form',
                                id: 'subsidyFormId',
                                autoheight: true,
                                elements: [
                                    {
                                        view: 'text',
                                        label: 'Наименование',
                                        labelPosition: 'top',
                                        name: 'name',
                                        required: true,
                                        invalidMessage: 'Поле не может быть пустым',
                                    },
                                    {
                                        view: 'text',
                                        label: 'Краткое наим.',
                                        labelPosition: 'top',
                                        name: 'shortName',
                                        required: true,
                                        invalidMessage: 'Поле не может быть пустым',
                                    },
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
                                                            'onHide': function () {
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
                                                        let subsidy = $$('subsidyFormId').getValues();
                                                        subsidy.departmentId = $$('departmentId').getValue();

                                                        let okveds = $$('okved_table').serialize();
                                                        let requiredSubsidyFiles = $$('requiredSubsidyFiles').serialize();

                                                        subsidy.okveds = okveds;

                                                        let params = {
                                                            "clsSubsidy": subsidy,
                                                            "tpRequiredSubsidyFiles": requiredSubsidyFiles
                                                        }

                                                        console.dir(params)

                                                        webix.ajax()
                                                            .headers({'Content-type': 'application/json'})
                                                            .post('save_subsidy', params)
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
                    },
                    {
                        id: 'requiredFiles',
                        rows: [
                            {
                                view: 'form',
                                id: 'subsidyRequiredFiles',
                                autoheight: true,
                                elements: [
                                    // {
                                    //     view: 'text',
                                    //     id: 'clsSubsidy',
                                    //     name: 'clsSubsidy',
                                    //         formData: {
                                    //             clsSubsidy: ""
                                    //         },
                                    //     hidden: true
                                    // },
                                    // {
                                    //     view: 'text',
                                    //     id: 'selectedFileType',
                                    //     name: 'clsFileType',
                                    //     hidden: true
                                    // },
                                    // view_section("Добавление документа"),
                                    // {
                                    //     view: 'text',
                                    //     label: 'Описание',
                                    //     labelWidth: 85,
                                    //     name: 'comment',
                                    //     required: true,
                                    //     invalidMessage: 'Поле не может быть пустым',
                                    // },
                                    // {
                                    //     margin: 10,
                                    //     cols: [
                                    //         {
                                    //             view: 'text',
                                    //             label: 'Вес',
                                    //             labelWidth: 85,
                                    //             labelAlign: 'right',
                                    //             width: 220,
                                    //             // labelPosition: 'top',
                                    //             name: 'weight',
                                    //             required: true,
                                    //             invalidMessage: 'Поле не может быть пустым',
                                    //         },
                                    //         {
                                    //             view: 'checkbox',
                                    //             label: 'Обязательный документ?',
                                    //             name: 'required',
                                    //             labelWidth: 195,
                                    //             width: 225,
                                    //             value: 0
                                    //         },
                                    //         {}
                                    //     ]
                                    // },
                                    // {
                                    //     margin: 10,
                                    //     cols: [
                                    //         {
                                    //             view: 'button',
                                    //             css: 'webix_primary',
                                    //             value: 'Выбор типа документа',
                                    //             width: 220,
                                    //             click: () => {
                                    //                 clsFileTypesWindow();
                                    //             }
                                    //         },
                                    //         {
                                    //             view: 'button',
                                    //             css: 'webix_primary save_btn',
                                    //             // disabled: true,
                                    //             value: 'Сохранить документ',
                                    //             width: 220,
                                    //             click: () => {
                                    //                 let form = $$('subsidyRequiredFiles');
                                    //                 let data = form.getValues();
                                    //
                                    //                 data.clsFileType = $$('selectedFileType').config.formData.item;
                                    //                 //data.clsSubsidy = $$('clsSubsidy').config.formData.clsSubsidy;
                                    //
                                    //                 console.dir(data)
                                    //                 $$('requiredSubsidyFiles').add(data)
                                    //                 // webix.ajax()
                                    //                 //     .headers({'Content-type': 'application/json'})
                                    //                 //     .post('save_required_subsidy_file', data)
                                    //                 //     .then((response) => {
                                    //                 //         let responseJSON = response.json();
                                    //                 //         console.dir(responseJSON);
                                    //                 //     })
                                    //             }
                                    //         },
                                    //     ]
                                    // },
                                    view_section("Список документов субсидии"),
                                    {
                                        view: 'datatable',
                                        id: 'requiredSubsidyFiles',
                                        pager: 'rsfPager',
                                        editable: true,
                                        editaction: 'click',
                                        rules: {
                                            weight: function (obj) {
                                                if(!webix.rules.isNumber(obj)) {
                                                    webix.message("Вес должен иметь числовое значение","error")
                                                    return false;
                                                } else {
                                                    return true;
                                                }
                                            }
                                        },
                                        columns: [
                                            {
                                                id: 'clsFileType',
                                                header: 'Файл',
                                                width: 60,
                                                css: {
                                                    "text-align": "center !important"
                                                },
                                                template: (obj) => {
                                                    if (obj.clsFileType !== null) {
                                                        return '<span title="Информация о типе файла" style="margin-top: 10px; color: royalblue" class="webix_icon fas fa-eye view-file-type"></span>'
                                                    } else {
                                                        return '<span title="Выбрать тип файла" style="color: royalblue" class="webix_icon fas fa-plus add-file-type"></span>';
                                                    }
                                                },
                                            },
                                            {
                                                id: 'required',
                                                header: 'Статус',
                                                width: 70,
                                                css: {
                                                    "text-align": "center !important"
                                                },
                                                template: '<span class="custom-checkbox">{common.checkbox()}</span>',
                                            },
                                            {
                                                id: 'weight',
                                                header: 'Вес',
                                                sort: 'text',
                                                width: 100,
                                                editor: 'text'
                                            },
                                            {
                                                id: 'comment',
                                                header: 'Описание',
                                                sort: 'text',
                                                fillspace: true,
                                                editor: 'text'
                                            },
                                            {
                                                id: 'addBtn',
                                                width: 50,
                                                header: '<span title="Добавить документ" style="color: royalblue" class="webix_icon fas fa-plus add-row-btn"></span>',
                                                template: '<span title="Удалить документ" class="webix_icon fas fa-trash del-icon"></span>'
                                            }
                                        ],
                                        onClick: {
                                            "add-row-btn": function () {
                                                $$('requiredSubsidyFiles').editStop();
                                                let rowId = $$('requiredSubsidyFiles').add({
                                                    clsFileType: null,
                                                    required: 0,
                                                    weight: 0,
                                                    comment: ''
                                                });
                                                $$('requiredSubsidyFiles').editRow(rowId);
                                            },
                                            "add-file-type": async function (event, item) {
                                                await clsFileTypesWindow(item);
                                            },
                                            "view-file-type": function (event, item) {
                                                clsFileTypesWindow(item, "view");
                                            },
                                            "del-icon": function (event, item) {
                                                let obj = this.getItem(item.row);
                                                console.log(obj);
                                                this.remove(item.row);
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
                                            onaftereditstop: function () {
                                                this.validateEditor();
                                            },
                                            "data->onStoreUpdated": function () {
                                                this.adjustRowHeight(null, true);
                                            }
                                        }
                                    },
                                    {
                                        view: 'pager',
                                        id: 'rsfPager',
                                        height: 38,
                                        size: 25,
                                        group: 5,
                                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                                    }
                                ]
                            }
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
                        resizeColumn: true,
                        pager: 'Pager',
                        readonly: true,
                        columns: [
                            {id: 'name', header: 'Наименование', adjust: true, sort: "string", fillspace: true},
                            {
                                id: 'departmentName',
                                header: 'Ответственное подразделение',
                                template: function (obj) {
                                    if (obj.department != null) {
                                        return obj.department.name;
                                    } else {
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
                                    function () {
                                        var xhr = webix.ajax().sync().get('subsidy_okveds/' + item.id);
                                        var jsonResponse = JSON.parse(xhr.responseText);
                                        for (var k in jsonResponse) {
                                            var row = jsonResponse[k];
                                            $$('okved_table').add(row);
                                        }
                                    });

                                // $$('clsSubsidy').config.formData = {clsSubsidy: item}

                                var xhr = webix.ajax().sync().get('required_subsidy_files/' + item.id);
                                var jsonResponse = JSON.parse(xhr.responseText);
                                console.log(jsonResponse)
                                for (var k in jsonResponse) {
                                    var row = jsonResponse[k];
                                    $$('requiredSubsidyFiles').add(row);
                                }


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

let clsFileTypesWindow = (cell, type) => {

    let title = type === "view" ? "Информация о типе файла" : "Типы файлов";

    let dataForm = $$('requiredSubsidyFiles').getItem(cell.row);

    let fileTypeData = dataForm.clsFileType;

    let tableBody = {
        type: 'space',
        margin: 10,
        rows: [
            {
                view: 'datatable',
                id: 'fileTypeDatatable',
                pager: 'fileTypeDatatablePager',
                fixedRowHeight: false,
                rowLineHeight: 28,
                select: 'row',
                columns: [
                    {
                        id: 'name',
                        header: 'Название',
                        width: 350,
                        fillspace: true,
                    },
                    {
                        id: 'shortName',
                        header: 'Сокращённое название',
                        adjust: true,
                    },
                    {
                        id: 'code',
                        header: 'Код',
                        adjust: true,
                    }
                ],
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
                },
                url: () => {
                    let requiredFiles = $$('requiredSubsidyFiles').serialize();
                    let ids = [];

                    requiredFiles.forEach(file => {
                        file.clsFileType != null ? ids.push(file.clsFileType.id) : "";
                    })
                    console.dir(ids)
                    let params = {
                        "clsSubsidy": {},
                        "tpRequiredSubsidyFiles": requiredFiles
                    }
                    return webix.ajax()
                        .headers({"Content-type": "application/json"})
                        .post('cls_file_types', params)
                        .then((data) => {
                            return data
                        })
                },
            },
            {
                cols: [
                    {
                        view: 'pager',
                        id: 'fileTypeDatatablePager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    },
                    {
                        view: 'button',
                        css: 'webix_primary save_btn',
                        width: 150,
                        value: 'Добавить',
                        click: () => {

                            let selectedItem = $$('fileTypeDatatable').getSelectedItem();
                            $$('fileTypeDatatable').remove(selectedItem.id)

                            let record = $$('requiredSubsidyFiles').getItem(cell.row);
                            record[cell.column] = selectedItem;
                            $$('requiredSubsidyFiles').refresh(cell.row);

                            window.hide();

                        }
                    }
                ]
            }
        ]
    }

    let fileTypeViewForm = {
        view: 'form',
        id: 'fileTypeForm',
        type: 'space',
        margin: 10,
        data: fileTypeData,
        elements: [
            {
                view: 'text',
                id: 'name',
                name: 'name',
                label: 'Название',
                labelPosition: 'top'
            },
            {
                view: 'text',
                id: 'shortName',
                name: 'shortName',
                label: 'Сокращённое название',
                labelPosition: 'top'
            },
            {
                view: 'text',
                id: 'code',
                name: 'code',
                label: 'Код',
                labelPosition: 'top'
            }
        ]
    }

    let window = webix.ui({
        view: 'window',
        id: 'clsFileTypesWindow',
        head: {
            view: 'toolbar',
            elements: [
                {view: 'label', label: title},
                {
                    view: 'icon', icon: 'wxi-close',
                    click: function () {
                        window.hide();
                    }
                }
            ]
        },
        width: 900,
        height: 680,
        position: 'center',
        modal: true,
        body: type === "view" ? fileTypeViewForm : tableBody
    })

    window.show();
}