create table if not exists reg_mailing_list_follower
(
    id serial not null
        constraint reg_mailing_list_follower_pkey
            primary key,
    id_principal integer not null
        constraint fk_cls_principal
            references cls_principal,
    id_mailing_list integer not null
        constraint fk_cls_mailing_list
            references cls_mailing_list,
    activation_date timestamp,
    deactivation_date timestamp
);