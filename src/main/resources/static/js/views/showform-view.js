function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

webix.i18n.setLocale("ru-RU");

webix.ready(function() {
    webix.ui({
        container: 'app',
        width: 1200,
        height: 800,
        css: { margin: "0 auto" },
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
                                        view_section('Данные об организации'),
                                        {
                                            type: 'space',
                                            margin: 5,
                                            cols: [
                                                {
                                                    rows: [
                                                        {
                                                            view: 'text',
                                                            name: 'organization.name',
                                                            label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                                                            labelPosition: 'top',
                                                            invalidMessage: 'Поле не может быть пустым',
                                                            readonly: true
                                                        },
                                                        {
                                                            view: 'text',
                                                            name: 'organization.shortName',
                                                            label: 'Краткое наименование организации',
                                                            labelPosition: 'top',
                                                            invalidMessage: 'Поле не может быть пустым',
                                                            readonly: true
                                                        },
                                                        {
                                                            cols: [
                                                                {
                                                                    view: 'text',
                                                                    name: 'organization.inn',
                                                                    label: 'ИНН',
                                                                    labelPosition: 'top',
                                                                    invalidMessage: 'Поле не может быть пустым',
                                                                    readonly: true
                                                                },
                                                                {
                                                                    view: 'checkbox',
                                                                    id: 'isSelfEmployed',
                                                                    label: 'Самозанятый',
                                                                    labelPosition: 'top',
                                                                    readonly: true
                                                                }
                                                            ]
                                                        },
                                                        {
                                                            view: 'text',
                                                            name: 'organization.ogrn',
                                                            label: 'ОГРН',
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
                                                            name: 'typeRequest.activityKind',
                                                            label: 'Тип заявки',
                                                            labelPosition: 'top',
                                                            invalidMessage: 'Поле не может быть пустым',
                                                            readonly: true
                                                        },
                                                    ]
                                                },
                                                {
                                                    rows: [
                                                        {
                                                            view: 'text',
                                                            name: 'organization.okved',
                                                            label: 'Основной вид осуществляемой деятельности (отрасль)',
                                                            labelPosition: 'top',
                                                            readonly: true
                                                        },
                                                        {
                                                            view: 'textarea',
                                                            name: 'organization.okvedAdd',
                                                            label: 'Дополнительные виды осуществляемой деятельности',
                                                            height: 100,
                                                            labelPosition: 'top'
                                                        },
                                                        {
                                                            view: 'select',
                                                            name: 'department.id',
                                                            label: 'Министерство, курирующее вашу деятельность',
                                                            labelPosition: 'top',
                                                            invalidMessage: 'Поле не может быть пустым',
                                                            readonly: true,
                                                            disabled:true,
                                                            options: '../cls_departments'
                                                        },
                                                        {
                                                            view: 'textarea',
                                                            label: '* области деятельности министерств',
                                                            labelPosition: 'top',
                                                            height: 150,
                                                            readonly: true,
                                                            value: '№\tНаимнование органа власти\tОписание\n' +
                                                                '1\tМинистерство финансов Республики Бурятия\tНет курируемых предприятий/организаций\n' +
                                                                '2\tМинистерство экономики Республики Бурятия\tВ сфере финансовой, страховой деятельности\n' +
                                                                '3\tМинистерство имущественных и земельных отношений  Республики Бурятия\t"оценочная деятельность,\n' +
                                                                'деятельность кадастровых инженеров"\n' +
                                                                '4\tМинистерство промышленности и торговли Республики Бурятия\t"1. Машиностроение и металообработка\n' +
                                                                '2. Легкая промышленность\n' +
                                                                '3. Промышленность строительных материалов\n' +
                                                                '4. Целлюлюзно-бумажное производство\n' +
                                                                '5. Деревообработка, лесопромышленный комплекс\n' +
                                                                '6. Торговля\n' +
                                                                '7. Общественное питание\n' +
                                                                '8. Бытовые услуги\n' +
                                                                '9. Ритуальные услуги\n' +
                                                                '10. Ремонт автотранспортных средств\n' +
                                                                '11. Траспортировка, хранение и логистические услуги, оказываемые органиациям торговли и общественного питания\n' +
                                                                '12. Организации инфраструктуры поддержки МСП"\n' +
                                                                '5\tМинистерство природных ресурсов Республики Бурятия\t"Предприятия добывающей промышленности, имеющие непрерывный производственный процесс или обеспечивающие углем объекты ЖКХ и энергетики, или иные предприятия, осуществляющие добычу полезных ископаемых в удалении от населённых пунктов при условии соблюдения режима самоизоляции на месте ведения работ;\n' +
                                                                '- Предприятия и организации, осуществляющие мероприятия по предотвращению негативного воздействия вод;\n' +
                                                                '- Предприятия, оказывающие услуги в сфере ЖКХ по содержанию санитарного состояния территорий (обращение с отходами);\n' +
                                                                '- Организации (хозяйствующие субъекты), осуществляющие работы по охране, защите, воспроизводству лесов и тушению лесных пожаров.\n' +
                                                                '"\n' +
                                                                '6\tМинистерство сельского хозяйства и продовольствия Республики Бурятия\t"1) Организации осуществляющие производство, реализацию и хранение сельскохозяйственной продукции, продуктов ее переработки (включая продукты питания) удобрений, средств защиты растений, кормов и кормовых добавок, семян и посадочного материала;\n' +
                                                                '2) Организации, осуществляющие формирование товарных запасов сельскохозяйственной продукции и продовольствия на будущие периоды;\n' +
                                                                '3) Организации, занятые на сезонных полевых работах,\n' +
                                                                '4) Рыбодобывающие, рыбоперерабатывающие предприятия, рыбоводные хозяйства, организации обслуживающие суда рыбопромыслового флота;\n' +
                                                                '5) Животноводческие хозяйства, организации по искусственному осеменению сельскохозяйственных животных, производству, хранению и реализации семени сельскохозяйственных животных и перевозке криоматериала для искусственного осеменения животных;\n' +
                                                                '6) Организации, осуществляющие лечение, профилактику, диагностику болезней животных, в т.ч. проводящие ветеринарные и ветринарно-санитарные экспертизы;\n' +
                                                                '7) Организации, осуществляющие производство, обращение и хранение ветеринарных лекарственных средств диагностики болезней животных, зоотоваров;\n' +
                                                                '8) Организации, осуществляющие реализацию сельскохозяйственной техники и ее техническое обслуживание или ремонт, в т.ч. машинотракторные станции;\n' +
                                                                '9) Предприятия пищевой и перерабатывающей промышленности;\n' +
                                                                '10) Организации, осуществляющие поставку ингредиентов, упаковки, сервисное обслуживание оборудования, а также компании занятые в перевозках погрузочно-разгрузочных работах, оказывающих логистические и сервисные услуги в указанных выше сферах;\n' +
                                                                '11) Организации, осуществляющие иные виды деятельности, направленные на обеспечение продовольственной безопасности Российской Федерации"\n' +
                                                                '7\tМинистерство строительства и модернизации жилищно-коммунального комплекса Республики Бурятия\t"Строительство:\n' +
                                                                'Организации (в том числе работающие с ними по договорам подряда и/или оказания услуг юридические лица и индивидуальные предприниматели):\n' +
                                                                '• С которыми заключены гос. или мун. контракты на строительство, реконструкцию объектов капитального строительства, проведение инженерных, экологических изысканий, разработку проектной документации;\n' +
                                                                '• Осуществляющие строительство объектов кап. строительства в рамкам концессионных соглашений, заключенных Правительством РБ;\n' +
                                                                '• Осуществляющие строительство многоквартирных домов, разрешение на строительство которых получено до 01.04.2020 года;\n' +
                                                                '• Осуществляющие кап. ремонт общего имущества многоквартирных домов (за исключением домов, в которых  подтвержден факт заражения проживающего короновирусной инфекцией);\n' +
                                                                'Юр. лица и/или ИП - изготовители и поставщики строительных материалов, изделий, оборудования, инструментов и расходных материалов к ним, а также авторизованные сервисные центры по обслуживанию и ремонту, для вышеуказанных организаций и/или индивидуальных предпринимателей.\n' +
                                                                'Юр. лица и/или ИП, оказывающие услуги по предоставлению грузоподъемных машин и механизмов, и автотранспорта для обслуживания объектов вышеуказанных организаций и/или индивидуальных предпринимателей.\n' +
                                                                'ЖКК:\n' +
                                                                'Юр. лица и/или ИП, осуществляющие поставку твердого, жидкого, газового топлива, а также предприятия, осуществляющие их доставку.\n' +
                                                                'Юр. лица и/или ИП, привлекаемые к аварийно-восстановительным работам, в части использования спецтехники, механизмов и оборудования, а также персонала обслуживающего указанное (исключительно в период проведения таких работ)."\n' +
                                                                '8\tМинистерство по развитию транспорта, энергетики и дорожного хозяйства Республики Бурятия\tСфера транспорта, энергетики, связи и дорожного хозяйства, а также в области энергосбережения и повышения энергетической эффективности в сфере транспорта, энергетики, связи и дорожного хозяйства\n' +
                                                                '9\tМинистерство социальной защиты населения Республики Бурятия\tОрганизации социального обслуживания населения\n' +
                                                                '10\tМинистерство здравоохранения Республики Бурятия\tОрганизации по техническому обслуживанию медецинского оборудования\n' +
                                                                '11\tМинистерство культуры Республики Бурятия\tНет курируемых предприятий/организаций\n' +
                                                                '12\tМинистерство образования и науки Республики Бурятия\t"1. учреждения дошкольного образования, где функционируют дежурные группы. \n' +
                                                                '2. Учреждения среднего общего образования, где очно-заочное обучение для 9,11 классов."\n' +
                                                                '13\tМинистерство спорта и молодежной политики Республики Бурятия\t"1. Содержание, эксплуатация и обеспечение безопасности на спортивных объектов\n' +
                                                                '2. Строительство спортивных объектов\n' +
                                                                '3. Волонтерская деятельность"\n' +
                                                                '14\tМинистерство туризма Республики Бурятия\t"1. Санаторно-курортная сфера\n' +
                                                                '2. Гостиничный комплекс\n' +
                                                                '3. Туроператоры, турагентства, экскурсоводы"\n' +
                                                                '15\tРеспубликанское агентство лесного хозяйства\tВыполнение мероприятий по использованию, охране, защите, воспроизводству лесов, лесозаготовка, лесопереработка\n'
                                                        }
                                                    ]
                                                }
                                            ]
                                        },
                                        view_section('Рассмотрение заявки'),
                                        {
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
                                        {
                                            cols: [
                                                {
                                                    rows : [
                                                        {
                                                            view: 'text',
                                                            name: 'statusReviewName',
                                                            labelPosition: 'top',
                                                            readonly: true,
                                                            label: 'Статус',
                                                        },
                                                        {
                                                            view: 'text',
                                                            id: 'processed_user',
                                                            name: 'processedUser.fullName',
                                                            labelPosition: 'top',
                                                            readonly: true,
                                                            label: 'Обработал',
                                                        },
                                                    ]
                                                },
                                                {
                                                    view: 'textarea',
                                                    readonly: true,
                                                    name: 'rejectComment',
                                                    label: 'Обоснование отказа',
                                                    id: 'reject_comment',
                                                    labelPosition: 'top',
                                                    height: 100
                                                }
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
                                            view: 'textarea',
                                            name: 'organization.addressJur',
                                            label: 'Юридический адрес',
                                            labelPosition: 'top',
                                            height: 170,
                                            readonly: true
                                        },
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
                                                    header: 'Фактический адрес осуществления деятельности',
                                                    //width: 300,
                                                    fillspace: 5
                                                },
                                                {
                                                    id: 'personOfficeFactCnt',
                                                    header: 'Числ. работающих',
                                                    fillspace: 1
                                                }
                                            ],
                                            url: '../doc_address_fact/' + ID,
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
                            {
                                header: "Обоснование",
                                body: {
                                    rows : [
                                        view_section('Обоснование заявки'),
                                        {
                                            rows: [
                                                {
                                                    view: 'textarea',
                                                    height: 250,
                                                    label: 'Обоснование заявки',
                                                    name: 'reqBasis',
                                                    invalidMessage: 'Поле не может быть пустым',
                                                    readonly: true,
                                                    labelPosition: 'top'
                                                },
                                                {
                                                    cols: [
                                                        {
                                                            id: 'filename_label',
                                                            view: "label",
                                                            label: 'Вложенный файл:',
                                                            width: 150
                                                        },
                                                        {
                                                            paddingLeft: 10,
                                                            view: 'list',
                                                            //height: 100,
                                                            autoheight: true,
                                                            select: false,
                                                            template: '#value#',
                                                            label: '',
                                                            name: 'attachmentFilename',
                                                            borderless: true,
                                                            data: [],
                                                            id: 'filename'
                                                        },
                                                    ]
                                                }

                                            ]
                                        },
                                        {
                                            cols: [
                                                {
                                                    view: 'checkbox',
                                                    // width:255,
                                                    id: 'actualization',
                                                    name: 'actualization',
                                                    // labelPosition: 'right',
                                                    readonly: true,
                                                    label: 'Актуализированная'
                                                },
                                                {
                                                    paddingLeft: 10,
                                                    view: 'label',
                                                    //height: 100,
                                                    autoheight: true,
                                                    select: false,
                                                    template: '#value#',
                                                    label: '',
                                                    name: 'actualizedRequestLink',
                                                    borderless: true,
                                                    id: 'actualizedRequestLink'
                                                }
                                            ]
                                        },
                                        view_section('Данные о численности работников'),
                                        {
                                            type: 'space',
                                            rows: [
                                                {
                                                    view: 'text',
                                                    name: 'personSlrySaveCnt',
                                                    label: 'Суммарная численность работников, в отношении которых установлен режим работы нерабочего дня с сохранением заработной платы',
                                                    labelPosition: 'top',
                                                    validate: function (val) {
                                                        return !isNaN(val * 1);
                                                    },
                                                    invalidMessage: 'Поле не может быть пустым',
                                                    readonly: true
                                                },
                                                {
                                                    view: 'text',
                                                    name: 'personRemoteCnt',
                                                    label: 'Суммарная численность работников, подлежащих переводу на дистанционный режим работы',
                                                    invalidMessage: 'Поле не может быть пустым',
                                                    validate: function (val) {
                                                        return !isNaN(val * 1);
                                                    },
                                                    readonly: true,
                                                    labelPosition: 'top'
                                                },
                                                {
                                                    view: 'text',
                                                    name: 'personOfficeCnt',
                                                    label: 'Суммарная численность работников, не подлежащих переводу на дистанционный режим работы (посещающие рабочие места)',
                                                    labelPosition: 'top',
                                                    validate: function (val) {
                                                        return !isNaN(val * 1);
                                                    },
                                                    invalidMessage: 'Поле не может быть пустым',
                                                    readonly: true
                                                },
                                            ]
                                        },
                                    ]
                                }
                            },
                            {
                                header: "Сотрудники",
                                body: {
                                    rows : [
                                        view_section('Данные о работниках, чья деятельность предусматривает выход на работу'),
                                        {
                                            rows: [
                                                {
                                                    id: 'person_table',
                                                    view: 'datatable',
                                                    height: 600,
                                                    name: 'persons',
                                                    select: 'row',
                                                    resizeColumn: true,
                                                    readonly: true,
                                                    columns: [
                                                        {
                                                            id: 'lastname',
                                                            header: 'Фамилия',
                                                            adjust: true,
                                                            sort: 'string',
                                                            fillspace: true
                                                        },
                                                        {
                                                            id: 'firstname',
                                                            header: 'Имя',
                                                            adjust: true,
                                                            sort: 'string',
                                                            fillspace: true
                                                        },
                                                        {id: 'patronymic', header: 'Отчество', adjust: true, sort: 'string'},
                                                    ],
                                                    url: '../doc_persons/' + ID
                                                },
                                            ]
                                        },
                                    ]
                                }
                            }
                        ]
                    }
                ],
            }
        ]
    });

    $$('form').load('../doc_requests/' + ID).then(function (data) {
        data = data.json();

        let paths = data.attachmentPath.split(',')

        let fileList = []
        paths.forEach((path, index) => {
            let filename = path.split('\\').pop().split('/').pop()
            if(filename != '' &&
                ((filename.toUpperCase().indexOf('.PDF') != -1) ||
                    (filename.toUpperCase().indexOf('.ZIP') != -1)
                )){
                filename = '<a href="' + LINK_PREFIX + filename + LINK_SUFFIX + '" target="_blank">'
                    + filename + '</a>'
                fileList.push({ id: index, value: filename })
            }
        })
        if(fileList.length > 0) {
            $$('filename').parse(fileList)
        }
        else {
            $$('filename_label').hide()
            $$('filename').hide()
        }

        if (data.statusReview == 0) {
            $$('processed_user').hide()
            $$('reject_comment').hide()
        } else if (data.statusReview == 1) {
            $$('reject_comment').hide()
        }
    });
})
