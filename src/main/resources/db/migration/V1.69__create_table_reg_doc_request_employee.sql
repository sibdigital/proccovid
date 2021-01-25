create table if not exists reg_doc_request_employee
(
    id          serial  not null
    constraint reg_doc_request_employee_pkey primary key,
    id_request  integer not null
    constraint fk_doc_request references doc_request,
    id_employee integer not null
    constraint fk_doc_employee references doc_employee
);

create index if not exists idx_doc_request_employee_id_request
    on reg_doc_request_employee (id_request);
