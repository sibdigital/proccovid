create table if not exists reg_type_request_okved
(
    primary key(id_type_request, id_okved),
    id_type_request integer
        constraint fk_cls_type_request
            references cls_type_request,
    id_okved        uuid
        constraint fk_okved
            references okved
);
