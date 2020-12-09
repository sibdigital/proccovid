alter table cls_type_request add column if not exists status_publication integer;
alter table cls_type_request add column if not exists time_publication timestamp;