create schema if not exists subs
;

create table cls_file_type -- справочник типов документов/файлов,
-- которые прикладываекются к завявке: приказы, доверенности, ведомости и тд
(
    id serial not null
        constraint cls_file_type_pkey
            primary key,
    is_deleted boolean default false,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    name text,
    short_name text,
    code varchar(15)
)
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
    id_type_organization integer,
    is_require_signature_verification boolean default true --обязательна проверка эл. подписи для этого типа подателей заявок
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

create table subs.tp_required_subsidy_file -- ТРЕБУЕМЫЕ для подачи субсии файлы
-- используется для того, чтобы сформировать список файлоы,
-- которые необходимо прикрпеить к субсиидии в tp_request_subsidy_file
(
    id serial not null
        constraint tp_required_subsidy_file_pkey
            primary key,
    id_subsidy integer not null
        constraint fk_tp_required_subsidy_file_cls_subsidy
            references subs.cls_subsidy,
    id_file_type integer not null
        constraint fk_tp_required_subsidy_file_cls_file_type
            references cls_file_type,
    is_deleted boolean default false,
    is_required boolean default false, --обязателен или нет для прикрепления
    time_create timestamp default CURRENT_TIMESTAMP not null,
    comment text
);

create table subs.cls_subsidy_request_status -- справочник статусов субсидий
(
    id serial not null
        constraint cls_subsidy_request_status_pkey
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

create table subs.doc_request_subsidy --заявка на субсидию по аналогии с doc_request
(
    id serial not null
        constraint doc_request_subsidy_pkey
            primary key,
    id_organization integer not null
        constraint fk_org
            references cls_organization,
    id_department integer not null
        constraint doc_request_subsidy_cls_department_id_fk
            references cls_department,
    attachment_path text,
    id_subsidy_request_status integer not null--статус рассмотрения заявки, аналог status review в doc_request
        constraint doc_request_subsidy_cls_subsidy_request_status_id_fk
            references subs.cls_subsidy_request_status,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    time_update timestamp,
    time_review timestamp,
    req_basis text default ''::text,
    resolution_comment text,
    old_department_id integer,
    id_processed_user integer
        constraint doc_request_subsidy_processed_user_id_fk
            references cls_user,
    id_reassigned_user integer
        constraint doc_request_subsidy_reassigned_user_id_fk
            references cls_user,
    id_subsidy integer not null--аналогия с id_type_request в doc_request
        constraint doc_request_subsidy_cls_subsidy_id_fk
            references subs.cls_subsidy,
    additional_attributes jsonb,
    id_district integer
        constraint doc_request_subsidy_cls_district_id_fk
            references cls_district,
    status_activity integer default 1
)
;

create index idx_subsidy_request_id_organization
	on subs.doc_request_subsidy (id_organization)
;

create index idx_doc_request_subsidy_review_status
	on subs.doc_request_subsidy (id_department, id_subsidy_request_status)
;

create table subs.tp_request_subsidy_file --файлы заявки
(
    id serial not null
        constraint tp_request_subsidy_file_pkey
            primary key,
    id_request integer not null
        constraint fk_doc_request_subsidy
            references subs.doc_request_subsidy,
    id_organization integer not null
        constraint fk_tp_request_subsidy_file_cls_organization
            references cls_organization,
    id_department integer not null -- для файлов ответов, которые дают министерства
        constraint fk_tp_request_subsidy_file_cls_department
            references cls_department,
    id_processed_user integer -- пользователь ИОГВ, давший ответ
        constraint fk_tp_request_subsidy_file_processed_user_id_fk
            references cls_user,
    id_file_type integer not null
        constraint fk_tp_required_subsidy_file_cls_file_type
            references cls_file_type, --тип файла согласно требуемым для подачи файлам
    is_deleted boolean,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    attachment_path text,
    file_name text,
    view_file_name text,
    original_file_name text,
    file_extension varchar(16),
    hash text,
    file_size integer,
    is_signature boolean default false, --если это эл. подпись, то true
    id_subsidy_request_file integer --ссылка на файл, который подписан этой подписьмю
        constraint fk_subsidy_request_file
            references subs.tp_request_subsidy_file
);

create table subs.reg_verification_signature_file --таблица с результатаами проверки эл. подписи файлов
(
    id serial not null
        constraint reg_verification_signature_file_pkey
            primary key,
    id_request integer not null
        constraint fk_doc_request_subsidy
            references subs.doc_request_subsidy,
    id_request_subsidy_file integer not null -- ссылка на проверяемый файд
        constraint reg_verification_signature_file_tp_request_subsidy_file
            references subs.tp_request_subsidy_file,
    id_request_subsidy_signature_file integer not null --ссылка на подпись проверяемого файла
        constraint reg_verification_signature_file_tp_request_subsidy_signature_file
            references subs.tp_request_subsidy_file,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    time_begin_verification timestamp, -- время начала проверки подписи
    time_end_verification timestamp,-- время завершения проверки подписи
    verify_status integer default 0, -- 0 - проверка не проводилась
    -- 1 - проверка прошла успешно
    -- 2 - подпись не соответствует файлу
    -- 3  в сертификате или цепочке сертификатов есть ошибки
    -- 4 в подписи есть ошибки
    verify_result text
)
;

create index idx_tp_request_subsidy_file
	on subs.tp_request_subsidy_file (id_request)
;

create or replace function subs.default_new_status_code() returns varchar(15)
language plpgsql
as
$$
begin
    return 'NEW';
end
$$
;

insert into subs.cls_subsidy_request_status (name, short_name, code)
values ('Новая заявка', 'Новая', subs.default_new_status_code())
;

create or replace function subs.set_default_doc_request_subsidy_values() returns trigger
language plpgsql
as
$$
declare
    dflt_id integer;
BEGIN
    if new.id_subsidy_request_status is null then

        select id
        into dflt_id
        from subs.cls_subsidy_request_status
        where code = subs.default_new_status_code()
        fetch first 1 rows only
        ;
        NEW.id_subsidy_request_status := dflt_id;
    end if;

RETURN NEW;
END;
$$;

create trigger trg_set_default_doc_request_subsidy_values
    before insert
    on subs.doc_request_subsidy
    for each row
    execute procedure subs.set_default_doc_request_subsidy_values()
;





