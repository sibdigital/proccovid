create table if not exists cls_mailing_list
(
    id              serial not null
        constraint cls_mailing_list_pkey
            primary key,
    name            text,
    description     text,
    status          smallint
);

INSERT INTO cls_mailing_list(id, name) VALUES (1, 'Системная рассылка') ON CONFLICT
    ON CONSTRAINT cls_mailing_list_pkey DO UPDATE SET name = 'Системная рассылка';

