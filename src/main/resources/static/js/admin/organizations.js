const organizations = {
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
                                cols:[
                                    {
                                        view: 'search',
                                        id: 'search',
                                        maxWidth: 300,
                                        minWidth: 100,
                                        tooltip: 'после ввода значения нажмите Enter',
                                        placeholder: "Поиск по ИНН и названию",
                                        on: {
                                            onEnter: function () {
                                                reload();
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
                                                reload();
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
                        datafetch: 25,
                        columns: [
                            {id: "orgName", header: "Наименование организации/ИП", template: "#name#", adjust: true},
                            {id: "inn", header: "ИНН", template: "#inn#", adjust: true},
                            {id: "ogrn", header: "ОГРН", template: "#ogrn#", adjust: true},
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
                            },
                            {
                                id: 'deleted',
                                header: 'Удалена',
                                adjust: true,
                                template: function (obj, type, value) {
                                    if (value) {
                                        return '<span>Да</span>'
                                    } else {
                                        return '<span>Нет</span>'
                                    }
                                }
                            },
                        ],
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
                            }
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
}

function reload() {
    $$('organizations_table').clearAll();

    const url = 'cls_organizations';
    let params = '';

    const inn = $$('search').getValue();
    if (inn != '') {
        params = '?inn=' + inn;
    }

    const prescription = $$('prescription').getValue();
    if (prescription != '') {
        params += params == '' ? '?' : '&';
        params += 'id_prescription=' + prescription;
    }

    $$('organizations_table').load(url + params);
}
