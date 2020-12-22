create table if not exists reg_doc_request_file
(
    id                    serial  not null
    constraint reg_doc_request_file_pkey primary key,
    id_request       integer not null
    constraint fk_doc_request references doc_request,
    id_organization_file       integer not null
    constraint fk_reg_organization_file references reg_organization_file
);
