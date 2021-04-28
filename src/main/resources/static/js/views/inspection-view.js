webix.i18n.setLocale("ru-RU");

function getFileIcon(fileExtension) {
    let docImg;
    switch (fileExtension) {
        case '.zip':
            docImg = 'zip.png';
            break;
        case '.pdf':
            docImg = 'pdf.png';
            break;
        case '.jpeg':
            docImg = 'jpg.png';
            break;
        case '.jpg':
            docImg = 'jpg.png';
            break;
        case '.doc':
            docImg = 'doc.png';
            break;
        case '.docx':
            docImg = 'doc.png';
            break;
        default:
            docImg = 'file.png';
            break;
    }
    return docImg;
}

function inspectionForm(inspectionId) {
    return {
        view: 'form',
        id: 'inspectionForm',
        elements: [
            {
                view: 'datepicker',
                label: 'Дата проведения контрольно-надзорного мероприятия',
                labelPosition: 'top',
                name: 'dateOfInspection',
                readonly: true,
            },
            {
                view: 'text',
                label: 'Результат контрольно-надзорного мероприятия',
                labelPosition: 'top',
                name: "inspectionResultName",
                readonly: true
            },
            {
                view: 'text',
                label: 'Контролирующий орган',
                labelPosition: 'top',
                name: "controlAuthorityName",
                readonly: true
            },
            {
                view: 'textarea',
                label: 'Комментарий',
                labelPosition: 'top',
                name: 'comment',
                minHeight: 200,
                readonly: true,
            },
            {
                view: "dataview",
                label: 'Прикрепленные файлы',
                labelPosition: 'top',
                id: 'inspection_docs_grid',
                css: 'contacts',
                scroll: 'y',
                minWidth: 320,
                minHeight: 200,
                select: 1,
                template: function (obj) {
                    let docImg = getFileIcon(obj.fileExtension);
                    let downloadTime = obj.timeCreate.substr(11, 8) + ', ' + obj.timeCreate.substr(0, 10)
                    let result = "<div class='overall'>" +
                        "<div>" +
                        "<img style='position: absolute' src = ../imgs/" + docImg + "> " +
                        "<div class='doc_title'>" + obj.originalFileName.slice(0, -4) + "</div>";
                        result += "<div class='doc_time_create'>" + downloadTime + "</div>" +
                        "<div class='download_docs'>" +
                        "<a style='text-decoration: none; color: #1ca1c1' href=" + LINK_PREFIX + obj.fileName + LINK_SUFFIX + obj.fileExtension + " download>Скачать файл</a>" +
                        "</div>" +
                        "</div>" +
                        "</div>"
                    return result;
                },
                url: 'inspection_files/' + inspectionId,
                xCount: 2,
                type: {
                    height: "auto",
                    width: "auto",
                    float: "right"
                },
                scheme: {},
                on: {
                    onBeforeLoad: () => {
                        if (document.body.clientWidth < 980) {
                            $$('inspection_docs_grid').config.xCount = 1;
                        }
                    },
                    onAfterLoad: () => {
                        if ($$('inspection_docs_grid').count() === 0) {
                            $$('inspection_docs_grid').hide();
                        }
                    },
                }
            },
        ]
    }
}

webix.ready(function() {
    var xhr = webix.ajax().sync().get('../inspection/' + ID);
    var inspection = JSON.parse(xhr.responseText);

    var inspectionResultName = (inspection.inspectionResult) ? inspection.inspectionResult.name : "";
    var controlAuthorityName = (inspection.controlAuthority) ? inspection.controlAuthority.name : "";
    var data = {
        'dateOfInspection': inspection.dateOfInspection,
        'inspectionResultName': inspectionResultName,
        'controlAuthorityName': controlAuthorityName,
        'comment': inspection.comment
    }
    setTimeout(function () {
        webix.ui(inspectionForm(inspection.id));
        $$('inspectionForm').parse(data);
    }, 100);
})