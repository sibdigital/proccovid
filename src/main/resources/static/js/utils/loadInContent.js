function loadInContent(view = null) {
    webix.ui({
        id: 'content',
        rows: [
            view
        ]
    }, $$('content'))
}