const cntRemoteWithOkvedsReport = {
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
                    {
                        view: 'button',
                        id: 'generateCntRemoteReport',
                        value: 'Сформировать',
                        align: 'right',
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
                            let mainOkveds = $$('mainOkvedTreeId').getChecked().toString();
                            let additionalOkveds = $$('additionalOkvedTreeId').getChecked().toString();
                            let minDate = convertDateToString($$('startDateInspectionReport').getValue());
                            let maxDate = convertDateToString($$('endDateInspectionReport').getValue());
                            let minCnt = $$('minCountInspectionReport').getValue();

                            let url = 'inspectionReport/xlsx/params?minDate='+minDate+'&maxDate='+maxDate+'&minCnt='+minCnt+'&mainOkveds='+mainOkveds +'&additionalOkveds='+additionalOkveds;

                            webix.ajax().response("blob").get(url, function (text, data, xhr) {
                                webix.html.download(data, "inspectionReport.xlsx");
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
