let validatePasswordInputRules = {
    "password": (value) => {
        if(value.length < 8){
            $$("password").config.invalidMessage = "Длина пароля должна превышать 7 символов";
            return false
        }else if(!(/[0-9]/.test(value) && /[a-z]/i.test(value))){
            $$("password").config.invalidMessage = "Пароль должен содержать буквы и цифры";
            return false
        }else{
            return true
        }
    },
    "passwordConfirm":function (value){
        return value === $$("password").getValue()
    },
};

const newUserPasswordModal = {
    view: "window",
    id: "newUserPasswordModalId",
    minWidth: 200,
    maxWidth: 550,
    position: "center",
    modal: true,
    move: false,
    close: false,
    head: "Смена пароля при первом входе",
    body: {
        view: 'form',
        id: 'newPasswordFormId',
        rules: validatePasswordInputRules,
        rows: [
            {
                view: 'text',
                id: 'password',
                name: 'password',
                type: 'password',
                label: 'Пароль',
                labelPosition: 'top',
                required: true,
                attributes: {autocomplete: 'new-password'},
            },
            {
                view: 'text',
                id: 'passwordConfirm',
                name: 'passwordConfirm',
                type: 'password',
                label: 'Подтверждение пароля',
                labelPosition: 'top',
                required: true,
                attributes: {autocomplete: 'new-password'},
                invalidMessage: "Пароли не совпадают"
            },
            {
                cols: [
                    {},
                    {
                        view: 'button',
                        css: 'webix_primary',
                        value: 'Сохранить',
                        align: 'right',
                        click: function () {
                            let params = {
                                'password': $$('password').getValue()
                            };
                            if ($$('newPasswordFormId').validate()) {
                                webix.ajax().headers({
                                    'Content-Type': 'application/json'
                                }).post('save_user_password?password=' + $$('password').getValue())
                                .then(function (data) {
                                    if (data.text() === 'Пароль сохранен') {
                                        $$('newUserPasswordModalId').hide();
                                        window.location.reload(true)
                                    } else {
                                        webix.message(data.text(), 'error');
                                    }
                                });
                            }
                        }
                    }
                ]
            }
        ]
    }
}

function getUserStatus(){
    var xhr = webix.ajax().sync().get('get_user_status');
    var jsonResponse = JSON.parse(xhr.responseText);

    return jsonResponse.status;
}
