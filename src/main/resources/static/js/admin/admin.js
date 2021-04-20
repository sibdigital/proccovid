webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")
const xml_format =  webix.Date.strToDate("%Y-%m-%d %H:%i:%s.S");

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function addOkved(){
    let values = $$('form_okved').getValues()
    if(values.okved_richselect == ''){
        webix.message('не заполнены обязательные поля')
        return;
    }

    var name_okved = $$('okved_richselect').getText();
    var path = values.okved_richselect;
    var version = path.substring(0, 4);
    var found_element = $$('okved_table').find(function (obj) {
        return obj.name_okved == name_okved && obj.path == path;
    })

    if (found_element.length == 0) {
        $$('okved_table').add({
            name_okved: name_okved,
            path: path,
            version: version
        }, $$('okved_table').count() + 1)
    }
    else {
        webix.message('Уже добавлен этот ОКВЭД')
        return;
    }
}

function removeOkved() {
    if(!$$("okved_table").getSelectedId()){
        webix.message("Ничего не выбрано!");
        return;
    }
    webix.confirm('Вы действительно хотите удалить выбранный ОКВЭД?')
        .then(
            function () {
                $$("okved_table").remove($$("okved_table").getSelectedId());
            }
        )
}

webix.require(['js/views/requests.js', 'js/views/other-requests.js', 'js/views/showform-other.js', 'js/views/okved_list.js']);

const linkedOkvedsForm = {
    view: 'scrollview',
    id: 'linkedOkvedsForm',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    { template:"Редактируемая таблица ОКВЭДов", type:"section" },
                    {
                        view: 'datatable',
                        id: 'linked_okved_table',
                        columns: [
                            {
                                id: 'kindCode',
                                header: 'Код'
                            },
                            {
                                id: 'version',
                                header: 'Версия',
                            },
                            {
                                id: 'kindName',
                                header: 'Наименование',
                                fillspace: true,
                            },
                            {
                                id: 'btnDelete',
                                header: 'Удалить',
                                template:"{common.trashIcon()}"
                            },
                        ],
                        onClick:{
                            "wxi-trash":function(event, id, node){
                                this.remove(id)
                            }
                        }
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
                                    var data = $$('linked_okved_table').serialize();
                                    $$('okved_table').clearAll();
                                    $$('okved_table').parse(data);
                                    $$('windowCLO').close();
                                }
                            }
                        ]
                    },
                    { template:"Поиск и добавление ОКВЭДов", type:"section" },
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
                                                        break
                                                    case '2014':
                                                        version = '2014';
                                                        break
                                                    case 'synt':
                                                        version = 'synt';
                                                        break
                                                }

                                                let params = '';
                                                let search_text = $$('search').getValue();
                                                if (search_text) {
                                                    params += params == '' ? '?' : '&';
                                                    params += 'searchText=' + search_text;
                                                }

                                                let view = okvedslist_chooseOkved('list_okved/' + version + params, version, true);

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

webix.ready(function() {
    let layout = webix.ui({
        rows: [
            {
                view: 'toolbar',
                autoheight: true,
                rows: [
                    {
                        responsive: 't1',
                        css: 'webix_dark',
                        cols: [
                            {
                                view: 'label',
                                width: 40,
                                template: "<img height='35px' width='35px' src = \"favicon.ico\">"
                            },
                            {
                                view: 'label',
                                minWidth: 400,
                                label: '<span style="font-size: 1.0rem">Личный кабинет администратора</span>',
                            },
                            {},
                            {
                                view: 'label',
                                label: DEPARTMENT + ' (<a href="logout" title="Выйти">' + USER_NAME + '</a>)',
                                align: 'right'
                            }
                        ]
                    }
                ]
            },
            {
                cols: [
                    {
                        view: 'sidebar',
                        id: 'sidebar',
                        css: 'webix_dark',
                        width: 300,
                        data: [
                            { id: "Departments", icon: "fas fa-globe", value: 'Подразделения' },
                            { id: "DepartmentUsers", icon: "fas fa-user-tie", value: 'Пользователи подразделений' },
                            { id: "Organizations",icon: "fas fa-file-alt", value: 'Организации' },
                            { id: "ControlAuthority", icon: "fas fa-file-alt", value: "Контрольно-надзорные органы" },
                            { id: "Requests", icon: "fas fa-file", value: 'Заявки' },
                            { id: "TypeRequests", icon: "fas fa-file-alt", value: 'Виды деятельности' },
                            // { id: "RestrictionTypes", icon: "fas fa-file-alt", value: 'Типы ограничений' },
                            { id: "Prescriptions", icon: "fas fa-file-alt", value: 'Предписания' },
                            { id: "TypeViolations", icon: "fas fa-file-alt", value: 'Виды нарушений' },
                            { id: "ViolationSearchQueries", icon: "fas fa-file-alt", value: 'Поиск нарушений организаций' },
                            { id: "PersonViolationSearchQueries", icon: "fas fa-file-alt", value: 'Поиск нарушений физ. лиц' },
                            { id: "Principals", icon: "fas fa-user", value: 'Пользователи' },
                            { id: "Templates", icon: "fas fa-comment-alt", value: 'Шаблоны сообщений' },
                            { id: "Statistic", icon: "fas fa-chart-bar", value: 'Статистика' },
                            { id: "Okveds", icon: "fas fa-folder", value: 'ОКВЭДы' },
                            { id: "Mailing", icon: "fas fa-paper-plane", value: 'Типы рассылок'},
                            { id: "MailingMessages", icon: "fas fa-envelope", value: 'Сообщения рассылок'},
                            { id: "Fias", icon: "fas fa-download", value: 'Загрузка ФИАС, ЕГРЮЛ'},
                            { id: "News", icon: "fas fa-newspaper", value: 'Новости'},
                            { id: "Help", icon: "fas fa-newspaper", value: 'Помощь'},
                        ],
                        on: {
                            onAfterSelect: function(id) {
                                let view;
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
                                    case 'Prescriptions': {
                                        view = prescriptions;
                                        break;
                                    }
                                    case 'TypeViolations': {
                                        view = typeViolations;
                                        break;
                                    }
                                    case 'ViolationSearchQueries': {
                                        view = violationSearchQueries;
                                        break;
                                    }
                                    case 'PersonViolationSearchQueries': {
                                        view = personViolationSearchQueries;
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
                                        view = helpForm;
                                        break;
                                    }
                                    case 'ControlAuthority': {
                                        view = controlAuthority;
                                        break;
                                    }
                                }
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        view
                                    ]
                                }, $$('content'))

                                if (id === 'Requests') {
                                    $$('department_filter').getList().add({id:'', value:'Все подразделения', $empty: true}, 0);
                                    $$('request_type').getList().add({id:'', value:'Все виды деятельности', $empty: true}, 0);
                                    $$('district_filter').getList().add({id:'', value:'Все районы', $empty: true}, 0);
                                    $$('actualization_type').getList().add({id:'', value:'Все заявки', $empty: true}, 0)
                                } else if (id === 'Organizations') {
                                    $$('prescription').getList().add({id:'', value:'Предписание не выбрано', $empty: true}, 0);
                                }
                            }
                        }
                    },
                    {
                        id: 'content'
                    }
                ],
            }
        ]
    })

    let status = getUserStatus();
    if (status === 0) {
        webix.ui(newUserPasswordModal).show();
    };

    webix.event(window, "resize", function (event) {
        layout.define("width",document.body.clientWidth);
        layout.define("height",window.innerHeight);
        layout.resize();
    });

})

