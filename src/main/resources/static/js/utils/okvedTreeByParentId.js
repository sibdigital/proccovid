let okvedTreeByParentId = (id = "tree") => {
    return {
        view: "tree",
        id: id,
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
            onDataRequest: async (parentNode) => {
                let treeView = $$(id);
                let checkBoxStatus = treeView.isChecked(parentNode);
                await treeView.parse(
                    webix.ajax().get(
                        'okved_tree_by_parent_id', {parent_node: parentNode}
                    ).then((data) => {
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

                /* if node was checked -> check node childs */
                checkBoxStatus && treeView.checkAll(parentNode);

                return false;
            }
        }
    }
}
