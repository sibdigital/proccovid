create table if not exists cls_mailing_list_okved
(
    id serial not null
        constraint cls_mailing_list_okved_pkey
            primary key,
    id_mailing integer not null
        constraint fk_cls_mailing_list
            references cls_mailing_list,
    id_okved uuid not null
        constraint fk_okved
            references okved
);