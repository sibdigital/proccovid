const inspectionReports = {
    id: 'inspectionReportsId',
    view: 'scrollview',
    scroll: 'xy',
    borderless: true,
    body: {
        // type: 'space',

        rows: [
            {
                autowidth: true,
                autoheight: true,
                rows: [
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
                    }
                ]
            }
        ]
    }
}
