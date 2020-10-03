create table if not exists doc_employee
(
    id                  serial  not null
        constraint doc_employee_pk
            primary key,
    id_organization     integer not null
        constraint doc_employee_cls_organization_id_fk
            references cls_organization,
    id_person           integer not null
        constraint doc_employee_doc_person_id_fk
            references doc_person,
    is_vaccinated_flu   boolean default false,
    is_vaccinated_covid boolean default false
);
