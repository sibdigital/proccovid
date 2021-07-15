
webix.i18n.setLocale("ru-RU");

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function getFilesListByTypeView(docRequestSubsidyId) {
    webix.ajax(`../request_subsidy_files_verification/${ docRequestSubsidyId }`).then(function (filesVerification) {
        filesVerification = filesVerification.json();

        filesVerification.map((file) => {
            switch (file.verify_status) {
               case 1: file.verify_status = "проверка прошла успешно"; break;
               case 2: file.verify_status = "подпись не соответствует файлу"; break;
               case 3: file.verify_status = "в сертификате или цепочке сертификатов есть ошибки"; break;
               case 4: file.verify_status = "в подписи есть ошибки"; break;
               default: file.verify_status = "проверка не проводилась"; break;
            }
            return file;
        });

        webix.ajax(`../request_subsidy_files/${docRequestSubsidyId}`).then(function (data) {
            const views = [];
            data = data.json();
            console.dir({ data, filesVerification });
            if (data.length > 0) {
                const filesTypes = {};
                const byFileType = data.reduce(function (result, file) {
                    const fileVerificationStatus = filesVerification.find((fileVerification) => fileVerification.id_request_subsidy_file === file.id);
                    result[file.fileType.id] = result[file.fileType.id] || [];
                    result[file.fileType.id].push({ ...file, verificationStatus: fileVerificationStatus ?? { verify_status: 'отсутствует подпись' } });

                    filesTypes[file.fileType.id] = file.fileType.name;

                    return result;
                }, Object.create(null));

                console.dir({ byFileType });

                for (const [key, filesArray] of Object.entries(byFileType)) {
                    views.push({
                        rows: [
                            view_section(filesTypes[key]),
                            {
                                id: `request_subsidy_files_table/${key}`,
                                view: 'datatable',
                                pager: `Pager/${key}`,
                                autoheight: true,
                                header: `id = ${key}`,
                                select: 'row',
                                resizeColumn: true,
                                readonly: true,
                                data: filesArray,
                                columns: [
                                    {
                                        id: 'viewFileName',
                                        header: 'Название файла',
                                        adjust: true,
                                        fillspace: true,
                                        sort: 'string',
                                    },
                                    {
                                        id: 'signature',
                                        template: function (request) {
                                            let label = '';
                                            let style = '';

                                            if (request.signature) {
                                                label = 'Подпись загружена';
                                                style = 'color: green';
                                            } else {
                                                label = 'Подпись не загружена';
                                                style = 'color: red';
                                            }

                                            return `<div style="${style}" role="gridcell" aria-rowindex="1" aria-colindex="1" aria-selected="true" tabindex="0" class="webix_cell webix_row_select">${label}</div>`;
                                        },
                                        header: 'Подпись',
                                        adjust: true,
                                        fillspace: true,
                                        sort: 'string',
                                    },
                                    {
                                        id: 'verificationStatus',
                                        header: 'Статус проверки подписи',
                                        adjust: true,
                                        sort: 'string',
                                        template: '#verificationStatus.verify_status#',
                                    },
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
                                }
                            },
                            {
                                view: 'pager',
                                id: `Pager/${key}`,
                                height: 38,
                                size: 25,
                                group: 5,
                                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            },
                        ]
                    })
                }
            } else {
                views.push({
                    rows: [
                        view_section('Отсутствуют файлы'),
                        {
                            id: `request_subsidy_files_table`,
                            view: 'datatable',
                            // pager: `Pager`,
                            autoheight: true,
                            select: 'row',
                            resizeColumn: true,
                            readonly: true,
                            data: [],
                            columns: [
                                {
                                    id: 'viewFileName',
                                    header: 'Название файла',
                                    adjust: true,
                                    fillspace: true,
                                },
                                {
                                    id: 'signature',
                                    header: 'Подпись',
                                    adjust: true,
                                    fillspace: true,
                                },
                                {
                                    id: 'verificationStatus',
                                    header: 'Статус проверки подписи',
                                    adjust: true,
                                },
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
                            }
                        }
                    ]
                })
            }

            webix.ui({
                id: 'filesListViewByType',
                rows: views,
            }, $$('filesListViewByType'));
        })
    });
}

webix.ready(function () {
    webix.ajax('../doc_requests_subsidy/' + ID).then(function (data) {
        data = data.json();
        console.dir({ data });

        getFilesListByTypeView(data.id);

        webix.ui({
            container: 'app',
            width: 1200,
            height: 840,
            css: { margin: '0 auto' },
            rows: [
                {
                    id: 'form',
                    view: 'form',
                    complexData: true,
                    elements: [
                        {
                            id: 'tabview',
                            view: 'tabview',
                            cells: [
                                {
                                    header: 'Основное',
                                    body: {
                                        view: 'scrollview',
                                        scroll: 'y',
                                        autowidth: true,
                                        autoheight: true,
                                        body: {
                                            rows: [
                                                view_section('Мера поддержки'),
                                                {
                                                    view: 'text',
                                                    id: 'subsidyId',
                                                    label: 'Мера поддержки',
                                                    labelPosition: 'top',
                                                    readonly: true,
                                                    name: 'subsidy.name',
                                                },
                                                {
                                                    view: 'textarea',
                                                    id: 'reqBasis',
                                                    label: 'Обоснование заявки',
                                                    labelPosition: 'top',
                                                    height: 150,
                                                    minWidth: 250,
                                                    readonly: true,
                                                    name: 'reqBasis',
                                                },
                                                view_section('Данные об организации'),
                                                {
                                                    margin: 5,
                                                    responsive: "respLeftToRight",
                                                    cols: [
                                                        {
                                                            minWidth: 300,
                                                            rows: [
                                                                {
                                                                    view: 'text',
                                                                    name: 'organization.shortName',
                                                                    id: 'shortOrganizationName',
                                                                    label: 'Краткое наименование организации',
                                                                    labelPosition: 'top',
                                                                    readonly: true,
                                                                },
                                                                {
                                                                    view: 'textarea',
                                                                    name: 'organization.name',
                                                                    height: 80,
                                                                    id: 'organizationName',
                                                                    label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
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
                                                                            name: 'organization.inn',
                                                                            id: "inn",
                                                                            label: 'ИНН',
                                                                            minWidth: 200,
                                                                            labelPosition: 'top',
                                                                            readonly: true,
                                                                        },
                                                                        {
                                                                            view: 'text',
                                                                            name: 'organization.ogrn',
                                                                            id: 'ogrn',
                                                                            label: 'ОГРН',
                                                                            minWidth: 200,
                                                                            validate: function (val) {
                                                                                return !isNaN(val * 1);
                                                                            },
                                                                            labelPosition: 'top',
                                                                            readonly: true,
                                                                        },
                                                                    ]
                                                                },
                                                            ]
                                                        },
                                                    ]
                                                },
                                            ]
                                        }
                                    }
                                },
                                {
                                    header: 'Файлы',
                                    body: {
                                        view: 'scrollview',
                                        scroll: 'y',
                                        autowidth: true,
                                        autoheight: true,
                                        body: {
                                            rows: [
                                                // view_section('Файлы'),
                                                {
                                                    id: 'filesListViewByType',
                                                }
                                            ]
                                        }
                                    }
                                },
                                {
                                    header: 'Утверждение',
                                    body: {
                                        view: 'scrollview',
                                        scroll: 'y',
                                        autowidth: true,
                                        autoheight: true,
                                        body: {
                                            rows: [
                                                view_section('Комментарий'),
                                                {
                                                    id: 'resolutionComment',
                                                    view: 'textarea',
                                                    name: 'resolutionComment',
                                                    label: 'Комментарий',
                                                    labelPosition: 'top',
                                                    height: 300,
                                                    value: data.resolutionComment,
                                                },
                                                view_section('Утверждение'),
                                                {
                                                    rows: [
                                                        {
                                                            view: 'text',
                                                            label: 'Статус заявки',
                                                            labelPosition: 'top',
                                                            value: data.subsidyRequestStatus.name,
                                                        },
                                                        {
                                                            cols: [
                                                                {
                                                                    view: 'button',
                                                                    value: 'Отклонить',
                                                                    click: () => changeRequestSubsidyStatus(false, data.id),
                                                                },
                                                                {
                                                                    view: 'button',
                                                                    value: 'Утвердить',
                                                                    click: () => changeRequestSubsidyStatus(true, data.id),
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }
                                            ]
                                        }
                                    }
                                },
                            ]
                        }
                    ],
                    data: data,
                }
            ]
        });

    })
})

function changeRequestSubsidyStatus(approve, id_request_subsidy) {
    const params = {
        resolutionComment: $$('resolutionComment').getValue(),
    };

    console.log(params);


    webix.ajax()
        .headers({ 'Content-Type': 'application/json' })
        .post(`../change_request_subsidy_status/${ id_request_subsidy }/${ approve }`, params)
        .then((data) => {});
}
