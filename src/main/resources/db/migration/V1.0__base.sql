
create table if not exists cls_department
(
    id integer not null
        constraint cls_department_pkey
            primary key,
    name varchar(255),
    description text,
    status_import integer default 0,
    time_import timestamp
)
;

create table if not exists cls_type_request(
                                               id integer not null
                                                   constraint cls_type_request_pkey
                                                       primary key, --соответствует нынешщнему idTypeRequest
                                               activity_kind   text, --вид деятельности для вывода в заголовок формы
                                               id_department        integer
                                                   constraint rcls_type_request_department_id_fk
                                                       references cls_department, --министерство по умолчанию в выпадающий список министерств
                                               prescription text, --текст предприсания роспотребнадзора, форматированный в хтмл
                                               prescription_link text, --ссылка на файл с предписанием, пока прозапас
                                               settings text, --JSON с настройками, пока прозапас на будущее через него можно будет передавать видимость элементов если что.
                                               status_registration integer not null default 0,-- статус регистрации 0 - закрыта, 1 - используется для регистрации
                                               status_visible int default 0, -- отображение в списк
                                               begin_registration timestamp, -- начало возможности проверки
                                               end_registration timestamp, -- завершение возможности проверки
                                               begin_visible timestamp, --  начало возможности видимости
                                               end_visible timestamp,  -- завершение возможности видимости
                                               sort_weight integer default 0  --для сортировки
);

---- cls_district ----

create table if not exists cls_district
(
    id integer not null
        constraint cls_district_pkey
            primary key,
    name varchar(255)
);


create table if not exists cls_principal
(
    id            serial not null
        constraint cls_principal_pkey
            primary key,
    password varchar(255) not null
)
;

create table if not exists cls_user
(
    id            serial       not null
        constraint dep_user_pk
            primary key,
    id_department integer      not null
        constraint dep_user_cls_department_id_fk
            references cls_department,
    lastname      varchar(100),
    firstname     varchar(100),
    patronymic    varchar(100),
    login         varchar(100) not null,
    password      varchar(100) not null,
    is_admin      boolean default false
);

create unique index if not exists fki_dep_user_login
    on cls_user (login);

create table if not exists cls_organization
(
    id                   serial not null
        constraint cls_organization_pkey
            primary key,
    name                 text,
    short_name           varchar(255),
    inn                  varchar(12),
    ogrn                 varchar(15),
    address_jur          varchar(255),
    okved_add            text,
    okved                text,
    email                varchar(100),
    phone                varchar(100),
    status_import        integer default 0,
    time_import          timestamp,
    hash_code            text,
    id_type_request      integer,
    type_tax_reporting   integer,
    id_type_organization integer,
    id_principal         integer
        constraint cls_organization_cls_principal_id_fk
            references cls_principal
);

create table if not exists doc_request
(
    id                    serial  not null
        constraint doc_request_pkey
            primary key,
    person_office_cnt     integer,
    person_remote_cnt     integer,
    person_slry_save_cnt  integer,
    id_organization       integer not null
        constraint fk_org
            references cls_organization,
    id_department         integer not null
        constraint doc_request_cls_department_id_fk
            references cls_department,
    attachment_path       text,
    status_review         integer default 0,
    time_create           timestamp,
    status_import         integer default 0,
    time_import           timestamp,
    time_review           timestamp,
    req_basis             text    default ''::text,
    is_agree              boolean,
    is_protect            boolean,
    org_hash_code         text,
    reject_comment        text,
    old_department_id     integer,
    id_processed_user     integer
        constraint doc_request_processed_user_id_fk
            references cls_user,
    id_reassigned_user    integer
        constraint doc_request_reassigned_user_id_fk
            references cls_user,
    id_type_request       integer
        constraint doc_request_cls_type_request_id_fk
            references cls_type_request,
    status_pause          integer default 0,
    additional_attributes jsonb,
    id_district           integer
        constraint doc_request_cls_district_id_fk
            references cls_district,
    is_actualization      boolean default false,
    id_actualized_request integer
);

create index if not exists  idx_doc_request_neobr
    on doc_request (id_department, status_review)
    where (status_review = 0);

create index if not exists  idx_doc_request_neobr_minstroi
    on doc_request (id_department, status_review)
    where ((id_department = 7) AND (status_review = 0));

create index if not exists  idx_doc_request_review_status
    on doc_request (id_department, status_review);


create index if not exists fki_organization
    on doc_request (id_organization)
;

create table if not exists doc_person
(
    id              serial not null
        constraint doc_person_pk
            primary key,
    id_request      integer
        constraint doc_person_doc_request_id_fk
            references doc_request
            on delete cascade,
    lastname        varchar(100),
    firstname       varchar(100),
    patronymic      varchar(100),
    status_import   integer default 0,
    time_import     timestamp,
    id_organization integer
        constraint doc_person_cls_organization_id_fk
            references cls_organization
);

create index if not exists fki_request
    on doc_person (id_request)
;

create table if not exists doc_address_fact
(
    id serial not null
        constraint doc_address_fact_pk
            primary key,
    address_fact varchar(255),
    person_office_fact_cnt integer,
    id_request integer not null
        constraint fk_req_addr
            references doc_request
)
;

create index if not exists fki_request_addr
    on doc_address_fact (id_request)
;

create table if not exists doc_dacha
(
    id serial not null
        constraint doc_dacha_pk
            primary key,
    district varchar(255), --район дачи
    address text,        -- ДНТ/СНТ, населенный пункт дачи
    valid_date TIMESTAMP WITHOUT TIME ZONE, --дата действия
    link varchar(255),
    raion varchar(100), -- район убытия
    naspunkt text, -- населенный пункт убытия
    is_agree boolean,
    is_protect boolean,
    time_create timestamp,
    status_import integer,
    time_import timestamp,
    status_review integer,
    time_review timestamp,
    reject_comment text,
    phone varchar(100),
    email varchar(100)
);


create table if not exists doc_dacha_person
(
    id serial not null
        constraint doc_dacha_addr_pk
            primary key,
    id_doc_dacha integer not null
        constraint doc_dacha_addr_doc_dacha_id_fk
            references doc_dacha
            on delete cascade,
    lastname varchar(100),
    firstname varchar(100),
    patronymic varchar(100),
    age integer
);

drop view if exists v_doc_person_and_org_info;
create or replace view v_doc_person_and_org_info as (
    select pers.*,  org.inn, org.short_name from (
                                                     select *
                                                     from doc_person as dp
                                                 ) as pers
                                                     inner join ( select
                                                                      dr.id as id_request, co.inn, co.short_name
                                                                  from doc_request dr
                                                                           inner join cls_organization as co on dr.id_organization = co.id
                                                                  where dr.status_review = 1
    ) as org using (id_request)
);

CREATE TABLE if not exists public.reg_statistic (
                                                    id serial not null
                                                        constraint reg_statistic_pk
                                                            primary key,
                                                    lastname character varying(100),
                                                    firstname character varying(100),
                                                    patronymic character varying(100),
                                                    inn character varying(15),
                                                    reg_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                    additional_info text,
                                                    results integer NOT NULL
);

create table if not exists cls_excel
(
    id serial not null
        constraint cls_excel_pkey
            primary key,
    name text,
    status integer,
    description text,
    time_upload timestamp not null
)
;

create table if not exists reg_history_request
(
    id                   serial  not null
        constraint reg_history_request_pkey
            primary key,
    person_office_cnt    integer,
    person_remote_cnt    integer,
    person_slry_save_cnt integer,
    id_organization      integer not null
        constraint fk_reg_history_request_org
            references cls_organization,
    id_department        integer not null
        constraint reg_history_request_cls_department_id_fk
            references cls_department,
    attachment_path      text,
    status_review        integer default 0,
    time_create          timestamp,
    status_import        integer default 0,
    time_import          timestamp,
    time_review          timestamp,
    req_basis            text    default ''::text,
    is_agree             boolean,
    is_protect           boolean,
    org_hash_code        text,
    reject_comment       text,
    old_department_id    integer,
    id_processed_user    integer
        constraint reg_history_request_processed_user_id_fk
            references cls_user,
    id_reassigned_user   integer
        constraint reg_history_request_reassigned_user_id_fk
            references cls_user,
    id_type_request      integer,
    id_doc_request       integer,
    id_user              integer
        constraint reg_history_request_id_user_fk
            references cls_user,
    reg_time             timestamp not null default current_timestamp
);