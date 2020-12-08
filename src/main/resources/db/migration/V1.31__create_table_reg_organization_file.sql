
create table if not exists reg_organization_file
(
    id serial not null
        constraint reg_organization_file_pkey primary key,
    id_organization integer not null
        constraint fk_cls_organization references cls_organization,
    id_request integer  --ссылка на заявку (для ранее загруженных файлов, либо если файл загружается при подаче заявки)
        constraint fk_doc_request references doc_request,
    is_deleted boolean, -- пометка мягкого удаления
    time_create timestamp not null default current_timestamp, -- датавремя создания
    attachment_path text, -- путь к файлу относительно каталога приложения + имя файла
    file_name text, --имя файла
    original_file_name text, -- имя файла при загрузке
    file_extension varchar(16), --расширение файла
    hash text, -- хэшсумма файла
    file_size integer -- размер файла
);