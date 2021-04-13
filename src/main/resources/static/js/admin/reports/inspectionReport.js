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
                                }
                            });
                        },
                    },
                ]
            },
            {
                view: 'scrollview',
                scroll: 'xy',
                body: {
                    rows: [
                        {
                            id: 'templateInspectionReportId',
                            view: 'template',
                            autoheight: true,
                            autowidth:  true,
                        }
                    ]
                }
            }
        ]

    }
}