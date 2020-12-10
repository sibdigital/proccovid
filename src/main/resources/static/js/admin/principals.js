
const principals = {
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
                        view: 'datatable',
                        id: 'principals_table',
                        minHeight: 570,
                        select: 'row',
                        navigation: true,
                        resizeColumn: true,
                        pager: 'Pager',
                        datafetch: 25,
                        columns: [
                            {id: "inn", header: "ИНН", template: "#organization.inn#", adjust: true},
                            {id: "email", header: "Email", template: "#organization.email#", adjust: true},
                            {id: "orgPhone", header: "Телефон", template: "#organization.phone#", adjust: true},
                            {id: "orgName", header: " Наименование организации/ИП", template: "#organization.name#", adjust: true},
                        ],
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
                        },
                        url: 'cls_principals'
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
            },
            {
                view: 'checkbox',
                label: 'Выбрать всех',
                labelWidth: 'auto',
                on: {
                    onChange(newVal, oldVal) {
                        if (newVal === 1) {
                            $$('button_send').enable()
                        } else {
                            $$('button_send').disable()
                        }
                    }
                }
            },
            {
                view: 'button',
                id: 'button_send',
                value: 'Отправить приглашение',
                disabled: true,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email', 'type=123').then(function (data) {
                        webix.message(data.text());
                    });
                }
            },
            {
                view: 'button',
                id: 'button_send_message',
                value: 'Отправить сообщение',
                disabled: false,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email_message', 'type=1402').then(function (data) {
                        webix.message(data.text());
                    });
                }
            },
            {
                view: 'button',
                id: 'button_send_message24',
                value: 'Отправить сообщение (не получившие в последние 24 часа)',
                disabled: false,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email_message24', 'type=1402').then(function (data) {
                        webix.message(data.text());
                    });
                }
            },
            {
                view: 'button',
                id: 'button_send_test_message',
                value: 'Отправить тестовое сообщение',
                disabled: false,
                click: function () {
                    this.disable();
                    webix.ajax().get('send_email_test_message', 'type=1402').then(function (data) {
                        webix.message(data.text());
                    });
                }
            }
        ]
    }
}