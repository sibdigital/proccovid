const remoteCntReport = {
    body: {
        type: "space",
        autowidth: true,
        rows: [
            {
                cols: [
                    {
                        view: 'datepicker',
                        id: 'reportDate',
                        label: 'Дата:',
                        labelWidth: 70,
                        timepicker: true,
                    },
                    {},
                    {
                        view: 'button',
                        id: 'generateRemoteCntReport',
                        value: 'Сформировать',
                        align: 'right',
                        css: 'webix_primary',
                        click: function () {
                            let tmlpt = $$('templateReportId');
                            tmlpt.setHTML("");

                            if ($$('reportDate').getValue() != null) {
                                let params = {
                                    reportDate: $$('reportDate').getValue(),
                                };
                                webix.ajax().get('generate_remote_cnt_report', params).then(function (data) {
                                    if (data.text() != null) {
                                        tmlpt.$view.childNodes[0].setAttribute('style', 'width:100%');
                                        tmlpt.setHTML(data.text());
                                        webix.message("Сформировано", 'success');
                                    } else {
                                        webix.message("Не удалось сформировать", 'error');
                                    }
                                });
                            } else {
                                webix.message("Укажите дату", 'error');
                            }
                        },
                    },
                    {
                        view: 'icon',
                        icon: 'fas fa-file-excel',
                        css: 'xlsIcon',
                        tooltip: 'Сформировать и скачать в xlsx формате',
                        click: function () {
                            if ($$('reportDate').getValue() != null) {
                                let reportDate = convertDateWithTimeToString($$('reportDate').getValue());
                                let url = 'remoteCntReport/xlsx/params?reportDate=' + reportDate;

                                webix.ajax().response("blob").get(url, function (text, data, xhr) {
                                    webix.html.download(data, "remoteCount.xlsx");
                                });
                            } else {
                                webix.message("Укажите дату", 'error');
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

    }
}

function convertDateWithTimeToString(date){
    if (date == null) {
        return "";
    } else {
        let yyyy = ye = new Intl.DateTimeFormat('en', { year: 'numeric' }).format(date);
        let MM = new Intl.DateTimeFormat('en', { month: '2-digit' }).format(date);
        let dd = new Intl.DateTimeFormat('en', { day: '2-digit' }).format(date);
        let HH = ("0" + date.getHours()).slice(-2);
        let mm = new Intl.DateTimeFormat('en', { minute: '2-digit' }).format(date);
        let ss = new Intl.DateTimeFormat('en', { second: '2-digit' }).format(date);

        return yyyy + "-" + MM + "-" + dd + " " + HH + ":" + mm + ":" + ss;
    }
}