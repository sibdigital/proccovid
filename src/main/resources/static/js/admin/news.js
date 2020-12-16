function changeLinkedNewsOkveds(){
    let newsFormValues = $$('newsForm').getValues();
    let data = $$('okved_table').serialize();

    let window = webix.ui({
        view: 'window',
        id: 'windowCLO',
        head: 'ОКВЭДы новости \"' + newsFormValues.heading + '\" (id: '+ newsFormValues.id +')',
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

const newsListForm = {
    view: 'scrollview',
    id: 'newsListFormId',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        id: 'news_table',
                        view: 'datatable',
                        select: 'row',
                        multiselect: true,
                        resizeColumn:true,
                        readonly: true,
                        columns: [
                            { id: 'startTime', header: 'Время начала публикации ', adjust: true, format: dateFormat, sort: "date", fillspace: true },
                            { id: 'endTime', header: 'Время окончания  публикации', adjust: true, format: dateFormat, sort: "date", fillspace: true },
                            { id: 'heading', header: 'Заголовок',  adjust: true, fillspace: true, sort: 'text'},
                        ],
                        scheme: {
                            $init: function (obj) {
                                obj.startTime = obj.startTime.replace("T", " ");
                                obj.startTime = xml_format(obj.startTime);
                                obj.endTime = obj.endTime.replace("T", " ");
                                obj.endTime = xml_format(obj.endTime);
                            },
                            $update:function (obj) {
                                obj.startTime = obj.startTime.replace("T", " ");
                                obj.startTime = xml_format(obj.startTime);
                                obj.endTime = obj.endTime.replace("T", " ");
                                obj.endTime = xml_format(obj.endTime);
                            },

                        },
                        on: {
                            onItemDblClick: function (id) {
                                item = this.getItem(id);
                                var xhr = webix.ajax().sync().get('news/' + item.id);
                                var jsonResponse = JSON.parse(xhr.responseText);
                                var data = {
                                    id: item.id,
                                    heading: jsonResponse.heading,
                                    message: jsonResponse.message,
                                    startTime: jsonResponse.startTime.replace("T", " "),
                                    endTime: jsonResponse.endTime.replace("T", " "),
                                    hashId: jsonResponse.hashId,
                                };
                                webix.ui(newsFormTab, $$('newsListFormId'));

                                $$('newsForm').parse(data);

                                $$('newsForm').load(function (){
                                    var xhr = webix.ajax().sync().get('news_tables/' + data.id);
                                    var jsonResponse = JSON.parse(xhr.responseText);
                                    var okveds    = jsonResponse['okveds'];
                                    var inn       = jsonResponse['inn'];
                                    var files     = jsonResponse['files'];
                                    var statuses  = jsonResponse['statuses'];

                                    for (var k in okveds) {
                                        $$('okved_table').add(okveds[k].okved);
                                    }

                                    for (var k in inn) {
                                        var row= {value: inn[k]};
                                        $$('inn_table').add(row);
                                    }

                                    if (files.length > 0) {
                                        $$('uploadedFiles').show();
                                    }
                                    for (var k in files) {
                                        $$('uploadedFiles').add(files[k]);
                                    }

                                    for (var k in statuses) {
                                        $$('status_table').add(statuses[k]);
                                    }

                                })

                                $$('newsForm').load(
                                    function (){
                                        var xhr = webix.ajax().sync().get('subdomainWork/' );
                                        var link = xhr.responseText + "/news?hash_id=" + data.hashId
                                        $$('link').setValue("<a href ='" + link + "'>" + link + "</a>")
                                    });
                            }
                        },
                        data: [],
                        url: 'news',
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
                                    webix.ui(newsFormTab, $$('newsListFormId'));
                                    $$('newsForm').load(
                                        function (){
                                            var xhr = webix.ajax().sync().get('init_news_statuses');
                                            var jsonResponse = JSON.parse(xhr.responseText);
                                            for (var k in jsonResponse) {
                                                var row = jsonResponse[k];
                                                $$('status_table').add(row);
                                            }
                                        });
                                }
                            }
                        ]
                    }]
            }]
    }
}

const newsFormTab = {
    view: 'scrollview',
    id: 'newsFormTabId',
    scroll: 'xy',
    // autowidth: true,
    // autoheight: true,
    body: {
        rows: [
            {
                id: 'newsForm',
                view: 'form',
                complexData: true,
                elements: [
                    {
                        id: 'tabview',
                        view: 'tabview',
                        cells: [
                            {
                                header: 'Основное',
                                body: {
                                    rows: [
                                        view_section('Основные сведения'),
                                        {view: 'text', label: 'Заголовок', labelPosition: 'top', name: 'heading', id: 'heading'},
                                        {cols:[
                                                {
                                                    view: 'datepicker',
                                                    label: 'Время начала публикации',
                                                    labelPosition: 'top',
                                                    name: 'startTime',
                                                    timepicker: true,
                                                    id: 'startTime'},
                                                {},
                                                {
                                                    view: 'datepicker',
                                                    label: 'Время окончания публикации',
                                                    labelPosition: 'top',
                                                    name: 'endTime',
                                                    timepicker: true,
                                                    id: 'endTime'},
                                            ]},
                                        {
                                            view: 'nic-editor',
                                            id: 'message',
                                            name: 'message',
                                            css: "myClass",
                                            cdn: false,
                                            config: {
                                                iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                                            }
                                        },
                                        {
                                            view: 'datatable',
                                            id: 'uploadedFiles',
                                            autoheight: true,
                                            header: 'Загруженные файлы',
                                            hidden: true,
                                            columns: [
                                                {
                                                    id: 'originalFileName',
                                                    header: '',
                                                    fillspace: true
                                                },
                                                {
                                                    id: 'btnDelete',
                                                    header: " ",
                                                    template: "{common.trashIcon()}"
                                                },
                                            ],
                                            onClick: {
                                                "wxi-trash": function (event, id, node) {
                                                    webix.ajax().get('/delete_news_file',
                                                        {id: id.row}
                                                    ).then(function (data) {
                                                        if (data.text() === 'Файл удален') {
                                                            $$('uploadedFiles').remove(id);
                                                        } else {
                                                            webix.message({text: data.text(), type: 'error'});
                                                        }
                                                    })
                                                }
                                            }
                                        },
                                        {
                                            view: 'list',
                                            id: 'newsFiles',
                                            type: 'uploader',
                                            autoheight: true,
                                        },
                                        {
                                            cols: [
                                                {
                                                    view: 'uploader',
                                                    id: 'uploader',
                                                    css: 'webix_primary',
                                                    value: 'Прикрепить файл(-ы)',
                                                    autosend: false,
                                                    upload: '/upload_news_file',
                                                    required: true,
                                                    accept: 'application/pdf, application/zip',
                                                    multiple: true,
                                                    link: 'newsFiles',
                                                    maxWidth: 200
                                                },
                                                {}
                                            ]
                                        },
                                        {
                                            view: 'label',
                                            id: 'link'
                                        }
                                    ]
                                }
                            },
                            {
                                header: 'Фильтры',
                                body: {
                                    rows:[
                                        { view: 'tabview',
                                            cells:[
                                                {
                                                    header: 'По ИНН',
                                                    id:"innView",
                                                    view:"form",
                                                    rows: [
                                                        {
                                                            view: 'datatable',
                                                            id: 'inn_table',
                                                            label: '',
                                                            labelPosition: 'top',
                                                            minHeight: 200,
                                                            select: 'row',
                                                            editable: true,
                                                            columns: [
                                                                {
                                                                    id: 'value',
                                                                    editor:"text",
                                                                    header: 'ИНН',
                                                                    fillspace: true
                                                                },
                                                                {
                                                                    id: 'btnDelete',
                                                                    header: " ",
                                                                    template: "{common.trashIcon()}"
                                                                },
                                                            ],
                                                            onClick: {
                                                                "wxi-trash": function (event, id, node) {
                                                                    this.remove(id)
                                                                }
                                                            }
                                                        },
                                                        {
                                                            cols: [
                                                                {},
                                                                {
                                                                    view:"button",
                                                                    maxWidth:200,
                                                                    label:"Добавить",
                                                                    click:function(){
                                                                        $$('inn_table').add({});
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                },
                                                {
                                                    header: 'По статусам заявок',
                                                    id:"statusView",
                                                    view:"form",
                                                    rows: [
                                                        {
                                                            view: 'datatable',
                                                            id: 'status_table',
                                                            label: '',
                                                            labelPosition: 'top',
                                                            minHeight: 200,
                                                            select: 'row',
                                                            editable: true,
                                                            columns: [
                                                                {
                                                                    id: 'checked',
                                                                    header:"",
                                                                    css:"center",
                                                                    template:"{common.checkbox()}"
                                                                },
                                                                {
                                                                    id:"value",
                                                                    header:"Статус заявки",
                                                                    fillspace: true
                                                                },
                                                                {
                                                                    id:"reviewStatus",
                                                                    hidden: true,
                                                                },
                                                            ],
                                                        },
                                                    ]
                                                },
                                                {
                                                    header:'По ОКВЭДам',
                                                    id:"okvedView",
                                                    view:"form",
                                                    rows: [
                                                        {
                                                            view: 'datatable', name: 'okved_table', label: '', labelPosition: 'top',
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
                                                        {
                                                            cols: [
                                                                {},
                                                                {
                                                                    view: 'button',
                                                                    value: 'Изменить ОКВЭДы',
                                                                    align: 'right',
                                                                    css: 'webix_primary',
                                                                    maxWidth: 200,
                                                                    click: changeLinkedNewsOkveds
                                                                },
                                                            ]
                                                        },
                                                    ]
                                                },
                                            ]
                                        }
                                    ]
                                }
                            }
                            ]
                    },
                    { cols: [
                            {},
                            {
                                view: 'button',
                                align: 'right',
                                maxWidth: 200,
                                css: 'webix_primary',
                                value: 'Сохранить',
                                click: function () {
                                    if ($$('newsForm').validate()) {
                                        let params = $$('newsForm').getValues();
                                        let okveds = $$('okved_table').serialize();
                                        let innList = $$('inn_table').serialize();
                                        let statuses = $$('status_table').serialize();
                                        params.okveds = okveds;
                                        params.innList = innList;
                                        params.statuses = statuses;

                                        webix.ajax().headers({
                                            'Content-Type': 'application/json'
                                        }).post('/save_news',
                                            params).then(function (data) {
                                                const savedNews = data.json();
                                                if (savedNews.id)  {
                                                    let uploader = $$('uploader');
                                                    if (uploader) {
                                                        successfullyUploaded = true
                                                        uploader.define('formData', {idNews: savedNews.id})
                                                        uploader.send(function (response) {
                                                            if (response) {
                                                                console.log(response.cause);
                                                                if (response.cause != 'Файл успешно загружен') {
                                                                    successfullyUploaded = false
                                                                }
                                                            }

                                                            if (successfullyUploaded) {
                                                                webix.message({text: 'Новость сохранена', type: 'success'});
                                                                window.location.reload(true);
                                                            }
                                                        })


                                                    }
                                                }
                                                else {
                                                    webix.message({text: 'Не удалось сохранить новость', type: 'error'});
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
                                    // webix.ui(newsListForm, $$('newsFormTabId'));
                                    window.location.reload(true)
                                }
                            }]
                    }]
            },
        ]
    }
}