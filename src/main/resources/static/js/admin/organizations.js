const dateFormatWithoutTime = webix.Date.dateToStr("%d.%m.%Y");

const organizations = {
    body: {
        type: 'space',
        rows: [
            {
                rows: [
                    {
                        view: 'toolbar',
                        rows: [
                            {
                                cols: [
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
                        fixedRowHeight: false,
                        datafetch: 25,
                        columns: [
                            {
                                id: "orgName",
                                header: "Наименование организации/ИП",
                                template: "#name#",
                                minWidth: 550,
                                fillspace: true,
                            },
                            {id: "inn", header: "ИНН", template: "#inn#", minWidth: 150, fillspace: true, adjust: true},
                            {
                                id: "ogrn",
                                header: "ОГРН",
                                template: (obj) => {
                                    if (obj.ogrn !== null) {
                                        return obj.ogrn;
                                    } else {
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
                            },],
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
                                            organizationForm(data),
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
                                    if (data.activated) {
                                        $$('activated').setValue(1);
                                    }
                                }, 100);
                            },
                            'data->onStoreUpdated': function () {
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

let organizationForm = (data) => {
    return {
        view: 'tabview',
        cells: [
            {
                header: 'Информация',
                width: 150,
                body: {
                    type: 'space',
                    rows: [
                        {
                            view: "scrollview",
                            body: {
                                view: 'form',
                                id: "organization_form",
                                rows: [
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
                                        cols: [
                                            {
                                                view: 'richselect',
                                                id: 'idTypeOrganization',
                                                name: 'idTypeOrganization',
                                                width: 450,
                                                label: 'Тип организации',
                                                labelPosition: 'top',
                                                css: 'smallText',
                                                placeholder: 'Выберите тип организации',
                                                options: 'organization_types',
                                            },
                                            {},
                                        ]
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
                                                required: true,
                                            },
                                            {
                                                view: 'text',
                                                name: 'phone',
                                                minWidth: 200,
                                                label: 'Телефон',
                                                labelPosition: 'top',
                                                required: true,
                                                readonly: true,
                                            },
                                        ]
                                    },
                                    {
                                        cols: [
                                            { view: 'checkbox', label: 'Активирован', labelPosition: 'top', name: 'activated', id: 'activated' },
                                            { view: 'checkbox', label: 'Удалить организацию', labelPosition: 'top', name: 'deleted', id: 'deleted' },
                                            { view: 'checkbox', label: 'Получено согласие на обработку персональных данных', labelPosition: 'top', name: 'consentDataProcessing', readonly: true},
                                            {}
                                        ]
                                    },
                                    {
                                        cols: [
                                            {},
                                            {
                                                view: 'button',
                                                align: 'right',
                                                maxWidth: 200,
                                                css: 'webix_primary',
                                                value: 'Сохранить',
                                                click: function () {
                                                    if ($$('deleted').getValue() == true) {
                                                        webix.confirm({
                                                            title: 'Подтверждение',
                                                            type: 'confirm-warning',
                                                            ok: 'Да', cancel: 'Нет',
                                                            text: 'Вы уверены что хотите удалить организацию?'
                                                        }).then(() => {
                                                            saveOrganization();
                                                        })
                                                    } else {
                                                        saveOrganization();
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
                                                    webix.ui({
                                                        id: 'content',
                                                        rows: [
                                                            webix.copy(organizations)
                                                        ]
                                                    }, $$('content'));
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }
                        },
                    ]
                }
            },
            {
                header: 'Проверки',
                width: 150,
                body: {
                    type: 'space',
                    rows: [
                        {
                            rows: [
                                {
                                    view: 'datatable',
                                    id: 'revision_table',
                                    select: 'row',
                                    scrollX: false,
                                    resizeColumn: true,
                                    navigation: true,
                                    fixedRowHeight: false,
                                    rowLineHeight: 28,
                                    columns: [
                                        {
                                            id: 'controlAuthorityName',
                                            header: 'Контрольно-надзорный орган',
                                            width: 500,
                                            minWidth: 300,
                                            fillspace: true,
                                            // template: '#controlAuthority.name#'
                                            template: function (obj) {
                                                if (obj.controlAuthority) {
                                                    return obj.controlAuthority.name;
                                                } else {
                                                    return "";
                                                }
                                            },
                                            sort: 'text',
                                        },
                                        {
                                            id: 'inspectionResult',
                                            header: 'Результат проверки',
                                            width: 200,
                                            // template: '#inspectionResult.name#'
                                            template: function (obj) {
                                                if (obj.inspectionResult) {
                                                    return obj.inspectionResult.name;
                                                } else {
                                                    return "";
                                                }
                                            },
                                            sort: 'text',
                                        },
                                        {
                                            id: 'dateOfInspection',
                                            header: 'Дата проверки',
                                            width: 200,
                                            name: 'dateOfInspection',
                                            format: dateFormatWithoutTime,
                                            sort: 'date',
                                        },
                                        {
                                            id: 'comment',
                                            header: 'Комментарий',
                                            width: 250,
                                            template: '#comment#',
                                            sort: 'text',
                                        }
                                    ],
                                    scheme: {
                                        $init: function (obj) {
                                            var xml_format_ = webix.Date.strToDate("%Y-%m-%d %H:%i:%s.S");
                                            obj.dateOfInspection = obj.dateOfInspection.replace("T", " ");
                                            obj.dateOfInspection = xml_format_(obj.dateOfInspection);
                                        },
                                        $update:function (obj) {
                                            var xml_format_ = webix.Date.strToDate("%Y-%m-%d %H:%i:%s.S");
                                            obj.dateOfInspection = obj.dateOfInspection.replace("T", " ");
                                            obj.dateOfInspection = xml_format_(obj.dateOfInspection);
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
                                        'data->onStoreUpdated': function () {
                                            this.adjustRowHeight(null, true);
                                        },
                                        onItemDblClick: function (id, e, trg) {
                                            let item = $$('revision_table').getSelectedItem();
                                            window.open('inspection/view?id=' + item.id)
                                        }
                                    },
                                    url: 'org_inspections?id=' + data.id,
                                },
                                {
                                    cols: [
                                        {},
                                        {
                                            view: "button",
                                            id: "downloadButton",
                                            minWidth: 150,
                                            maxWidth: 250,
                                            value: "Скачать список",
                                            css: 'webix_primary',
                                            click: function () {
                                                webix.html.download('download_file/' + data.id);
                                            }
                                        },
                                    ]
                                }
                            ]
                        }
                    ]
                }
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


function saveOrganization() {
    let params = $$('organization_form').getValues();
    webix.ajax().headers({
        'Content-Type': 'application/json'
    }).post('save_organization',
        params).then(function (data) {
        var response = JSON.parse(data.text());
        if (response.cause == "Сохранено") {
            webix.message({text: response.cause, type: 'success'});
            webix.ui({
                id: 'content',
                rows: [
                    webix.copy(organizations)
                ]
            }, $$('content'));
        } else {
            webix.message({text: response.cause, type: 'error'});
        }
    })
}