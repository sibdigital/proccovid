alter table cls_organization add column if not exists time_create timestamp
;
alter table cls_organization add column if not exists is_activated boolean default false
;