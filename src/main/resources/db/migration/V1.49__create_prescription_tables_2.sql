drop table if exists reg_type_request_prescription_file;
drop table if exists reg_type_request_prescription;

create table if not exists cls_prescription
(
    id                serial  not null
    constraint cls_prescription_pkey primary key,
    id_type_request   integer not null
    constraint fk_cls_type_request references cls_type_request,
    name              text,
    description       text,
    status            int default 0,
    additional_fields jsonb,
    time_publication  timestamp
);

create table if not exists reg_prescription_text
(
    id              serial  not null
    constraint reg_prescription_text_pkey primary key,
    id_prescription integer not null
    constraint fk_cls_prescription references cls_prescription,
    num             smallint,
    content         text
);

create table if not exists reg_prescription_text_file
(
    id                           serial    not null
    constraint reg_prescription_text_file_pkey primary key,
    id_prescription_text integer   not null
    constraint fk_reg_prescription_text references reg_prescription_text,
    is_deleted                   boolean,                                      -- пометка логического удаления
    time_create                  timestamp not null default current_timestamp, -- датавремя создания
    attachment_path              text,                                         -- путь к файлу относительно каталога приложения + имя файла
    file_name                    text,                                         --имя файла
    original_file_name           text,                                         -- имя файла при загрузке
    file_extension               varchar(16),                                  --расширение файла
    hash                         text,                                         -- хэшсумма файла
    file_size                    integer                                       -- размер файла
);