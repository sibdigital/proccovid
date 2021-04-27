function reloadOrgs() {
    $$('organizations_table').clearAll();

    var url = 'cls_organizations';
    let params = '';

    var inn = $$('search').getValue();
    if (inn != '') {
        params = '?inn=' + inn;
    }

    var prescription = $$('prescription').getValue();
    if (prescription != '') {
        params += params == '' ? '?' : '&';
        params += 'id_prescription=' + prescription;
    }

    $$('organizations_table').load(url + params);
}

var organizationListForm = {
    type: 'space',
    rows: [
        {
            rows: [
                {
                    view: 'toolbar',
                    rows: [
                        {
                            cols: [
                                {
                                    view: 'search',
                                    id: 'search',
                                    maxWidth: 300,
                                    minWidth: 100,
                                    tooltip: 'После ввода значения нажмите Enter',
                                    placeholder: "Введите ИНН или наименование организации",
                                    on: {
                                        onEnter: function () {
                                            reloadOrgs();
                                        }
                                    }
                                },
                                {
                                    view: 'richselect',
                                    id: 'prescription',
                                    // width: 450,
                                    css: 'smallText',
                                    placeholder: 'Предписание не выбрано',
                                    options: 'cls_prescriptions_short',
                                    on: {
                                        onChange() {
                                            reloadOrgs();
                                        }
                                    }
                                },
                            ]
                        }
                    ]
                },
                {
                    view: 'datatable',
                    id: 'organizations_table',
                    select: 'row',
                    navigation: true,
                    resizeColumn: true,
                    pager: 'Pager',
                    fixedRowHeight: false,
                    datafetch: 25,
                    columns: [
                        {
                            id: "orgName",
                            header: "Наименование организации/ИП",
                            template: "#name#",
                            minWidth: 550,
                            fillspace: true,
                        },
                        {id: "inn", header: "ИНН", template: "#inn#", minWidth: 150, fillspace: true, adjust: true},
                        {
                            id: "ogrn",
                            header: "ОГРН",
                            template: (obj) => {
                                if (obj.ogrn !== null) {
                                    return obj.ogrn;
                                } else {
                                    return "";
                                }
                            },
                            adjust: true
                        },
                        {
                            id: 'activated',
                            header: 'Активирована',
                            adjust: true,
                            template: function (obj, type, value) {
                                if (value) {
                                    return '<span>Да</span>'
                                } else {
                                    return '<span>Нет</span>'
                                }
                            }
                        },],
                    scheme: {
                        $init: function (obj) {
                        },
                    },
                    on: {
                        onBeforeLoad: function () {
                            this.showOverlay("Загружаю...");
                        },
                        onAfterLoad: function () {
                            this.hideOverlay();
                            if (!this.count()) {
                                this.showOverlay("Отсутствуют данные")
                            }
                        },
                        onLoadError: function () {
                            this.hideOverlay();
                        },
                        onItemClick: function (id) {
                            this.hideOverlay();
                        },
                        onItemDblClick: function (id) {
                            let item = $$('organizations_table').getItem(id);
                            $$('nameOrganizationId').setValue(item.name);
                            $$('idOrganization').setValue(item.id);
                            $$('orgWindow').close();
                        },
                        'data->onStoreUpdated': function () {
                            this.adjustRowHeight(null, true);
                        },
                    },
                    url: 'cls_organizations'
                },
                {
                    view: 'pager',
                    id: 'Pager',
                    height: 38,
                    size: 25,
                    group: 5,
                    template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                }
            ]
        }
    ]
}

var organizationRow = {
    id: 'organizationRowId',
    cols: [
        {
            id: 'idOrganization',
            view: 'label',
            hidden: true
        },
        {
            id: 'nameOrganizationId',
            view: 'text',
            placeholder: "Выберите организацию",
            readonly:true,
            on: {
                onItemClick: () => {
                    // webix.message("Клик")

                    let window = webix.ui({
                        view: 'window',
                        id: 'orgWindow',
                        head: 'Выбор организации',
                        close: true,
                        width: 1000,
                        height: 800,
                        position: 'center',
                        modal: true,
                        body: organizationListForm,
                        on: {
                            'onHide': function() {
                                window.destructor();
                            }
                        }

                    });
                    window.show();
                }
            }
        },
        {gravity:0.5},
    ]
}

var controlAuthorityRow = {
    id: 'controlAuthorityRowId',
    hidden: true,
    cols: [
        {
            view: 'richselect',
            id: 'authorityRichselectId',
            options: {
                view: 'suggest',
                body: {
                    view: 'list',
                    css: 'multiline', // чтобы в моб версии было понятны, что за службы
                    type: {
                        autoheight: true,
                    },
                    url: 'control_authorities_list_short',
                }
            },
            placeholder: 'Выберите проверяющий орган'
        },
        {gravity:0.5},
    ]
}

const inspectionCountReport = {
    body: {
        type: "space",
        autowidth: true,
        rows: [
            {
                view: "radio",
                id: 'typeReportId',
                label:"Отчет о количестве проверок", labelPosition:"top",
                value:1,
                options:[
                    { id:1, value:"по организации" }, // the initially selected item
                    { id:2, value:"по проверяющему органу" }
                ],
                on: {
                    onItemClick: function(id, e) {
                        if ($$('typeReportId').getValue() == "1") {
                            $$('controlAuthorityRowId').hide();
                            $$('organizationRowId').show();
                        } else  if ($$('typeReportId').getValue() == "2"){
                            $$('controlAuthorityRowId').show();
                            $$('organizationRowId').hide();
                        }
                    }
                }
            },
            controlAuthorityRow,
            organizationRow,
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
                            if (!validateForm()) {
                                webix.message("Не выбран обязательный параметр: организация или орган")
                            } else {
                                let params = {
                                    minDate: $$('startDateInspectionReport').getValue(),
                                    maxDate: $$('endDateInspectionReport').getValue(),
                                    minCnt: $$('minCountInspectionReport').getValue(),
                                    typeRecord: $$('typeReportId').getValue(),
                                    idOrganization: $$('idOrganization').getValue(),
                                    idAuthority: $$('authorityRichselectId').getValue()
                                };
                                webix.ajax().get('generate_count_inspection_report', params).then(function (data) {
                                    if (data.text() != null) {
                                        let tmlpt = $$('templateInspectionReportId');
                                        tmlpt.$view.childNodes[0].setAttribute('style', 'width:100%');
                                        tmlpt.setHTML(data.text());
                                        webix.message("Сформировано", 'success');
                                    } else {
                                        webix.message("Не удалось сформировать", 'error');
                                    }
                                });
                            }
                        },
                    },
                    {
                        view: 'icon',
                        icon: 'fas fa-file-excel',
                        css: 'xlsIcon',
                        tooltip: 'Сформировать и скачать в xlsx формате',
                        click: function () {
                            if (!validateForm()) {
                                webix.message("Не выбран обязательный параметр: организация или орган")
                            } else {
                                let typeRecord = $$('typeReportId').getValue();
                                let minDate = convertDateToString($$('startDateInspectionReport').getValue());
                                let maxDate = convertDateToString($$('endDateInspectionReport').getValue());
                                let minCnt = $$('minCountInspectionReport').getValue();
                                let idOrganization = $$('idOrganization').getValue();
                                let idAuthority = $$('authorityRichselectId').getValue();

                                let url = 'inspectionCountReport/xlsx/params?minDate=' + minDate + '&maxDate=' + maxDate + '&minCnt=' + minCnt + '&idOrganization=' + idOrganization + '&idAuthority=' + idAuthority + '&typeRecord=' + typeRecord;

                                webix.ajax().response("blob").get(url, function (text, data, xhr) {
                                    webix.html.download(data, "inspectionReport.xlsx");
                                });
                            }
                        },
                    },
                ]
            },
            {
                id: 'templateInspectionReportId',
                view: 'template',
                css: 'jr_link',
                scroll: "xy"
            }
        ]

    }
}

function validateForm() {
    var typeRecord = $$('typeReportId').getValue();
    if ((typeRecord == 1) && ($$('idOrganization').getValue() == "")) {
        return false;
    } else if ((typeRecord == 2) && $$('authorityRichselectId').getValue() == "") {
        return false;
    }
   return true;
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