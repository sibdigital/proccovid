function showform_other(data) {
    return {
        view: 'window',
        id: 'showQueryWin',
        head: {
            view: 'toolbar',
            elements: [
                {view: 'label', label: 'Просмотр заявки (id: ' + data.id + ').'},
                {
                    view: 'icon', icon: 'wxi-close',
                    click: function () {
                        $$('showQueryWin').close()
                    }
                }
            ]
        },
        width: 1200,
        height: 700,
        position: 'center',
        item: data,
        modal: true,
        body: {
            view: 'scrollview',
            scroll: 'y',
            id: 'show_layout',
            autowidth: true,
            autoheight: true,
            body: {
                rows: [
                    {
                        id: 'form',
                        view: 'form',
                        complexData: true,
                        elements: [
                            {
                                id: 'tabview',
                                view: 'tabview',
                                cells: [
                                    {
                                        header: 'Основное',
                                        body: {
                                            rows : [
                                                view_section('Основные сведения'),
                                                {
                                                    type: 'space',
                                                    margin: 5,
                                                    cols: [
                                                        {
                                                            rows: [
                                                                {
                                                                    view: 'text',
                                                                    name: 'organization.name',
                                                                    label: 'ФИО',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'organization.email',
                                                                    label: 'e-mail',
                                                                    labelPosition: 'top',
                                                                    validate: webix.rules.isEmail,
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'organization.phone',
                                                                    label: 'Телефон',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'organization.inn',
                                                                    label: 'ИНН',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true
                                                                },
                                                                {
                                                                    view: 'select',
                                                                    name: 'organization.typeTaxReporting',
                                                                    label: 'Способ сдачи налоговой отчетности',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true,
                                                                    disabled: true,
                                                                    options: [
                                                                        { id: 1, value: '3-НДФЛ' },
                                                                        { id: 2, value: 'Налог для самозанятых' }
                                                                    ]
                                                                },
                                                                {
                                                                    view: 'select',
                                                                    name: 'district.id',
                                                                    label: 'Район, в котором оказывается услуга',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true,
                                                                    disabled: true,
                                                                    options: 'cls_districts'
                                                                },
                                                                {
                                                                    view: 'text',
                                                                    name: 'typeRequest.activityKind',
                                                                    label: 'Тип заявки',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true
                                                                },
                                                            ]
                                                        },
                                                    ]
                                                },
                                                view_section('Рассмотрение заявки'),
                                                {
                                                    id: 'review_app_section',
                                                    cols: [
                                                        {
                                                            view: 'checkbox',
                                                            name: 'agree',
                                                            labelPosition: 'top',
                                                            invalidMessage: 'Поле не может быть пустым',
                                                            readonly: true,
                                                            labelRight: 'Подтверждено согласие работников на обработку персональных данных',
                                                        },
                                                        {
                                                            view: 'checkbox',
                                                            name: 'protect',
                                                            labelPosition: 'top',
                                                            invalidMessage: 'Поле не может быть пустым',
                                                            readonly: true,
                                                            labelRight: 'Подтверждено обязательное выполнение требований по защите от COVID-19',
                                                        },
                                                    ]
                                                },
                                            ]
                                        }
                                    },
                                    {
                                        header: "Адрес",
                                        body: {
                                            rows : [
                                                view_section('Адресная информация'),
                                                {
                                                    view: 'datatable', name: 'addressFact', label: '', labelPosition: 'top',
                                                    height: 300,
                                                    select: 'row',
                                                    editable: true,
                                                    id: 'addr_table',
                                                    resizeColumn:true,
                                                    readonly: true,
                                                    // resizeRow:true,
                                                    fixedRowHeight:false,
                                                    rowLineHeight:25,
                                                    rowHeight:25,
                                                    //autowidth:true,
                                                    columns: [
                                                        {
                                                            id: 'addressFact',
                                                            header: 'Фактический адрес нахождения жилья, сдаваемого в аренду',
                                                            //width: 300,
                                                            fillspace: 5
                                                        }
                                                    ],
                                                    data: [],
                                                    on:{
                                                        onAfterLoad:function(){
                                                            webix.delay(function(){
                                                                this.adjustRowHeight("addressFact", true);
                                                                this.render();
                                                            }, this);
                                                        },
                                                        onColumnResize:function(){
                                                            this.adjustRowHeight("addressFact", true);
                                                            this.render();
                                                        }
                                                    }
                                                },
                                            ]
                                        }
                                    },
                                ]
                            }
                        ],
                    }
                ]
            }
        },
        on: {
            'onShow': function () {
                $$('form').load('doc_requests/' + data.id);

                let addr_table_data = new webix.DataCollection({
                    url: 'doc_address_fact/' + data.id
                })
                $$('addr_table').sync(addr_table_data);

                if (data.additionalAttributes) {
                    if (data.additionalAttributes.isCheckingAgree){
                        let v = {
                            view: 'checkbox',
                            name: 'additionalAttributes.isCheckingAgree',
                            labelPosition: 'top',
                            readonly: true,
                            labelRight: 'Ознакомлен, обязуется проверять сведения для размещения граждан'
                        };
                        $$('review_app_section').addView(v);
                    }
                }
            }
        }
    }
}
