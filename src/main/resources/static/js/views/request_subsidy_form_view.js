
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

function getVerificationInfoButton(request = {}, status = "Статус", idDataTable = '0') {
    const id_request_subsidy_file = request?.verificationStatus?.id_request_subsidy_file ?? -1;
    const id_user = request?.verificationStatus?.id_user ?? -1;
    const id_principal = request?.verificationStatus?.id_principal ?? -1;
    const idVerificationSignatureFile = request?.verificationStatus?.id ?? -1;
    idDataTable = '"' + idDataTable + '"';
    return `<button class="verification_request_subsidy_file_signature_status_button" onclick=getVerificationStatus(${ [id_request_subsidy_file, id_user, id_principal, idVerificationSignatureFile, idDataTable].join(',') })>${ status }</button>`
}

function getFileVerificationFieldsByUserAndIOGV(
    userVerId = null,
    userTimeVerification = "",
    userVerificationStatus = "Проверка не проводилась",
    iogvVerId = null,
    iogvTimeVerification = "",
    iogvVerificationStatus = "Проверка не проводилась",
) {
    return {
        userVerId,
        userTimeVerification,
        userVerificationStatus,
        iogvVerId,
        iogvTimeVerification,
        iogvVerificationStatus
    };
}

function getFilesListByTypeView(docRequestSubsidyId) {
    webix.ajax(`../request_subsidy_files_verification/${ docRequestSubsidyId }`).then(function (filesVerification) {
        filesVerification = filesVerification.json();

        const newDateFormat = webix.Date.dateToStr("%H:%i:%s %d.%m.%Y");
        filesVerification.map((file) => {
            file.verify_status = getVerifyStatusStringByIntStatus(file.verify_status);

            if (file.id_user) {
                file.userVerId = file.id;
                file.userTimeVerification = newDateFormat(new Date(file.time_verification));
                file.userVerificationStatus = file.verify_status;
            } else if (file.id_principal) {
                file.iogvVerId = file.id;
                file.iogvTimeVerification = newDateFormat(new Date(file.time_verification));
                file.iogvVerificationStatus = file.verify_status;
            }

            return file;
        });

        webix.ajax(`../request_subsidy_files/${docRequestSubsidyId}`).then(function (data) {
            const views = [];
            data = data.json();

            const dataTablesKeys = [];

            console.dir({ data, filesVerification });

            if (data.length > 0) {
                const filesTypes = {};
                const byFileType = data.reduce(function (result, file) {
                    result[file.fileType.id] = result[file.fileType.id] || [];

                    const fileVerificationStatus = filesVerification.filter((fileVerification) => fileVerification.id_request_subsidy_file === file.id);
                    let fileVerification = {
                        verify_status: 'Проверка не проводилась',
                        ...getFileVerificationFieldsByUserAndIOGV()
                    };

                    fileVerificationStatus.forEach((fileVer, index) => {
                        fileVer.id_request_subsidy_file && !fileVerification.id_request_subsidy_file && Object.assign(fileVerification, { id_request_subsidy_file: fileVer.id_request_subsidy_file });
                        fileVer.id_user && !fileVerification.id_user && Object.assign(fileVerification, { id_user: fileVer.id_user });
                        fileVer.id_principal && !fileVerification.id_principal && Object.assign(fileVerification, { id_principal: fileVer.id_principal });
                        fileVer.download_link && !fileVerification.download_link && Object.assign(fileVerification, { download_link: fileVer.download_link });


                        if (fileVer.id_user) {
                            fileVerification.userVerId = fileVer.id;
                            fileVerification.userTimeVerification = fileVer.userTimeVerification;
                            fileVerification.userVerificationStatus = fileVer.userVerificationStatus;
                        } else if (fileVer.id_principal) {
                            fileVerification.iogvVerId = fileVer.id;
                            fileVerification.iogvTimeVerification = fileVer.iogvTimeVerification;
                            fileVerification.iogvVerificationStatus = fileVer.iogvVerificationStatus;
                        }
                    });
                    console.dir({ fileVerificationStatus, fileVerification });

                    if (fileVerificationStatus.length === 0) {
                        result[file.fileType.id].push({
                            ...file,
                            verificationStatus: fileVerificationStatus ?? {
                                verify_status: 'Проверка не проводилась',
                                ...getFileVerificationFieldsByUserAndIOGV()
                            }
                        });
                    } else {
                        result[file.fileType.id].push({
                            ...file,
                            verificationStatus: fileVerification
                        });
                    }

                    filesTypes[file.fileType.id] = file.fileType.name;

                    return result;
                }, Object.create(null));

                console.dir({ byFileType });

                for (const [key, filesArray] of Object.entries(byFileType)) {
                    dataTablesKeys.push(key);
                    views.push({
                        rows: [
                            view_section(filesTypes[key]),
                            {
                                id: `request_subsidy_files_table/${key}`,
                                view: 'datatable',
                                pager: `Pager/${key}`,
                                autoheight: true,
                                // autowidth: true,
                                header: `id = ${key}`,
                                resizeColumn: true,
                                readonly: true,
                                data: filesArray,
                                fixedRowHeight: false,
                                scrollX: false,
                                autoConfig: true,
                                rowLineHeight: 25,
                                rowHeight: 60,
                                resizeRow: true,
                                headerRowHeight: 65,
                                columns: [
                                    {
                                        id: 'originalFileName',
                                        // header: 'Название файла',
                                        header: [{ text: 'Название файла', css: 'request_subsidy_files_table_header' }],
                                        adjust: true,
                                        css: 'request_subsidy_files_table_webix_cell',
                                        maxWidth: 260,
                                        sort: 'string',
                                        template: (request) => {
                                            return `<div class='download_docs'>
                                                        <a target="_blank" rel="noopener noreferrer" style='text-decoration: none; color: #1ca1c1' href="${ LINK_PREFIX }${ request.attachmentPath }${ LINK_SUFFIX }" download>${ request.originalFileName }</a>
                                                    </div>`
                                        }
                                    },
                                    {
                                        id: 'viewFileName',
                                        // header: 'Описание',
                                        header: [{ text: 'Описание', css: 'request_subsidy_files_table_header' }],
                                        css: 'request_subsidy_files_table_webix_cell',
                                        adjust: true,
                                        maxWidth: 260,
                                    },
                                    {
                                        id: 'signature',
                                        // header: 'Подпись',
                                        header: [{ text: 'Подпись', css: 'request_subsidy_files_table_header' }],
                                        css: 'request_subsidy_files_table_webix_cell',
                                        adjust: true,
                                        sort: 'string',
                                        maxWidth: 120,
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

                                            if (request.signature) {
                                                return `<div class='download_docs verification_request_subsidy_file_signature_status_button'>
                                                        <a target="_blank" rel="noopener noreferrer" style='text-decoration: none; color: green' href="${ LINK_PREFIX }${ request.verificationStatus.download_link }${ LINK_SUFFIX }" download>${ label }</a>
                                                   </div>`
                                            } else {
                                                return `<div style="${style}" role="gridcell" aria-rowindex="1" aria-colindex="1" aria-selected="true" tabindex="0" class="webix_cell webix_row_select">${label}</div>`;
                                            }
                                        },
                                    },
                                    {
                                        id: 'userTimeVerification',
                                        // header: 'Дата проверки пользователем',
                                        header: [{ text: 'Дата проверки пользователем', css: 'request_subsidy_files_table_header' }],
                                        css: 'request_subsidy_files_table_webix_cell',
                                        adjust: true,
                                        maxWidth: 120,
                                        template: '#verificationStatus.userTimeVerification#',
                                    },
                                    {
                                        id: 'userVerificationStatus',
                                        // header: 'Результат проверки пользователем',
                                        header: [{ text: 'Результат проверки пользователем', css: 'request_subsidy_files_table_header' }],
                                        css: 'request_subsidy_files_table_webix_cell',
                                        adjust: true,
                                        maxWidth: 140,
                                        template: (request) => {
                                            return getVerificationInfoButton(request, request.verificationStatus.userVerificationStatus, `request_subsidy_files_table/${key}`);
                                        }
                                    },
                                    {
                                        id: 'iogvTimeVerification',
                                        // header: 'Дата проверки ИОГВ',
                                        header: [{ text: 'Дата проверки ИОГВ', css: 'request_subsidy_files_table_header' }],
                                        css: 'request_subsidy_files_table_webix_cell',
                                        adjust: true,
                                        maxWidth: 110,
                                        template: '#verificationStatus.iogvTimeVerification#',
                                    },
                                    {
                                        id: 'iogvVerificationStatus',
                                        // header: 'Результат проверки ИОГВ',
                                        header: [{ text: 'Результат проверки ИОГВ', css: 'request_subsidy_files_table_header' }],
                                        css: 'request_subsidy_files_table_webix_cell',
                                        adjust: true,
                                        maxWidth: 140,
                                        template: (request) => {
                                            return getVerificationInfoButton(request, request.verificationStatus.iogvVerificationStatus, `request_subsidy_files_table/${key}`);
                                        }
                                    },
                                ],
                                on: {
                                    onBeforeLoad: function () {
                                        this.showOverlay("Загружаю...");
                                    },
                                    onAfterLoad: function () {
                                        this.hideOverlay();
                                        if (!this.count()) {
                                            this.showOverlay("Отсутствуют данные");
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
                            autoheight: true,
                            select: 'row',
                            resizeColumn: true,
                            readonly: true,
                            data: [],
                        }
                    ]
                })
            }

            webix.ui({
                id: 'filesListViewByType',
                rows: views,
            }, $$('filesListViewByType'));

            dataTablesKeys.forEach((key) => $$(`request_subsidy_files_table/${ key }`).adjustRowHeight('originalFileName'));
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
                                        css: { 'overflow': 'hidden !important;', 'padding': '10px' },
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
                                        css: { 'overflow': 'hidden !important;', 'padding': '10px' },
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
                                                                        if (!ID) {
                                                                            webix.message("Не найдена заявка", "error", 4000);
                                                                            return;
                                                                        }

                                                                        webix.ajax('../verification_request_subsidy_signature_files/' + ID).then(() => {
                                                                            //show progress on start event
                                                                            verify_progress(ID, "До начала проверки подписей не менее ");
                                                                            let timerId = setInterval(() => {
                                                                                verify_progress(ID, "До начала проверки подписей не менее ", timerId);
                                                                            }, 4000);
                                                                        });
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
                                        css: { 'overflow': 'hidden !important;', 'padding': '10px' },
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
                        getFilesListByTypeView(ID);
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

function getVerificationStatus(idRequestSubsidyFile, idUser, idPrincipal, idVerificationSignatureFile, idDataTable = '0') {
    const verificationData = webix.ajax().sync()
        .get('../find_verification_request_subsidy_signature_file/' + idRequestSubsidyFile, {
            idUser: idUser === -1 ? null : idUser,
            idPrincipal: idPrincipal === -1 ? null : idPrincipal,
            id: idVerificationSignatureFile === -1 ? null : idVerificationSignatureFile,
        });

    let jsonResponse = JSON.parse(verificationData?.responseText ?? 'null');

    if (!jsonResponse || !jsonResponse.length || jsonResponse.length === 0) {
        webix.message("Не найдены результаты проверки", "error", 4000);
        jsonResponse = [{}];
    }

    const pageScrollX = window.scrollX;
    const pageScrollY = window.scrollY;


    $$('filesListViewByType').hide();
    $$('verifyFilesButton').hide();

    const newDateFormat = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s");

    jsonResponse.map((file) => {
        if (file.timeCreate != null) {
            file.timeCreate = newDateFormat(new Date(file.timeCreate));
        }

        if (file.timeBeginVerification != null) {
            file.timeBeginVerification = newDateFormat(new Date(file.timeBeginVerification));
        }

        if (file.timeEndVerification != null) {
            file.timeEndVerification = newDateFormat(new Date(file.timeEndVerification));
        }

        if (file.verifyStatus != null) {
            file.verifyStatus = getVerifyStatusStringByIntStatus(file.verifyStatus);
        }

        if (file.user != null) {
            file.owner = file.user.fullName;
        } else if (file.principal != null) {
            file.owner = file.principal.organization.name;
        }

        return file;
    });

    console.dir({ jsonResponse });

    const verificationInfoViews = jsonResponse.map((file) => {
        return {
            rows: [
                {
                    cols: [
                        { view: 'text', value: file.owner ?? '', tooltip: file.owner ?? '', label: 'Проверено:', labelPosition: 'top', name: 'verifyOwner', readonly: true },
                        { view: 'text', value: file.verifyStatus ?? '', label: 'Результат проверки', labelPosition: 'top', name: 'verifyStatus', readonly: true },
                        { view: 'text', value: file.timeEndVerification ?? '', label: 'Дата проверки', labelPosition: 'top', name: 'timeEndVerification', readonly: true },
                    ]
                },
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
                        return `<div id="verify_result_template">${ file.verifyResult ?? '' }</div>`;
                    }
                }
            ]
        };
    });

    // console.dir({
    //     verificationInfoViews,
    //     list: verificationInfoViews.map((view) => {
    //         return view;
    //     }),
    // });

    webix.ui({
        id: 'verificationFileInfoView',
        rows: [
            view_section('Электронная подпись файла'),
            {
                view: 'scrollview',
                scroll: 'y',
                css: { 'overflow': 'hidden !important;', 'padding': '10px' },
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
                                ...verificationInfoViews.map((view) => {
                                    return view;
                                }),
                                {
                                    cols: [
                                        {},
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

                                                window.scrollTo(pageScrollX, pageScrollY);
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
