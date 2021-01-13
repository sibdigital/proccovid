alter table cls_organization add column if not exists is_actualized boolean default false;
alter table cls_organization add column if not exists time_actualization timestamp;