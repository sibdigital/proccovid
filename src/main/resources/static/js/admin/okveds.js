
function createOkved() {
    let params = $$('okvedCreateForm').getValues();
    webix.ajax().headers({
        'Content-Type': 'application/json'
    }).post('save_okved',
        JSON.stringify(params)
    ).then(function (data) {
        if (data.text() === 'ОКВЭД сохранен') {
            webix.message({text: data.text(), type: 'success'});
            $$('window').close();
            $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()]);

        } else {
            webix.message({text: data.text(), type: 'error'});
        }
    })
}

const okvedCreateForm = {
    view: 'scrollview',
    scroll: 'y',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        rows: [
            {
                view: 'form',
                id: 'okvedCreateForm',
                rows: [
                    {
                        cols: [
                            {view: 'text', label: 'Код', labelPosition: 'top', name: 'kindCode'},
                            {view: 'text', label: 'Версия', labelPosition: 'top', name: 'version', readonly: true},
                        ]
                    },
                    {view: 'text', label: 'Наименование', labelPosition: 'top', name: 'kindName'},
                    {view: 'textarea', label: 'Описание', labelPosition: 'top', name: 'description', autoheight: true},
                    {view: 'radio', label: 'Статус', name: 'status', name: 'status', options: [
                            {value: 'Работа разрешена', id: 1},
                            {value: 'Работа приостановлена', id: 0},
                        ]},
                    {cols: [
                            {view: 'button', value: 'Сохранить', click: createOkved},
                        ]}

                ]
            }
        ]
    }
}


const okveds = {
    view: 'scrollview',
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
                        rows: [
                            {
                                view: 'search',
                                id: 'search',
                                maxWidth: 300,
                                minWidth: 100,
                                tooltip: 'После ввода значения нажмите Enter',
                                placeholder: "Введите код или наименование из ОКВЭД",
                                on: {
                                    onEnter: function () {
                                        $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                    }
                                }
                            },
                            {
                                cols: [
                                    {
                                        view: 'segmented', id:'tabbar',  multiview: true,
                                        width: 600,
                                        optionWidth: 150,  align: 'left', padding: 10,
                                        options: [
                                            { value: '2001', id: '2001'},
                                            { value: '2014', id: '2014'},
                                            { value: 'Синтетические', id: 'synt'}
                                        ],
                                        on: {
                                            onAfterRender() {
                                                this.callEvent('onChange', ['2001']);
                                            },
                                            onChange: function (id) {
                                                let version = '2001';
                                                switch (id) {
                                                    case '2001':
                                                        version = '2001';
                                                        $$('upload_okved').show();
                                                        $$('create_okved').hide();
                                                        break
                                                    case '2014':
                                                        version = '2014';
                                                        $$('upload_okved').show()
                                                        $$('create_okved').hide();
                                                        break
                                                    case 'synt':
                                                        version = 'synt';
                                                        $$('upload_okved').hide()
                                                        $$('create_okved').show();
                                                        break
                                                }

                                                let params = '';
                                                let search_text = $$('search').getValue();
                                                if (search_text) {
                                                    params += params == '' ? '?' : '&';
                                                    params += 'searchText=' + search_text;
                                                }

                                                let view = okvedslist('list_okved/' + version + params, version, true);

                                                webix.delay(function () {
                                                    webix.ui({
                                                        id: 'subContentOkved',
                                                        rows: [
                                                            view
                                                        ]
                                                    }, $$('subContentOkved'))
                                                })
                                            }
                                        }
                                    },
                                    {},
                                    {
                                        view: 'button',
                                        align: 'right',
                                        id: 'create_okved',
                                        value: 'Добавить',
                                        width: 140,
                                        hidden: true,
                                        click: function() {
                                            data = {version: 'synt', status: 1};
                                            let window = webix.ui({
                                                view: 'window',
                                                id: 'window',
                                                head: 'Создание синтетического ОКВЭДа',
                                                close: true,
                                                width: 1000,
                                                height: 800,
                                                position: 'center',
                                                modal: true,
                                                body: okvedCreateForm,
                                            });

                                            $$('okvedCreateForm').parse(data);
                                            window.show();
                                        }
                                    },
                                    {
                                        view: 'button',
                                        align: 'right',
                                        id: 'upload_okved',
                                        value: 'Загрузить',
                                        width: 140,
                                        hidden: true,
                                        click: function() {
                                            var version = $$('tabbar').getValue();
                                            window.open('upload?version=' + version);
                                        }
                                    }
                                ],
                            }
                        ]
                    },
                    {
                        id: 'subContentOkved'
                    }
                ]
            }
        ]
    }
}
