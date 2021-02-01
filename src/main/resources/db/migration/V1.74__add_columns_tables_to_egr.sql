--alter table if exists egr.sulst rename to "reference_book";
--
--ALTER SEQUENCE if exists egr.sulst_id_seq RENAME TO "reference_book_id_seq";
--
--alter table egr.reference_book alter column code type varchar(5) using code::varchar(5);
--
--alter table egr.reference_book
--    add if not exists type smallint;
--
--alter table egr.reference_book
--    add if not exists status smallint;


-- reg_filial
alter table reg_filial
    add if not exists kladr_address text;

alter table reg_filial
    add if not exists kladr_address_hash integer;

alter table reg_filial
    add if not exists kladr_code varchar(23);

alter table reg_egrip
    add if not exists active_status smallint;

alter table reg_egrip
    add if not exists type_egrip smallint;

-- scheme egr

CREATE TABLE if not exists egr.sv_record_egr
(
    id          serial not null
        constraint sv_record_egr_pkey
            primary key,
    id_egrul    integer
        constraint fk_reg_egrul
            references reg_egrul,
    id_egrip    integer
        constraint fk_reg_egrip
            references reg_egrip,
    id_spvz     integer
        constraint fk_spvz
            references egr.reference_book,
    record_id   bigint,
    record_date date,
    data        jsonb,
    is_valid    boolean
);

create index if not exists reg_egrip_iogrn_idx
    on reg_egrip (iogrn);

