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
                    .get('help/statistic/globalIds', params)
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
                    },
                    {
                        cols: [
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
                            },
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
                            }
                        ]
                    }]
            }
    };
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
                                            .post('help/add', JSON.stringify(params))
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

                        on: {
                            onAfterSelect: function(id) {
                                console.log(id);
                                const item = $$('listHelps').getItem(id);
                                console.log(item);
                                let url = 'help?id=' + id;
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
                ]
            },
        ],
    }
};