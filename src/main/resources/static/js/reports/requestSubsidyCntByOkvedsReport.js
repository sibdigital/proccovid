const requestSubsidyCntByOkvedsReport = {
    body: {
        type: "space",
        autowidth: true,
        rows: [
            {
                view: 'accordion',
                multi:true,
                rows: [
                    {
                        header: 'Фильтр по ОКВЭД',
                        collapsed:true,
                        body: {
                            rows: [
                                tree('okvedTreeId'),
                            ]
                        }
                    }
                ]
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        id: 'generateCntRemoteReport',
                        value: 'Сформировать',
                        align: 'right',
                        width: 250,
                        css: 'webix_primary',
                        click: function () {
                            let okvedPaths = $$('okvedTreeId').getChecked().toString();

                            let params = {
                                okvedPaths: okvedPaths,
                            };
                            webix.ajax().get('generate_cnt_remote_with_okveds_report', params).then(function (data) {
                                if (data.text() != null) {
                                    let tmlpt =  $$('templateReportId');
                                    tmlpt.$view.childNodes[0].setAttribute('style','width:100%');
                                    tmlpt.setHTML(data.text());
                                    webix.message("Сформировано", 'success');
                                } else {
                                    webix.message("Не удалось сформировать", 'error');
                                }
                            });
                        },
                    },
                    {
                        view: 'icon',
                        icon: 'fas fa-file-excel',
                        css: 'xlsIcon',
                        tooltip: 'Сформировать и скачать в xlsx формате',
                        click: function () {
                            let okvedPaths = $$('okvedTreeId').getChecked().toString();

                            let url = 'remoteCntReportByOkveds/xlsx/params?okvedPaths='+okvedPaths;

                            webix.ajax().response("blob").get(url, function (text, data, xhr) {
                                webix.html.download(data, "employee_count_by_okved.xlsx");
                            })
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

    }
}
