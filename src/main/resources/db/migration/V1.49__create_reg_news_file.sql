create table if not exists reg_news_file
(
    id                           serial                              not null
        constraint reg_news_file_pkey
            primary key,
    id_news integer                             not null
        constraint fk_news
            references cls_news,
    is_deleted                   boolean,
    time_create                  timestamp default CURRENT_TIMESTAMP not null,
    attachment_path              text,
    file_name                    text,
    original_file_name           text,
    file_extension               varchar(16),
    hash                         text,
    file_size                    integer
);