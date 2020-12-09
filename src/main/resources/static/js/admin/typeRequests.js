function back() {
    $$("wizard").back();
}

function next() {
    const parentCell = this.getParentView().getParentView();
    const index = $$("wizard").index(parentCell);
    const next = $$("wizard").getChildViews()[index + 1]
    if (next) {
        next.show();
    }
}

const typeRequests = {
    view: 'scrollview',
    scroll: 'xy',
    id: "typeRequestsId",
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'datatable',
                        id: 'type_requests_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        // pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "activityKind", header: "Наименование", template: "#activityKind#", adjust: true, maxWidth: 500},
                            // {id: "shortName", header: "Краткое наименование", template: "#shortName#", width: 300},
                            // {id: "prescription", header: "Prescription", template: "#prescription#", adjust: true},
                            // {id: "prescriptionLink", header: "PrescriptionLink", template: "#prescriptionLink#", adjust: true},
                            // {id: "settings", header: "Настройки", template: "#settings#", adjust: true},
                            // {id: "statusRegistration", header: "Статус регистрации", template: "#statusRegistration#", adjust: true},
                            // {id: "statusVisible", header: "Статус видимости", template: "#statusVisible#", adjust: true},
                            // {id: "beginVisible", header: "Дата начала видимости", template: "#beginVisible#", adjust: true},
                            // {id: "endVisible", header: "Дата конца видимости", template: "#endVisible#", adjust: true},
                            {id: "status", header: "Статус", template: "#statusPublicationName#"},
                        ],
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
                                const item = $$('type_requests_table').getItem(id);

                                webix.ajax().get('cls_type_request', {id: item.id}).then(function (data) {
                                    data = data.json();

                                    const published = data.statusPublication === 1 ? true : false;

                                    if (data.department) {
                                        data.departmentId = data.department.id;
                                    }
                                    if (data.regTypeRequestRestrictionTypes && data.regTypeRequestRestrictionTypes.length > 0) {
                                        data.restrictionTypeIds = data.regTypeRequestRestrictionTypes[0].regTypeRequestRestrictionTypeId.clsRestricti
                                    }

                                    loadTypeRequestFormInContent();

                                    $$('typeRequestForm').parse(data);

                                    if (published) {
                                        $$('addPrescriptionButton').hide();
                                        $$('searchByOkvedButton').hide();
                                        $$('searchByInnButton').hide();
                                        $$('savePrescription').hide();
                                        $$('saveAndPublishPrescription').hide();
                                    }

                                    if (data.regTypeRequestPrescriptions && data.regTypeRequestPrescriptions.length > 0) {
                                        data.regTypeRequestPrescriptions.forEach(rtrp => {
                                            const files = [];
                                            if (rtrp.regTypeRequestPrescriptionFiles && rtrp.regTypeRequestPrescriptionFiles.length > 0) {
                                                rtrp.regTypeRequestPrescriptionFiles.forEach((file) => {
                                                    files.push({id: file.id, name: file.originalFileName});
                                                })
                                            }
                                            $$('prescriptions').addView({
                                                id: 'prescription' + rtrp.num,
                                                rows: [
                                                    {
                                                        view: 'text',
                                                        id: 'prescription_id' + rtrp.num,
                                                        value: rtrp.id,
                                                        hidden: true
                                                    },
                                                    {
                                                        cols: [
                                                            {
                                                                view: 'label',
                                                                label: 'Предписание ' + rtrp.num,
                                                                align: 'center'
                                                            },
                                                        ]
                                                    },
                                                    {
                                                        view: 'nic-editor',
                                                        id: 'prescription_text' + rtrp.num,
                                                        css: "myClass",
                                                        cdn: false,
                                                        minHeight: 280,
                                                        config: {
                                                            iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                                                        },
                                                        required: true,
                                                    },
                                                    {
                                                        view: 'list',
                                                        id: 'listFiles' + rtrp.num,
                                                        autoheight: true,
                                                        template: published ? '#name#' : `#name# <span class="webix_icon wxi-trash" onclick='deletePrescriptionFile(` + rtrp.num + `, #id#)'></span>`,
                                                        data: files,
                                                    },
                                                    {
                                                        view: 'list',
                                                        id: 'prescriptionFiles' + rtrp.num,
                                                        type: 'uploader',
                                                        autoheight: true,
                                                    },
                                                    {
                                                        view: 'uploader',
                                                        id: 'uploader' + rtrp.num,
                                                        css: 'webix_primary',
                                                        value: 'Прикрепить файл(-ы)',
                                                        autosend: false,
                                                        upload: '/upload_prescription_file',
                                                        required: true,
                                                        accept: 'application/pdf, application/zip',
                                                        multiple: true,
                                                        link: 'prescriptionFiles' + rtrp.num,
                                                        hidden: published
                                                    }
                                                ]
                                            });
                                            $$('prescription_text' + rtrp.num).setValue(rtrp.content);
                                        });
                                        $$('prescriptions').show();
                                    }

                                    if (data.additionalFields) {
                                        if (data.additionalFields.okvedIds && data.additionalFields.okvedIds.length > 0) {
                                            webix.ajax().get('okveds').then(function (okvedsData) {
                                                const okveds = okvedsData.json();
                                                let selectedOkveds = [];
                                                data.additionalFields.okvedIds.forEach(okvedId => {
                                                    let okved = okveds.find(okved => okved.id === okvedId);
                                                    selectedOkveds.push({
                                                        id: okved.id,
                                                        name: okved.kindCode + ' ' + okved.kindName
                                                    });
                                                })
                                                $$('selectedOkveds').parse(selectedOkveds);
                                                $$('selectedOkveds').show();
                                            })
                                        }
                                        if (data.additionalFields.organizationIds && data.additionalFields.organizationIds.length > 0) {
                                            const params = {
                                                additionalFields: {
                                                    organizationIds: data.additionalFields.organizationIds
                                                }
                                            }
                                            webix.ajax()
                                                .headers({'Content-type': 'application/json'})
                                                .post('selected_organizations', params).then(function (organizationsData) {
                                                const organizations = organizationsData.json();
                                                let selectedOrganizations = [];
                                                organizations.forEach(organization => {
                                                    selectedOrganizations.push({
                                                        id: organization.id,
                                                        name: organization.inn + ' ' + organization.name
                                                    });
                                                })
                                                $$('selectedOrganizations').parse(selectedOrganizations);
                                                $$('selectedOrganizations').show();
                                            })
                                        }
                                    }

                                    // $$('departments').getList().add({ id: '', value: '' });

                                    $$('settings').setValue(data.settings);

                                    if (data.beginRegistration) {
                                        $$('beginRegistration').setValue(new Date(data.beginRegistration));
                                    }
                                    if (data.endRegistration) {
                                        $$('endRegistration').setValue(new Date(data.endRegistration));
                                    }
                                    if (data.beginVisible) {
                                        $$('beginVisible').setValue(new Date(data.beginVisible));
                                    }
                                    if (data.endVisible) {
                                        $$('endVisible').setValue(new Date(data.endVisible));
                                    }
                                })
                            }
                        },
                        url: 'cls_type_requests'
                    },
                    {
                        cols: [
                            // {
                            //     view: 'pager',
                            //     id: 'Pager',
                            //     height: 38,
                            //     size: 25,
                            //     group: 5,
                            //     template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            // },
                            {},
                            {},
                            {},
                            {},
                            {
                                view: 'button',
                                css: 'webix_primary',
                                value: 'Добавить',
                                href: "/type_request",
                                click: function () {
                                    loadTypeRequestFormInContent()

                                    // $$('departments').getList().add({ id: '', value: '' });
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

function deletePrescriptionFile(num, id) {
    webix.ajax().get('delete_prescription_file', {id: id}).then(function (result) {
        result = result.text();
        if (result === 'Файл удален') {
            $$('listFiles' + num).remove(id);
        } else {
            webix.message(result, 'error');
        }
    });
}

//Загрузка формы в контент сайта
function loadTypeRequestFormInContent(){
    webix.ui({
        id: 'content',
        rows: [
            typeRequestForm
        ]
    }, $$('content'))

    $$("tabs").addOption('settings', 'Дополнительные настройки', true);
}
//fix for paste into nic-editor pane
webix.html.addStyle(".myClass p{margin-top: 0px !important;line-height: 16px !important;}");

const typeRequestForm = {
    view: 'scrollview',
    scroll: 'xy',
    id: 'show_layout',
    autowidth: true,
    autoheight: true,
    body: {
        type: 'space',
        rows: [
            {
                view: 'form',
                id: 'typeRequestForm',
                elements: [
                    {
                        view: 'multiview',
                        id: 'wizard',
                        cells: [
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 1 из 4. Укажите информацию о предписании' },
                                    {
                                        type: 'form',
                                        rows: [
                                            {
                                                view: 'text',
                                                labelWidth: 190,
                                                label: 'Наименование',
                                                name: 'activityKind',
                                                required: true,
                                                validate: webix.rules.isNotEmpty
                                            },
                                            {
                                                view: 'text',
                                                labelWidth: 190,
                                                label: 'Краткое наименование',
                                                name: 'shortName',
                                                required: true,
                                                validate: webix.rules.isNotEmpty
                                            },
                                            {
                                                view: 'combo',
                                                id: 'departments',
                                                name: 'departmentId',
                                                label: 'Подразделение, к которому по умолчанию будут направляться заявки',
                                                labelWidth: 500,
                                                required: true,
                                                validate: webix.rules.isNotEmpty,
                                                options: 'cls_departments'
                                            },
                                            {
                                                view: 'combo',
                                                id: 'restrictionTypeId',
                                                name: 'restrictionTypeIds',
                                                label: 'Тип ограничения',
                                                labelWidth: 190,
                                                invalidMessage: 'Поле не может быть пустым',
                                                options: 'cls_restriction_types'
                                            },
                                            {}, //  для выравнивания на всю страницу
                                        ]
                                    },
                                    {
                                        cols: [
                                            {},
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Продолжить',
                                                click: next
                                            }
                                        ]
                                    },
                                ]
                            },
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 2 из 4. Укажите дополнительные настройки, если необходимо' },
                                    {
                                        type: 'form',
                                        rows: [
                                            {
                                                view: "tabbar",
                                                id: "tabs",
                                                multiview: true,
                                                borderless:true,
                                                width: 350,
                                                options: []
                                            },
                                            {
                                                id:"views",
                                                animate:false,
                                                minHeight: 300,
                                                cells: [
                                                    {
                                                        view: 'ace-editor',
                                                        id: 'settings',
                                                        theme: 'github',
                                                        mode: 'json',
                                                        cdn: false
                                                    }
                                                ]
                                            },
                                            {
                                                cols: [
                                                    {
                                                        view: 'checkbox',
                                                        label: 'Тип заявки доступен для подачи',
                                                        labelPosition: 'top',
                                                        name: 'statusRegistration',
                                                        on: {
                                                            onAfterRender() {
                                                                if (this.getValue() === 0) {
                                                                    $$('beginRegistration').disable();
                                                                    $$('endRegistration').disable();
                                                                } else {
                                                                    $$('beginRegistration').enable();
                                                                    $$('endRegistration').enable();
                                                                }
                                                            },
                                                            onChange(newVal, oldVal) {
                                                                if (newVal === 0) {
                                                                    $$('beginRegistration').disable();
                                                                    $$('endRegistration').disable();
                                                                } else {
                                                                    $$('beginRegistration').enable();
                                                                    $$('endRegistration').enable();
                                                                }
                                                            }
                                                        }
                                                    },
                                                    { view: 'datepicker', label: 'Дата начала подачи', labelPosition: 'top', name: 'beginRegistration', timepicker: true, id: 'beginRegistration'},
                                                    { view: 'datepicker', label: 'Дата конца подачи', labelPosition: 'top', name: 'endRegistration', timepicker: true, id: 'endRegistration'},
                                                ]
                                            },
                                            {
                                                cols: [
                                                    {
                                                        view: 'checkbox',
                                                        label: 'Тип заявки виден для подачи',
                                                        labelPosition: 'top',
                                                        name: 'statusVisible',
                                                        on: {
                                                            onAfterRender() {
                                                                if (this.getValue() === 0) {
                                                                    $$('beginVisible').disable();
                                                                    $$('endVisible').disable();
                                                                } else {
                                                                    $$('beginVisible').enable();
                                                                    $$('endVisible').enable();
                                                                }
                                                            },
                                                            onChange(newVal, oldVal) {
                                                                if (newVal === 0) {
                                                                    $$('beginVisible').disable();
                                                                    $$('endVisible').disable();
                                                                } else {
                                                                    $$('beginVisible').enable();
                                                                    $$('endVisible').enable();
                                                                }
                                                            }
                                                        }
                                                    },
                                                    { view: 'datepicker', label: 'Дата начала видимости', labelPosition: 'top', name: 'beginVisible', timepicker: true, id: 'beginVisible'},
                                                    { view: 'datepicker', label: 'Дата конца видимости', labelPosition: 'top', name: 'endVisible', timepicker: true, id: 'endVisible'},
                                                ]
                                            },
                                            { view: 'text', label: 'Вес сортировки',labelWidth:190, name: 'sortWeight', required: true, validate: webix.rules.isNumber, value: 0 }, //
                                        ]
                                    },
                                    {
                                        cols: [
                                            {},
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Назад',
                                                click: back
                                            },
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Продолжить',
                                                click: next
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 3 из 4. Добавьте тексты предписаний и файлы к ним' },
                                    {
                                        type: 'form',
                                        rows: [
                                            {
                                                id: 'prescriptions',
                                                hidden: true,
                                                rows: []
                                            },
                                            {
                                                cols: [
                                                    {
                                                        view: 'button',
                                                        id: 'addPrescriptionButton',
                                                        css: 'webix_primary',
                                                        maxWidth: 301,
                                                        value: 'Добавить',
                                                        click: function () {
                                                            $$('prescriptions').show();
                                                            const num = $$('prescriptions').getChildViews().length + 1;
                                                            $$('prescriptions').addView({
                                                                id: 'prescription' + num,
                                                                rows: [
                                                                    {
                                                                        cols: [
                                                                            {
                                                                                view: 'label',
                                                                                label: 'Предписание ' + num,
                                                                                align: 'center'
                                                                            },
                                                                        ]
                                                                    },
                                                                    {
                                                                        view: 'nic-editor',
                                                                        id: 'prescription_text' + num,
                                                                        css: "myClass",
                                                                        cdn: false,
                                                                        minHeight: 280,
                                                                        config: {
                                                                            iconsPath: '../libs/nicedit/nicEditorIcons.gif'
                                                                        },
                                                                        required: true,
                                                                    },
                                                                    {
                                                                        view: 'list',
                                                                        id: 'prescriptionFiles' + num,
                                                                        type: 'uploader',
                                                                        autoheight: true,
                                                                    },
                                                                    {
                                                                        view: 'uploader',
                                                                        id: 'uploader' + num,
                                                                        css: 'webix_primary',
                                                                        value: 'Прикрепить файл(-ы)',
                                                                        autosend: false,
                                                                        upload: '/upload_prescription_file',
                                                                        required: true,
                                                                        accept: 'application/pdf, application/zip',
                                                                        multiple: true,
                                                                        link: 'prescriptionFiles' + num,
                                                                    }
                                                                ]
                                                            })
                                                        }
                                                    },
                                                    {}
                                                ]
                                            },
                                            {}, //  для выравнивания на всю страницу
                                        ]
                                    },
                                    {
                                        cols: [
                                            {},
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Назад',
                                                click: back
                                            },
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Продолжить',
                                                click: next
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 4 из 4. Выберите условия отбора организаций, которые получат предписания после публикации' },
                                    {
                                        view: 'tabbar',
                                        value: 'selectOkveds',
                                        multiview: true,
                                        options: [
                                            { id: 'selectOkveds', value: 'по ОКВЭД' },
                                            { id: 'selectInns', value: 'по ИНН' }
                                        ]
                                    },
                                    {
                                        cells: [
                                            {
                                                id: 'selectOkveds',
                                                type: 'form',
                                                rows: [
                                                    {
                                                        id: 'searchByOkved',
                                                        hidden: true,
                                                        rows: [
                                                            {
                                                                view: 'tree',
                                                                id: 'treeOkveds',
                                                                template: '{common.checkbox()}   #value#',
                                                                threeState: true,
                                                                minHeight: 450,
                                                                scheme: {
                                                                    $group: '#id#'
                                                                },
                                                                on: {
                                                                    onItemCheck(id, state) {
                                                                        let okved = this.getItem(id);
                                                                        if (state) {
                                                                            if (!$$('selectedOkveds').exists(id)) {
                                                                                $$('selectedOkveds').add({
                                                                                    id: okved.id,
                                                                                    name: okved.kindCode + ' ' + okved.kindName
                                                                                });
                                                                            }
                                                                        } else {
                                                                            $$('selectedOkveds').remove(id);
                                                                        }
                                                                    }
                                                                },
                                                                url: 'okveds',
                                                            },
                                                            {
                                                                cols: [
                                                                    {
                                                                        view: 'button',
                                                                        // align: 'right',
                                                                        css: 'webix_primary',
                                                                        value: 'Отмена',
                                                                        maxWidth: 300,
                                                                        click: function () {
                                                                            $$('searchByOkved').hide();
                                                                            $$('searchByOkvedButton').show();
                                                                        }
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        cols: [
                                                            {
                                                                view: 'button',
                                                                id: 'searchByOkvedButton',
                                                                // align: 'right',
                                                                css: 'webix_primary',
                                                                value: 'Добавить',
                                                                maxWidth: 300,
                                                                click: function () {
                                                                    $$('searchByOkvedButton').hide();
                                                                    $$('searchByOkved').show();
                                                                }
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        view: 'list',
                                                        id: 'selectedOkveds',
                                                        autoheight: true,
                                                        template: `#name# <span class="webix_icon wxi-trash" onclick="deleteSelectedOkved('#id#')"></span>`,
                                                    },
                                                    {}
                                                ]
                                            },
                                            {
                                                id: 'selectInns',
                                                type: 'form',
                                                rows: [
                                                    {
                                                        id: 'searchByInn',
                                                        hidden: true,
                                                        rows: [
                                                            {
                                                                view: 'search',
                                                                id: 'search',
                                                                maxWidth: 300,
                                                                minWidth: 100,
                                                                tooltip: 'после ввода значения нажмите Enter',
                                                                placeholder: "ИНН",
                                                                on: {
                                                                    onEnter: function () {
                                                                        if ($$('foundOrganizations')) {
                                                                            $$('searchByInn').removeView('foundOrganizations');
                                                                        }
                                                                        $$('searchByInn').addView({
                                                                            id: 'foundOrganizations',
                                                                            rows: [
                                                                                {
                                                                                    view: 'datatable',
                                                                                    id: 'organizations_table',
                                                                                    select: 'row',
                                                                                    navigation: true,
                                                                                    resizeColumn: true,
                                                                                    pager: 'Pager',
                                                                                    datafetch: 25,
                                                                                    columns: [
                                                                                        {id: "orgId", checkValue:'on', uncheckValue:'off', template: '{common.checkbox()}' },
                                                                                        {id: "inn", header: "ИНН", template: "#inn#", adjust: true},
                                                                                        {id: "name", header: "Наименование организации/ИП", template: "#name#", adjust: true},
                                                                                    ],
                                                                                    minHeight: 350,
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
                                                                                        onCheck: function (rowId, colId, state) {
                                                                                            let organization = this.getItem(rowId);
                                                                                            if (state === 'on') {
                                                                                                if (!$$('selectedOrganizations').exists(rowId)) {
                                                                                                    $$('selectedOrganizations').add({
                                                                                                        id: organization.id,
                                                                                                        name: organization.inn + ' ' + organization.name
                                                                                                    });
                                                                                                }
                                                                                            } else {
                                                                                                $$('selectedOrganizations').remove(rowId)
                                                                                            }
                                                                                        }
                                                                                    },
                                                                                    url: 'cls_organizations?inn=' + $$('search').getValue()
                                                                                },
                                                                                {
                                                                                    view: 'pager',
                                                                                    id: 'Pager',
                                                                                    height: 38,
                                                                                    size: 25,
                                                                                    group: 5,
                                                                                    template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                                                                                }
                                                                            ],
                                                                        }, 2);
                                                                    }
                                                                }
                                                            },
                                                            {
                                                                cols: [
                                                                    {
                                                                        view: 'button',
                                                                        // align: 'right',
                                                                        css: 'webix_primary',
                                                                        value: 'Отмена',
                                                                        maxWidth: 300,
                                                                        click: function () {
                                                                            $$('searchByInn').hide();
                                                                            $$('searchByInnButton').show();
                                                                        }
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        cols: [
                                                            {
                                                                view: 'button',
                                                                id: 'searchByInnButton',
                                                                // align: 'right',
                                                                css: 'webix_primary',
                                                                value: 'Добавить',
                                                                maxWidth: 300,
                                                                click: function () {
                                                                    $$('searchByInnButton').hide();
                                                                    $$('searchByInn').show();
                                                                }
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        view: 'list',
                                                        id: 'selectedOrganizations',
                                                        autoheight: true,
                                                        template: `#name# <span class="webix_icon wxi-trash" onclick="deleteSelectedOrganization('#id#')"></span>`,
                                                    },
                                                    {}
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        cols: [
                                            {},
                                            {
                                                view: 'button',
                                                css: 'webix_primary',
                                                maxWidth: 301,
                                                value: 'Назад',
                                                click: back
                                            },
                                            {
                                                view: 'button',
                                                id: 'savePrescription',
                                                align: 'right',
                                                css: 'webix_primary',
                                                value: 'Сохранить',
                                                maxWidth: 300,
                                                click: function () {
                                                    this.disable();

                                                    if ($$('typeRequestForm').validate()) {
                                                        webix.ajax().headers({
                                                            'Content-Type': 'application/json'
                                                        }).post('/save_cls_type_request',
                                                            JSON.stringify(getTypeRequestFormParams())
                                                        ).then(function (data) {
                                                            const savedTypeRequest = data.json();
                                                            if (savedTypeRequest.id && savedTypeRequest.statusPublication === 0) {
                                                                // сохраним файлы предписаний
                                                                if (savedTypeRequest.regTypeRequestPrescriptions && savedTypeRequest.regTypeRequestPrescriptions.length > 0) {
                                                                    savedTypeRequest.regTypeRequestPrescriptions.forEach(rtrp => {
                                                                        let uploader = $$('uploader' + rtrp.num);
                                                                        if (uploader) {
                                                                            uploader.define('formData', {idTypeRequestPrescription: rtrp.id})
                                                                            uploader.send(function (response) {
                                                                                if (response) {
                                                                                    console.log(response.cause)
                                                                                }
                                                                            });
                                                                        }
                                                                    })
                                                                }
                                                                //
                                                                webix.message({text: 'Предписание сохранено', type: 'success'});
                                                                webix.ui({
                                                                    id: 'content',
                                                                    rows: [
                                                                        typeRequests
                                                                    ]
                                                                }, $$('content'));
                                                            } else {
                                                                webix.message({text: 'Не удалось сохранить предписание', type: 'error'});
                                                            }
                                                            $$('savePrescription').enable();
                                                        })
                                                    } else {
                                                        webix.message({text: 'Не заполнены обязательные поля', type: 'error'});
                                                    }
                                                }
                                            },
                                            {
                                                view: 'button',
                                                id: 'saveAndPublishPrescription',
                                                align: 'right',
                                                css: 'webix_primary',
                                                value: 'Опубликовать',
                                                maxWidth: 300,
                                                click: function () {
                                                    this.disable();

                                                    if ($$('typeRequestForm').validate()) {
                                                        webix.ajax().headers({
                                                            'Content-Type': 'application/json'
                                                        }).post('/save_cls_type_request',
                                                            JSON.stringify(getTypeRequestFormParams())
                                                        ).then(function (data) {
                                                            const savedTypeRequest = data.json();
                                                            if (savedTypeRequest.id && savedTypeRequest.statusPublication === 0) {
                                                                // сохраним файлы предписаний
                                                                if (savedTypeRequest.regTypeRequestPrescriptions && savedTypeRequest.regTypeRequestPrescriptions.length > 0) {
                                                                    savedTypeRequest.regTypeRequestPrescriptions.forEach(rtrp => {
                                                                        let uploader = $$('uploader' + rtrp.num);
                                                                        if (uploader) {
                                                                            uploader.define('formData', {idTypeRequestPrescription: rtrp.id})
                                                                            uploader.send(function (response) {
                                                                                if (response) {
                                                                                    console.log(response.cause)
                                                                                }
                                                                            });
                                                                        }
                                                                    })
                                                                }
                                                            } else {
                                                                webix.message({text: 'Не удалось сохранить предписание', type: 'error'});
                                                            }
                                                            $$('saveAndPublishPrescription').enable();
                                                            return webix.ajax().get('publish_prescription', {id: savedTypeRequest.id});
                                                        }).then(function (result) {
                                                            result = result.text();
                                                            if (result === 'Предписание опубликовано') {
                                                                webix.message({text: result, type: 'success'});
                                                                webix.ui({
                                                                    id: 'content',
                                                                    rows: [
                                                                        typeRequests
                                                                    ]
                                                                }, $$('content'));
                                                            } else {
                                                                webix.message({text: result, type: 'error'});
                                                            }
                                                            $$('saveAndPublishPrescription').enable();
                                                        })
                                                    } else {
                                                        webix.message({text: 'Не заполнены обязательные поля', type: 'error'});
                                                    }
                                                }
                                            },
                                            {
                                                view: 'button',
                                                align: 'right',
                                                css: 'webix_primary',
                                                value: 'Отмена',
                                                maxWidth: 300,
                                                click: function () {
                                                    webix.ui({
                                                        id: 'content',
                                                        rows: [typeRequests]
                                                    }, $$('content'))
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ]
    }
}

function deleteSelectedOrganization(id) {
    $$('selectedOrganizations').remove(id);
}

function deleteSelectedOkved(id) {
    $$('selectedOkveds').remove(id);
}

function getTypeRequestFormParams() {
    let params = $$('typeRequestForm').getValues();
    params.settings = $$('settings').getValue();

    params.additionalFields = {};
    params.additionalFields.okvedIds = $$('selectedOkveds').serialize().map(okved => okved.id);
    params.additionalFields.organizationIds = $$('selectedOrganizations').serialize().map(organization => organization.id);

    const countPrescriptions = $$('prescriptions').getChildViews().length;
    if (countPrescriptions > 0) {
        params.regTypeRequestPrescriptions = [];
        for (let num = 1; num <= countPrescriptions; num++) {
            if ($$('prescription_text' + num).getValue()) {
                let id;
                if ($$('prescription_id' + num)) {
                    id = $$('prescription_id' + num).getValue();
                }
                params.regTypeRequestPrescriptions.push({
                    id,
                    num: num,
                    content: $$('prescription_text' + num).getValue()
                });
            }
        }
    }
    return params;
}
