create extension if not exists "ltree";

create table if not exists okved
(
    id uuid not null
        constraint okved_pk
            primary key,
    class_code     varchar(2),
    subclass_code  varchar(4),
    group_code     varchar(5),
    subgroup_code  varchar(7),
    kind_code      varchar(8),
    type_code      smallint,
    path           ltree,
    status         smallint,
    kind_name      text,
    description    text,
    ts_kind_name   tsvector,
    ts_description tsvector
);

create index if not exists kind_code_idx on okved (kind_code);

create index if not exists ts_kind_name_gin_idx on okved using gin(ts_kind_name);

create index if not exists ts_description_gin_idx on okved using gin(ts_description);

create table if not exists reg_organization_okved
(
    id_organization integer not null
        constraint reg_org_okved_cls_org_id_fk
            references cls_organization,
    id_okved        uuid
        constraint reg_org_okved_okved_id_fk
            references okved,
    is_main         boolean default false,
    constraint reg_organization_okved_pkey
        primary key (id_organization, id_okved)
);