create table if not exists cls_mailing_list
(
    id              serial not null
        constraint cls_mailing_list_pkey
            primary key,
    name            text,
    description     text,
    status          smallint
);