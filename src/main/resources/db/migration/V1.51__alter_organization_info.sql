alter table cls_organization
    add column if not EXISTS consent_data_processing boolean;

create table if not exists reg_person_count
(
    id serial not null
        constraint reg_person_count_pkey
            primary key,
    id_organization integer not null
        constraint fk_cls_organization
            references cls_organization,
    id_request integer
        constraint fk_doc_request
            references doc_request,
    time_edit timestamp,
    person_office_cnt integer,
    person_remote_cnt integer
);