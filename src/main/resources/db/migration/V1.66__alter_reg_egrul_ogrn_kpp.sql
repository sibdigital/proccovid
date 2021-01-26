alter table reg_egrul
	 add if not exists ogrn varchar(13);

alter table reg_egrul
    add if not exists iogrn bigint;

alter table reg_egrul
	 add if not exists kpp varchar(9);

create index if not exists reg_egrul_iogrn_idx
    on reg_egrul (iogrn);

create table if not exists reg_filial (
    id serial not null
        constraint reg_filial_pkey
            primary key,
    id_egrul integer not null
        constraint fk_reg_egrul
            references reg_egrul,
    inn varchar(10),
    kpp varchar(9),
    full_name text,
    data  jsonb
);


alter table reg_egrip
	 add if not exists ogrn varchar(15);

alter table reg_egrip
    add if not exists iogrn bigint;