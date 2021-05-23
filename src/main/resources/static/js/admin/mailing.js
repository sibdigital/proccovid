
function changeLinkedMailingOkveds(){
    let mailingFormValues = $$('mailingForm').getValues();
    let data = $$('okved_table').serialize();

    let window = webix.ui({
        view: 'window',
        id: 'windowCLO',
        head: 'ОКВЭДы рассылки \"' + mailingFormValues.name + '\" (id: '+ mailingFormValues.id +')',
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

const mailingFormMain = {
    rows: [
        { cols: [
                { view: 'text', label: 'Наименование', labelPosition: 'left', id: 'name', name: 'name', required: true, validate: webix.rules.isNotEmpty },
                { view: 'richselect',
                    name: 'status',
                    id: 'status',
                    label: 'Статус',
                    labelPosition: 'left',
                    required: true,
                    options: [
                        {id: "0", value:'Не действует'},
                        {id: "1", value:'Действует'}
                    ]},
            ]},
        {
            view: 'checkbox',
            label: 'Доступна пользователям',
            labelPosition: 'left',
            name: 'isUserVisibility',
        },
        { view: 'textarea', label: 'Описание', labelPosition: 'top', name: 'description', autoheight: true},
        { cols: [
                {},
                {
                    view: 'button',
                    align: 'right',
                    maxWidth: 200,
                    css: 'webix_primary',
                    value: 'Сохранить',
                    click: function () {
                        if ($$('mailingForm').validate()) {
                            let params = $$('mailingForm').getValues();

                            let okveds = $$('okved_table').serialize();
                            params.okveds = okveds;
                            params.status = parseInt(params.status);

                            webix.ajax().headers({
                                'Content-Type': 'application/json'
                            }).post('save_cls_mailing_list',
                                params).then(function (data) {
                                var response = JSON.parse(data.text());
                                if (response.success == true) {
                                    webix.message({text: response.message, type: 'success'});
                                    webix.ui(mailingList, $$('mailingFormId'));
                                    $$('mailing_table').clearAll();
                                    $$('mailing_table').load('cls_mailing_list');
                                } else {
                                    webix.message({text: response.message, type: 'error'});
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
                        webix.ui(mailingList, $$('mailingFormId'));
                    }
                }
            ]}
    ]
}

const mailingListOkved = {
    rows:[
        {
            view: 'form',
            autoheight: true,
            rows: [
                {
                    view: 'label',
                    label: 'ОКВЭДы',
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
                            click: changeLinkedMailingOkveds},
                    ]
                },

            ]
        },
    ]
}

const mailingListInn = {
    rows: [
        {
            view: 'textarea',
            label: 'Введите (через ;) ИНН для добавления к подписчикам рассылки',
            labelPosition: 'top',
            name: 'followerInns',
            id: 'followerInns',
            autoheight: true
        },
    ]
}

const mailingListFollower= {

}

const mailingList = {
    view: 'scrollview',
    id: 'mailingListId',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        id: 'mailing_table',
                        view: 'datatable',
                        select: 'row',
                        resizeColumn:true,
                        readonly: true,
                        columns: [
                            { id: 'name', header: 'Наименование', adjust: true, sort: 'string', fillspace: true },
                            { id: 'description', header: 'Описание', adjust: true, sort: 'string', fillspace: true },
                            { id: 'status', template: function (obj) {
                                    if (obj.status == 0) {
                                        return 'Не действует';
                                    }
                                    else {
                                        return 'Действует';
                                    }}, header: 'Статус', adjust: true, sort: 'string' },
                        ],
                        on: {
                            onChange: function () {
                                window.location.reload();
                            },
                            onItemDblClick: function (id) {
                                let data = $$('mailing_table').getItem(id);
                                data.status = '' + data.status;

                                webix.ui(mailingForm, $$('mailingListId'));

                                $$('mailingForm').parse(data);
                                $$('mailingForm').load(
                                    function (){
                                        var xhr = webix.ajax().sync().get('mailing_list_okveds/' + data.id);
                                        var responseText = xhr.responseText.replace("\"id\":", "\"index\":");
                                        var jsonResponse = JSON.parse(responseText);
                                        for (var k in jsonResponse) {
                                            var row = jsonResponse[k].okved;
                                            $$('okved_table').add(row);
                                        }
                                    });
                            }
                        },
                        data: [],
                        url: 'cls_mailing_list',
                    },
                    {
                        cols: [
                            {},
                            {},
                            {},
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                align: 'right',
                                value: 'Добавить',
                                click: function () {
                                    webix.ui(mailingForm, $$('mailingListId'));
                                }
                            }
                        ]
                    }]
            }]
    }
}

const mailingForm = {
    view: 'scrollview',
    //scroll: 'y',
    id: 'mailingFormId',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                id: 'mailingForm',
                view: 'form',
                complexData: true,
                elements: [
                    {
                        id: 'tabview',
                        view: 'tabview',
                        cells: [
                            {
                                header: 'Основное',
                                body: mailingFormMain
                            },
                            {
                                header: 'ОКВЭД',
                                body: mailingListOkved
                            },
                            {
                                header: 'ИНН',
                                body: mailingListInn
                            },
                            {
                                header: 'Подписаны',
                                body: mailingListFollower
                            },
                        ]
                    }
                ]
            }
        ]
    }
}
