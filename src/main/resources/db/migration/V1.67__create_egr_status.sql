create schema if not exists egr;

create table if not exists egr.sulst
(
    id   serial not null
        constraint sulst_pkey
            primary key,
    code varchar(3),
    name text
);

CREATE TABLE if not exists egr.sv_status
(
    id                          serial     not null
        constraint sv_status_pkey
            primary key,
    id_egrul                    integer
        constraint fk_reg_egrul
            references reg_egrul,
    id_egrip                    integer
        constraint fk_reg_egrip
            references reg_egrip,
    id_sulst                integer
        constraint fk_sulst
            references egr.sulst,
    excl_dec_date               date,
    excl_dec_num                text,
    publ_date                   date,
    journal_num                 varchar(50),
    grn                         varchar(15),
    record_date                 date,
    grn_corr                    varchar(15),
    record_date_corr            date
);

alter table reg_egrul
    add if not exists active_status smallint;