
webix.i18n.setLocale("ru-RU");

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function getVerifyStatusStringByIntStatus(verifyStatus) {
    let status = '';
    switch (verifyStatus) {
        case 1: status = "проверка прошла успешно"; break;
        case 2: status = "подпись не соответствует файлу"; break;
        case 3: status = "в сертификате или цепочке сертификатов есть ошибки"; break;
        case 4: status = "в подписи есть ошибки"; break;
        default: status = "проверка не проводилась"; break;
    }
    return status;
}



function getFilesListByTypeView(docRequestSubsidyId) {
    webix.ajax(`../request_subsidy_files_verification/${ docRequestSubsidyId }`).then(function (filesVerification) {
        filesVerification = filesVerification.json();

        filesVerification.map((file) => {
            file.verify_status = getVerifyStatusStringByIntStatus(file.verify_status);
            if (file.id_user) {
                file.verificationOwner = 'Пользователём';
            } else if (file.id_principal) {
                file.verificationOwner = 'Организацией';
            } else {
                file.verificationOwner = 'Проверка не проводилась';
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
                    // const fileVerificationStatus = filesVerification.find((fileVerification) => fileVerification.id_request_subsidy_file === file.id);
                    result[file.fileType.id] = result[file.fileType.id] || [];

                    const fileVerificationStatus = filesVerification.filter((fileVerification) => fileVerification.id_request_subsidy_file === file.id);
                    fileVerificationStatus.forEach((fileVer, index) => {
                        result[file.fileType.id].push({
                            ...file,
                            id: `${ file.id }/${ index }`,
                            verificationStatus: fileVer
                        });
                    });

                    if (fileVerificationStatus.length === 0) {
                        result[file.fileType.id].push({
                            ...file,
                            verificationStatus: fileVerificationStatus ?? {verify_status: 'Проверка не проводилась'}
                        });
                    }

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
                                // select: 'row',
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
                                        template: (request) => {
                                            return `<div class='download_docs'>
                                                        <a style='text-decoration: none; color: #1ca1c1' href="${ LINK_PREFIX }${ request.fileName }${ LINK_SUFFIX }" download>${ request.viewFileName }</a>
                                                        <span style='padding-left: 10px; color: #389a0d; font-weight: 400'></span>
                                                    </div>`
                                        }
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
                                        id: 'verificationOwner',
                                        header: 'Проверено',
                                        adjust: true,
                                        sort: 'string',
                                        template: '#verificationStatus.verificationOwner#',
                                    },
                                    {
                                        id: 'verificationStatus',
                                        header: 'Статус проверки подписи',
                                        adjust: true,
                                        template: (request) => {
                                            // console.dir({ request });
                                            const id_request_subsidy_file = request.verificationStatus.id_request_subsidy_file;
                                            const id_user = request.verificationStatus.id_user ?? -1;
                                            const id_principal = request.verificationStatus.id_principal ?? -1;
                                            return `<button class="verification_request_subsidy_file_signature_status_button" onclick=getVerificationStatus(${ [id_request_subsidy_file, id_user, id_principal].join(',') })>${ request.verificationStatus.verify_status }</button>`
                                        },
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
                                    minWidth: 650,
                                    fillspace: true,
                                },
                                {
                                    id: 'signature',
                                    header: 'Подпись',
                                    adjust: true,
                                    maxWidth: 250,
                                    // fillspace: true,
                                },
                                {
                                    id: 'verificationStatus',
                                    header: 'Статус проверки подписи',
                                    maxWidth: 300,
                                    adjust: true,
                                    // fillspace: true,
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
                                    id: 'filesTab',
                                    header: 'Файлы',
                                    body: {
                                        view: 'scrollview',
                                        scroll: 'y',
                                        autowidth: true,
                                        autoheight: true,
                                        body: {
                                            rows: [
                                                {
                                                    id: 'verifyFilesButton',
                                                    rows: [
                                                        view_section('Проверка подписей'),
                                                        {
                                                            margin: 10,
                                                            padding: 10,
                                                            borderless: true,
                                                            cols: [
                                                                {
                                                                    view: 'button',
                                                                    value: 'Проверить подписи',
                                                                    width: 170,
                                                                    css: "webix_primary custom-btn-border",
                                                                    click: () => {
                                                                        // let progressBar = $$("progress_bar");
                                                                        // webix.extend(progressBar, webix.ProgressBar);
                                                                        // let progress = 1 / 2;
                                                                        // progressBar.showProgress({type: "top", position: progress === 0 ? 0.001 : progress})
                                                                        // document.getElementById("progress_bar_text").innerHTML =
                                                                        //     "<span style='position: absolute; margin-top: 8px; left: 10px; z-index: 100; font-weight: 500'>" +
                                                                        //     "Проверено: " + 1 + "/" + 2 +
                                                                        //     "</span>";
                                                                        if (!ID) {
                                                                            webix.message("Не найдена заявка", "error", 4000);
                                                                            return;
                                                                        }
                                                                        webix.ajax('../verification_request_subsidy_signature_files/' + ID);
                                                                        verify_progress(ID, "До начала проверки подписей не менее "); //show progress on start event
                                                                        let timerId = setInterval(() => {
                                                                            verify_progress(ID, "До начала проверки подписей не менее ", timerId);
                                                                        }, 4000)
                                                                    }
                                                                },
                                                                {
                                                                    id: 'check',
                                                                    rows: [
                                                                        {
                                                                            id: 'progress_bar',
                                                                            padding: 10,
                                                                            borderless: true,
                                                                            hidden: false,
                                                                            template: "<div id='progress_bar_text'></div>"
                                                                        },
                                                                    ]
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                },
                                                {
                                                    id: 'filesListViewByType',
                                                },
                                                {
                                                    id: 'verificationFileInfoView',
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
                                                            id: 'subsidyRequestStatus',
                                                            view: 'text',
                                                            label: 'Статус заявки',
                                                            labelPosition: 'top',
                                                            value: data.subsidyRequestStatus.name,
                                                        },
                                                        {
                                                            cols: [
                                                                {
                                                                    view: 'button',
                                                                    id: 'refuseButton',
                                                                    value: 'Отклонить',
                                                                    on: {
                                                                        onAfterRender: () => {
                                                                            if (data.subsidyRequestStatus.code !== 'SUBMIT' && data.subsidyRequestStatus.code !== 'NEW') {
                                                                                $$('refuseButton').disable();
                                                                            }
                                                                        }
                                                                    },
                                                                    click: () => changeRequestSubsidyStatus(false, data.id),
                                                                },
                                                                {
                                                                    view: 'button',
                                                                    id: 'approveButton',
                                                                    value: 'Утвердить',
                                                                    on: {
                                                                        onAfterRender: () => {
                                                                            if (data.subsidyRequestStatus.code !== 'SUBMIT' && data.subsidyRequestStatus.code !== 'NEW') {
                                                                                $$('approveButton').disable();
                                                                            }
                                                                        }
                                                                    },
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

// function check_request_subsidy_files_signatures() {
//     let params = {
//         id_request: ID,
//     }
//     webix.ajax().get(`../check_request_subsidy_files_signatures`, params).then((response) => {
//         let responseJson = response.json();
//         if (responseJson.status === "ok") {
//             verify_progress(params.id_request, "До начала проверки подписей не менее "); //show progress on start event
//             let timerId = setInterval(() => {
//                 verify_progress(params.id_request, responseJson.cause, timerId);
//             }, 4000)
//         } else {
//             webix.message(responseJson.cause, responseJson.status, 4000);
//         }
//     });
// }

//ProgressBar event
function verify_progress(id, queueTime, timerId = null) {
    let progressBar = $$("progress_bar");
    if (id != null && progressBar !== undefined) {
        webix.extend(progressBar, webix.ProgressBar);
        webix.ajax()
            .headers({ 'Content-Type': 'application/json' })
            .get(`../check_signature_files_verify_progress`, { id_request: id })
            .then(async(response) => {
                let data = response.json();
                if (data.notFound) {
                    if (timerId == null) {
                        progressBar.hideProgress();
                        document.getElementById("progress_bar_text").innerHTML = "";
                    }
                    clearInterval(timerId)
                } else {
                    let verified = data.verified;
                    let numberOfFiles = data.numberOfFiles;
                    let progress = verified / numberOfFiles;

                    await progressBar.showProgress({type: "top", position: progress === 0 ? 0.001 : progress})

                    if (progress !== 0) {
                        document.getElementById("progress_bar_text").innerHTML =
                            "<span style='position: absolute; margin-top: 8px; left: 10px; z-index: 100; font-weight: 500'>" +
                            "Проверено: " + verified + "/" + numberOfFiles +
                            "</span>";
                        progress === 1 ? webix.message("Началась проверка подписей", "", 2000) : null;
                        let dataViews = $$('filesListViewByType').getChildViews();
                        console.dir({ dataInProgressBar: data });
                        dataViews.forEach((dataView) => {
                            console.dir({ dataView: dataView.qf ?? '' });
                            // let dataViewId = dataView.qf[1].id;
                            // updateDataview(dataViewId.slice(0, -9), $$(dataViewId).config.formData.fileTypeId)
                        })
                    } else {
                        document.getElementById("progress_bar_text").innerHTML =
                            "<span style='position: absolute; margin-top: 8px; left: 10px; z-index: 100; font-weight: 500'>" +
                            "Проверено: " + verified + "/" + numberOfFiles + " (" + queueTime + ")" +
                            "</span>";
                    }

                    console.dir({ verified, numberOfFiles, progress });
                    if (numberOfFiles === verified) {
                        clearInterval(timerId);
                        webix.message("Проверка подписей завершена", "success", 5000);
                    }

                }
            })
    } else {
        clearInterval(timerId);
    }
}

function changeRequestSubsidyStatus(approve, id_request_subsidy) {
    const params = {
        resolutionComment: $$('resolutionComment').getValue(),
    };

    webix.ajax()
        .headers({ 'Content-Type': 'application/json' })
        .post(`../change_request_subsidy_status/${ id_request_subsidy }/${ approve }`, params)
        .then((data) => {
            const parseData = data.json();
            if ((typeof parseData.success === 'string' && parseData.success === 'true') || (typeof parseData.success === 'boolean' && parseData.success)) {
                $$('subsidyRequestStatus').setValue(parseData.status);
                $$('refuseButton').disable();
                $$('approveButton').disable();
            }
        });
}

function getVerificationStatus(verificationStatusId, idUser, idPrincipal) {
    const verificationData = webix.ajax().sync()
        .get('../find_verification_request_subsidy_signature_file/' + verificationStatusId, { idUser: idUser === -1 ? null : idUser, idPrincipal: idPrincipal === -1 ? null : idPrincipal });

    const jsonResponse = JSON.parse(verificationData.responseText);

    if (!jsonResponse || ! verificationData) {
        return;
    }

    $$('filesListViewByType').hide();
    $$('verifyFilesButton').hide();

    const newDateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s");
    if (jsonResponse.timeCreate != null) {
        jsonResponse.timeCreate = newDateFormat(new Date(jsonResponse.timeCreate));
    }

    if (jsonResponse.timeBeginVerification != null) {
        jsonResponse.timeBeginVerification = newDateFormat(new Date(jsonResponse.timeBeginVerification));
    }

    if (jsonResponse.timeEndVerification != null) {
        jsonResponse.timeEndVerification = newDateFormat(new Date(jsonResponse.timeEndVerification));
    }

    if (jsonResponse.verifyStatus != null) {
        jsonResponse.verifyStatus = getVerifyStatusStringByIntStatus(jsonResponse.verifyStatus);
    }

    if (jsonResponse.user != null) {
        jsonResponse.owner = jsonResponse.user.fullName;
    } else if (jsonResponse.principal != null) {
        jsonResponse.owner = jsonResponse.principal.organization.name;
    }

    webix.ui({
        id: 'verificationFileInfoView',
        rows: [
            view_section('Электронная подпись файла'),
            {
                view: 'scrollview',
                scroll: 'y',
                id: 'show_layout',
                autowidth: true,
                autoheight: true,
                body: {
                    rows: [
                        {
                            view: 'form',
                            id: 'subsidyFormId',
                            autoheight: true,
                            rows: [
                                {view: 'text', value: jsonResponse.owner, label: 'Проверено:', labelPosition: 'top', name: 'verifyOwner', readonly: true,},
                                {view: 'text', value: jsonResponse.verifyStatus, label: 'Статус верификации', labelPosition: 'top', name: 'verifyStatus', readonly: true,},
                                {
                                    view: 'label',
                                    label: 'Результат верификации',
                                    height: 22,
                                },
                                {
                                    view: 'template',
                                    name: 'verifyResult',
                                    autoheight: true,
                                    readonly: true,
                                    template: () => {
                                        return `<div id="verify_result_template">${ jsonResponse.verifyResult }</div>`;
                                    }
                                },
                                {view: 'text', value: jsonResponse.timeCreate, label: 'Дата создания', labelPosition: 'top', name: 'timeCreate', readonly: true,},
                                {view: 'text', value: jsonResponse.timeBeginVerification, label: 'Дата начала верификации', labelPosition: 'top', name: 'timeBeginVerification', readonly: true,},
                                {view: 'text', value: jsonResponse.timeEndVerification, label: 'Дата окончания верификации', labelPosition: 'top', name: 'timeEndVerification', readonly: true,},
                                {
                                    cols: [
                                        {},
                                        // {
                                        //     view: 'button',
                                        //     maxWidth: 200,
                                        //     css: 'webix_primary',
                                        //     value: 'Проверка подписи',
                                        //     click: () => {
                                        //         console.log('check');
                                        //     },
                                        // },
                                        {
                                            view: 'button',
                                            align: 'right',
                                            maxWidth: 200,
                                            css: 'webix_secondary',
                                            value: 'Назад',
                                            click: () => {
                                                $$('verificationFileInfoView').hide();
                                                $$('filesListViewByType').show();
                                                $$('verifyFilesButton').show();
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
    }, $$('verificationFileInfoView'))
}