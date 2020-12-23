function processFiles() {
    webix.confirm('Вы действительно хотите запустить загрузку ЕГРЮЛ?')
        .then(
            function () {
                // webix.ajax().sync().get('/process_egrul_egrip_files');
                // webix.ajax().get('/process_egrul_egrip_files');
                webix.ajax().get('/process_egrul_files', )
                    .then(function (data) {
                    if (data.text() === 'Ok') {
                        webix.message({
                            text: 'Запущена загрузка',
                            type: 'success'
                        });
                    } else {
                        webix.message({
                            text: 'Не удалось запустить загрузку',
                            type: 'error'
                        });
                    }
                })
            }
        )
}
function processFilesEgrip() {
    webix.confirm('Вы действительно хотите запустить загрузку ЕГРИП?')
        .then(
            function () {
                webix.ajax().get('/process_egrip_files', )
                    .then(function (data) {
                        if (data.text() === 'Ok') {
                            webix.message({
                                text: 'Запущена загрузка',
                                type: 'success'
                            });
                        } else {
                            webix.message({
                                text: 'Не удалось запустить загрузку',
                                type: 'error'
                            });
                        }
                    })
            }
        )
}
function processFullZipFias() {
    webix.confirm('Вы действительно хотите запустить полную загрузку ФИАС?')
        .then(
            function () {
                webix.ajax().get('/process_fias_zip_full', )
                    .then(function (data) {
                        if (data.text() === 'Ok') {
                            webix.message({
                                text: 'Запущена загрузка',
                                type: 'success'
                            });
                        } else {
                            webix.message({
                                text: 'Не удалось запустить загрузку',
                                type: 'error'
                            });
                        }
                    })
            }
        )
}

function processUpdatesZipFias() {
    webix.confirm('Вы действительно хотите запустить загрузку обновлений ФИАС?')
        .then(
            function () {
                webix.ajax().get('/process_fias_files', )
                    .then(function (data) {
                        if (data.text() === 'Ok') {
                            webix.message({
                                text: 'Запущена загрузка',
                                type: 'success'
                            });
                        } else {
                            webix.message({
                                text: 'Не удалось запустить загрузку',
                                type: 'error'
                            });
                        }
                    })
            }
        )
}

const fias = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                id: 'formFias',
                view: 'form',
                complexData: true,
                rows: [
                    view_section('Загрузка ФИАС'),
                    {
                        cols: [
                            {
                                view: 'button',
                                value: 'Загрузка zip ФИАС через scheduler (первая полная)',
                                align: 'left',
                                maxWidth: 400,
                                css: 'webix_primary',
                                click: processFullZipFias
                            },
                            {
                                view: 'button',
                                value: 'Загрузка обновлений zip ФИАС',
                                align: 'left',
                                maxWidth: 400,
                                css: 'webix_primary',
                                click: processUpdatesZipFias
                            },
                            {},
                        ]
                    }
                ]
            },
            {
                id: 'formEgrulEgrip',
                view: 'form',
                complexData: true,
                rows: [
                    view_section('Загрузка ЕГРЮЛ/ЕГРИП'),
                    {
                        cols: [
                            {
                                view: 'button',
                                value: 'Загрузка ЕГРЮЛ',
                                align: 'left',
                                maxWidth: 400,
                                css: 'webix_primary',
                                click: processFiles
                            },
                            {
                                view: 'button',
                                value: 'Загрузка ЕГРИП',
                                align: 'left',
                                maxWidth: 400,
                                css: 'webix_primary',
                                click: processFilesEgrip
                            },
                            {
                                view: 'button',
                                value: 'Получить данные таблицы миграции',
                                align: 'left',
                                maxWidth: 400,
                                css: 'webix_primary',
                                click: function () {
                                    window.open('/migration_data');
                                }
                            }
                        ],
                    },
                ]
            },
        ]
    }
}