const requestSubsidyCntByOkvedsReport = {
    body: {
        type: "space",
        autowidth: true,
        cols: [
            {
                id: 'mainId',
                rows: [
                    {
                        cols: [
                            {
                                view: 'datepicker',
                                id: 'startDateReport',
                                label: 'Дата с:',
                                labelWidth: 60,
                                timepicker: false,
                                maxWidth: 250,
                            },
                            {
                                view: 'datepicker',
                                id: 'endDateReport',
                                label: 'по:',
                                labelWidth: 30,
                                timepicker: false,
                                maxWidth: 250,
                            },
                            {gravity: 0.01},
                            {
                                view: 'button',
                                id: 'generateReport',
                                value: 'Сформировать',
                                align: 'right',
                                maxWidth: 200,
                                css: 'webix_primary',
                                click: function () {
                                    let okvedPaths = $$('okvedTreeId').getChecked().toString();

                                    if (okvedPaths != "") {
                                        let params = {
                                            okvedPaths: okvedPaths,
                                            startDateReport: $$('startDateReport').getValue(),
                                            endDateReport: $$('endDateReport').getValue(),
                                        };
                                        webix.ajax().get('generate_request_subsidy_cnt_by_okveds_report', params).then(function (data) {
                                            if (data.text() != null) {
                                                let tmlpt = $$('templateReportId');
                                                tmlpt.$view.childNodes[0].setAttribute('style', 'width:100%');
                                                tmlpt.setHTML(data.text());
                                                webix.message("Сформировано", 'success');
                                            } else {
                                                webix.message("Не удалось сформировать", 'error');
                                            }
                                        });
                                    } else {
                                        webix.message("Выберите ОКВЭДы", 'error')
                                    }

                                },
                            },
                            {
                                view: 'icon',
                                icon: 'fas fa-file-excel',
                                css: 'xlsIcon',
                                tooltip: 'Сформировать и скачать в xlsx формате',
                                click: function () {
                                    let okvedPaths = $$('okvedTreeId').getChecked().toString();
                                    if (okvedPaths != "") {
                                        let startDateReport = webix.i18n.fullDateFormatStr($$('startDateReport').getValue());
                                        let endDateReport = webix.i18n.fullDateFormatStr($$('endDateReport').getValue());

                                        let url = 'request_subsidy_cnt_by_okveds/xlsx/params?okvedPaths=' + okvedPaths;
                                        // if (startDateReport != "") {
                                        url = url + "&startDateReport=" + startDateReport
                                        // }
                                        // if (endDateReport != "") {
                                        url = url + "&endDateReport=" + endDateReport
                                        // }
                                        webix.ajax().response("blob").get(url, function (text, data, xhr) {
                                            webix.html.download(data, "request_subsidy_count_by_okved.xlsx");
                                        })
                                    } else {
                                        webix.message("Выберите ОКВЭДы", 'error')
                                    }
                                },
                            },
                        ]
                    },
                    {
                        id: 'templateReportId',
                        view: 'template',
                        css: 'jr_link',
                        scroll: "xy"
                    }
                ]
            },
            {
                header: 'Фильтр по ОКВЭД',
                collapsed: false,
                body: {
                    rows: [
                        tree('okvedTreeId'),
                    ],
                },
                on: {
                    onItemClick: function () {
                        $$('mainId').resize();
                        // $$('templateReportId').resize();
                        let tmlpt =  $$('templateReportId');
                        tmlpt.$view.childNodes[0].setAttribute('style','width:100%');
                        tmlpt.resize();
                    }
                },
            },

        ]
    }
}
