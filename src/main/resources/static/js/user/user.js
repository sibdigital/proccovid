webix.i18n.setLocale("ru-RU");

const dateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s");
let btnBackHandler = null;


function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

let btnBack = {
    id: 'btnBackMainId',
    view: 'button',
    label: 'Назад',
    maxWidth: 100,
    align: 'left',
    type: 'icon',
    icon: 'fas fa-arrow-left',
    css: 'backBtnStyle',
    hidden: true,
    click: function () {

    }
}

function hideBtnBack() {
    $$('btnBackMainId').hide();
}

function showBtnBack(view, tableId) {
    $$('btnBackMainId').show();
    if (btnBackHandler != null) {
        $$('btnBackMainId').detachEvent(btnBackHandler);
    }
    btnBackHandler = $$('btnBackMainId').attachEvent("onItemClick", function(id, e) {
        if ($$(tableId) != null) {
            $$(tableId).destructor();
        }

        webix.ui({
            id: 'content',
            rows: [
                webix.copy(view)
            ]
        }, $$('content'));

        $$('btnBackMainId').hide();

        return false;
    });
}

webix.ready(function() {
    // let rolesStr = webix.ajax().sync().get('user_roles');
    // let userRoles = JSON.parse(rolesStr?.response);
    // console.log(userRoles)
    // let isAdmin = userRoles[0]?.status;
    // let isUser = userRoles[1]?.status;
    // let isViol = userRoles[2]?.status;
    let xhr = webix.ajax().sync().get("current_roles");
    let userRoles = JSON.parse(xhr.responseText);
    let isAdmin = userRoles.includes("ADMIN");
    let isUser = userRoles.includes("USER");
    let isViol = userRoles.includes("VIOLAT");
    let isKnd = userRoles.includes("KND");
    let isSubsidySupport = userRoles.includes('SUBSIDY_SUPPORT');

    let menuItems = [
        {id: "Requests", icon: "fas fa-file", value: 'Заявки', access: isUser || isAdmin},
        { id: "Organizations",icon: "fas fa-file-alt", value: 'Организации', access: isKnd || isAdmin},
        // { id: "Prescriptions", icon: "fas fa-file-alt", value: 'Предписания' },
        {id: "Violations", icon: "fas fa-file-alt", value: 'Нарушения организаций', access: isViol || isAdmin},
        {id: "PersonViolations", icon: "fas fa-file-alt", value: 'Нарушения физ. лиц', access: isViol || isAdmin},
        // {id: "InspectionReports", icon: "fas fa-chart-bar", value: 'Отчеты по проверкам', access: isKnd || isAdmin},
        {id: "Reports", icon: "fas fa-chart-bar", value: 'Отчеты', access: isUser || isAdmin},
        { id: "SubsidiesSupport", icon: "fas fa-chart-bar", value: 'Меры поддержки', access: isSubsidySupport || isAdmin },
    ].filter(item => item.access === true)

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
                        data: menuItems,
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
                                if (id === 'Requests') {
                                    view = userRequests;
                                } else if (id === 'Prescriptions') {
                                    view = userPrescriptions;
                                    let checkReqBadge = this.getMenuItem(id).badge
                                    if (checkReqBadge != null && checkReqBadge !== false) {
                                        prescriptBadge = "(" + checkReqBadge + ")";
                                    }

                                    // fix bug при двойном клике пустая таблица выходила
                                    if ($$('prescriptions_table') != null) {
                                        $$('prescriptions_table').destructor();
                                    }
                                } else if (id === 'Organizations') {
                                    view = organizations;
                                } else if (id === 'Violations') {
                                    view = violations;
                                    margin = {"margin-top":"10px !important"};
                                } else if (id === 'PersonViolations') {
                                    view = personViolations;
                                    margin = {"margin-top":"10px !important","width":"100% !important"};
                                } else if (id === 'InspectionReports') {
                                    view = inspectionReports;
                                } else if (id === 'Reports') {
                                    view = UserReports(userRoles);
                                } else if (id === 'SubsidiesSupport') {
                                    view = subsidiesSupport;
                                }
                                hideBtnBack();
                                this.select(id)
                                if (view != null) {
                                    if (id === 'SubsidiesSupport') {
                                        webix.ui({
                                            id: 'contentWithoutScrollViewBody',
                                            rows: [
                                                view,
                                            ]
                                        }, $$('contentWithoutScrollViewBody'));
                                        $$('contentWithoutScrollViewBody').show();
                                        $$('contentWithScrollViewBody').hide();
                                    } else {
                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                view
                                            ]
                                        }, $$('content'));
                                        $$('contentWithoutScrollViewBody').hide();
                                        $$('contentWithScrollViewBody').show();
                                    }
                                    itemValue = this.getMenuItem(id).value
                                    $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + itemValue + " " + requestsBadge + prescriptBadge + "</span>");
                                    if (id === 'Requests') {
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
                            btnBack,
                            {
                                view: 'label',
                                id: 'labelLK',
                                align: 'left',
                                css: {"padding-left": "5px"},
                                label: 'Личный кабинет',
                            },
                            {
                                cols: [
                                    // {
                                    //     view: 'label',
                                    //     id: 'logout_label',
                                    //     label: DEPARTMENT + ' (<a href="logout" title="Выйти">' + USER_NAME + '</a>)',
                                    //     align: 'right'
                                    // }
                                    {
                                        view: 'icon',
                                        icon: 'fas fa-user-circle',
                                        css: 'topMenuIcon',
                                        tooltip: 'Профиль (' + USER_NAME + ")",
                                        click: function () {
                                            webix.ui({
                                                id: 'content',
                                                rows: [
                                                    profile
                                                ]
                                            }, $$('content'))

                                            $$("labelLK").setValue("Личный кабинет > " + "<span style='color: #1ca1c1'>" + "Профиль" + "</span>");
                                            $$('menu').unselectAll();
                                        },
                                    },
                                    {
                                        view: 'icon',
                                        css: 'topMenuIcon',
                                        icon: 'fas fa-sign-out-alt',
                                        tooltip: 'Выход',
                                        click: function () {
                                            webix.send("logout");
                                        },
                                    },
                                ]
                            }
                        ]
                    },
                    {
                        id: 'contentWithScrollViewBody',
                        hidden: true,
                        view: 'scrollview',
                        scroll: 'xy',
                        css: { 'overflow':'hidden !important' },
                        body: {
                            padding: 20,
                            rows: [
                                { id: 'content' }
                            ]
                        }
                    },
                    {
                        id: 'contentWithoutScrollViewBody',
                        hidden: true,
                    },
                ]
            }
        ]
    })

    let status = getUserStatus();
    if (status === 0) {
        webix.ui(newUserPasswordModal).show();
    }

    if (ID_VIOLATION !== null) {
        getViolationForm(ID_VIOLATION);
    }
    if (ID_PERSON_VIOLATION !== null) {
        getPersonViolationForm(ID_PERSON_VIOLATION);
    }
    webix.event(window, "resize", function(event){
        layout.define("width",document.body.clientWidth);
        layout.define("height",window.innerHeight);
        layout.resize();
    });
})
