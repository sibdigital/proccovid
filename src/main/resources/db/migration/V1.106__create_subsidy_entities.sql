create schema if not exists subs
;

create table subs.cls_subsidy -- справочник субсидий по аналогии с cls_type_request
(
    id serial not null
        constraint cls_subsidy_request_pkey
            primary key,
    name text,
    short_name text,
    is_deleted boolean default false,
    id_department integer
        constraint cls_subsidy_department_id_fk
            references cls_department,
    settings text,
    status_registration integer default 0 not null,
    status_visible integer default 0,
    begin_registration timestamp,
    end_registration timestamp,
    begin_visible timestamp,
    end_visible timestamp,
    sort_weight integer default 0,
    additional_fields jsonb,
    status_publication integer,
    time_publication timestamp,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    calendar_day_to_resolution integer default 0,
    work_day_to_resolution integer default 0
)
;

create table subs.tp_subsidy_okved -- табличная часть для справочника субсидий
-- задает по каким ОКВЭДАм или группам ОКВЭД может подаваться субсидия, при это учитывается тип организации
-- если тип - null, то по любым, если установлен, то по сочетанию ОКВЭД и типа,
-- если к тому же ОКВЭД надо добавить другой тип, то заводится новая запись в таблице
(
    id         serial  not null
        constraint cls_subsidy_okved_pkey
            primary key,
    is_deleted boolean default false,
    id_subsidy integer not null
        constraint fk_cls_subsidy_okved_cls_subsidy
            references subs.cls_subsidy,
    id_okved   uuid    not null
        constraint fk_cls_subsidy_okved_okved
            references okved,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    id_type_organization integer
)
;

create table subs.tp_subsidy_file -- файлы субсидии
-- нужны на тот случай, если для описания условий предоставления субсидии понадобится выложить какие-нибудь файлы:
--порядок предоставления, инструкцию и т.д
(
    id serial not null
        constraint cls_subsidy_file_pkey
            primary key,
    id_subsidy integer not null
        constraint fk_cls_subsidy_file_cls_subsidy
            references subs.cls_subsidy,
    is_deleted boolean default false,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    attachment_path text,
    file_name text,
    original_file_name text,
    file_extension varchar(16),
    hash text,
    file_size integer
);

create table subs.cls_subsidy_request_status -- справочник статусов субсидий
(
    id serial not null
        constraint cls_subsidy_file_pkey
            primary key,
    id_subsidy integer
        constraint fk_cls_subsidy_file_cls_subsidy
            references subs.cls_subsidy,
    is_deleted boolean default false,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    name text,
    short_name text,
    code varchar(15)
)
;
insert into subs.cls_subsidy_request_status (name, short_name, code) values ('Новая заявка', 'Новая', 'NEW')
;

create table subs.doc_subsidy_request --заявка на субсидию по аналогии с doc_request
(
    id serial not null
        constraint doc_subsidy_request_pkey
            primary key,
    id_organization integer not null
        constraint fk_org
            references cls_organization,
    id_department integer not null
        constraint doc_subsidy_request_cls_department_id_fk
            references cls_department,
    attachment_path text,
    id_status_review integer not null
        default (select id from subs.cls_subsidy_request_status where code = 'NEW' fetch first 1 rows only)
        constraint doc_subsidy_request_cls_subsidy_request_status_id_fk
            references subs.cls_subsidy_request_status,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    time_update timestamp,
    time_review timestamp,
    req_basis text default ''::text,
    resolution_comment text,
    old_department_id integer,
    id_processed_user integer
        constraint doc_subsidy_request_processed_user_id_fk
            references cls_user,
    id_reassigned_user integer
        constraint doc_subsidy_request_reassigned_user_id_fk
            references cls_user,
    id_type_request integer
        constraint doc_subsidy_request_cls_subsidy_id_fk
            references subs.cls_subsidy,
    additional_attributes jsonb,
    id_district integer
        constraint doc_subsidy_request_cls_district_id_fk
            references cls_district,
    status_activity integer default 1
)
;

create index subs.idx_subsidy_request_id_organization
	on doc_request (id_organization)
;

create index subs.idx_doc_subsidy_request_review_status
	on doc_request (id_department, status_review)
;

create table subs.tp_subsidy_request_file --файлы заявки
(
    id serial not null
        constraint tp_subsidy_request_file_pkey
            primary key,
    id_request integer not null
        constraint fk_doc_subsidy_request
            references subs.doc_subsidy_request,
    id_organization integer not null
        constraint fk_tp_subsidy_request_file_cls_organization
            references cls_organization,
    id_department integer not null -- для файлов ответов, которые дают министерства
        constraint fk_tp_subsidy_request_file_cls_organization
            references cls_department,
    id_processed_user integer -- пользователь ИОГВ, давший ответ
        constraint fk_tp_subsidy_request_file_processed_user_id_fk
            references cls_user,
    is_deleted boolean,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    attachment_path text,
    file_name text,
    original_file_name text,
    file_extension varchar(16),
    hash text,
    file_size integer,
    is_signature boolean default false, --если это эл. подпись, то true
    id_subsidy_request_file integer --ссылка на файл, который подписан этой подписьмю
        constraint fk_subsidy_request_file
            references subs.tp_subsidy_request_file
);

create index subs.idx_tp_subsidy_request_file
	on tp_subsidy_request_file (id_request);






