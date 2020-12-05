create extension if not exists "ltree";

alter table okved
    add if not exists ts_kind_name tsvector;

alter table okved
    add if not exists ts_description tsvector;

create index if not exists kind_code_idx on okved (kind_code);

create index if not exists ts_kind_name_gin_idx on okved using gin(ts_kind_name);

create index if not exists ts_description_gin_idx on okved using gin(ts_description);

create table if not exists reg_egrul
(
    id uuid not null
        constraint reg_egrul_pk
            primary key,
    load_date timestamp,
    inn varchar(20),
    data jsonb,
    file_path varchar(255)
);

create index if not exists reg_egrul_inn_idx on reg_egrul (inn);

create table if not exists reg_egrip
(
    id uuid not null
        constraint reg_egrip_pk
            primary key,
    load_date timestamp,
    inn varchar(20),
    data jsonb,
    file_path varchar(255)
);

create index if not exists reg_egrip_inn_idx on reg_egrip (inn);

create table if not exists reg_egrul_okved
(
    id_egrul uuid
        constraint reg_egrul_okved_reg_egrul_id_fk
            references reg_egrul,
    id_okved uuid
        constraint okved_reg_egrul_id_fk
            references okved,
    is_main bool default false,
    primary key(id_egrul, id_okved)
);

create table if not exists reg_egrip_okved
(
    id_egrip uuid
        constraint reg_egrip_okved_reg_egrip_id_fk
            references reg_egrip,
    id_okved uuid
        constraint okved_reg_egrip_id_fk
            references okved,
    is_main bool default false,
    primary key(id_egrip, id_okved)
);

CREATE TABLE if not exists cls_migration (
                                             id serial not null
                                                 constraint cls_migration_pk
                                                     primary key,
                                             type    smallint,
                                             load_date timestamp,
                                             filename varchar(255),
                                             status smallint,
                                             error text
);