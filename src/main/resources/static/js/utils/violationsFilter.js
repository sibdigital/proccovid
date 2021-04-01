/*FIX BOOTSTRAP CLICK INNER*/
$(document).on("click.bs.dropdown.data-api", ".noclose", function (e) { e.stopPropagation() });

/*FIX WEBIX SELECT CLICK EVENT INNER DROPDOWN PANEL*/
$(document).on("click.bs.dropdown",".webix_popup", function (e) { e.stopPropagation() });

/*FIX WEBIX CALENDAR CLICK EVENTS INNER DROPDOWN PANEL*/
$(document).on("click.bs.dropdown.webix_popup.webix_calendar",".webix_cal_month", function (e) { e.stopPropagation() });
$(document).on("click.bs.dropdown.webix_popup.webix_calendar",".webix_cal_header", function (e) { e.stopPropagation() });
$(document).on("click.bs.dropdown.webix_popup.webix_calendar",".webix_cal_body", function (e) { e.stopPropagation() });
$(document).on("click.bs.dropdown.webix_popup.webix_calendar",".webix_cal_footer", function (e) { e.stopPropagation() });

const get_group_filter_btns = (filter_data, type) => {
    let result_html = '';
    filter_data.map(panel =>
        result_html +=
            `<div id="cart" style="margin-left:12px" class="input-group noclose" id="adv-search">
                <button id="` + panel.id + `" onclick=get_webix_object_by_css("` + panel.id + `","` + panel.css + `") aria-expanded="false" class="btn btn-default dropdown-toggle btn-filter" data-toggle="dropdown" type="button">
                    ` + panel.name + `<span class="caret"></span></button>
                <div style="padding: 0;" class="dropup dropdown-menu noclose" role="menu">
                    <form class="form-horizontal" role="form"> 
                        <div id="test2" class="form-group form-group--` + panel.css + `">
                            <button aria-expanded="false" class="btn btn-default dropdown-toggle inner-btn-filter" data-toggle="collapse" type="button">
                                ` + panel.name + `<span class="caret"></span></button>
                        </div>
                    </form>
                </div>
        </div>`
    )

    result_html += `<div class="btn-group">
                        <button onclick=drop_filters("` + type + `") type="button" class="btn btn-default filter-func-btn" data-toggle="tooltip" data-placement="bottom" title="Сбросить фильтры">
                            Сбросить
                            <i class="fas fa-redo fa-sm"></i>
                        </button>
                        <button onclick=filter_data_by_type("` + type + `") type="button" class="btn btn-default filter-func-btn">
                            Найти
                            <i class="fas fa-filter fa-sm"></i>                               
                        </button>
                    </div>`

    console.log(result_html)

    return result_html;
}

const filter_data_by_type = (type) => {
    type === 'person' ? reloadPersonViolations() : reloadViolations();
}

const get_webix_object_by_css = (id, css) => {
    $$(id).show();
    $('.form-group--' + css).append($('.' + css));
}

const drop_filters = (type) => {
    if (type === 'person') {
        $$("search_lastname").setValue("");
        $$("search_firstname").setValue("");
        $$("search_patronymic").setValue("");
        $$("search_passportData").setValue("");
        $$("search_numberFile").setValue("");
        $$("search_district").setValue("");
    } else {
        $$("search_district").setValue("");
        $$("search_beginDateRegOrg").setValue("");
        $$("search_endDateRegOrg").setValue("");
        $$("search_numberFile").setValue("");
        $$("search_name").setValue("");
        $$("search_inn").setValue("");
        $$("search_district").setValue("");
    }
}