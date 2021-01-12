//define(['views/showform'], function(showform) {
// define(function() {
//
//     webix.i18n.setLocale("ru-RU");
//
    const DATE_FORMAT = webix.Date.dateToStr("%d.%m.%Y %H:%i:%s")
//
//     return function(param_url, status) {
    function requests(param_url, status, view_item_another_page) {
        return {
            autowidth: true,
            autoheight: true,
            rows: [
                {
                    view: 'datatable',
                    id: 'requests_table',
                    select: 'row',
                    navigation: true,
                    resizeColumn: true,
                    pager: 'Pager',
                    datafetch: 25,
                    columns: [
                        {
                            id: "orgName",
                            header: "Организация/ИП",
                            template: "#organization.name#",
                            adjust: true
                        },
                        {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                        {id: "ogrn", header: "ОГРН", template: "#organization.ogrn#", adjust: true},
                        {id: "typeRequest", header: "Вид деятельности", template: "#typeRequest.activityKind#", adjust: true},
                        {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                        {id: "time_Create", header: "Дата подачи", adjust: true, format: DATE_FORMAT },
                        {id: "personSlrySaveCnt", header: "Числ. с сохр. зп", adjust: true},
                        {id: "personOfficeCnt", header: "Числ. работающих", adjust: true},
                        {id: "personRemoteCnt", header: "Числе. удал. режим", adjust: true},
                    ],
                    scheme: {
                        $init: function (obj) {
                            obj.time_Create = obj.timeCreate.replace("T", " ") //dateUtil.toDateFormat(obj.timeCreate);
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
                        onItemDblClick: function (id) {
                            this.hideOverlay();
                            let data = $$('requests_table').getItem(id);

                            if (!view_item_another_page) {

                                // TODO временное условие, пока не перейдем на новую схему данных
                                if (data.docRequestPrescriptions && data.docRequestPrescriptions.length > 0) {
                                    showRequestForm(data.id);
                                    return;
                                }

                                let form = status == 0 ? showform(status) : showform_processed();

                                let reassignedUserName = '';
                                if (data.reassignedUser != null) {
                                    reassignedUserName = ' Переназначил: ' + data.reassignedUser.fullName + ' (' + data.reassignedUser.idDepartment.name + ')';
                                }

                                let queryWin = webix.ui({
                                    view: 'window',
                                    id: 'showQueryWin',
                                    head: {
                                        view: 'toolbar',
                                        elements: [
                                            {view: 'label', label: 'Просмотр заявки (id: ' + data.id + ').' + reassignedUserName},
                                            {
                                                view: 'icon', icon: 'wxi-close',
                                                click: function () {
                                                    $$('showQueryWin').close()
                                                }
                                            }
                                        ]
                                    },
                                    width: 1200,
                                    height: 800,
                                    position: 'center',
                                    item: data,
                                    modal: true,
                                    body: form,
                                    on: {
                                        'onShow': function () {
                                            let person_table_data = new webix.DataCollection({
                                                url: 'doc_persons/' + data.id
                                            })
                                            $$('person_table').sync(person_table_data);

                                            let addr_table_data = new webix.DataCollection({
                                                url: 'doc_address_fact/' + data.id
                                            })
                                            $$('addr_table').sync(addr_table_data);
                                            if (data.additionalAttributes) {
                                                if (data.additionalAttributes.isCheckingAgree) {
                                                    var v = {
                                                        view: 'checkbox',
                                                        name: 'additionalAttributes.isCheckingAgree',
                                                        labelPosition: 'top',
                                                        readonly: true,
                                                        labelRight: 'Ознакомлен, обязуется проверять сведения для размещения граждан'
                                                    };
                                                    $$('review_app_section').addView(v);
                                                }
                                            }

                                            let history_table_data = new webix.DataCollection({
                                                url: 'history_doc_request/' + data.organization.inn
                                            })
                                            $$('history_table').sync(history_table_data);

                                        }
                                    }
                                });

                                if (data.attachmentPath) {
                                    let paths = data.attachmentPath.split(',')

                                    let fileList = []
                                    paths.forEach((path, index) => {
                                        let filename = path.split('\\').pop().split('/').pop()
                                        if (filename != '' &&
                                            ((filename.toUpperCase().indexOf('.PDF') != -1) ||
                                                (filename.toUpperCase().indexOf('.ZIP') != -1)
                                            )) {
                                            filename = '<a href="' + LINK_PREFIX + filename + LINK_SUFFIX + '" target="_blank">'
                                                + filename + '</a>'
                                            fileList.push({id: index, value: filename})
                                        }
                                    })

                                    if (fileList.length > 0) {
                                        $$('filename').parse(fileList)
                                    } else {
                                        $$('filename_label').hide()
                                        $$('filename').hide()
                                    }
                                }

                                let actualizedRequestLink = "";
                                let actualizedRequestLinkLabel = "Предыдущая утвержденная заявка: ";
                                if (data.idActualizedRequest != null) {
                                    actualizedRequestLink = "<a target='_blank' href='/request/view?id=" + data.idActualizedRequest + "'>" + actualizedRequestLinkLabel + data.idActualizedRequest + "</a>";
                                    $$('actualizedRequestLink').setValue(actualizedRequestLink);
                                }
                                else {
                                    $$('actualizedRequestLink').setValue("");
                                }

                                if (data.organization.idTypeOrganization) {
                                    let typeOrg = data.organization.idTypeOrganization;
                                    if (typeOrg === 3) {
                                        $$('isSelfEmployed').setValue(1);
                                    }
                                }

                                $$('form').load('doc_requests/' + data.id);

                                webix.extend($$('show_layout'), webix.ProgressBar);

                                queryWin.show()
                            } else {

                                window.open('request/view?id=' + data.id);

                            }
                        }
                    },
                    url: param_url
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
    }
// })