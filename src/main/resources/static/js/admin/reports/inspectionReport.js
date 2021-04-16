let mainOkvedTree = {
    view: "tree",
    id: "mainOkvedTreeId",
    template: "{common.icon()} {common.checkbox()} {common.folder()} <span>#value#</span>",
    threeState: true,
    data: [
        {
            id: 'root',
            value: 'ОКВЭДЫ',
            // open: true,
            data: [
                {
                    id: '2014',
                    value: '2014',
                    webix_kids: true
                },
                {
                    id: '2001',
                    value: '2001',
                    webix_kids: true
                }
            ]
        }
    ],
    on: {
        onDataRequest: (parentNode) => {
            $$('mainOkvedTreeId').parse(
                webix.ajax().get(
                    'okved_tree', {parent_node: parentNode}
                ).then((data) => {
                    data = {
                        parent: parentNode,
                        data: data.json().map((e) => {
                            return {
                                id: e.id,
                                value: e.value,
                                webix_kids: true
                            }
                        })
                    };
                    return data;
                })
            );
            return false;
        }
    }
}

let additionalOkvedTree = {
    view: "tree",
    id: "additionalOkvedTreeId",
    template: "{common.icon()} {common.checkbox()} {common.folder()} <span>#value#</span>",
    threeState: true,
    data: [
        {
            id: 'root',
            value: 'ОКВЭДЫ',
            // open: true,
            data: [
                {
                    id: '2014',
                    value: '2014',
                    webix_kids: true
                },
                {
                    id: '2001',
                    value: '2001',
                    webix_kids: true
                }
            ]
        }
    ],
    on: {
        onDataRequest: (parentNode) => {
            $$('additionalOkvedTreeId').parse(
                webix.ajax().get(
                    'okved_tree', {parent_node: parentNode}
                ).then((data) => {
                    data = {
                        parent: parentNode,
                        data: data.json().map((e) => {
                            return {
                                id: e.id,
                                value: e.value,
                                webix_kids: true
                            }
                        })
                    };
                    return data;
                })
            );
            return false;
        }
    }
}


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
                            let mainOkveds = $$('mainOkvedTreeId').getChecked().toString();
                            let additionalOkveds = $$('additionalOkvedTreeId').getChecked().toString();

                            let params = {
                                minDate: $$('startDateInspectionReport').getValue(),
                                maxDate: $$('endDateInspectionReport').getValue(),
                                minCnt:  $$('minCountInspectionReport').getValue(),
                                mainOkveds: mainOkveds,
                                additionalOkveds: additionalOkveds,
                            };
                            webix.ajax().get('generate_inspection_report', params).then(function (data) {
                                if (data.text() != null) {
                                    let tmlpt =  $$('templateInspectionReportId');
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
                view: 'accordion',
                multi:true,
                rows: [
                    {
                        header: 'Фильтр по основным ОКВЭД',
                        collapsed:true,
                        body: {
                            rows: [
                                mainOkvedTree,
                            ]
                        }
                    }
                ]
            },
            {
                view: 'accordion',
                multi:true,
                rows: [
                    {
                        header: 'Фильтр по дополнительным ОКВЭД',
                        collapsed:true,
                        body: {
                            rows: [
                                additionalOkvedTree,
                            ]
                        }
                    }
                ]
            },
            {
                id: 'templateInspectionReportId',
                view: 'template',
                scroll: "xy"
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