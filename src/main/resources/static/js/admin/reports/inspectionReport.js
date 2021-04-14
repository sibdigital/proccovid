const inspectionReport = {
    body: {
        type: "space",
        autowidth: true,
        rows: [
            {
                cols: [
                    {
                        view: 'datepicker',
                        id: 'startDateInspectionReport',
                        label: 'Дата с:',
                        labelWidth: 70,
                        timepicker: false,
                    },
                    {
                        view: 'datepicker',
                        id: 'endDateInspectionReport',
                        label: 'по:',
                        labelWidth: 30,
                        timepicker: false,
                    },
                    {gravity: 0.1},
                    {
                        view: 'text',
                        id: 'minCountInspectionReport',
                        label: 'Мин. кол-во:',
                        labelWidth: 100,
                        // width: 300,
                    },
                    {gravity: 0.1},
                    {
                        view: 'button',
                        id: 'generateInspectionReport',
                        value: 'Сформировать',
                        align: 'right',
                        css: 'webix_primary',
                        click: function () {
                            let params = {
                                minDate: $$('startDateInspectionReport').getValue(),
                                maxDate: $$('endDateInspectionReport').getValue(),
                                minCnt:  $$('minCountInspectionReport').getValue(),
                            };
                            webix.ajax().get('generate_inspection_report', params).then(function (data) {
                                if (data.text() != null) {
                                    $$('templateInspectionReportId').setHTML(data.text());
                                    $$('templateInspectionReportId').resize();
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
                            let minDate = convertDateToString($$('startDateInspectionReport').getValue());
                            let maxDate = convertDateToString($$('endDateInspectionReport').getValue());
                            let minCnt = $$('minCountInspectionReport').getValue();

                            let url = 'inspectionReport/xlsx/params?minDate='+minDate+'&maxDate='+maxDate+'&minCnt='+minCnt;

                            webix.ajax().response("blob").get(url, function (text, data, xhr) {
                                webix.html.download(data, "inspectionReport.xlsx");
                            })
                        },
                    },
                ]
            },
            {
                // view: 'scrollview',
                // id: 'scrollTemplateId',
                // scroll: 'xy',
                // autowidth: true,
                // body: {
                    rows: [
                        {
                            id: 'templateInspectionReportId',
                            view: 'template',
                            scroll: "xy"
                        }
                    ]
                // }
            }
        ]

    }
}

function convertDateToString(date){
    if (date == null) {
        return "";
    } else {
        let yyyy = ye = new Intl.DateTimeFormat('en', { year: 'numeric' }).format(date);
        let MM = new Intl.DateTimeFormat('en', { month: '2-digit' }).format(date);
        let dd = new Intl.DateTimeFormat('en', { day: '2-digit' }).format(date);
        return yyyy + "-" + MM + "-" + dd;
    }
}