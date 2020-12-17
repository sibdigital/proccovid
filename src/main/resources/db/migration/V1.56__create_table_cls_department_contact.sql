create table if not exists cls_department_contact
(
    id serial not null
        constraint cls_department_contact_pkey
            primary key,
    id_department integer not null
        constraint fk_cls_department
            references cls_department,
    type integer not null,
    description text,
    contact_value text
);

alter table cls_department
    add column if not EXISTS full_name text;