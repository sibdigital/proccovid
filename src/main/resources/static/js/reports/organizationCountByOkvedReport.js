const organizationCountByOkvedReport = {
    body: {
        type: "space",
        autowidth: true,
        rows: [
            {
                view: 'accordion',
                multi:true,
                rows: [
                    {
                        cols: [
                            {
                                view: 'datepicker',
                                id: 'requestTimeCreate',
                                label: 'Дата подачи заявки:',
                                labelPosition: 'top',
                                timepicker: true,
                            },
                            {
                                view: 'richselect',
                                id: 'statusReview',
                                name: 'statusReview',
                                width: 450,
                                label: 'Статус заявки',
                                labelPosition: 'top',
                                css: 'smallText',
                                placeholder: 'Выберите статус',
                                options: 'review_statuses',
                            },
                            {}
                        ]
                    },
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
                        id: 'generateReport',
                        value: 'Сформировать',
                        align: 'right',
                        width: 250,
                        css: 'webix_primary',
                        click: function () {
                            let okvedPaths = $$('okvedTreeId').getChecked().toString();
                            let requestTimeCreate = $$('requestTimeCreate').getValue();
                            let statusValue = $$('statusReview').getValue();

                            let params = {
                                okvedPaths: okvedPaths,
                                requestTimeCreate: requestTimeCreate,
                                statusValue: statusValue
                            };
                            webix.ajax().get('generate_org_count_by_okved_report', params).then(function (data) {
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
                            let requestTimeCreate = $$('requestTimeCreate').getValue();
                            let statusValue = $$('statusReview').getValue();
                            // var dateFormat = webix.Date.dateToStr("yyyy-MM-dd HH:mm:ss")
                            let requestTimeCreateString = webix.i18n.fullDateFormatStr(requestTimeCreate);

                            let url = 'org_count_by_okved_report/xlsx/params?okvedPaths='+okvedPaths+'&requestTimeCreate='+requestTimeCreateString
                                                    +"&statusValue="+statusValue;

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
