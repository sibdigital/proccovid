const department_contacts = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        id: 'contactsMainLayout',
        rows: [
            {
                type: 'wide',
                responsive: 'contactsMainLayout',
                cols: [
                    {
                        view: "dataview",
                        id: "contact_grid",
                        minWidth: 320,
                        css: 'contacts',
                        select: 1,
                        xCount: 3,
                        type: {
                            template: "<div class='overall'>" +
                                "<div class='description'>#description#</div>" +
                                "<div class='contactValue'>#contactValue#</div></div>",
                            height: "auto",
                            width: "auto"
                        },
                        on: {
                            onItemDblClick: function (id) {
                                let item = this.getItem(id);
                                $$('contact_form').parse(item);
                                let contactValue = $$('contactValueText').getValue();
                                if (webix.rules.isEmail(contactValue)) {
                                    $$('type_combo').setValue("2")
                                    changeComboConfig(2)
                                } else {
                                    $$('type_combo').setValue("1")
                                    changeComboConfig(1)
                                }
                            }
                        }
                    },
                    {
                        gravity: 0.4,
                        view: 'form',
                        id: 'contact_form',
                        minWidth: 200,
                        complexData: true,
                        rules: {
                            "type": webix.rules.isNotEmpty
                        },
                        elements: [
                            {gravity: 0.5},
                            {
                                view: 'text',
                                id: 'descriptionText',
                                name: 'description',
                                label: 'Описание',
                                labelPosition: 'top',
                            },
                            {
                                view: "combo",
                                id: "type_combo",
                                name: "type",
                                label: 'Вид',
                                labelPosition: 'top',
                                value: "1",
                                options: [
                                    {id: 1, value: "Номер телефона"},
                                    {id: 2, value: "Почтовый адрес"}
                                ],
                                on: {
                                    onChange: () => {
                                        let currentComboValue = $$('type_combo').getValue();
                                        changeComboConfig(currentComboValue)
                                    }
                                }
                            },
                            {
                                view: 'text',
                                id: 'contactValueText',
                                name: 'contactValue',
                                label: 'Номер телефона',
                                validate: webix.rules.isNotEmpty,
                                placeholder: '+7 (xxx) xxx-xx-xx',
                                labelPosition: 'top',
                                invalidMessage: "Контакт не может быть пустым"
                            },
                            {
                                cols: [
                                    {
                                        view: 'button',
                                        id: 'add_contact',
                                        css: 'webix_primary',
                                        label: "<span class='mdi mdi-plus-circle' style='padding-right: 5px'></span><span class='text'>Добавить</span>",
                                        hotkey: 'enter',
                                        click: () => addContact()
                                    },
                                    {
                                        view: 'button',
                                        id: 'del_contact',
                                        css: 'webix_primary',
                                        label: "<span class='mdi mdi-minus-circle' style='padding-right: 5px'></span><span class='text'>Удалить</span>",
                                        hotkey: 'delete',
                                        click: () => deleteContact()
                                    }
                                ]
                            },
                            {}
                        ],
                        elementsConfig: {
                            on: {
                                "onChange": function () {
                                    this.validate();
                                }
                            }
                        }
                    }
                ]
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        css: 'webix_primary',
                        align: 'right',
                        maxWidth: 200,
                        value: 'Сохранить изменения',
                        click: function () {
                            var data = $$('contact_grid').serialize();
                            $$('contact_table').clearAll();
                            $$('contact_table').parse(data);
                            $$('windowCD').close();
                        }
                    },
                    {
                        view: 'button',
                        align: 'right',
                        maxWidth: 150,
                        value: 'Отмена',
                        click: function () {
                            $$('windowCD').close();
                        }
                    }
                ]
            },
        ],
    }
}

function addContact() {
    let form = $$('contact_form')
    let params = form.getValues()
    if (params.type == 2) {
        params.type = 0;
    }
    if (form.validate()) {
        $$('contact_grid').add(params);
        form.clear()
        form.clearValidation()
        if (params["type"] == 0) {
            $$('type_combo').setValue('2')
        } else {
            $$('type_combo').setValue('1')
        }
    }
}

function deleteContact() {
    $$("contact_grid").remove($$("contact_grid").getSelectedId());
}

function changeComboConfig(val) {
    if (val === 2) {
        $$('contactValueText').config.label = "Почта";
        $$('contactValueText').config.placeholder = "sibdigital@mail.ru";
        // $$('contactValueText').config.validate = webix.rules.isEmail;
        $$('contactValueText').config.invalidMessage = "Неверный формат почты"
        $$('contactValueText').refresh();
    } else if (val === 1) {
        $$('contactValueText').config.label = "Номер телефона";
        $$('contactValueText').config.placeholder = "+7 (3012) xx-xx-xx";
        $$('contactValueText').config.validate = webix.rules.isNotEmpty;
        $$('contactValueText').config.invalidMessage = "Контакт не может быть пустым"
        $$('contactValueText').refresh()
    }
}