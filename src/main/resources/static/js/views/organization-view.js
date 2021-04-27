webix.i18n.setLocale("ru-RU");


webix.ready(function() {
    var xhr = webix.ajax().sync().get('../cls_organization/' + ID);
    var data = JSON.parse(xhr.responseText);
    setTimeout(function () {
        let okveds = data.regOrganizationOkveds
        webix.ui(organizationForm(data))
        if (okveds.length > 0) {
            for (let i in okveds) {
                let status = okveds[i].main;
                let listId;
                if (status) {
                    listId = "regOrganizationMainOkveds";
                } else {
                    listId = "regOrganizationOtherOkveds";
                }
                $$(listId).add({
                    kindCode: okveds[i].regOrganizationOkvedId.okved.kindCode,
                    kindName: okveds[i].regOrganizationOkvedId.okved.kindName
                })
            }
        }
        $$("organization_form").parse(data)
    }, 100);
})
