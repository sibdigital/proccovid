create table if not exists reg_organization_address_fact
(
    id serial not null
        constraint reg_organization_address_fact_pkey primary key,
    id_organization integer not null
        constraint fk_cls_organization references cls_organization,
    id_request integer  --ссылка на заявку (для ранее загруженных)
        constraint fk_doc_request references doc_request,
    is_deleted boolean, -- пометка мягкого удаления
    time_create timestamp not null default current_timestamp, -- датавремя создания
    fias_objectguid varchar(36), -- идентификатор адреса из ФИАС
    fias_region_objectguid varchar(36), -- идентификатор региона
    fias_raion_objectguid varchar(36), --идентификатор района
    full_address text, -- либо ввденный руками адрес, либо адрес полученный из адреса ФИАС
    is_hand boolean --адрес введен руками, тогда fias_objectguid должен быть null
);