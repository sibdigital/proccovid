
webix.i18n.setLocale("ru-RU");

function view_section(title) {
    return {
        view: 'template',
        type: 'section',
        template: title
    }
}

function getFilesListByTypeView(docRequestSubsidyId) {
    webix.ajax(`../request_subsidy_files_verification/${ docRequestSubsidyId }`).then(function (filesVerification) {
        filesVerification = filesVerification.json();

        filesVerification.map((file) => {
            switch (file.verify_status) {
               case 1: file.verify_status = "проверка прошла успешно"; break;
               case 2: file.verify_status = "подпись не соответствует файлу"; break;
               case 3: file.verify_status = "в сертификате или цепочке сертификатов есть ошибки"; break;
               case 4: file.verify_status = "в подписи есть ошибки"; break;
               default: file.verify_status = "проверка не проводилась"; break;
            }
            return file;
        });

//                case "1": hashMap.put("verify_status", "проверка прошла успешно"); break;
//                case "2": hashMap.put("verify_status", "подпись не соответствует файлу"); break;
//                case "3": hashMap.put("verify_status", "в сертификате или цепочке сертификатов есть ошибки"); break;
//                case "4": hashMap.put("verify_status", "в подписи есть ошибки"); break;
//                default: hashMap.put("verify_status", "проверка не проводилась"); break;


        webix.ajax(`../request_subsidy_files/${docRequestSubsidyId}`).then(function (data) {
            const views = [];
            data = data.json();
            console.dir({ data, filesVerification });
            if (data.length > 0) {
                const filesTypes = {};
                const byFileType = data.reduce(function (result, file) {
                    const fileVerificationStatus = filesVerification.find((fileVerification) => fileVerification.id_request_subsidy_file === file.id);
                    result[file.fileType.id] = result[file.fileType.id] || [];
                    result[file.fileType.id].push({ ...file, verificationStatus: fileVerificationStatus ?? { verify_status: 'отсутствует подпись' } });

                    filesTypes[file.fileType.id] = file.fileType.name;

                    return result;
                }, Object.create(null));

                console.dir({ byFileType });

                for (const [key, filesArray] of Object.entries(byFileType)) {
                    console.dir({ filesArray });
                    views.push({
                        rows: [
                            view_section(filesTypes[key]),
                            {
                                id: `request_subsidy_files_table/${key}`,
                                view: 'datatable',
                                pager: `Pager/${key}`,
                                autoheight: true,
                                header: `id = ${key}`,
                                select: 'row',
                                resizeColumn: true,
                                readonly: true,
                                data: filesArray,
                                columns: [
                                    {
                                        id: 'viewFileName',
                                        header: 'Название файла',
                                        adjust: true,
                                        fillspace: true,
                                        sort: 'string',
                                    },
                                    {
                                        id: 'signature',
                                        template: function (request) {
                                            let label = '';
                                            let style = '';

                                            if (request.signature) {
                                                label = 'Подпись загружена';
                                                style = 'color: green';
                                            } else {
                                                label = 'Подпись не загружена';
                                                style = 'color: red';
                                            }

                                            return `<div style="${style}" role="gridcell" aria-rowindex="1" aria-colindex="1" aria-selected="true" tabindex="0" class="webix_cell webix_row_select">${label}</div>`;
                                        },
                                        header: 'Подпись',
                                        adjust: true,
                                        fillspace: true,
                                        sort: 'string',
                                    },
                                    {
                                        id: 'verificationStatus',
                                        header: 'Статус проверки подписи',
                                        adjust: true,
                                        sort: 'string',
                                        template: '#verificationStatus.verify_status#',
                                    },
                                ],
                            },
                            {
                                view: 'pager',
                                id: `Pager/${key}`,
                                height: 38,
                                size: 25,
                                group: 5,
                                template: '{common.first()}{common.prev()}{common.pages()}{common.next()}{common.last()}'
                            },
                        ]
                    })
                }

                webix.ui({
                    id: 'filesListViewByType',
                    rows: views,
                }, $$('filesListViewByType'));
            }

        })
    });
}

webix.ready(function () {
    webix.ajax('../doc_requests_subsidy/' + ID).then(function (data) {
        data = data.json();
        const docRequestSubsidyId = data.id;
        console.dir({ data });
        getFilesListByTypeView(data.id);

        webix.ui({
            container: 'app',
            width: 1200,
            height: 840,
            css: { margin: '0 auto' },
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
                                        view: 'scrollview',
                                        scroll: 'y',
                                        autowidth: true,
                                        autoheight: true,
                                        body: {
                                            rows: [
                                                view_section('Мера поддержки'),
                                                {
                                                    view: 'text',
                                                    id: 'subsidyId',
                                                    label: 'Мера поддержки',
                                                    labelPosition: 'top',
                                                    readonly: true,
                                                    name: 'subsidy.name',
                                                },
                                                {
                                                    view: 'textarea',
                                                    id: 'reqBasis',
                                                    label: 'Обоснование заявки',
                                                    labelPosition: 'top',
                                                    height: 150,
                                                    minWidth: 250,
                                                    readonly: true,
                                                    name: 'reqBasis',
                                                },
                                                view_section('Данные об организации'),
                                                {
                                                    margin: 5,
                                                    responsive: "respLeftToRight",
                                                    cols: [
                                                        {
                                                            minWidth: 300,
                                                            rows: [
                                                                {
                                                                    view: 'text',
                                                                    name: 'organization.shortName',
                                                                    id: 'shortOrganizationName',
                                                                    label: 'Краткое наименование организации',
                                                                    labelPosition: 'top',
                                                                    readonly: true,
                                                                },
                                                                {
                                                                    view: 'textarea',
                                                                    name: 'organization.name',
                                                                    height: 80,
                                                                    id: 'organizationName',
                                                                    label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                                                                    labelPosition: 'top',
                                                                    readonly: true,
                                                                },
                                                                {
                                                                    id: "innplace",
                                                                    rows: []
                                                                },
                                                                {
                                                                    responsive: 'innplace',
                                                                    cols: [
                                                                        {
                                                                            view: 'text',
                                                                            name: 'organization.inn',
                                                                            id: "inn",
                                                                            label: 'ИНН',
                                                                            minWidth: 200,
                                                                            labelPosition: 'top',
                                                                            readonly: true,
                                                                        },
                                                                        {
                                                                            view: 'text',
                                                                            name: 'organization.ogrn',
                                                                            id: 'ogrn',
                                                                            label: 'ОГРН',
                                                                            minWidth: 200,
                                                                            validate: function (val) {
                                                                                return !isNaN(val * 1);
                                                                            },
                                                                            labelPosition: 'top',
                                                                            readonly: true,
                                                                        },
                                                                    ]
                                                                },
                                                            ]
                                                        },
                                                        // {
                                                        //     minWidth: 300,
                                                        //     id: "respLeftToRight",
                                                        //     rows:
                                                        //         [
                                                        //             {
                                                        //                 height: 27,
                                                        //                 view: 'label',
                                                        //                 label: 'Основной вид осуществляемой деятельности (отрасль)',
                                                        //             },
                                                        //             {
                                                        //                 view: 'list',
                                                        //                 layout: 'x',
                                                        //                 id:"okved_main",
                                                        //                 css: {'white-space': 'normal !important;'},
                                                        //                 height: 50,
                                                        //                 template: '#kindCode# - #kindName#',
                                                        //                 url: 'reg_organization_okved', //<span class="mdi mdi-close"></span>
                                                        //                 type: {
                                                        //                     css: "chip",
                                                        //                     height: 'auto'
                                                        //                 },
                                                        //             },
                                                        //             {
                                                        //                 height: 26,
                                                        //                 view: 'label',
                                                        //                 label: 'Дополнительные виды осуществляемой деятельности',
                                                        //             },
                                                        //             {
                                                        //                 view: "list",
                                                        //                 layout: 'x',
                                                        //                 id: 'okveds_add',
                                                        //                 css: {'white-space': 'normal !important;'},
                                                        //                 height: 170,
                                                        //                 template: '#kindCode# - #kindName#',
                                                        //                 url: "reg_organization_okved_add",
                                                        //                 type: {
                                                        //                     css: "chip",
                                                        //                     height: 'auto'
                                                        //                 },
                                                        //             },
                                                        //         ]
                                                        // },
                                                    ]
                                                },
                                                // {
                                                //     type: 'space',
                                                //     margin: 5,
                                                //     cols: [
                                                //         {
                                                //             rows: [
                                                //                 {
                                                //                     view: 'text',
                                                //                     name: 'organization.name',
                                                //                     label: 'Полное наименование организации/фамилия, имя, отчество индивидуального предпринимателя',
                                                //                     labelPosition: 'top',
                                                //                     invalidMessage: 'Поле не может быть пустым',
                                                //                     readonly: true
                                                //                 },
                                                //                 {
                                                //                     view: 'text',
                                                //                     name: 'organization.shortName',
                                                //                     label: 'Краткое наименование организации',
                                                //                     labelPosition: 'top',
                                                //                     invalidMessage: 'Поле не может быть пустым',
                                                //                     readonly: true
                                                //                 },
                                                //                 {
                                                //                     cols: [
                                                //                         {
                                                //                             view: 'text',
                                                //                             name: 'organization.inn',
                                                //                             label: 'ИНН',
                                                //                             labelPosition: 'top',
                                                //                             invalidMessage: 'Поле не может быть пустым',
                                                //                             readonly: true
                                                //                         },
                                                //                         {
                                                //                             view: 'checkbox',
                                                //                             id: 'isSelfEmployed',
                                                //                             label: 'Самозанятый',
                                                //                             labelPosition: 'top',
                                                //                             readonly: true
                                                //                         }
                                                //                     ]
                                                //                 },
                                                //                 {
                                                //                     view: 'text',
                                                //                     name: 'organization.ogrn',
                                                //                     label: 'ОГРН',
                                                //                     labelPosition: 'top',
                                                //                     invalidMessage: 'Поле не может быть пустым',
                                                //                     readonly: true
                                                //                 },
                                                //                 {
                                                //                     view: 'text',
                                                //                     name: 'organization.email',
                                                //                     label: 'e-mail',
                                                //                     labelPosition: 'top',
                                                //                     validate: webix.rules.isEmail,
                                                //                     invalidMessage: 'Поле не может быть пустым',
                                                //                     readonly: true
                                                //                 },
                                                //                 {
                                                //                     view: 'text',
                                                //                     name: 'organization.phone',
                                                //                     label: 'Телефон',
                                                //                     labelPosition: 'top',
                                                //                     invalidMessage: 'Поле не может быть пустым',
                                                //                     readonly: true
                                                //                 },
                                                //             ]
                                                //         },
                                                //         {
                                                //             rows: [
                                                //                 {
                                                //                     view: 'text',
                                                //                     id: 'orgMainOkved',
                                                //                     name: 'organization.okved',
                                                //                     label: 'Основной вид осуществляемой деятельности (отрасль)',
                                                //                     labelPosition: 'top',
                                                //                     readonly: true
                                                //                 },
                                                //                 {
                                                //                     view: 'textarea',
                                                //                     id: 'orgAddOkveds',
                                                //                     name: 'organization.okvedAdd',
                                                //                     label: 'Дополнительные виды осуществляемой деятельности',
                                                //                     height: 100,
                                                //                     labelPosition: 'top',
                                                //                     readonly: true,
                                                //                 },
                                                //                 {
                                                //                     view: 'select',
                                                //                     id: 'departmentId',
                                                //                     name: 'department.id',
                                                //                     label: 'Министерство, рассматривающее заявку',
                                                //                     labelPosition: 'top',
                                                //                     invalidMessage: 'Поле не может быть пустым',
                                                //                     disabled: true,
                                                //                     readonly: true,
                                                //                     options: '../cls_departments',
                                                //                 },
                                                //                 {
                                                //                     view: 'textarea',
                                                //                     label: '* области деятельности министерств',
                                                //                     labelPosition: 'top',
                                                //                     height: 150,
                                                //                     readonly: true,
                                                //                     value: '№\tНаимнование органа власти\tОписание\n' +
                                                //                         '1\tМинистерство финансов Республики Бурятия\tНет курируемых предприятий/организаций\n' +
                                                //                         '2\tМинистерство экономики Республики Бурятия\tВ сфере финансовой, страховой деятельности\n' +
                                                //                         '3\tМинистерство имущественных и земельных отношений  Республики Бурятия\t"оценочная деятельность,\n' +
                                                //                         'деятельность кадастровых инженеров"\n' +
                                                //                         '4\tМинистерство промышленности и торговли Республики Бурятия\t"1. Машиностроение и металообработка\n' +
                                                //                         '2. Легкая промышленность\n' +
                                                //                         '3. Промышленность строительных материалов\n' +
                                                //                         '4. Целлюлюзно-бумажное производство\n' +
                                                //                         '5. Деревообработка, лесопромышленный комплекс\n' +
                                                //                         '6. Торговля\n' +
                                                //                         '7. Общественное питание\n' +
                                                //                         '8. Бытовые услуги\n' +
                                                //                         '9. Ритуальные услуги\n' +
                                                //                         '10. Ремонт автотранспортных средств\n' +
                                                //                         '11. Траспортировка, хранение и логистические услуги, оказываемые органиациям торговли и общественного питания\n' +
                                                //                         '12. Организации инфраструктуры поддержки МСП"\n' +
                                                //                         '5\tМинистерство природных ресурсов Республики Бурятия\t"Предприятия добывающей промышленности, имеющие непрерывный производственный процесс или обеспечивающие углем объекты ЖКХ и энергетики, или иные предприятия, осуществляющие добычу полезных ископаемых в удалении от населённых пунктов при условии соблюдения режима самоизоляции на месте ведения работ;\n' +
                                                //                         '- Предприятия и организации, осуществляющие мероприятия по предотвращению негативного воздействия вод;\n' +
                                                //                         '- Предприятия, оказывающие услуги в сфере ЖКХ по содержанию санитарного состояния территорий (обращение с отходами);\n' +
                                                //                         '- Организации (хозяйствующие субъекты), осуществляющие работы по охране, защите, воспроизводству лесов и тушению лесных пожаров.\n' +
                                                //                         '"\n' +
                                                //                         '6\tМинистерство сельского хозяйства и продовольствия Республики Бурятия\t"1) Организации осуществляющие производство, реализацию и хранение сельскохозяйственной продукции, продуктов ее переработки (включая продукты питания) удобрений, средств защиты растений, кормов и кормовых добавок, семян и посадочного материала;\n' +
                                                //                         '2) Организации, осуществляющие формирование товарных запасов сельскохозяйственной продукции и продовольствия на будущие периоды;\n' +
                                                //                         '3) Организации, занятые на сезонных полевых работах,\n' +
                                                //                         '4) Рыбодобывающие, рыбоперерабатывающие предприятия, рыбоводные хозяйства, организации обслуживающие суда рыбопромыслового флота;\n' +
                                                //                         '5) Животноводческие хозяйства, организации по искусственному осеменению сельскохозяйственных животных, производству, хранению и реализации семени сельскохозяйственных животных и перевозке криоматериала для искусственного осеменения животных;\n' +
                                                //                         '6) Организации, осуществляющие лечение, профилактику, диагностику болезней животных, в т.ч. проводящие ветеринарные и ветринарно-санитарные экспертизы;\n' +
                                                //                         '7) Организации, осуществляющие производство, обращение и хранение ветеринарных лекарственных средств диагностики болезней животных, зоотоваров;\n' +
                                                //                         '8) Организации, осуществляющие реализацию сельскохозяйственной техники и ее техническое обслуживание или ремонт, в т.ч. машинотракторные станции;\n' +
                                                //                         '9) Предприятия пищевой и перерабатывающей промышленности;\n' +
                                                //                         '10) Организации, осуществляющие поставку ингредиентов, упаковки, сервисное обслуживание оборудования, а также компании занятые в перевозках погрузочно-разгрузочных работах, оказывающих логистические и сервисные услуги в указанных выше сферах;\n' +
                                                //                         '11) Организации, осуществляющие иные виды деятельности, направленные на обеспечение продовольственной безопасности Российской Федерации"\n' +
                                                //                         '7\tМинистерство строительства и модернизации жилищно-коммунального комплекса Республики Бурятия\t"Строительство:\n' +
                                                //                         'Организации (в том числе работающие с ними по договорам подряда и/или оказания услуг юридические лица и индивидуальные предприниматели):\n' +
                                                //                         '• С которыми заключены гос. или мун. контракты на строительство, реконструкцию объектов капитального строительства, проведение инженерных, экологических изысканий, разработку проектной документации;\n' +
                                                //                         '• Осуществляющие строительство объектов кап. строительства в рамкам концессионных соглашений, заключенных Правительством РБ;\n' +
                                                //                         '• Осуществляющие строительство многоквартирных домов, разрешение на строительство которых получено до 01.04.2020 года;\n' +
                                                //                         '• Осуществляющие кап. ремонт общего имущества многоквартирных домов (за исключением домов, в которых  подтвержден факт заражения проживающего короновирусной инфекцией);\n' +
                                                //                         'Юр. лица и/или ИП - изготовители и поставщики строительных материалов, изделий, оборудования, инструментов и расходных материалов к ним, а также авторизованные сервисные центры по обслуживанию и ремонту, для вышеуказанных организаций и/или индивидуальных предпринимателей.\n' +
                                                //                         'Юр. лица и/или ИП, оказывающие услуги по предоставлению грузоподъемных машин и механизмов, и автотранспорта для обслуживания объектов вышеуказанных организаций и/или индивидуальных предпринимателей.\n' +
                                                //                         'ЖКК:\n' +
                                                //                         'Юр. лица и/или ИП, осуществляющие поставку твердого, жидкого, газового топлива, а также предприятия, осуществляющие их доставку.\n' +
                                                //                         'Юр. лица и/или ИП, привлекаемые к аварийно-восстановительным работам, в части использования спецтехники, механизмов и оборудования, а также персонала обслуживающего указанное (исключительно в период проведения таких работ)."\n' +
                                                //                         '8\tМинистерство по развитию транспорта, энергетики и дорожного хозяйства Республики Бурятия\tСфера транспорта, энергетики, связи и дорожного хозяйства, а также в области энергосбережения и повышения энергетической эффективности в сфере транспорта, энергетики, связи и дорожного хозяйства\n' +
                                                //                         '9\tМинистерство социальной защиты населения Республики Бурятия\tОрганизации социального обслуживания населения\n' +
                                                //                         '10\tМинистерство здравоохранения Республики Бурятия\tОрганизации по техническому обслуживанию медецинского оборудования\n' +
                                                //                         '11\tМинистерство культуры Республики Бурятия\tНет курируемых предприятий/организаций\n' +
                                                //                         '12\tМинистерство образования и науки Республики Бурятия\t"1. учреждения дошкольного образования, где функционируют дежурные группы. \n' +
                                                //                         '2. Учреждения среднего общего образования, где очно-заочное обучение для 9,11 классов."\n' +
                                                //                         '13\tМинистерство спорта и молодежной политики Республики Бурятия\t"1. Содержание, эксплуатация и обеспечение безопасности на спортивных объектов\n' +
                                                //                         '2. Строительство спортивных объектов\n' +
                                                //                         '3. Волонтерская деятельность"\n' +
                                                //                         '14\tМинистерство туризма Республики Бурятия\t"1. Санаторно-курортная сфера\n' +
                                                //                         '2. Гостиничный комплекс\n' +
                                                //                         '3. Туроператоры, турагентства, экскурсоводы"\n' +
                                                //                         '15\tРеспубликанское агентство лесного хозяйства\tВыполнение мероприятий по использованию, охране, защите, воспроизводству лесов, лесозаготовка, лесопереработка\n'
                                                //                 }
                                                //             ]
                                                //         }
                                                //     ]
                                                // },
                                            ]
                                        }
                                    }
                                },
                                {
                                    header: 'Файлы',
                                    body: {
                                        view: 'scrollview',
                                        scroll: 'y',
                                        autowidth: true,
                                        autoheight: true,
                                        body: {
                                            rows: [
                                                // view_section('Файлы'),
                                                {
                                                    id: 'filesListViewByType',
                                                }
                                            ]
                                        }
                                    }
                                },
                                {
                                    header: 'Утверждение',
                                    body: {
                                        view: 'scrollview',
                                        scroll: 'y',
                                        autowidth: true,
                                        autoheight: true,
                                        body: {
                                            rows: [
                                                view_section('Адресная информация'),
                                                {
                                                    id: 'resolutionComment',
                                                    view: 'textarea',
                                                    name: 'resolutionComment',
                                                    label: 'Комментарий',
                                                    labelPosition: 'top',
                                                    height: 300,
                                                    value: data.resolutionComment,
                                                },
                                                {
                                                    cols: [
                                                        {
                                                            view: 'button',
                                                            value: 'Отклонить',
                                                            click: () => changeRequestSubsidyStatus(false, data.id),
                                                        },
                                                        {
                                                            view: 'button',
                                                            value: 'Утвердить',
                                                            click: () => changeRequestSubsidyStatus(true, data.id),
                                                        }
                                                    ]
                                                }
                                            ]
                                        }
                                    }
                                },
                            ]
                        }
                    ],
                    data: data,
                }
            ]
        });

    })
})

function changeRequestSubsidyStatus(approve, id_request_subsidy) {
    const params = {
        resolutionComment: $$('resolutionComment').getValue(),
    };

    console.log(params);


    webix.ajax()
        .headers({ 'Content-Type': 'application/json' })
        .post(`../change_request_subsidy_status/${ id_request_subsidy }/${ approve }`, params)
        .then((data) => {});
}
