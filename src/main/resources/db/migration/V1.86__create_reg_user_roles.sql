create table if not exists cls_role
(
    id serial not null
        constraint cls_role_pkey primary key,
    name text,
    code varchar(255)
);

insert into cls_role (name, code)
values ('Администратор', 'ADMIN'), ('Работа с заявками', 'USER'), ('Работа с проверками', 'VIOLAT');

create table if not exists reg_user_role
(
    id serial not null
        constraint reg_user_role_pkey primary key,
    id_user integer not null
        constraint cls_reg_user_role_user_id_fk references cls_user,
    id_role  integer not null
        constraint cls_reg_user_role_role_id_fk references cls_role
);

