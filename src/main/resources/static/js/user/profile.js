let profileRules = {
    "new_pass":(val) => {
        if(val.length !== 0 ){
            if(val.length < 8){
                $$("new_pass").config.invalidMessage = "Длина пароля должна превышать 7 символов";
                return false
            }else if(!(/[0-9]/.test(val) && /[a-z]/i.test(val))){
                $$("new_pass").config.invalidMessage = "Пароль должен содержать буквы и цифры";
                return false
            }
        }
        return true
    },
    "retry_pass":(val) => {
        if(val === $$("new_pass").getValue()){
            return true;
        }else {
            $$("retry_pass").config.invalidMessage = "Пароли не совпадают";
            return false;
        }
    },
}

const profile = {
    view: 'form',
    id: 'common_info_form',
    complexData: true,
    rules: profileRules,
    elements: [
        view_section('Данные пользователя'),
        {
            cols: [
                {
                    view: 'text',
                    name: 'idDepartment.name',
                    label: 'Подразделение',
                    labelPosition: 'top',
                    readonly: true,
                },
                {
                    view: 'text',
                    name: 'district.name',
                    label: 'Район',
                    labelPosition: 'top',
                    readonly: true,
                },
            ]
        },
        { view: 'text', label: 'Фамилия', labelPosition: 'top', name: 'lastname', readonly: true,},
        { view: 'text', label: 'Имя', labelPosition: 'top', name: 'firstname', readonly: true,},
        { view: 'text', label: 'Отчество', labelPosition: 'top', name: 'patronymic', readonly: true,},
        {
            cols: [
                { view: 'text', label: 'Адрес электронной почты', labelPosition: 'top', name: 'email', readonly: true,},
                { view: 'text', label: 'Логин', labelPosition: 'top', name: 'login', readonly: true,},
            ]
        },
        view_section("Управление личным кабинетом"),
        {
            view: 'text',
            id: 'current_pass',
            width: 300,
            label: 'Введите текущий пароль',
            tooltip: "После ввода пароля нажмите Enter",
            labelPosition: 'top',
            type: 'password',
            required: true,
            on:{
                onEnter() {
                    let param = $$('current_pass').getValue()
                    webix.ajax().headers({'Content-Type': 'application/json'})
                        .post('check_current_pass', param).then(function (data) {
                        if (data.text() === "Пароли не совпадают") {
                            $$("save_user_pass_changes").disable();
                            $$("invalidMessages").setValue("Введен неверный текущий пароль");
                            $$('invalidMessages2').setValue("");
                        }else{
                            $$("invalidMessages").setValue("")
                            $$('invalidMessages2').setValue("");
                            $$("save_user_pass_changes").enable();
                        }
                    });
                }
            }
        },
        {
            view: 'label',
            height: 19,
            id: 'invalidMessages',
            borderless: true,
            autoheight: true,
            template:"<span style='padding: 2px;text-align: center; font-size: 0.8rem; color: red'></span>"
        },
        {
            cols:[
                {
                    view: 'text',
                    id: 'new_pass',
                    name: 'new_pass',
                    label: 'Новый пароль',
                    labelPosition: 'top',
                    type: 'password',
                    maxWidth: 300
                },
                {
                    view: 'text',
                    id: 'retry_pass',
                    name: 'retry_pass',
                    label: 'Подтвердите новый пароль',
                    labelPosition: 'top',
                    type: 'password',
                    maxWidth: 300
                },
            ]
        },
        {
            view: 'label',
            height: 19,
            id: 'invalidMessages2',
            borderless: true,
            autoheight: true,
            template:"<span style='padding: 2px;text-align: center; font-size: 0.8rem; color: green'></span>"
        },
        {
            view: 'button',
            css: 'webix_primary',
            id: 'save_user_pass_changes',
            width: 300,
            value: 'Сохранить изменения',
            disabled: true,
            click: () => {
                if($$("common_info_form").validate()){
                    let newPass = $$("retry_pass").getValue()
                    webix.ajax().headers({'Content-Type': 'application/json'})
                        .post('edit_user_pass?new_pass=' + newPass).then(function (response) {
                        if(response.json().status == "server"){
                            webix.message(response.json().cause,"success");
                            $$("save_user_pass_changes").disable();
                            $$('invalidMessages2').setValue("Пароль успешно изменен");
                            $$('current_pass').setValue("");
                            $$('new_pass').setValue("");
                            $$('retry_pass').setValue("");
                        }else{
                            webix.message("Не удалось обновить пароль","error")
                        }
                    });
                }
            }
        },
    ],
    url: 'profile',
}
