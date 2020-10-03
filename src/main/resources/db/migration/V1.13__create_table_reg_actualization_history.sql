create table if not exists reg_actualization_history
(
    id                    serial    not null
        constraint reg_actualization_history_pk
            primary key,
    id_request            integer   not null
        constraint reg_actual_history_doc_request_id_fk
            references doc_request,
    id_actualized_request integer   not null
        constraint reg_actual_history_doc_actualized_request_id_fk
            references doc_request,
    time_actualization    timestamp not null,
    inn                   varchar(12)
);