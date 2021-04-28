function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

let violations_form = (id) => {
    return {
        rows: [
            {
                view: 'toolbar',
                elements: [
                    {view: 'label', css: {"padding-left": "10px"}, label: id != null ? 'Просмотр нарушения (id: ' + id + ').' : 'Создание нового нарушения'},
                ],
            },
            {
                view: 'form',
                id: 'violationForm',
                elements: [
                    {
                        view: 'text',
                        id: 'idEgrul',
                        name: 'idEgrul',
                        hidden: true,
                    },
                    {
                        view: 'text',
                        id: 'idEgrip',
                        name: 'idEgrip',
                        hidden: true,
                    },
                    {
                        view: 'text',
                        id: 'idFilial',
                        name: 'idFilial',
                        hidden: true,
                    },
                    view_section('Реквизиты нарушителя'),
                    {
                        margin: 10,
                        rows: [
                            {
                                margin: 10,
                                cols: [
                                    {
                                        view: 'textarea',
                                        name: 'nameOrg',
                                        id: 'nameOrg',
                                        height: 130,
                                        label: 'Наименование организации/ИП',
                                        labelPosition: 'top',
                                        required: true,
                                        validate: webix.rules.isNotEmpty,
                                        invalidMessage: 'Поле не может быть пустым',
                                    },
                                    {
                                        margin: 10,
                                        rows: [
                                            {
                                                view: 'text',
                                                name: 'opfOrg',
                                                id: 'opfOrg',
                                                label: 'Организационно-правовая форма',
                                                labelPosition: 'top',
                                                required: true,
                                                validate: webix.rules.isNotEmpty,
                                                invalidMessage: 'Поле не может быть пустым',
                                            },
                                            {
                                                view: 'text',
                                                name: 'innOrg',
                                                id: 'innOrg',
                                                label: 'ИНН',
                                                labelPosition: 'top',
                                                required: true,
                                                validate: function (val) {
                                                    if (!val || isNaN(val) || !(val.length == 10 || val.length == 12)) {
                                                        return false;
                                                    }
                                                    return true;
                                                },
                                                invalidMessage: 'ИНН не соответствует формату',
                                            },
                                        ]
                                    },
                                ]
                            },
                        ]
                    },
                    view_section('Реквизиты нарушения'),
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'datepicker',
                                name: 'dateRegOrg',
                                id: 'dateRegOrg',
                                label: 'Дата регистрации',
                                editable: true,
                                labelPosition: 'top',
                                // required: true,
                                // validate: webix.rules.isNotEmpty,
                                // invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'richselect',
                                name: 'idDistrict',
                                id: 'idDistrict',
                                label: 'Район',
                                labelPosition: 'top',
                                options: 'cls_districts',
                                required: true,
                                validate: webix.rules.isNotEmpty
                            },
                            {
                                view: 'richselect',
                                id: 'idTypeViolation',
                                name: 'idTypeViolation',
                                label: 'Вид нарушения',
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                options: 'type_violations',
                                invalidMessage: 'Поле не может быть пустым',
                            },
                        ]
                    },
                    {
                        view: 'text',
                        name: 'ogrnOrg',
                        id: 'ogrnOrg',
                        label: 'ОГРН',
                        labelPosition: 'top',
                        // required: true,
                        hidden: true,
                        invalidMessage: 'ОГРН не соответствует формату',
                        validate: function (val) {
                            if (!val) {
                                return false;
                            }
                            if (isNaN(val)) {
                                return false;
                            }
                            if (val.length != 15) {
                                return false;
                            }
                            return true;
                        }
                    },
                    {
                        view: 'text',
                        name: 'kppOrg',
                        id: 'kppOrg',
                        label: 'КПП',
                        hidden: true,
                        labelPosition: 'top',
                        invalidMessage: 'КПП не соответствует формату',
                        validate: function (val) {
                            if (val) {
                                if (isNaN(val)) {
                                    return false;
                                }
                                if (val.length != 9) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    },
                    view_section('Реквизиты судебного решения'),
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'numberFile',
                                id: 'numberFile',
                                label: 'Номер дела',
                                labelPosition: 'top',
                                required: true,
                                invalidMessage: 'Поле не может быть пустым и длина номера не должна превышать 100 симв.',
                                validate: function (val) {
                                    if (val) {
                                        if (val.length > 100 || val.length == 0) {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                    return true;
                                }
                            },
                            {
                                view: 'datepicker',
                                name: 'dateFile',
                                id: 'dateFile',
                                label: 'Дата',
                                editable: true,
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            }
                        ]
                    },
                    {
                        id: 'btnsPanel',
                        margin: 10,
                        cols: [
                            {},
                            {
                                view: 'button',
                                id: 'save_button',
                                align: 'right',
                                css: 'webix_primary',
                                value: 'Сохранить',
                                maxWidth: 300,
                                click: function () {
                                    if ($$('violationForm').validate()) {
                                        $$('save_button').disable();

                                        let params = $$('violationForm').getValues();

                                        webix.ajax().headers({'Content-Type': 'application/json'}).post('save_violation', JSON.stringify(params))
                                            .then(function (data) {
                                                if (data.text() === 'Нарушение сохранено') {
                                                    webix.message({text: data.text(), type: 'success'});
                                                    showViolations();
                                                } else {
                                                    webix.message({text: data.text(), type: 'error'});
                                                }
                                            });
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
                                    showViolations();
                                    hideBtnBack()
                                }
                            }
                        ]
                    }
                ]
            }
        ]

    }
}

let person_violations_form = (id) => {
    return {
        rows: [
            {
                view: 'toolbar',
                elements: [
                    {view: 'label', css: {"padding-left": "10px"}, label: id != null ? 'Просмотр нарушения (id: ' + id + ').' : 'Создание нового нарушения'},
                ],
            },
            {
                view: 'form',
                id: 'personViolationForm',
                elements: [
                    view_section('Реквизиты нарушителя'),
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'lastname',
                                id: 'lastname',
                                label: 'Фамилия',
                                labelWidth: 85,
                                width: 528,
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'text',
                                name: 'firstname',
                                id: 'firstname',
                                label: 'Имя',
                                labelWidth: 135,
                                labelPosition: 'right',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'text',
                                name: 'patronymic',
                                id: 'patronymic',
                                label: 'Отчество',
                                // required: true,
                                // validate: webix.rules.isNumber,
                                // invalidMessage: 'Поле не может быть пустым',
                            },
                        ]
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'datepicker',
                                name: 'birthday',
                                id: 'birthday',
                                label: 'Дата рождения',
                                labelWidth: 130,
                                editable: true,
                                width: 528,
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'text',
                                name: 'placeBirth',
                                id: 'placeBirth',
                                label: 'Место рождения',
                                labelWidth: 135,
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                        ]
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'textarea',
                                name: 'registrationAddress',
                                height: 90,
                                id: 'registrationAddress',
                                label: 'Адрес регистрации',
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            // {
                            //     view: 'text',
                            //     name: 'residenceAddress',
                            //     id: 'residenceAddress',
                            //     label: 'Адрес проживания',
                            //     labelPosition: 'top',
                            //     required: true,
                            //     validate: webix.rules.isNotEmpty,
                            //     invalidMessage: 'Поле не может быть пустым',
                            // },
                            {
                                view: 'textarea',
                                name: 'passportData',
                                height: 90,
                                id: 'passportData',
                                label: 'Паспортные данные',
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            },
                        ]
                    },
                    {
                        view: 'text',
                        name: 'placeWork',
                        id: 'placeWork',
                        label: 'Место работы',
                        labelWidth: 105,
                        // required: true,
                        // validate: webix.rules.isNotEmpty,
                        // invalidMessage: 'Поле не может быть пустым',
                    },
                    view_section('Реквизиты нарушения'),
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'datepicker',
                                name: 'dateRegPerson',
                                id: 'dateRegPerson',
                                label: 'Дата регистрации',
                                editable: true,
                                labelPosition: 'top',
                                // required: true,
                                // validate: webix.rules.isNotEmpty,
                                // invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'richselect',
                                id: 'idTypeViolation',
                                name: 'idTypeViolation',
                                label: 'Вид нарушения',
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                options: 'type_violations',
                                invalidMessage: 'Поле не может быть пустым',
                            },
                            {
                                view: 'richselect',
                                name: 'idDistrict',
                                id: 'idDistrict',
                                label: 'Район',
                                labelPosition: 'top',
                                options: 'cls_districts',
                                required: true,
                                validate: webix.rules.isNotEmpty
                            },
                        ]
                    },
                    {
                        view: 'template',
                        type: 'section',
                        template: 'Реквизиты судебного решения'
                    },
                    {
                        margin: 10,
                        cols: [
                            {
                                view: 'text',
                                name: 'numberFile',
                                id: 'numberFile',
                                label: 'Номер дела',
                                labelPosition: 'top',
                                required: true,
                                invalidMessage: 'Поле не может быть пустым, и длина номера не должна превышать 100 симв.',
                                validate: function (val) {
                                    if (val) {
                                        if (val.length > 100 || val.length == 0) {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                    return true;
                                }
                            },
                            {
                                view: 'datepicker',
                                name: 'dateFile',
                                id: 'dateFile',
                                label: 'Дата',
                                editable: true,
                                labelPosition: 'top',
                                required: true,
                                validate: webix.rules.isNotEmpty,
                                invalidMessage: 'Поле не может быть пустым',
                            }
                        ]
                    },
                    {
                        id: 'btnsPanel',
                        margin: 10,
                        cols: [
                            {},
                            {
                                view: 'button',
                                id: 'save_button',
                                align: 'right',
                                css: 'webix_primary',
                                value: 'Сохранить',
                                maxWidth: 300,
                                click: function () {
                                    if ($$('personViolationForm').validate()) {
                                        $$('save_button').disable();

                                        let params = $$('personViolationForm').getValues();

                                        webix.ajax().headers({'Content-Type': 'application/json'}).post('save_person_violation', JSON.stringify(params))
                                            .then(function (data) {
                                                if (data.text() === 'Нарушение сохранено') {
                                                    webix.message({text: data.text(), type: 'success'});
                                                    showPersonViolations();
                                                } else {
                                                    webix.message({text: data.text(), type: 'error'});
                                                }
                                            });
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
                                    showPersonViolations();
                                    hideBtnBack()
                                }
                            }
                        ]
                    }
                ],
            }
        ]
    }
}