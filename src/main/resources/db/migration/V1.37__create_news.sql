create table if not exists cls_news
(
    id              serial not null
        constraint cls_news_pkey
            primary key,
    heading         text,
    message         text,
    start_time      timestamp,
    end_time        timestamp
);

create table if not exists reg_news_okved
(
    id serial not null
        constraint reg_news_okved_pkey
            primary key,
    id_news integer not null
        constraint fk_news
            references cls_news,
    id_okved uuid not null
        constraint fk_okved
            references okved
);

create table if not exists reg_news_status
(
    id serial not null
        constraint reg_news_status_pkey
            primary key,
    id_news integer not null
        constraint fk_news
            references cls_news,
    status_review integer
);

create table if not exists reg_news_organization
(
    id serial not null
        constraint reg_news_organization_pkey
            primary key,
    id_news integer not null
        constraint fk_news
            references cls_news,
    id_organization integer not null
        constraint fk_organization
            references cls_organization
);

