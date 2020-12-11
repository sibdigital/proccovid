const getDropDownView = (header, desc) => {
    const buttonValue = header?.title ?? 'Раскрыть';
    const textAreaValue = desc?.description ?? 'Описание';

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

    // return {
    //     view: 'scrollview',
    //     autowidth: true,
    //     autoheight: true,
    //     body: {
    //         type: 'wide',
    //         rows: getFormWithData(url)
    //         // view: 'list',
    //         // scroll: 'xy',
    //         // url: url,
    //         // datatype: 'json',
    //         // template: '#title#',
    //     }
    // };

    return {
        view: 'scrollview',
        autowidth: true,
        //autoheight: true,
        body: {
            //type: 'space',
            rows: [
                {
                    //type: 'wide',
                    rows: [
                        {
                            view: 'button',
                            id: 'dropDownButton',
                            css: 'webix_primary',
                            value: 'Страница: Статистика по заявкам',
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
                            view: 'label',
                            id: 'dropDownTextArea',
                            label: 'Здесь собрана статистика по заявкам организаций и количестве работиков в оффисах и на удаленке',
                            //autoheight: true,
                            //height: 200,
                            hidden: true,
                            //readonly: true,
                        }]
                },
            ]
        }
    };
}

const getAddHelpForm = (data = null) => {

    return {
        view: 'nic-editor',
        id: 'message',
        name: 'message',
        css: "myClass",
        cdn: false,
        config: {
            iconsPath: '../libs/nicedit/nicEditorIcons.gif'
        }
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
                cols: [
                    {
                        view: 'list',
                        id: 'listHelps',
                        select: true,
                        data: [
                            { id: "Departments", icon: "fas fa-globe", value: 'Подразделения' },
                            { id: "DepartmentUsers", icon: "fas fa-user-tie", value: 'Пользователи подразделений' },
                            { id: "Organizations",icon: "fas fa-file-alt", value: 'Организации' },
                            { id: "Requests", icon: "fas fa-file", value: 'Заявки' },
                            { id: "TypeRequests", icon: "fas fa-file-alt", value: 'Предписания' },
                            { id: "RestrictionTypes", icon: "fas fa-file-alt", value: 'Типы ограничений' },
                            { id: "Principals", icon: "fas fa-user", value: 'Пользователи' },
                            { id: "Templates", icon: "fas fa-comment-alt", value: 'Шаблоны сообщений' },
                            { id: "Statistic", icon: "fas fa-chart-bar", value: 'Статистика' },
                            { id: "Okveds", icon: "fas fa-folder", value: 'ОКВЭДы' },
                            { id: "Mailing", icon: "fas fa-paper-plane", value: 'Типы рассылок'},
                            { id: "MailingMessages", icon: "fas fa-envelope", value: 'Сообщения рассылок'},
                            { id: "Fias", icon: "fas fa-download", value: 'Загрузка ФИАС, ЕГРЮЛ'},
                            { id: "News", icon: "fas fa-newspaper", value: 'Новости'},
                        ],
                        on: {
                            onAfterSelect: function(id) {
                                console.log(id);
                                let view;
                                let url = '/help/statistic';
                                switch (id) {
                                    case 'Departments': {
                                        view = departments;
                                        break;
                                    }
                                    case 'DepartmentUsers': {
                                        view = departmentUsers;
                                        break;
                                    }
                                    case 'Principals': {
                                        view = principals;
                                        break;
                                    }
                                    case 'Templates': {
                                        view = templates;
                                        break;
                                    }
                                    case 'TypeRequests': {
                                        view = typeRequests;
                                        break;
                                    }
                                    case 'Requests': {
                                        view = adminRequests;
                                        break;
                                    }
                                    case 'Statistic': {
                                        view = statistic;
                                        break;
                                    }
                                    case 'Okveds': {
                                        view = okveds;
                                        break;
                                    }
                                    case 'Mailing': {
                                        view = mailingList;
                                        break;
                                    }
                                    case 'MailingMessages': {
                                        view = mailingMessages;
                                        break;
                                    }
                                    case 'Fias': {
                                        view = fias;
                                        break;
                                    }
                                    case 'News': {
                                        view = newsListForm;
                                        break;
                                    }
                                    case 'RestrictionTypes': {
                                        view = restrictionTypes;
                                        break;
                                    }
                                    case 'Organizations': {
                                        view = organizations;
                                        break;
                                    }
                                    case 'Help': {
                                        url = '/help/statistic';
                                        break;
                                    }
                                }
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        getHelp(url)
                                    ]
                                }, $$('content'))

                            }
                        }
                    },
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