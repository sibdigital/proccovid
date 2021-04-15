/*FIX BOOTSTRAP CLICK INNER*/
$(document).on("click.bs.dropdown.data-api", ".noclose",
    (e) => { e.stopPropagation() }
);

/*FIX WEBIX SELECT CLICK EVENT INNER DROPDOWN PANEL*/
$(document).on("click.bs.dropdown.data-api",".webix_popup",
    (e) => { e.stopPropagation() }
);

/*FIX WEBIX CALENDAR CLICK EVENTS INNER DROPDOWN PANEL*/
$(document).on("click.bs.dropdown.data-api.webix_popup.webix_calendar",".webix_cal_month, .webix_cal_header, .webix_cal_body, .webix_cal_footer",
    (e) => { e.stopPropagation() }
);

const get_group_filter_btns = (filter_data, reload_func) => {
    let result_html = `<div class="filter_view">`;
    filter_data.map(panel =>
        result_html +=
            `<div id="cart" style="margin-left:12px" class="input-group" id="adv-search">
                <button id="` + panel.id + `" onclick=get_webix_object_by_css("` + panel.id + `","` + panel.css + `") aria-expanded="false" class="btn btn-default dropdown-toggle btn-filter" data-toggle="dropdown" type="button">
                    ` + panel.name + `<span class="caret"></span></button>
                <div id="dropMenu" style="padding: 0;" class="dropup dropdown-menu" role="menu">
                    <button aria-expanded="false" class="btn btn-default dropdown-toggle inner-btn-filter" data-toggle="collapse" type="button">
                                ` + panel.name + `<span class="caret"></span></button>
                    <form class="form-horizontal noclose" role="form" onsubmit="$('.dropdown-menu').removeClass('show'); return false;"> 
                        <div id="test2" class="form-group form-group--` + panel.css + `">
                        </div>
                    </form>
                </div>
        </div>`
    )

    result_html += `<div class="btn-group">
                        <button id="clear_filter_` + reload_func.name + `" type="button" class="btn btn-default filter-func-btn" data-toggle="tooltip" data-placement="bottom" title="Сбросить фильтры">
                            Сбросить
                            <i class="fas fa-redo fa-sm"></i>
                        </button>
                        <button id="filter_` + reload_func.name + `" type="button" class="btn btn-default filter-func-btn">
                            Найти
                            <i class="fas fa-filter fa-sm"></i>                               
                        </button>
                    </div>
                </div>`

    $(document).on("click","#clear_filter_" + reload_func.name, function (){
        clear_filter_fields(filter_data);
        reload_func();
    });

    $(document).on("click","#filter_" + reload_func.name, function (){
        reload_func();
    });
    return result_html;
}

const get_webix_object_by_css = (id, css) => {
    $$(id).show();
    $('.form-group--' + css).append($('.' + css));
}

const clear_filter_fields = (filter_data) => {
    filter_data.forEach(item => {
        if($$(item.id).getChildViews().length > 0) {
            $$(item.id).getChildViews().forEach(item => $$(item.config.id).setValue(""));
        } else {
            $$(item.id).setValue("");
        }
    });
}
