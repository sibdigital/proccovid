
create table if not exists cls_organization_contact
(
    id serial not null
        constraint cls_organization_contact_pkey
            primary key,
    id_organization integer not null
        constraint fk_cls_organization
            references cls_organization,
    type integer not null default 0, --0 - эл. почта, 1- телефон
    contact_value text, -- значение контакта
    contact_person text --контактное лицо
);