create table if not exists reg_mailing_history
(
    id           serial    not null
        constraint reg_mailing_history_pk
            primary key,
    id_principal integer   not null
        constraint reg_mailing_history_cls_principal_id_fk
            references cls_principal,
    time_send    timestamp not null,
    status       smallint,
    id_template  integer
        constraint reg_mailing_history_cls_template_id_fk
            references cls_template
);
