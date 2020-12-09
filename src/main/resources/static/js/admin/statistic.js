
const statistic = {
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
                    }
                ]
            }
        ]
    }
}