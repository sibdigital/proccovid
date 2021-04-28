webix.i18n.setLocale("ru-RU");

const controlAuthorityForm = {
    view: "form",
    id: "controlAuthorityForm",
    elements: [
        {
            type: "space",
            rows: [
                {
                    view: 'text',
                    id: 'controlAuthorityParent',
                    name: 'controlAuthorityParentName',
                    css: 'smallText',
                    label: "Группа",
                    readonly: true,
                },
                {
                    view: "text",
                    id: "name",
                    name: "name",
                    label: "Наименование",
                    labelWidth: 120,
                    readonly: true,
                },
                {
                    view: "text",
                    id: "shortName",
                    name: "shortName",
                    label: "Сокращенное наименование",
                    labelWidth: 220,
                    readonly: true,
                },
                {
                    cols: [
                        {
                            view: 'text',
                            id: 'weight',
                            name: 'weight',
                            label: 'Вес',
                            labelWidth: 100,
                            readonly: true,
                        },
                        {gravity: 3}
                    ]
                },
            ]
        },
        {}
    ]
}

webix.ready(function() {
    var xhr = webix.ajax().sync().get('../control_authority/' + ID);
    var data = JSON.parse(xhr.responseText);
    data.controlAuthorityParentName = data.controlAuthorityParent.name;

    // var inspectionResultName = (inspection.inspectionResult) ? inspection.inspectionResult.name : "";
    // var controlAuthorityName = (inspection.controlAuthority) ? inspection.controlAuthority.name : "";
    // var data = {
    //     'dateOfInspection': inspection.dateOfInspection,
    //     'inspectionResultName': inspectionResultName,
    //     'controlAuthorityName': controlAuthorityName,
    //     'comment': inspection.comment
    // }
    setTimeout(function () {
        webix.ui(controlAuthorityForm);
        $$('controlAuthorityForm').parse(data);
    }, 100);
})