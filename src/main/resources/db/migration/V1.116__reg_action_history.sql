create schema if not exists hist
;
create table cls_action_type -- справочник типов действий
(
    id serial not null
        constraint cls_action_type_pkey
            primary key,
    is_deleted boolean default false,
    time_create timestamp default CURRENT_TIMESTAMP not null,
    name text,
    short_name text,
    code varchar(15)
)
;
create table hist.reg_action_history -- история действий
(
    id serial not null
        constraint reg_action_history_pkey
            primary key,
    id_organization integer
        constraint cls_organization_id_fk
            references cls_organization,
    id_principal integer
        constraint cls_principal_id_fk
            references cls_principal,
    id_user integer
        constraint cls_user_id_fk
            references cls_user,
    id_action_type integer
        constraint cls_action_type_id_fk
            references cls_action_type,
    time_action timestamp default CURRENT_TIMESTAMP not null
)
;
insert into cls_action_type (name, short_name, code)
values ('Авторизация', 'Авторизация', 'AUTH')
;





