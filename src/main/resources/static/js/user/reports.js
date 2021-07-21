
function UserReports(userRoles) {
    let isAdmin = userRoles.includes("ADMIN");
    let isUser = userRoles.includes("USER");
    let isViol = userRoles.includes("VIOLAT");
    let isKnd = userRoles.includes("KND");
    let isSubsidySupport = userRoles.includes("SUBSIDY_SUPPORT");

    return {
        id: 'statisticId',
        view: 'scrollview',
        scroll: 'y',
        body: {
            // type: 'space',
            rows: [
                {
                    autowidth: true,
                    autoheight: true,
                    rows: [
                        {
                            view: 'label',
                            hidden: !(isUser || isAdmin),
                            label: "<a href='statistic' target='_blank'>Статистика по заявкам</a>"
                        },
                        {
                            view: 'label',
                            hidden: !isAdmin,
                            label: "<a href='dacha/statistic' target='_blank'>Статистика по заявкам от дачников</a>"
                        },
                        {
                            view: 'label',
                            hidden: !isAdmin,
                            label: "<a href='barber/statistic' target='_blank'>Статистика по заявкам от парикмахерских</a>"
                        },
                        {
                            view: 'label',
                            hidden: !isAdmin,
                            label: "<a href='actualOrganizations/statistic' target='_blank')>Статистика по актуальным заявкам по организациям</a>"
                        },
                        {
                            view: 'label',
                            hidden: !isAdmin,
                            label: "<a href='actualDepartments/statistic' target='_blank'>Статистика по актуальным заявкам по подразделениям</a>"
                        },
                        {
                            view: 'label',
                            hidden: !isAdmin,
                            label: "<a href='numberOfSubscribersForEachMailing/statistic' target='_blank'>Количество подписчиков на каждый вид рассылки</a>"
                        },
                        {
                            view: 'label',
                            hidden: !isAdmin,
                            label: "<a href='numberOfMailsSent/statistic' target='_blank'>Количество отправленных сообщений</a>"
                        },
                        {
                            view: 'label',
                            hidden: !(isKnd || isAdmin),
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
                            hidden: !(isKnd || isAdmin),
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
                            hidden: !(isUser || isAdmin),
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
                            hidden: !(isUser || isAdmin),
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
                            hidden: !(isUser || isAdmin),
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
                            hidden: !(isSubsidySupport || isAdmin),
                            label: "<a href='' onclick='return false'>Отчет по количеству заявок на субсидию в разрезе ОКВЭД\n</a>",
                            click: () => {
                                webix.ui({
                                    id: 'content',
                                    rows: [
                                        webix.copy(requestSubsidyCntByOkvedsReport)
                                    ]
                                }, $$("content"));
                            },
                        }
                    ]
                }
            ]
        }
    }
}
