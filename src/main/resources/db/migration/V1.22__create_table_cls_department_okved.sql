create table if not exists reg_department_okved(
   id uuid not null
       constraint reg_department_okved_pkey
           primary key,
   id_department integer
       constraint fk_cls_department
           references cls_department,
   id_okved uuid
       constraint fk_okved
           references okved
);