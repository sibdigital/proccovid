create table if not exists cls_restriction_type
(
    id   serial not null
        constraint cls_restriction_type_pkey
            primary key,
    name varchar(255)
);

create table if not exists reg_type_request_restriction_type
(
    primary key (id_type_request, id_restriction_type),
    id_type_request     integer
        constraint fk_cls_type_request
            references cls_type_request,
    id_restriction_type integer
        constraint fk_cls_restriction_type
            references cls_restriction_type
);
