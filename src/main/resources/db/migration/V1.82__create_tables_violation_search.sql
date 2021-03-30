alter table reg_violation alter column name_org type text using name_org::text;

create table if not exists reg_violation_search
(
    id serial not null
    constraint reg_violation_search_pkey primary key,
    id_user integer not null
    constraint reg_violation_search_user_id_fk references cls_user,
    time_create timestamp not null,
    id_district integer
    constraint reg_violation_search_cls_district_id_fk references cls_district,
    inn_org varchar(12),
    name_org text,
    number_file varchar(100),
    begin_date_reg_org date,
    end_date_reg_org date,
    number_found integer
);

create table if not exists reg_person_violation_search
(
    id serial not null
    constraint reg_person_violation_search_pkey primary key,
    id_user integer not null
    constraint reg_person_violation_search_user_id_fk references cls_user,
    time_create timestamp not null,
    id_district integer
    constraint reg_violation_search_cls_district_id_fk references cls_district,
    lastname varchar(100),
    firstname varchar(100),
    patronymic varchar(100),
    passport_data text,
    number_file varchar(100),
    number_found integer
);