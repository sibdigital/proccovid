function changeLinkedDepartmentOkveds(){
    let departmentFormValues = $$('departmentForm').getValues();
    let data = $$('okved_table').serialize();

    let window = webix.ui({
        view: 'window',
        id: 'windowCLO',
        head: 'ОКВЭДы подразделения \"' + departmentFormValues.name + '\" (id: '+ departmentFormValues.id +')',
        close: true,
        width: 1000,
        height: 800,
        position: 'center',
        modal: true,
        body: linkedOkvedsForm,
        on: {
            'onHide': function() {
                window.destructor();
            }
        }

    });
    $$('linked_okved_table').parse(data);

    window.show();
}

const departments = {
    view: 'scrollview',
    id: 'departmentsId',
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
                        id: 'departments_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        // pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "name", header: "Наименование", template: "#name#", adjust: true},
                            {id: "description", header: "Описание", template: "#description#", adjust: true},
                            {
                                id: "deleted", header: "Удален",
                                template: function (obj) {
                                    let text = '';
                                    if (obj.deleted === true) {
                                        text = 'Да'
                                    }
                                    return text;
                                },
                                adjust: true},
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
                            onItemDblClick: function (id) {

                                let data = $$('departments_table').getItem(id);
                                webix.ui(departmentForm, $$('departmentsId'));

                                // let window = webix.ui({
                                //     view: 'window',
                                //     id: 'window',
                                //     head: 'Редактирование подразделения (id: ' + data.id + ').',
                                //     close: true,
                                //     width: 1000,
                                //     height: 800,
                                //     position: 'center',
                                //     modal: true,
                                //     body: departmentForm,
                                //     on: {
                                //         'onShow': function () {
                                //             var xhr = webix.ajax().sync().get('department_okveds/' + data.id);
                                //             var jsonResponse = JSON.parse(xhr.responseText);
                                //             for (var k in jsonResponse) {
                                //                 var row = {
                                //                     name_okved: jsonResponse[k].value,
                                //                     path: jsonResponse[k].id,
                                //                     version: jsonResponse[k].id.substring(0, 4)
                                //                 }
                                //                 $$('okved_table').add(row);
                                //             }
                                //             $$('okved_version').setValue('2014');
                                //         },
                                //         'onHide': function(){
                                //             window.destructor();
                                //         }
                                //     }
                                // });

                                $$('departmentForm').parse(data);
                                $$('departmentForm').load(
                                    function (){
                                        var xhr = webix.ajax().sync().get('department_okveds/' + data.id);
                                        var responseText = xhr.responseText.replace("\"id\":", "\"index\":");
                                        var jsonResponse = JSON.parse(responseText);
                                        for (var k in jsonResponse) {
                                            var row = jsonResponse[k].okved;
                                            $$('okved_table').add(row);
                                        }
                                    });

                                window.show();
                            }
                        },
                        url: 'cls_departments'
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

                                    let window = webix.ui({
                                        view: 'window',
                                        id: 'window',
                                        head: 'Добавление подразделения',
                                        close: true,
                                        width: 1000,
                                        height: 800,
                                        position: 'center',
                                        modal: true,
                                        body: departmentForm,
                                        on: {
                                            'onShow': function () {
                                            }
                                        }
                                    });

                                    window.show();
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

const departmentForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'departmentFormId',
    autowidth: true,
    autoheight: true,
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'departmentForm',
                elements: [
                    { view: 'text', label: 'Наименование', labelPosition: 'top', name: 'name', required: true, validate: webix.rules.isNotEmpty },
                    { view: 'textarea', label: 'Описание', labelPosition: 'top', name: 'description', required: true, validate: webix.rules.isNotEmpty },
                    {
                        rows: [
                            {
                                view: 'label',
                                label: 'ОКВЭД',
                                align: 'left',
                            },
                            {
                                view: 'datatable', name: 'okved_table', label: '', labelPosition: 'top',
                                // autoheight: true,
                                minHeight: 200,
                                select: 'row',
                                editable: true,
                                id: 'okved_table',
                                columns: [
                                    {
                                        id: 'index',
                                        hidden: true
                                    },
                                    {
                                        id: 'kindCode',
                                        header: 'Код',
                                    },
                                    {
                                        id: 'version',
                                        header: 'Версия',
                                    },
                                    {
                                        id: 'kindName',
                                        header: 'ОКВЭД',
                                        fillspace: true,
                                    },
                                ],
                                data: [],
                            },
                            {cols: [
                                    {},
                                    {
                                        view: 'button',
                                        value: 'Изменить ОКВЭДы',
                                        align: 'right',
                                        css: 'webix_primary',
                                        maxWidth: 200,
                                        click: changeLinkedDepartmentOkveds},
                                ]
                            },
                        ]
                    },
                    // { view: ''},
                    { view: 'checkbox', label: 'Удален', labelPosition: 'top', name: 'deleted' },
                    {
                        cols: [
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                align: 'right',
                                maxWidth: 200,
                                value: 'Сохранить',
                                click: function () {
                                    if ($$('departmentForm').validate()) {
                                        let params = $$('departmentForm').getValues();

                                        let okveds = $$('okved_table').serialize();
                                        params.okveds = okveds;

                                        webix.ajax().headers({
                                            'Content-Type': 'application/json'
                                        }).post('/save_cls_department',
                                            params).then(function (data) {
                                            if (data.text() === 'Подразделение сохранено') {
                                                webix.message({text: data.text(), type: 'success'});

                                                webix.ui(departments, $$('departmentFormId'));
                                                const departmentsTable = $$('departments_table');
                                                const url = departmentsTable.data.url;
                                                departmentsTable.clearAll();
                                                departmentsTable.load(url);
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
                                maxWidth: 200,
                                css: 'webix_secondary',
                                value: 'Отмена',
                                click: function () {
                                    webix.ui(departments, $$('departmentFormId'));
                                }
                            },
                        ]
                    }
                ]
            }
        ]
    }
}