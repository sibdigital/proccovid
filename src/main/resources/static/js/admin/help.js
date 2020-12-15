const getDropDownView = (header, desc) => {
    const buttonValue = header.title  || 'Раскрыть';
    const textAreaValue = desc.description || 'Описание';

    // const dropDownView = {
    //     view: 'scrollview',
    //     autowidth: true,
    //     autoheight: true,
    //     body: {
    //         type: 'space',
    //         rows: [
    //             {
    //                 type: 'wide',
    //                 cols: [
    //                     {
    //                         view: 'button',
    //                         id: 'dropDownButton',
    //                         css: 'webix_primary',
    //                         value: buttonValue,
    //                         click: (id, event) => {
    //                             console.log(id, event);
    //                             const textArea = $$('dropDownTextArea');
    //                             if (textArea.isVisible()) {
    //                                 textArea.hide();
    //                             } else {
    //                                 textArea.show();
    //                             }
    //                         }
    //                     },
    //                     {
    //                         view: 'textarea',
    //                         id: 'dropDownTextArea',
    //                         value: textAreaValue,
    //                         readonly: true,
    //                     }]
    //             },
    //         ]
    //     }
    // };

    const dropDownView = [
        {
            type: 'wide',
            cols: [
                {
                    view: 'button',
                    id: 'dropDownButton',
                    css: 'webix_primary',
                    value: buttonValue,
                    click: (id, event) => {
                        console.log(id, event);
                        const textArea = $$('dropDownTextArea');
                        if (textArea.isVisible()) {
                            textArea.hide();
                        } else {
                            textArea.show();
                        }
                    }
                },
                {
                    view: 'textarea',
                    id: 'dropDownTextArea',
                    value: textAreaValue,
                    readonly: true,
                }]
        },
    ];


    console.log(dropDownView);
    return dropDownView;
}

function parseData(data, globalIds) {
    const usedIndex = {};
    const getNode = (data, id) => {
        const node = [];
        data.forEach((item, index) => {
            if (!usedIndex[index]) {
                usedIndex[index] = true;
                if (item.global_parent_id === id) {
                    node.push(item);
                }
            }
        });
        return node;
    }
    const nodes = [];
    globalIds.forEach((item) => nodes.push(getNode(data, item)));
    return nodes;
}

function getFormWithData(url) {
    const typenames = {
        'dropdown-header': 'dropdown-menu',
        'dropdown-title': '',
        'dropdown-description': '',
    };

    const getForm = (data) => {
        console.log(data);

        return data.forEach((item) => {
            {
                if (!item) return;

                const headerIndex = item.find((it, index) => {
                    if (it.parent_id === null)
                        return index;
                });

                return getDropDownView(data[headerIndex], data[1]);
            }
        });
    }


    const params = '';
    return webix.ajax()
        .headers({'Content-Type': 'application/json'})
        .get(url, params)
        .then(function (data) {
            if (data.json()) {
                const nodes = data.json();
                return webix.ajax()
                    .headers({'Content-Type': 'application/json'})
                    .get('/help/statistic/globalIds', params)
                    .then(function (data) {
                        if (data.json()) {
                            const globalIds = data.json();
                            console.log('array global id', globalIds);
                            const resultNodes = parseData(nodes, globalIds);
                            console.log(resultNodes);
                            return getForm(resultNodes);
                        }
                    });
            } else {
                webix.message({ text: data.json(), type: 'error' });
            }
    });

}

function getHelp(url) {
    // const view = getFormWithData(url);
    // console.log(view);
    // return view;

    return {
        view: 'scrollview',
        autowidth: true,
        autoheight: true,
        body:
            {
                type: 'space',
                rows: [
                    {
                        view: "dataview",
                        id: 'currentHelpDataView',
                        scroll: true,
                        select: 1,
                        url: url,
                        xCount: 1,
                        yCount: 1,
                        type: {
                            height: "auto",
                            width: "auto",
                            template: "<div class='webix_strong'>#name#</div><br>#description#",
                        },
                        // body: {
                        //     // type: 'wide',
                        //     // rows: getFormWithData(url)
                        //     view: 'dataview',
                        //     //scroll: 'xy',
                        //     xCount: 1,
                        //     yCount: 1,
                        //     url: url,
                        //     type: {
                        //         height: "auto",
                        //         width: "auto",
                        //         template: "<div class='webix_strong'>#name#</div><br>#description#",
                        //     },
                        // }
                    },
                    {
                        cols: [
                            {
                                view: 'button',
                                value: 'Редактировать',
                                click: () => {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            getAddHelpForm($$('currentHelpDataView').getItem($$('currentHelpDataView').getIdByIndex(0)))
                                        ]
                                    }, $$('content'))
                                }
                            },
                            {
                                view: 'button',
                                value: 'Назад',
                                click: () => {
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            helpForm
                                        ]
                                    }, $$('content'))
                                }
                            }
                        ]
                    }]
            }
    };

    // return {
    //     view: 'scrollview',
    //     autowidth: true,
    //     //autoheight: true,
    //     body: {
    //         //type: 'space',
    //         rows: [
    //             {
    //                 //type: 'wide',
    //                 rows: [
    //                     {
    //                         view: 'button',
    //                         id: 'dropDownButton',
    //                         css: 'webix_primary',
    //                         value: 'Страница: Статистика по заявкам',
    //                         click: (id, event) => {
    //                             console.log(id, event);
    //                             const textArea = $$('dropDownTextArea');
    //                             if (textArea.isVisible()) {
    //                                 textArea.hide();
    //                             } else {
    //                                 textArea.show();
    //                             }
    //                         }
    //                     },
    //                     {
    //                         view: 'label',
    //                         id: 'dropDownTextArea',
    //                         label: 'Здесь собрана статистика по заявкам организаций и количестве работиков в оффисах и на удаленке',
    //                         //autoheight: true,
    //                         //height: 200,
    //                         hidden: true,
    //                         //readonly: true,
    //                     }]
    //             },
    //         ]
    //     }
    // };
}

const getAddHelpForm = (data = null) => {
    console.log(data);
    const nameLabel = data !== null ? data.name : '';
    const descLabel = data !== null ? data.description : '';
    const updateButtonLabel = data !== null ? 'Сохранить' : 'Добавить';

    return {
        view: 'scrollview',
        autowidth: true,
        autoheight: true,
        body: {
            type: 'space',
            rows: [
                {
                    view: 'form',
                    id: 'newHelpForm',
                    complexData: true,
                    elements: [
                        {
                            view: 'text',
                            id: 'header',
                            name: 'name',
                            label: 'Название',
                            labelPosition: 'top',
                            value: nameLabel
                        },
                        {
                            view: 'label',
                            label: 'Описание',
                        },
                        {
                            view: 'nic-editor',
                            id: 'message',
                            name: 'description',
                            css: "myClass",
                            cdn: false,
                            value: descLabel,
                            config: {
                                iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                            }
                        },
                        {
                            cols: [
                                {},
                                {
                                    view: 'button',
                                    maxWidth: 200,
                                    label: 'Отмена',
                                    click: function() {
                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                helpForm
                                            ]
                                        }, $$('content'))
                                    }
                                },
                                {
                                    view: 'button',
                                    maxWidth: 200,
                                    label: updateButtonLabel,
                                    click: function() {
                                        const params = $$('newHelpForm').getValues();

                                        if (data !== null) {
                                            params.id = data.id;
                                            params.key = data.key;
                                        }

                                        webix.ajax()
                                            .headers({ 'Content-Type': 'application/json' })
                                            .post('/help/add', JSON.stringify(params))
                                            .then((data) => {
                                                if (data !== null) {
                                                    $$('listHelps').load('helps');
                                                }
                                            });

                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                helpForm
                                            ]
                                        }, $$('content'))
                                    }
                                }
                            ]
                        },
                    ]
                }
            ]
        },

    };
}

const helpForm = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                type: 'wide',
                rows: [
                    {
                        view: 'list',
                        id: 'listHelps',
                        select: true,
                        url: 'helps',
                        template: '#name#',
                        // data: [
                        //     { id: "Departments", icon: "fas fa-globe", value: 'Подразделения' },
                        //     { id: "DepartmentUsers", icon: "fas fa-user-tie", value: 'Пользователи подразделений' },
                        //     { id: "Organizations",icon: "fas fa-file-alt", value: 'Организации' },
                        //     { id: "Requests", icon: "fas fa-file", value: 'Заявки' },
                        //     { id: "TypeRequests", icon: "fas fa-file-alt", value: 'Предписания' },
                        //     { id: "RestrictionTypes", icon: "fas fa-file-alt", value: 'Типы ограничений' },
                        //     { id: "Principals", icon: "fas fa-user", value: 'Пользователи' },
                        //     { id: "Templates", icon: "fas fa-comment-alt", value: 'Шаблоны сообщений' },
                        //     { id: "Statistic", icon: "fas fa-chart-bar", value: 'Статистика' },
                        //     { id: "Okveds", icon: "fas fa-folder", value: 'ОКВЭДы' },
                        //     { id: "Mailing", icon: "fas fa-paper-plane", value: 'Типы рассылок'},
                        //     { id: "MailingMessages", icon: "fas fa-envelope", value: 'Сообщения рассылок'},
                        //     { id: "Fias", icon: "fas fa-download", value: 'Загрузка ФИАС, ЕГРЮЛ'},
                        //     { id: "News", icon: "fas fa-newspaper", value: 'Новости'},
                        // ],
                        on: {
                            onAfterSelect: function(id) {
                                console.log(id);
                                const item = $$('listHelps').getItem(id);
                                console.log(item);
                                let url = '/help?id=' + id;

                                // switch (id) {
                                //     case 'Departments': {
                                //         url = '/help?name=departments';
                                //         break;
                                //     }
                                //     case 'DepartmentUsers': {
                                //         url = '/help?name=departmentUsers';
                                //         break;
                                //     }
                                //     case 'Principals': {
                                //         url = '/help?name=principals';
                                //         break;
                                //     }
                                //     case 'Templates': {
                                //         url = '/help?name=templates';
                                //         break;
                                //     }
                                //     case 'TypeRequests': {
                                //         url = '/help?name=typeRequests';
                                //         break;
                                //     }
                                //     case 'Requests': {
                                //         url = '/help?name=requests';
                                //         break;
                                //     }
                                //     case 'Statistic': {
                                //         url = '/help?name=statistic';
                                //         break;
                                //     }
                                //     case 'Okveds': {
                                //         url = '/help?name=okveds';
                                //         break;
                                //     }
                                //     case 'Mailing': {
                                //         url = '/help?name=mailing';
                                //         break;
                                //     }
                                //     case 'MailingMessages': {
                                //         url = '/help?name=mailingMessages';
                                //         break;
                                //     }
                                //     case 'Fias': {
                                //         url = '/help?name=fias';
                                //         break;
                                //     }
                                //     case 'News': {
                                //         url = '/help?name=news';
                                //         break;
                                //     }
                                //     case 'RestrictionTypes': {
                                //         url = '/help?name=restrictionTypes';
                                //         break;
                                //     }
                                //     case 'Organizations': {
                                //         url = '/help?name=organizations';
                                //         break;
                                //     }
                                // }
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        getHelp(url)
                                    ]
                                }, $$('content'))

                            }
                        }
                    },
                    {
                        view: 'button',

                        label: 'Добавить',
                        click: function() {
                            webix.ui({
                                id: 'content',
                                rows: [
                                    getAddHelpForm()
                                ]
                            }, $$('content'))
                        }
                    }
                    // {
                    //     gravity: 0.2,
                    //     view: 'form',
                    //     id: 'add_help_form',
                    //     complexData: true,
                    //     elements: [
                    //         { gravity: 0.4 },
                    //         {
                    //             view: "combo",
                    //             id: "typenames",
                    //             name: "typename",
                    //             label: 'typename',
                    //             labelPosition: 'top',
                    //             invalidMessage: 'typename не может быть пустым',
                    //             options: ['dropdown', 'text'],
                    //             on : {
                    //                 onChange: (newVal, oldVal) => {
                    //                     if (newVal !== oldVal) {
                    //                         $$('title').hide();
                    //                         $$('description').hide();
                    //                         let check = false;
                    //                         switch (newVal) {
                    //                             case 'dropdown':
                    //                                 check = true;
                    //                                 break;
                    //                         }
                    //                         if (check) {
                    //                             $$('title').show();
                    //                         }
                    //                         $$('description').show();
                    //                     }
                    //                 }
                    //             },
                    //         },
                    //         {
                    //             view: "textarea",
                    //             id: "title",
                    //             name: "title",
                    //             label: 'title',
                    //             labelPosition: 'top',
                    //             hidden: true,
                    //             invalidMessage: 'typename не может быть пустым',
                    //         },
                    //         {
                    //             view: "textarea",
                    //             id: "description",
                    //             name: "description",
                    //             label: 'description',
                    //             labelPosition: 'top',
                    //             hidden: true,
                    //             invalidMessage: 'typename не может быть пустым',
                    //         },
                    //         {
                    //             cols: [
                    //                 {
                    //                     view: 'button',
                    //                     id: 'add_contact',
                    //                     css: 'webix_primary',
                    //                     label: "<span class='mdi mdi-plus-circle' style='padding-right: 5px'></span><span class='text'>Добавить</span>",
                    //                     hotkey: 'enter',
                    //                 },
                    //                 {
                    //                     view: 'button',
                    //                     id: 'del_contact',
                    //                     css: 'webix_primary',
                    //                     label: "<span class='mdi mdi-minus-circle' style='padding-right: 5px'></span><span class='text'>Удалить</span>",
                    //                     hotkey: 'delete',
                    //                 }
                    //             ]
                    //         },
                    //         {}
                    //     ]
                    // }
                ]
            },
        ],
    }
};