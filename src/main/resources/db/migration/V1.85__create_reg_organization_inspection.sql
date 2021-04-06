create table if not exists cls_inspection_result
(
    id serial not null
        constraint cls_inspection_result_pkey primary key,
    name varchar(255)
);

insert into cls_inspection_result (name)
values ('Штраф'), ('Предписание'), ('Предупреждение'), ('Устное замечание'), ('Без замечаний');

create table if not exists reg_organization_inspection
(
    id serial not null
        constraint reg_organization_inspection_pkey primary key,
    id_organization integer not null
        constraint cls_organization_pkey references cls_organization,
    id_control_authority  integer not null
        constraint cls_control_authority_id_fk references cls_control_authority,
    date_of_inspection date,
    id_inspection_result integer not null
        constraint cls_inspection_result_id_fk references cls_inspection_result,
    comment text
);

create table if not exists reg_organization_inspection_file
(
    id serial not null
        constraint reg_organization_inspection_file_pkey primary key,
    id_organization_inspection integer not null
        constraint reg_organization_inspection_pkey references reg_organization_inspection,
    is_deleted                   boolean,
    time_create                  timestamp default CURRENT_TIMESTAMP not null,
    attachment_path              text,
    file_name                    text,
    original_file_name           text,
    file_extension               varchar(16),
    hash                         text,
    file_size                    integer
);
