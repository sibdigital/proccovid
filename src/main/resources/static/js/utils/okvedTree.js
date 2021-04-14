let tree = {
    view: "tree",
    id: "tree",
    name: "tree",
    template: "{common.icon()} {common.checkbox()} {common.folder()} <span>#value#</span>",
    threeState: true,
    data: [
        {
            id: 'root',
            value: 'ОКВЭДЫ',
            // open: true,
            data: [
                {
                    id: '2014',
                    value: '2014',
                    webix_kids: true
                },
                {
                    id: '2001',
                    value: '2001',
                    webix_kids: true
                }
            ]
        }
    ],
    on: {
        onDataRequest: (parentNode) => {
            // console.log(parentNode);
            $$('tree').parse(
                webix.ajax().get(
                    'okved_tree', {parent_node: parentNode}
                ).then((data) => {
                    // console.log(data.json());
                    data = {
                        parent: parentNode,
                        data: data.json().map((e) => {
                            return {
                                id: e.id,
                                value: e.value,
                                webix_kids: true
                            }
                        })
                    };
                    return data;
                })
            );
            return false;
        }
    }
}

webix.ui(
    {
        view: 'scrollview',
        scroll: 'xy',
        body: {
            rows: [
                {
                    view: 'button',
                    css: 'webix_primary',
                    id: 'getCheckedValues',
                    value: 'check',
                    click: () => {
                        let checkedNodes = $$('tree').getChecked();
                        console.log(checkedNodes);
                    }
                },
                tree
            ]
        }
    }
);
