
const statistic = {
    id: 'statisticId',
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
                        view: 'label',
                        label: "<a href='statistic' target='_blank'>Статистика по заявкам</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='dacha/statistic' target='_blank'>Статистика по заявкам от дачников</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='barber/statistic' target='_blank'>Статистика по заявкам от парикмахерских</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='actualOrganizations/statistic' target='_blank')>Статистика по актуальным заявкам по организациям</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='actualDepartments/statistic' target='_blank'>Статистика по актуальным заявкам по подразделениям</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='numberOfSubscribersForEachMailing/statistic' target='_blank'>Количество подписчиков на каждый вид рассылки</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='numberOfMailsSent/statistic' target='_blank'>Количество отправленных сообщений</a>"
                    },
                    {
                        view: 'label',
                        label: "<a href='' onclick='return false'>Отчет по контрольно-надзорным мероприятиям</a>",
                        click: () => {
                            webix.ui({
                                id: 'content',
                                rows: [
                                    webix.copy(inspectionReport)
                                ]
                            }, $$("content"));
                        },
                    },
                    {
                        view: 'label',
                        label: "<a href='' onclick='return false'>Отчет о количестве проверок\n</a>",
                        click: () => {
                            webix.ui({
                                id: 'content',
                                rows: [
                                    webix.copy(inspectionCountReport)
                                ]
                            }, $$("content"));
                        },
                    },
                    {
                        view: 'label',
                        label: "<a href='' onclick='return false'>Отчет о количестве сотрудников на удаленке и в офисе\n</a>",
                        click: () => {
                            webix.ui({
                                id: 'content',
                                rows: [
                                    webix.copy(remoteCntReport)
                                ]
                            }, $$("content"));
                        },
                    },
                    {
                        view: 'label',
                        label: "<a href='' onclick='return false'>Отчет по количеству сотрудников в организации\n</a>",
                        click: () => {
                            webix.ui({
                                id: 'content',
                                rows: [
                                    webix.copy(cntRemoteWithOkvedsReport)
                                ]
                            }, $$("content"));
                        },
                    },
                    {
                        view: 'label',
                        label: "<a href='' onclick='return false'>Отчет по количествам в разрезе ОКВЭД\n</a>",
                        click: () => {
                            webix.ui({
                                id: 'content',
                                rows: [
                                    webix.copy(organizationCountByOkvedReport)
                                ]
                            }, $$("content"));
                        },
                    },
                    {
                        view: 'label',
                        label: "<a href='' onclick='return false'>Статистика по количеству организаций в районах\n</a>",
                        click: () => {
                            let data = new FormData();
                            data.append("grant_type", "password");
                            data.append("scope", "read");
                            data.append("username", "admin");
                            data.append("password", "admin");

                            let xhr = new XMLHttpRequest();
                            xhr.withCredentials = true;
                            xhr.open("POST", "http://188.72.76.57:8180/oauth/token");
                            xhr.setRequestHeader("Authorization", "Basic cG9sYXJpc19jbGllbnQ6cG9sYXJpcw==");
                            xhr.send(data);

                            xhr.onload = function () {
                                if (xhr.readyState === xhr.DONE) {
                                    if (xhr.status === 200) {
                                        let responseJSON = JSON.parse(xhr.response);
                                        webix.ui({
                                            id: 'content',
                                            rows: [
                                                webix.copy(metatronMapDashboard)
                                            ]
                                        }, $$("content"));
                                        openMetatron(responseJSON.access_token,responseJSON.refresh_token,'bearer','admin',
                                            'http://188.72.76.57:8180/app/v2/embedded/dashboard/83453411-721c-4063-9813-d066104c71c9');

                                    }
                                }
                            };
                        }
                    }
                ]
            }
        ]
    }
}
