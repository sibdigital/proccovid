
const adminRequests = {
    view: 'scrollview',
    scroll: 'xy',
    body: {
        type: 'space',
        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
                    {
                        view: 'toolbar',
                        rows: [
                            {
                                view: 'richselect',
                                id: 'department_filter',
                                css: 'smallText',
                                placeholder: 'Все подразделения',
                                options: 'cls_departments',
                                on: {
                                    onChange() {
                                        $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                    }
                                }
                            },
                            {
                                cols: [
                                    {
                                        view: 'segmented', id:'tabbar', value: 'requests', multiview: true,
                                        width: 600,
                                        optionWidth: 150,  align: 'center', padding: 10,
                                        options: [
                                            { value: 'Необработанные', id: 'requests'},
                                            { value: 'Принятые', id: 'accepted'},
                                            { value: 'Отклоненные', id: 'rejected'},
                                            { value: 'Прочие', id: 'other'}
                                        ],
                                        on: {
                                            onAfterRender() {
                                                this.callEvent('onChange', ['requests']);
                                            },
                                            onChange: function (id) {
                                                let status = 0
                                                switch (id) {
                                                    case 'requests':
                                                        status = 0;
                                                        $$('district_filter').setValue('');
                                                        $$('district_filter').hide();
                                                        $$('export_to_xlsx').hide();
                                                        $$('request_type').show();
                                                        $$('actualization_type').show();
                                                        break
                                                    case 'accepted':
                                                        status = 1;
                                                        $$('district_filter').setValue('');
                                                        $$('district_filter').hide();
                                                        $$('export_to_xlsx').hide();
                                                        $$('request_type').show();
                                                        $$('actualization_type').show();
                                                        break
                                                    case 'rejected':
                                                        status = 2;
                                                        $$('district_filter').setValue('');
                                                        $$('district_filter').hide();
                                                        $$('export_to_xlsx').hide();
                                                        $$('request_type').show();
                                                        $$('actualization_type').show();
                                                        break
                                                    case 'other':
                                                        status = 4;
                                                        $$('district_filter').show();
                                                        $$('export_to_xlsx').show();
                                                        $$('request_type').setValue('');
                                                        $$('request_type').hide();
                                                        $$('actualization_type').hide();
                                                        break
                                                }

                                                let departmentId = $$('department_filter').getValue();
                                                if (!departmentId) {
                                                    departmentId = 0;
                                                }

                                                let req_tbl_url = 'list_request/' + departmentId + '/' + status;

                                                let params = '';
                                                let request_type = $$('request_type').getValue();
                                                if (request_type) {
                                                    params = '?id_type_request=' + request_type;
                                                }

                                                let actualization = $$('actualization_type').getValue();
                                                if (actualization) {
                                                    let boolean_actualization = 0;
                                                    if (actualization=='id_true') {
                                                        boolean_actualization = 1;
                                                    }
                                                    params += params == '' ? '?' : '&';
                                                    params += 'is_actualization=' + boolean_actualization;
                                                }

                                                let district = $$('district_filter').getValue();
                                                if (district) {
                                                    params += params == '' ? '?' : '&';
                                                    params += 'id_district=' + district;
                                                }

                                                let search_text = $$('search').getValue();
                                                if (search_text) {
                                                    params += params == '' ? '?' : '&';
                                                    params += 'innOrName=' + search_text;
                                                }

                                                let view = requests(req_tbl_url + params, status, true);
                                                if (status == 4) {
                                                    view = other_requests(req_tbl_url + params);
                                                }

                                                webix.delay(function () {
                                                    webix.ui({
                                                        id: 'subContent',
                                                        rows: [
                                                            view
                                                        ]
                                                    }, $$('subContent'))
                                                })
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'request_type',
                                        width: 450,
                                        css: 'smallText',
                                        placeholder: 'Все виды деятельности',
                                        options: 'cls_type_requests_short',
                                        on: {
                                            onChange() {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'district_filter',
                                        width: 250,
                                        css: 'smallText',
                                        placeholder: 'Все районы',
                                        options: 'cls_districts',
                                        hidden: true,
                                        on: {
                                            onChange() {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {
                                        view: 'richselect',
                                        id: 'actualization_type',
                                        css: 'smallText',
                                        placeholder: 'Все заявки',
                                        hidden: true,
                                        width: 200,
                                        options: [
                                            { value: 'Не актуализированные', id: 'id_false'},
                                            { value: 'Актуализированные', id: 'id_true'}
                                        ],
                                        on: {
                                            onChange() {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {
                                        view: 'search',
                                        id: 'search',
                                        maxWidth: 300,
                                        minWidth: 100,
                                        tooltip: 'после ввода значения нажмите Enter',
                                        placeholder: "Поиск по ИНН и названию",
                                        on: {
                                            onEnter: function () {
                                                $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
                                            }
                                        }
                                    },
                                    {},
                                    {
                                        view: 'button',
                                        align: 'right',
                                        id: 'export_to_xlsx',
                                        value: 'Выгрузить',
                                        width: 140,
                                        hidden: true,
                                        click: function() {
                                            let params = {};
                                            params.id_department = $$('department_filter').getValue();
                                            let status = $$('tabbar').getValue();
                                            switch (status) {
                                                case 'requests':
                                                    status = 0;
                                                    break
                                                case 'accepted':
                                                    status = 1;
                                                    break
                                                case 'rejected':
                                                    status = 2;
                                                    break
                                                case 'other':
                                                    status = 4;
                                                    break
                                            }
                                            params.status = status;
                                            params.id_type_request = $$('request_type').getValue();
                                            params.id_district = $$('district_filter').getValue();
                                            params.is_actualization = $$('actualization_filter').getValue();
                                            params.innOrName = $$('search').getValue();
                                            webix.ajax().response("blob").get('export_to_xlsx', params, function(text, data) {
                                                webix.html.download(data, 'request.xlsx');
                                            });
                                        }
                                    }
                                ],
                            }
                        ]
                    },
                    {
                        id: 'subContent'
                    }
                ]
            }
        ]
    }
}