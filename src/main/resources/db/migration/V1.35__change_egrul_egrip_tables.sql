DROP TABLE reg_egrul_okved
;
DROP TABLE reg_egrip_okved
;
DROP TABLE reg_egrul
;
DROP TABLE reg_egrip
;

create sequence IF NOT EXISTS public.seq_okved_key
;
create sequence IF NOT EXISTS public.seq_reg_egrul_pk
;
create sequence IF NOT EXISTS public.seq_reg_egrip_pk
;

create  table IF NOT EXISTS reg_egrul
(
    id           integer default nextval('seq_reg_egrul_pk'::regclass) not null
        constraint reg_egrul_pk
            primary key,
    id_migration integer,
    load_date    timestamp,
    inn          varchar(10),
    data         jsonb
)
;
create index IF NOT EXISTS reg_egrul_inn_idx
    on reg_egrul (inn)
;
create table reg_egrip
(
    id           integer default nextval('seq_reg_egrip_pk'::regclass) not null
        constraint reg_egrip_pk
            primary key,
    id_migration integer,
    load_date    timestamp,
    inn          varchar(12),
    data         jsonb
)
;
create index IF NOT EXISTS reg_egrip_inn_idx
    on reg_egrip (inn)
;

alter table okved add column IF NOT EXISTS id_serial integer default nextval('public.seq_okved_key')
;
create unique index IF NOT EXISTS idx_id_serial on okved(id_serial)
;

create table if not exists reg_egrul_okved
(
    id serial not null
        constraint reg_egrul_okved_pk primary key,
    id_egrul integer,
    id_okved integer,
    is_main bool default false
);
ALTER TABLE reg_egrul_okved
    ADD CONSTRAINT reg_egrul_reg_egrul_okved_id_fk
        FOREIGN KEY (id_egrul) REFERENCES reg_egrul (id)
;
ALTER TABLE reg_egrul_okved
    ADD CONSTRAINT okved_reg_egrul_okved_id_fk
        FOREIGN KEY (id_okved) REFERENCES okved (id_serial)
;

create table if not exists reg_egrip_okved
(
    id serial not null
        constraint reg_egrip_okved_pk primary key,
    id_egrip integer,
    id_okved integer,
    is_main bool default false
);
ALTER TABLE reg_egrip_okved
    ADD CONSTRAINT reg_egrul_reg_egrip_okved_id_fk
        FOREIGN KEY (id_egrip) REFERENCES reg_egrip (id)
;
ALTER TABLE reg_egrip_okved
    ADD CONSTRAINT okved_reg_egrip_okved_id_fk
        FOREIGN KEY (id_okved) REFERENCES okved (id_serial)
;
