create table if not exists cls_control_authority_parent (
    id serial not null constraint cls_control_authority_parent_pkey primary key,
    name text
);

insert into cls_control_authority_parent (name)
    values
           ('Территориальные подразделения ФОИВ'),
           ('Органы государственного контроля (надзора) Республики Бурятия');

update cls_control_authority set id_parent = 1 where id <= 13;
update cls_control_authority set id_parent = 2 where id >= 14;