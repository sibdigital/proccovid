webix.i18n.setLocale("ru-RU");

const windowHeight = window.innerHeight

const descrStep1 = '<span style=" height: auto; font-size: 0.8rem; color: #fff6f6">' +
    'Для восстановления доступа к Вашему личному кабинету, Вам необходимо ввести логин.' +
    '</span>';

const leftLayout = {
    id: "leftLayout",
    margin: 0,
    padding: {
        top: 55, bottom: 25
    },
    width: 400,
    css: {
        "background": "#475466 !important" //#2b334a
    },
    rows: [
        {
            view: "label",
            height: 200,
            id: "logo",
            align: "center",
            template: "<img src = \"logo.png\">"
            // template:"<img src ='${LOGO_PATH}'>"
        },
        {
            view: "label",
            id: "titleReg",
            label: `<span style="font-size: 1.5rem; color: #ccd7e6">Восстановление доступа</span>`,
            height: 50,
            align: "center"
        },
        {
            view: "label",
            id: "appNameReg",
            label: `<span style="font-size: 1.2rem; color: #ccd7e6">"${APPLICATION_NAME}"</span>`,
            height: 50,
            align: "center"
        },
        {
            view: "template",
            id: "description",
            css:{"background-color":"#475466", "text-align":"center", "padding-left":"2px","padding-right":"2px"},
            borderless: true,
            autoheight: true,
            template: descrStep1,
        },
    ]
}

const step1 = {
    rows: [
        {
            view: 'label',
            css: 'errorLabel',
            //height: 19,
            id: 'invalidMessagesStep1',
            borderless: true,
            autoheight: true,
            template:"<span style='padding: 2px;text-align: center; font-size: 0.8rem; color: red'></span>"
        },
        {
            view: 'text',
            name: 'searchLogin',
            id: 'searchLogin',
            minWidth: 250,
            labelPosition: 'top',
            label: 'Введите свой логин',
            placeholder: 'Логин',
        },
        {
            cols: [
                {
                    view: 'button',
                    css: 'myClass',
                    value: 'Отмена',
                    click: () => { window.location.href = 'login' }
                },
                {width: 5},
                {
                    id: 'send_btn',
                    view: 'button',
                    css: 'myClass',
                    value: 'Отправить пароль',
                    align: 'center',
                    click: toRecover
                }
            ]
        }
    ]
};

const rightLayout = {
    id: 'form',
    view: 'form',
    maxWidth: 450,
    width: 350,
    minWidth: 250,
    complexData: true,
    elements: [
        {},
        {
            id: "firstRow", rows:[]
        },
        {
            view: 'multiview',
            id: 'wizard',
            value: 0,
            cells: [
                step1,
                {
                    view: 'template',
                    id: 'descriptionStep3',
                    borderless: true,
                    autoheight: true,
                    template: '',
                }
            ]
        },
        {}
    ]
}

const regLayout = webix.ui({
    height: windowHeight,
    css: {"background-color": "#ccd7e6"},
    id: 'mainLayout',
    rows: [
        {
            id: 'topSpacer',
            gravity: 0.9,
        },
        {
            view: "align",
            align: "middle,center",
            body: {
                cols: [
                    {
                        view: "align",
                        align: "middle,center",
                        body: {
                            cols: [
                                leftLayout,
                                rightLayout,
                            ]
                        }
                    }
                ]
            }
        },
        {}
    ]
})

function checkLogin() {
    const login = $$('searchLogin').getValue();
    if (login === '') {
        $$('searchLogin').focus();
        $$("invalidMessagesStep1").setValue("Логин не введен");
        $$('searchLogin').hideProgress();
        return;
    }
}

function toRecover() {
    $$('send_btn').disable();

    checkLogin();

    let params = {
        'login': $$('searchLogin').getValue()
    }

    webix.ajax()
        .headers({'Content-type': 'application/json'})
        .post('recovery', JSON.stringify(params))
        .then(function (data) {
            const text = data.text();
            if (text === 'Ок') {
                webix.message(text === 'Ок' ? 'Письмо отправлено на вашу почту' : text);
                $$("descriptionStep3").setHTML('<span style="font-size: 1rem;text-align: center">На Ваш почтовый ящик отправлен новый пароль.</span>');
                $$("description").hide();
                $$("wizard").getChildViews()[1].show();
            } else if (text === 'Не удалось отправить письмо') {
                $$("invalidMessagesStep1").config.height = 35;
                $$("invalidMessagesStep1").resize()
                $$("invalidMessagesStep1").setValue('Не удалось отправить пароль на привязанный адрес электронной почты')
            } else {
                $$("invalidMessagesStep1").setValue(text)
            }
            $$('send_btn').enable();
        })
        .catch(function () {
            $$("invalidMessagesStep1").setValue('Не удалось отправить письмо');
            $$('send_btn').enable();
        })
}

webix.ready(function() {
    let clientScreenWidth = document.body.clientWidth;
    if (clientScreenWidth < 760) {
        $$("leftLayout").hide();
        $$("form").config.width = document.body.clientWidth-40;
        $$("titleReg").setValue(`<span style="font-size: 1.1rem; color: #475466">Восстановление доступа</span>`)
        $$("appNameReg").setValue(`<span style="font-size: 1.1rem; color: #475466">"${APPLICATION_NAME}"</span>`)
        $$("firstRow").addView($$("titleReg"),-1);
        $$("firstRow").addView($$("appNameReg"),-1);
        $$("firstRow").addView($$("description"),-1);
        $$("form").adjust();
        $$("form").resize();
    }
    webix.extend($$('searchLogin'), webix.ProgressBar);
    if (document.body.clientWidth < 480){
        regLayout.config.width = document.body.clientWidth; regLayout.resize();
    }
})
