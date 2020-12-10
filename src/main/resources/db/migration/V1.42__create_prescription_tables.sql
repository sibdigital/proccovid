create table if not exists reg_type_request_prescription
(
    id              serial  not null
    constraint reg_type_request_prescription_pkey primary key,
    id_type_request integer not null
    constraint fk_cls_type_request references cls_type_request,
    num             smallint,
    content         text
);

create table if not exists reg_type_request_prescription_file
(
    id                           serial    not null
    constraint reg_type_request_prescription_file_pkey primary key,
    id_type_request_prescription integer   not null
    constraint fk_reg_type_request_prescription references reg_type_request_prescription,
    is_deleted                   boolean,                                      -- пометка логического удаления
    time_create                  timestamp not null default current_timestamp, -- датавремя создания
    attachment_path              text,                                         -- путь к файлу относительно каталога приложения + имя файла
    file_name                    text,                                         --имя файла
    original_file_name           text,                                         -- имя файла при загрузке
    file_extension               varchar(16),                                  --расширение файла
    hash                         text,                                         -- хэшсумма файла
    file_size                    integer                                       -- размер файла
    );