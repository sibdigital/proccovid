const prescriptions = {
    view: 'scrollview',
    scroll: 'xy',
    id: "prescriptionsId",
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'datatable',
                        id: 'prescriptions_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        // pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "name", header: "Наименование", template: "#name#", adjust: true, fillspace: true},
                            {id: "typeRequest", header: "Вид деятельности", template: "#typeRequest.activityKind#", adjust: true, fillspace: true},
                            {id: "status", header: "Статус", template: "#statusName#", adjust: true},
                            {id: "time_Publication", header: "Дата публикации", adjust: true, format: dateFormat},
                        ],
                        scheme: {
                            $init: function (obj) {
                                if (obj.status == 1) {
                                    obj.time_Publication = obj.timePublication ? obj.timePublication.replace("T", " ") : "";
                                }
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
                                const item = $$('prescriptions_table').getItem(id);

                                webix.ajax().get('cls_prescription', {id: item.id}).then(function (data) {
                                    data = data.json();

                                    const published = data.status == 1 ? true : false;

                                    if (data.typeRequest) {
                                        data.typeRequestId = data.typeRequest.id;
                                    }

                                    loadPrescriptionFormInContent();

                                    $$('prescriptionForm').parse(data);

                                    if (published) {
                                        $$('addPrescriptionButton').hide();
                                        $$('searchByOkvedButton').hide();
                                        $$('searchByInnButton').hide();
                                        $$('savePrescription').hide();
                                        $$('saveAndPublishPrescription').hide();
                                    }

                                    if (data.prescriptionTexts && data.prescriptionTexts.length > 0) {
                                        data.prescriptionTexts.forEach(pt => {
                                            const files = [];
                                            if (pt.prescriptionTextFiles && pt.prescriptionTextFiles.length > 0) {
                                                pt.prescriptionTextFiles.forEach((file) => {
                                                    files.push({id: file.id, name: file.originalFileName});
                                                })
                                            }
                                            $$('prescriptions').addView({
                                                id: 'prescription' + pt.num,
                                                rows: [
                                                    {
                                                        view: 'text',
                                                        id: 'prescription_id' + pt.num,
                                                        value: pt.id,
                                                        hidden: true
                                                    },
                                                    {
                                                        cols: [
                                                            {
                                                                view: 'label',
                                                                label: 'Текст №' + pt.num,
                                                                align: 'center'
                                                            },
                                                        ]
                                                    },
                                                    {
                                                        view: 'nic-editor',
                                                        id: 'prescription_text' + pt.num,
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
                                                        id: 'listFiles' + pt.num,
                                                        autoheight: true,
                                                        template: published ? '#name#' : `#name# <span class="webix_icon wxi-trash" onclick='deletePrescriptionFile(` + pt.num + `, #id#)'></span>`,
                                                        data: files,
                                                    },
                                                    {
                                                        view: 'list',
                                                        id: 'prescriptionFiles' + pt.num,
                                                        type: 'uploader',
                                                        autoheight: true,
                                                    },
                                                    {
                                                        view: 'uploader',
                                                        id: 'uploader' + pt.num,
                                                        css: 'webix_primary',
                                                        value: 'Прикрепить файл(-ы)',
                                                        autosend: false,
                                                        upload: '/upload_prescription_file',
                                                        required: true,
                                                        accept: 'application/pdf, application/zip',
                                                        multiple: true,
                                                        link: 'prescriptionFiles' + pt.num,
                                                        hidden: published
                                                    }
                                                ]
                                            });
                                            $$('prescription_text' + pt.num).setValue(pt.content);
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
                                })
                            }
                        },
                        url: 'cls_prescriptions'
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
                                    loadPrescriptionFormInContent()

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
function loadPrescriptionFormInContent(){
    webix.ui({
        id: 'content',
        rows: [
            prescriptionForm
        ]
    }, $$('content'))
}
//fix for paste into nic-editor pane
webix.html.addStyle(".myClass p{margin-top: 0px !important;line-height: 16px !important;}");

const prescriptionForm = {
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
                id: 'prescriptionForm',
                elements: [
                    {
                        view: 'multiview',
                        id: 'wizard',
                        cells: [
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 1 из 3. Укажите информацию о предписании' },
                                    {
                                        type: 'form',
                                        rows: [
                                            {
                                                view: 'text',
                                                labelWidth: 190,
                                                label: 'Наименование',
                                                name: 'name',
                                                required: true,
                                                validate: webix.rules.isNotEmpty,
                                                invalidMessage: 'Поле не может быть пустым',
                                            },
                                            {
                                                view: 'text',
                                                labelWidth: 190,
                                                label: 'Описание',
                                                name: 'description',
                                                required: true,
                                                validate: webix.rules.isNotEmpty,
                                                invalidMessage: 'Поле не может быть пустым',
                                            },
                                            {
                                                view: 'combo',
                                                id: 'typeRequestId',
                                                name: 'typeRequestId',
                                                label: 'Вид деятельности',
                                                labelWidth: 190,
                                                required: true,
                                                validate: webix.rules.isNotEmpty,
                                                options: 'cls_type_requests',
                                                invalidMessage: 'Поле не может быть пустым',
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
                                                click: function () {
                                                    if ($$('prescriptionForm').validate()) {
                                                        next(1);
                                                    }
                                                }
                                            }
                                        ]
                                    },
                                ]
                            },
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 2 из 3. Добавьте тексты предписаний и файлы к ним' },
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
                                                                                label: 'Текст №' + num,
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
                                                click: function () {
                                                    if ($$('prescriptionForm').validate()) {
                                                        next(2);
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                rows: [
                                    { type: 'header', template: 'Шаг 3 из 3. Выберите условия отбора организаций, которые получат предписания после публикации' },
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

                                                    if ($$('prescriptionForm').validate()) {
                                                        webix.ajax().headers({
                                                            'Content-Type': 'application/json'
                                                        }).post('/save_cls_prescription',
                                                            JSON.stringify(getPrescriptionFormParams())
                                                        ).then(function (data) {
                                                            const savedPrescription = data.json();
                                                            if (savedPrescription.id && savedPrescription.status === 0) {
                                                                // сохраним файлы предписаний
                                                                if (savedPrescription.prescriptionTexts && savedPrescription.prescriptionTexts.length > 0) {
                                                                    savedPrescription.prescriptionTexts.forEach(pt => {
                                                                        let uploader = $$('uploader' + pt.num);
                                                                        if (uploader) {
                                                                            uploader.define('formData', {idPrescriptionText: pt.id})
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
                                                                        prescriptions
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

                                                    const message = 'Вы уверены, что хотитет опубликовать предписание? ' /*+
                                                        'После публикации будет запущен процесс создания шаблонов заявок для выбранных организаций.'*/;

                                                    webix.confirm(message).then(function () {
                                                        if ($$('prescriptionForm').validate()) {
                                                            webix.ajax().headers({
                                                                'Content-Type': 'application/json'
                                                            }).post('/save_cls_prescription',
                                                                JSON.stringify(getPrescriptionFormParams())
                                                            ).then(function (data) {
                                                                const savedPrescription = data.json();
                                                                if (savedPrescription.id && savedPrescription.status === 0) {
                                                                    // сохраним файлы предписаний
                                                                    if (savedPrescription.prescriptionTexts && savedPrescription.prescriptionTexts.length > 0) {
                                                                        savedPrescription.prescriptionTexts.forEach(pt => {
                                                                            let uploader = $$('uploader' + pt.num);
                                                                            if (uploader) {
                                                                                uploader.define('formData', {idPrescriptionText: pt.id})
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
                                                                return webix.ajax().get('publish_prescription', {id: savedPrescription.id});
                                                            }).then(function (result) {
                                                                result = result.text();
                                                                if (result === 'Предписание опубликовано') {
                                                                    webix.message({text: result, type: 'success'});
                                                                    webix.ui({
                                                                        id: 'content',
                                                                        rows: [
                                                                            prescriptions
                                                                        ]
                                                                    }, $$('content'));
                                                                } else {
                                                                    webix.message({text: result, type: 'error'});
                                                                }
                                                                $$('saveAndPublishPrescription').enable();
                                                            })
                                                        } else {
                                                            webix.message({text: 'Не заполнены обязательные поля',type: 'error'});
                                                        }
                                                    }).fail(function () {
                                                        $$('saveAndPublishPrescription').enable();
                                                    })
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
                                                        rows: [
                                                            prescriptions
                                                        ]
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

function getPrescriptionFormParams() {
    let params = $$('prescriptionForm').getValues();
    console.log(params)

    params.additionalFields = {};
    params.additionalFields.okvedIds = $$('selectedOkveds').serialize().map(okved => okved.id);
    params.additionalFields.organizationIds = $$('selectedOrganizations').serialize().map(organization => organization.id);

    const countPrescriptionTexts = $$('prescriptions').getChildViews().length;
    if (countPrescriptionTexts > 0) {
        params.prescriptionTexts = [];
        for (let num = 1; num <= countPrescriptionTexts; num++) {
            if ($$('prescription_text' + num).getValue()) {
                let id;
                if ($$('prescription_id' + num)) {
                    id = $$('prescription_id' + num).getValue();
                }
                params.prescriptionTexts.push({
                    id,
                    num: num,
                    content: $$('prescription_text' + num).getValue()
                });
            }
        }
    }
    return params;
}

function back() {
    $$("wizard").back();
}

function next(page) {
    $$("wizard").getChildViews()[page].show();
}
