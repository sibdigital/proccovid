// requirejs.config({
//     baseUrl: 'js'
// })
//
// require(
//     [
//         'views/requests',
//         'views/showform',
//         'utils/filter'
//     ],
//     function(requests
//              , showform
//              , filter
//     ) {

        webix.i18n.setLocale("ru-RU");

        webix.ready(function() {
            layout = webix.ui({
                container: 'app',
                width: document.body.clientWidth,
                height: window.innerHeight, //document.body.clientHeight,
                rows: [
                    {
                        view: 'toolbar',
                        height: 40,
                        cols: [
                            {
                                view: 'label',
                                label: '<span style="font-size: 1.5rem">Работающая Бурятия. Список заявок.</span>',
                            },
                            {
                            },
                            {
                                view: 'label',
                                label: DEPARTMENT + ' ( <a href="/logout" title="Выйти">' + USER_NAME + '</a>)',
                                align: 'right'
                            }
                        ]
                    },
                    {
                        view: 'toolbar',
                        cols: [
                            {
                                value: 1, view: 'segmented', id:'tabbar', value: 'listView', multiview: true,
                                width: 600,
                                optionWidth: 150,  align: 'center', padding: 10,
                                options: [
                                    { value: 'Необработанные', id: 'requests'},
                                    { value: 'Принятые', id: 'accepted'},
                                    { value: 'Отклоненные', id: 'rejected'},
                                    { value: 'Прочие', id: 'other'}
                                ],
                                on:{
                                    onChange:function(id){
                                        let status = 0
                                        switch(id) {
                                            case 'requests':
                                                status = 0;
                                                $$('district_filter').setValue('');
                                                $$('district_filter').hide();
                                                $$('export_to_xlsx').hide();
                                                $$('request_type').show();
                                                break
                                            case 'accepted':
                                                status = 1;
                                                $$('district_filter').setValue('');
                                                $$('district_filter').hide();
                                                $$('export_to_xlsx').hide();
                                                $$('request_type').show();
                                                break
                                            case 'rejected':
                                                status = 2;
                                                $$('district_filter').setValue('');
                                                $$('district_filter').hide();
                                                $$('export_to_xlsx').hide();
                                                $$('request_type').show();
                                                break
                                            case 'other':
                                                status = 4;
                                                $$('district_filter').show();
                                                $$('export_to_xlsx').show();
                                                $$('request_type').setValue('');
                                                $$('request_type').hide();
                                                break
                                        }

                                        let req_tbl_url = 'list_request/' + ID_DEPARTMENT + '/' + status;

                                        let params = '';
                                        let request_type = $$('request_type').getValue();
                                        if (request_type) {
                                            params = '?id_type_request=' + request_type;
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

                                        let view = requests(req_tbl_url + params, status);
                                        if (status == 4) {
                                            view = other_requests(req_tbl_url + params);
                                        }

                                        webix.ui({
                                            id: 'root',
                                            rows: [
                                                view
                                            ]
                                        }, $$('root'))
                                    }
                                }
                            },
                            {
                                view: 'richselect',
                                id: 'request_type',
                                width: 450,
                                css: 'smallText',
                                placeholder: 'Все типы заявок',
                                options: 'cls_type_requests_short',
                                on: {
                                    onChange(id) {
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
                            filter.searchBar('requests_table'),
                            {},
                            {
                                view: 'button',
                                align: 'right',
                                id: 'export_to_xlsx',
                                value: 'Выгрузить',
                                width: 140,
                                click: function() {
                                    let params = {};
                                    params.id_department = ID_DEPARTMENT;
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
                                    params.innOrName = $$('search').getValue();
                                    webix.ajax().response("blob").get('export_to_xlsx', params, function(text, data) {
                                        webix.html.download(data, 'request.xlsx');
                                    });
                                }
                            }
                        ]
                    },
                    {
                        id: 'root'
                    }
                ]
            })
            webix.event(window, "resize", function(event){
                layout.define("width",document.body.clientWidth);
                layout.define("height",window.innerHeight);
                layout.resize();
            });
            // по умолчанию
            $$('tabbar').setValue('requests');
            $$('request_type').getList().add({id:'', value:'Все типы заявок', $empty: true}, 0)
            $$('district_filter').getList().add({id:'', value:'Все районы', $empty: true}, 0)
        })
    // })