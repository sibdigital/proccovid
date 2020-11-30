create table if not exists reg_mailing_message
(
    id serial not null
        constraint reg_mailing_message_pkey
            primary key,
    id_mailing integer not null
        constraint fk_cls_mailing_list
            references cls_mailing_list,
    message text not null,
    sending_time timestamp,
    status smallint not null
);