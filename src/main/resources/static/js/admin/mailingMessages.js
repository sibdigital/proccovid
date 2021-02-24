
function queueUp() {
    webix.confirm('Вы действительно хотите поставить в очередь?')
        .then(
            function () {
                var selectedRows = $$('mailing_messages_table').getSelectedId(true);
                selectedRows.forEach(element => {
                    var item = $$('mailing_messages_table').getItem(element.id);
                    params = {id: item.id, status: 1, sendingTime: item.sendingTime};
                    webix.ajax().get('/change_status', params).then(function (data) {
                        if (data.text() === 'Статус изменен') {
                            webix.message({
                                text: 'Сообщение (id: ' + item.id + ') поставлено в очередь',
                                type: 'success'
                            });

                            $$('mailing_messages_table').clearAll();
                            $$('mailing_messages_table').load('reg_mailing_message');
                        } else {
                            webix.message({
                                text: 'Не получилось поставить в очередь сообщение (id: ' + item.id + ')',
                                type: 'error'
                            });
                        }
                    })
                })
            }
        )
}

function deleteFromQueue() {
    webix.confirm('Вы действительно хотите удалить из очереди?')
        .then(
            function () {
                var selectedRows = $$('mailing_messages_table').getSelectedId(true);
                selectedRows.forEach(element => {
                    var item = $$('mailing_messages_table').getItem(element.id);
                    params = {id: item.id, status: 0, sendingTime: item.sendingTime};
                    webix.ajax().get('/change_status', params).then(function (data) {
                        if (data.text() === 'Статус изменен') {
                            webix.message({
                                text: 'Сообщение (id: ' + item.id + ') удалено из очереди',
                                type: 'success'
                            });
                            $$('mailing_messages_table').clearAll();
                            $$('mailing_messages_table').load('reg_mailing_message');
                        } else {
                            webix.message({
                                text: 'Не получилось удалить из очереди сообщение (id: ' + item.id + ')', type: 'error'
                            });
                        }
                    })
                })
            })
}

const mailingMessages = {
    view: 'scrollview',
    id: 'mailingMessagesId',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'toolbar',
                        id: 'mMToolbar',
                        cols:[
                            {},
                            {},
                            {},
                            {},
                            { view: 'button', id: 'BtnQueueUp', value: 'Поставить в очередь', align: 'right', click: queueUp},
                            { view: 'button', id: 'BtnDeleteFromQueue', value: 'Удалить из очереди', align: 'right', click: deleteFromQueue}
                            ]
                    },
                    {
                        id: 'mailing_messages_table',
                        view: 'datatable',
                        select: 'row',
                        multiselect: true,
                        resizeColumn:true,
                        readonly: true,
                        columns: [
                            { id: 'sendingTime', header: 'Время начала отправки', adjust: true, format: dateFormat, sort: "date", fillspace: true },
                            { id: 'mailing', header: 'Тип рассылки', template: '#clsMailingList.name#', adjust: true, sort: 'string', fillspace: true },
                            { id: 'statusMailing', header: 'Статус типа рассылки', template: function (obj) {
                                    if (obj.clsMailingList.status == 0) {
                                        return 'Не действует';
                                    }
                                    else {
                                        return 'Действует';
                                    }}, adjust: true, sort: 'string', fillspace: true },
                            { id: 'message', header: 'Текст сообщения', adjust: true, fillspace: true, sort: 'text'},
                            { id: 'status', template: function (obj) {
                                    switch (obj.status) {
                                        case 0: return 'Создано';
                                            break;
                                        case 1: return 'В очереди на отправку';
                                            break;
                                        case 2: return 'Отправка проведена';
                                            break;
                                    }}, header: 'Статус', adjust: true, sort: 'string' },
                        ],
                        scheme: {
                            $init: function (obj) {
                                obj.sendingTime = obj.sendingTime.replace("T", " ");
                                obj.sendingTime = xml_format(obj.sendingTime);
                            },
                            $update:function (obj) {
                                obj.sendingTime = obj.sendingTime.replace("T", " ");
                                obj.sendingTime = xml_format(obj.sendingTime);
                            },

                        },
                        on: {
                            onItemDblClick: function (id) {
                                let item = $$('mailing_messages_table').getItem(id);
                                var xhr = webix.ajax().sync().get('reg_mailing_message/' + item.id);
                                var jsonResponse = JSON.parse(xhr.responseText);
                                var data = {
                                    id: item.id,
                                    mailingId: jsonResponse.clsMailingList.id,
                                    message: jsonResponse.message,
                                    sendingTime: (jsonResponse.sendingTime != null ? jsonResponse.sendingTime.replace("T", " ") : ""),
                                    status: ''+jsonResponse.status
                                };

                                webix.ui(mailingMessageForm, $$('mailingMessagesId'));

                                $$('mailingMessageForm').parse(data);
                            }
                        },
                        data: [],
                        url: 'reg_mailing_message',
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
                                    webix.ui(mailingMessageForm, $$('mailingMessagesId'));
                                }
                            }
                        ]
                    }]
            }]
    }
}

const mailingMessageForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'mailingMessageFormId',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'mailingMessageForm',
                rows: [
                    { view: 'text',
                        label: 'Тема',
                        id: 'subject',
                        name: 'subject',
                        required: true,
                        validate: webix.rules.isNotEmpty
                    },
                    {
                        cols: [
                            {
                                view: 'richselect',
                                name: 'mailingId',
                                id: 'mailingId',
                                label: 'Тип рассылки',
                                labelPosition: 'left',
                                required: true,
                                options: 'mailing_list_short',
                            },
                            { view: 'richselect',
                                name: 'status',
                                id: 'status',
                                label: 'Статус',
                                labelPosition: 'left',
                                required: true,
                                options: [
                                    {id: "0", value:'Создано'},
                                    {id: "1", value:'В очереди на отправку'},
                                    {id: "2", value: 'Отправка проведена'}
                                ]
                            },
                            { view: 'datepicker',
                                label: 'Время начала отправки',
                                labelPosition: 'left',
                                name: 'sendingTime',
                                stringResult:true,
                                timepicker:true,
                                format:webix.i18n.fullDateFormat
                            },
                        ]
                    },
                    {
                        id:"views",
                        animate:false,
                        minHeight: 300,
                        cells: [
                            {
                                view: 'nic-editor',
                                id: 'message',
                                name: 'message',
                                //required: true,
                                css: "myClass",
                                cdn: false,
                                config: {
                                    iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                                }
                            },
                            {
                                view: 'ace-editor',
                                id: 'settings',
                                theme: 'github',
                                mode: 'json',
                                cdn: false
                            }
                        ]
                    },
                    {
                        cols: [
                            {},
                            { view: 'text',
                                label: 'Адрес тестового сообщения',
                                id: 'test_adress',
                                name: 'test_adress',
                                maxWidth: 200,
                            },
                            {
                                view: 'button',
                                align: 'right',
                                maxWidth: 200,
                                css: 'webix_secondary',
                                value: 'Отправить тест',
                                click: sendTest
                            },
                            {
                                view: 'button',
                                align: 'right',
                                maxWidth: 200,
                                css: 'webix_secondary',
                                value: 'Отмена',
                                click: function () {
                                    webix.ui(mailingMessages, $$('mailingMessageFormId'));
                                }
                            },
                            {
                                view: 'button',
                                align: 'right',
                                maxWidth: 200,
                                css: 'webix_primary',
                                value: 'Сохранить',
                                click: saveMessage
                            },
                        ]
                    }
                ]
            }
        ]
    }
}

function saveMessage(){
    if ($$('mailingMessageForm').validate()) {
        let params = $$('mailingMessageForm').getValues();
        params.status = parseInt(params.status);

        webix.ajax()
            .headers({
                'Content-Type': 'application/json'
            })
            .post('/save_reg_mailing_message',params)
            .then(function (data) {
                if (data.text() === 'Сообщение сохранено') {
                    webix.message({text: data.text(), type: 'success'});

                    webix.ui(mailingMessages, $$('mailingMessageFormId'));
                    $$('mailing_messages_table').clearAll();
                    $$('mailing_messages_table').load('reg_mailing_message');

                } else {
                    webix.message({text: data.text(), type: 'error'});
                }
            })
    } else {
        webix.message({text: 'Не заполнены обязательные поля', type: 'error'});
    }
}

function sendTest(){
    let params = $$('mailingMessageForm').getValues();
    const postParams = {
        address: params.test_adress,
        message: params.message,
        subject: params.subject
    }
    webix.ajax()
        .headers({
            'Content-Type': 'application/json'
        })
        .post('/send_test_message',postParams)
        .then(function (data) {
            const responce = data.json();
            if (responce.success == true) {
                webix.message({text: responce.message(), type: 'success'});
            } else {
                webix.message({text: responce.message(), type: 'error'});
            }
        })
}