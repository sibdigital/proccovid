create index if not exists idx_doc_employee_id_organization
    on doc_employee (id_organization);

create index if not exists idx_reg_organization_file_id_organization
    on reg_organization_file (id_organization);

create index if not exists idx_reg_organization_address_fact_id_organization
    on reg_organization_address_fact (id_organization);

create index if not exists idx_reg_organization_prescription_id_organization
    on reg_organization_prescription (id_organization);


create index if not exists idx_doc_request_file_id_request
    on reg_doc_request_file (id_request);

create index if not exists idx_doc_request_prescription_id_request
    on reg_doc_request_prescription (id_request);
