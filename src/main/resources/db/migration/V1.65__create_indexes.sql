create index if not exists idx_doc_request_id_organization
    on doc_request (id_organization);

create index if not exists idx_doc_addres_fact_id_request
    on doc_address_fact (id_request);

create index if not exists idx_doc_person_id_request
    on doc_person (id_request);
