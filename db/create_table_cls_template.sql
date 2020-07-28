create table cls_template
(
    id    serial      not null
        constraint cls_template_pk
            primary key,
    key   varchar(20) not null,
    value text
);
