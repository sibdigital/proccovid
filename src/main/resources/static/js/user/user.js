webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

webix.ready(function() {
    let layout = webix.ui({
        cols: [
            {
                width: 225,
                id: 'menuRow',
                rows: [
                    {
                        view: 'label',
                        css: {
                            'background-color': '#565B67 !important',
                            'color': '#FFFFFF'
                        },
                        height: 46,
                        label: `<img height='36px' width='36px' style="padding: 0 0 2px 2px" src = \"favicon.ico\"><span style="color: white; font-size: 16px; font-family: Roboto, sans-serif;">${APPLICATION_NAME}</span>`,
                    },
                    {
                        view: 'menu',
                        id: 'menu',
                        css: 'my_menubar',
                        layout: 'y',
                        data: [
                            { id: "Requests", icon: "fas fa-file", value: 'Заявки' },
                            // { id: "Prescriptions", icon: "fas fa-file-alt", value: 'Предписания' },
                            { id: "Violations", icon: "fas fa-file-alt", value: 'Нарушения организаций' },
                            { id: "PersonViolations", icon: "fas fa-file-alt", value: 'Нарушения физ. лиц' },
                        ],
                        type: {
                            css: 'my_menubar_item',
                            height: 44
                        },
                        on: {
                            onMenuItemClick: function (id) {
                                let view;
                                let itemValue;
                                let requestsBadge = "";
                                let prescriptBadge = "";
                                let margin = "";
                                if (id == 'Requests') {
                                    view = userRequests;
                                } else if (id == 'Prescriptions') {
                                    view = userPrescriptions;
                                    let checkReqBadge = this.getMenuItem(id).badge
                                    if (checkReqBadge != null && checkReqBadge != false) {
                                        prescriptBadge = "(" + checkReqBadge + ")";
                                    }

                                    // fix bug при двойном клике пустая таблица выходила
                                    if ($$('prescriptions_table') != null) {
                                        $$('prescriptions_table').destructor();
                                    }
                                } else if (id == 'Violations') {
                                    view = violations;
                                    margin = {"margin-top":"10px !important"};
                                } else if (id == 'PersonViolations') {
                                    view = personViolations;
                                    margin = {"margin-top":"10px !important","width":"100% !important"};
                                }
                                this.select(id)
                                if (view != null) {
                                    webix.ui({
                                        id: 'content',
                                        css: margin ?? '',
                                        rows: [
                                            view
                                        ]
                                    }, $$('content'));
                                    itemValue = this.getMenuItem(id).value
                                    $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + itemValue + " " + requestsBadge + prescriptBadge + "</span>");
                                    if (id == 'Requests') {
                                        $$('tabbar').setValue('requests');
                                        $$('request_type').getList().add({id:'', value:'Все виды деятельности', $empty: true}, 0);
                                        $$('district_filter').getList().add({id:'', value:'Все районы', $empty: true}, 0);
                                        $$('actualization_type').getList().add({id:'', value:'Все заявки', $empty: true}, 0);
                                    }
                                }
                            }
                        }
                    }
                ]
            },
            {
                rows: [
                    {
                        view: 'toolbar',
                        autoheight: true,
                        elements: [
                            {
                                view: 'label',
                                id: 'labelLK',
                                align: 'left',
                                css: {"padding-left": "5px"},
                                label: 'Личный кабинет',
                            },
                            {
                                cols: [
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
                        view: 'scrollview',
                        scroll: 'xy',
                        css: {'overflow':'hidden !important'},
                        body: {
                            padding: 20,
                            rows: [
                                {id: 'content',}
                            ]
                        }
                    }
                ]
            }
        ]
    })
    webix.event(window, "resize", function(event){
        layout.define("width",document.body.clientWidth);
        layout.define("height",window.innerHeight);
        layout.resize();
    });
})
