function getBackButton(from = null, goTo = {}) {
    from = from ?? 'content';
    return {
        view: "button",
        id: "back",
        css: "webix_primary",
        type: "icon",
        width: 150,
        icon: "fas fa-arrow-left",
        label: "Назад",
        click: () => {
            webix.ui({
                id: from,
                rows: [
                    goTo
                ]
            }, $$(from))
        }
    }
}