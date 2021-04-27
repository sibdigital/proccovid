controlAuthority = {
    body: {
        type: "space",
        rows: [
            {
                rows: [
                    {
                        view: "datatable",
                        id: "controlAuthorityTable",
                        select: 'row',
                        scrollX: false,
                        navigation: true,
                        resizeColumn: true,
                        fixedRowHeight: false,
                        pager: 'Pager',
                        rowLineHeight: 28,
                        datafetch: 25,
                        columns: [
                            {
                                id: "name",
                                header: "Наименование",
                                width: 300,
                                minWidth: 300,
                                fillspace: true,
                                template: "#name#",
                                sort: 'text',
                            },
                            {
                                id: "shortName",
                                header: "Сокращенное наименование",
                                width: 350,
                                minWidth: 250,
                                template: "#shortName#",
                                sort: 'text',
                            },
                            {
                                id: "weight",
                                header: "Вес",
                                width: 100,
                                sort: 'int',
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
                            onItemClick: function (id) {
                                this.hideOverlay();
                            },
                            onItemDblClick: function (id) {
                                let item = $$("controlAuthorityTable").getItem(id);
                                setTimeout(() => {
                                    getControlAuthorityEditForm(item);

                                    $$('controlAuthorityForm').parse(item);
                                }, 100)
                            },
                            "data->onStoreUpdated": function () {
                                this.adjustRowHeight(null, true);
                            }
                        },
                        url: "control_authorities"
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
                                view: "button",
                                label: "Добавить",
                                width: 200,
                                type: "icon",
                                icon: "fas fa-plus",
                                css: "webix_primary",
                                click: () => {
                                    getControlAuthorityEditForm();
                                    $$('del_btn').hide();
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

function getControlAuthorityEditForm(item) {
    const controlAuthorityForm = {
        view: "form",
        id: "controlAuthorityForm",
        elements: [
            {
                cols: [
                    getBackButton(null, controlAuthority),
                    {}
                ]
            },
            {
                type: "space",
                rows: [
                    {
                        view: 'richselect',
                        id: 'controlAuthorityParent',
                        value: item?.controlAuthorityParent?.id ?? "",
                        css: 'smallText',
                        label: "Группа",
                        options: 'cls_control_authority_parents',
                    },
                    {
                        view: "text",
                        id: "name",
                        name: "name",
                        label: "Наименование",
                        labelWidth: 120,
                    },
                    {
                        view: "text",
                        id: "shortName",
                        name: "shortName",
                        label: "Сокращенное наименование",
                        labelWidth: 220,
                    },
                    {
                        cols: [
                            {
                                view: 'text',
                                id: 'weight',
                                name: 'weight',
                                label: 'Вес',
                                labelWidth: 100,
                                validate: webix.rules.isNumber,
                            },
                            {gravity: 3}
                        ]
                    },
                    {
                        cols: [
                            {},
                            {
                                view: "button",
                                label: "Удалить",
                                id: "del_btn",
                                type: "icon",
                                width: 170,
                                icon: "fas fa-minus",
                                css: "webix_danger",
                                click: () => {
                                    let ca = $$('controlAuthorityForm').getValues();
                                    webix.ajax()
                                        .get('delete_control_authority', {id: ca.id})
                                        .then((answer) => {
                                            if (answer.text()) {
                                                webix.message("Контрольно-надзорный орган удалён", "success");
                                                webix.ui({
                                                    id: 'content',
                                                    rows: [
                                                        controlAuthority
                                                    ]
                                                }, $$("content"))
                                            } else {
                                                webix.message("Не удалось удалить контрольно-надзорный орган", "error");
                                            }
                                        })
                                }
                            },
                            {
                                view: "button",
                                label: "Сохранить",
                                type: "icon",
                                icon: "fas fa-edit",
                                width: 170,
                                css: "webix_primary",
                                click: () => {
                                    if ($$('weight').validate()) {
                                        let ca = $$('controlAuthorityForm').getValues();
                                        ca.controlAuthorityParent = {
                                            id: $$('controlAuthorityParent').getValue(),
                                            name: $$('controlAuthorityParent').getText()
                                        }
                                        console.log(ca)
                                        webix.ajax()
                                            .headers({'Content-type': 'application/json'})
                                            .post('save_control_authority', JSON.stringify(ca))
                                            .then((answer) => {
                                                if (answer.text() && ca.id) {
                                                    webix.message("Контрольно-надзорный орган обновлен", "success");
                                                } else if (answer.text()) {
                                                    webix.message("Контрольно-надзорный орган добавлен", "success");
                                                } else {
                                                    webix.message("Не удалось сохранить контрольно-надзорный орган", "error");
                                                }
                                            })
                                    }
                                }
                            }
                        ]
                    }
                ]
            },
            {}
        ]
    }

    webix.ui({
        id: 'content',
        rows: [
            controlAuthorityForm
        ]
    }, $$("content"))
}