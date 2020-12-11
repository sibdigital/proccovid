create table if not exists reg_news_link_clicks
(
    id serial not null
        constraint reg_news_link_clicks_pkey
            primary key,
    id_news integer not null
        constraint fk_news
            references cls_news,
    ip varchar(45),
    time timestamp
);

alter table  cls_news
	 add  if not exists hash_id varchar(20);