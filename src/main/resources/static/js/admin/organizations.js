const organizations = {
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
                                cols:[
                                    {
                                        view: 'search',
                                        id: 'search',
                                        maxWidth: 300,
                                        minWidth: 100,
                                        tooltip: 'После ввода значения нажмите Enter',
                                        placeholder: "Введите ИНН или наименование организации",
                                        on: {
                                            onEnter: function () {
                                                reload();
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'prescription',
                                        // width: 450,
                                        css: 'smallText',
                                        placeholder: 'Предписание не выбрано',
                                        options: 'cls_prescriptions_short',
                                        on: {
                                            onChange() {
                                                reload();
                                            }
                                        }
                                    },
                                ]
                            }
                        ]
                    },
                    {
                        view: 'datatable',
                        id: 'organizations_table',
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        fixedRowHeight:false,
                        datafetch: 25,
                        columns: [
                            {
                                id: "orgName",
                                header: "Наименование организации/ИП",
                                template: "#name#",
                                minWidth: 550,
                                fillspace: true,
                            },
                            {id: "inn", header: "ИНН", template: "#inn#", minWidth: 150,  fillspace: true, adjust: true},
                            {
                                id: "ogrn",
                                header: "ОГРН",
                                template: (obj)=>{
                                    if (obj.ogrn !== null){
                                        return obj.ogrn;
                                    }else{
                                        return "";
                                    }
                                },
                                adjust: true
                            },
                            {
                                id: 'activated',
                                header: 'Активирована',
                                adjust: true,
                                template: function (obj, type, value) {
                                    if (value) {
                                        return '<span>Да</span>'
                                    } else {
                                        return '<span>Нет</span>'
                                    }
                                }
                            },                        ],
                        scheme: {
                            $init: function (obj) {
                            },
                        },
                        on: {
                            onBeforeLoad: function () {
                                this.showOverlay("Загружаю...");
                            },
                            onAfterLoad: function () {
                                this.hideOverlay();
                                if (!this.count()) {
                                    this.showOverlay("Отсутствуют данные")
                                }
                            },
                            onLoadError: function () {
                                this.hideOverlay();
                            },
                            onItemClick: function (id) {
                                this.hideOverlay();
                            },
                            onItemDblClick: function (id) {
                                let data = this.getItem(id);
                                setTimeout(function () {
                                    let okveds = data.regOrganizationOkveds
                                    webix.ui({
                                        id: 'content',
                                        rows: [
                                            organizationForm
                                        ]
                                    }, $$('content'))
                                    if (okveds.length > 0) {
                                        for (let i in okveds) {
                                            let status = okveds[i].main;
                                            let listId;
                                            if (status) {
                                                listId = "regOrganizationMainOkveds";
                                            } else {
                                                listId = "regOrganizationOtherOkveds";
                                            }
                                            $$(listId).add({
                                                kindCode: okveds[i].regOrganizationOkvedId.okved.kindCode,
                                                kindName: okveds[i].regOrganizationOkvedId.okved.kindName
                                            })
                                        }
                                    }
                                    $$("organization_form").parse(data)
                                }, 100);
                            },
                            'data->onStoreUpdated': function() {
                                this.adjustRowHeight(null, true);
                            },
                        },
                        url: 'cls_organizations'
                    },
                    {
                        view: 'pager',
                        id: 'Pager',
                        height: 38,
                        size: 25,
                        group: 5,
                        template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                    }
                ]
            }
        ]
    }
}

organizationForm = {
    view: 'scrollview',
    scroll: 'xy',
    id: 'organization_layout_form',
    autowidth: true,
    autoheight: true,
    body: {
        type: 'space',
        rows: [
            {
                view: "form",
                id: "organization_form",
                elements: [
                    {
                        view: 'text',
                        name: 'name',
                        id: 'organizationName',
                        label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                        labelPosition: 'top',
                        readonly: true,
                    },
                    {
                        view: 'text',
                        name: 'shortName',
                        id: 'shortOrganizationName',
                        label: 'Краткое наименование организации',
                        labelPosition: 'top',
                        readonly: true,
                    },
                    {
                        id: "innplace",
                        rows: []
                    },
                    {
                        responsive: 'innplace',
                        cols: [
                            {
                                view: 'text',
                                name: 'inn',
                                id: "inn",
                                label: 'ИНН',
                                minWidth: 200,
                                labelPosition: 'top',
                                readonly: true,
                            },
                            {
                                view: 'text',
                                name: 'ogrn',
                                id: 'ogrn',
                                label: 'ОГРН',
                                minWidth: 200,
                                labelPosition: 'top',
                                readonly: true,
                            },
                        ]
                    },

                    {
                        rows:
                            [
                                {
                                    height: 30,
                                    view: 'label',
                                    label: 'Основной вид осуществляемой деятельности (отрасль)',
                                },
                                {
                                    view: 'list',
                                    id: 'regOrganizationMainOkveds',
                                    layout: 'x',
                                    css: {'white-space': 'normal !important;'},
                                    height: 50,
                                    template: '#kindCode# - #kindName#',
                                    //url: 'reg_organization_okved', //<span class="mdi mdi-close"></span>
                                    type: {
                                        css: "chip",
                                        height: 'auto'
                                    },
                                },
                            ]
                    },
                    {
                        rows:
                            [
                                {
                                    height: 30,
                                    view: 'label',
                                    label: 'Дополнительные виды осуществляемой деятельности',
                                },
                                {
                                    view: "list",
                                    layout: 'x',
                                    id: 'regOrganizationOtherOkveds',
                                    css: {'white-space': 'normal !important;'},
                                    height: 150,
                                    template: '#kindCode# - #kindName#',
                                    //url: "reg_organization_okved_add",
                                    type: {
                                        css: "chip",
                                        height: 'auto'
                                    },
                                }
                            ]
                    },
                    {
                        view: 'textarea',
                        name: 'addressJur',
                        label: 'Юридический адрес',
                        labelPosition: 'top',
                        height: 80,
                        readonly: true,
                        required: true
                    },
                    {
                        id: "emailPlace",
                        rows: []
                    },
                    {
                        responsive: "emailPlace",
                        cols: [
                            {
                                view: 'text',
                                name: 'email',
                                minWidth: 200,
                                label: 'Адрес электронной почты',
                                labelPosition: 'top',
                                required: true
                            },
                            {
                                view: 'text',
                                name: 'phone',
                                minWidth: 200,
                                label: 'Телефон',
                                labelPosition: 'top',
                                required: true
                            },
                        ]
                    },
                ]
            }
        ]
    }
}

function reload() {
    $$('organizations_table').clearAll();

    const url = 'cls_organizations';
    let params = '';

    const inn = $$('search').getValue();
    if (inn != '') {
        params = '?inn=' + inn;
    }

    const prescription = $$('prescription').getValue();
    if (prescription != '') {
        params += params == '' ? '?' : '&';
        params += 'id_prescription=' + prescription;
    }

    $$('organizations_table').load(url + params);
}
