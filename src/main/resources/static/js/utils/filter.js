// define(function () {
//     return filter = {
    filter = {
        searchBar: function (tablename) {
            return {
                view: 'search',
                id: 'search',
                maxWidth: 300,
                minWidth: 100,
                tooltip: 'после ввода значения нажмите Enter',
                placeholder: "Поиск по ИНН и названию",
                on: {
                    // onTimedKeyPress: function () {
                    //     let text = this.getValue().toLowerCase();
                    //     let table = $$(tablename);
                    //     if (!text) {
                    //         table.filter()
                    //     }
                    //     else {
                    //         let columns = table.config.columns
                    //         table.filter(function (obj) {
                    //             let flag = 0
                    //             if(obj.organization.inn.indexOf(text) !== -1) flag += 1
                    //             if(obj.organization.name.toUpperCase().indexOf(text.toUpperCase()) !== -1) flag += 1
                    //             return flag > 0 ? true : false
                    //         })
                    //     }
                    // }

                    //onTimedKeyPress: function () {
                    onEnter: function () {
                        let text = this.getValue().toLowerCase();
                        let table = $$(tablename);

                        $$('tabbar').callEvent('onChange', [$$('tabbar').getValue()])
/*
                        if (!text) {
                            table.clearAll()
                            table.load(table.config.url)
                        }
                        else {
                            table.clearAll();
                            table.load(table.config.url + '/' +  text)
                        }
*/
                    }
                }
            }
        }
    }
// })