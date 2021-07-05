function openMetatron(token, refreshToken, type, userId, redirectUri) {

    var target = 'metatron';

    var formName = 'metatronForm';

    let existForm = document.getElementsByName(formName)[0];

    if (existForm) {

        existForm.remove();

    }

    let form = document.createElement('form');

    form.setAttribute('name', formName);

    form.setAttribute('method', 'post');

    form.setAttribute('action', 'http://188.72.76.57:8180/api/sso?token='+token+'&refreshToken='+refreshToken+'&type='+type+'&userId='+userId+'&forwardUrl='+redirectUri);
    console.log('http://188.72.76.57:8180/api/sso?token='+token+'&refreshToken='+refreshToken+'&type='+type+'&userId='+userId+'&forwardUrl='+redirectUri)
    form.setAttribute('target', target);

    document.getElementsByTagName('body')[0].appendChild(form);

    window.open('', target);

    form.submit();

}

const metatronMapDashboard = {
    body: {
        type: "space",
        autowidth: true,
        rows: [
            {
                template: '<iframe frameborder="0" name="metatron" width="100%" height="100%"></iframe>'
            },
        ]

    }
}