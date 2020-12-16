create table if not exists reg_doc_request_prescription
(
    id                    serial  not null
    constraint reg_doc_request_prescription_pkey primary key,
    id_request            integer not null
    constraint fk_doc_request references doc_request,
    id_prescription       integer not null
    constraint fk_cls_prescription references cls_prescription,
    additional_attributes jsonb
);