create sequence cls_department_id_seq;

alter table cls_department alter column id set default nextval('cls_department_id_seq');

alter sequence cls_department_id_seq owned by cls_department.id;

alter sequence cls_department_id_seq restart with 40;