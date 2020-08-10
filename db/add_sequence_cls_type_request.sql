create sequence cls_type_request_id_seq;

alter table cls_type_request alter column id set default nextval('public.cls_type_request_id_seq');

alter sequence cls_type_request_id_seq owned by cls_type_request.id;

alter sequence cls_type_request_id_seq restart with 150;