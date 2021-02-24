create table if not exists cls_type_violation
(
    id serial not null
    constraint cls_type_violation_pkey primary key,
    name varchar(200),
    description text
);

create table if not exists reg_violation
(
    id serial not null
    constraint reg_violation_pkey primary key,
    id_type_violation integer not null
    constraint reg_violation_cls_type_id_fk references cls_type_violation,
    id_added_user integer not null
    constraint reg_violation_added_user_id_fk references cls_user,
    id_updated_user integer not null
    constraint reg_violation_updated_user_id_fk references cls_user,
    id_egrul integer
    constraint reg_violation_reg_egrul_id_fk references reg_egrul,
    id_egrip integer
    constraint reg_violation_reg_egrip_id_fk references reg_egrip,
    id_filial integer
    constraint reg_violation_reg_filial_id_fk references reg_filial,
    time_create timestamp not null,
    time_update timestamp not null,
    name_org varchar(255),
    opf_org varchar(100),
    inn_org varchar(12),
    ogrn_org varchar(15),
    kpp_org varchar(9),
    date_reg_org date,
    number_file varchar(100),
    date_file date,
    is_deleted boolean not null default false
);
