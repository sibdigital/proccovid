create table if not exists reg_person_violation
(
    id serial not null
    constraint reg_person_violation_pkey primary key,
    id_type_violation integer not null
    constraint reg_person_violation_cls_type_id_fk references cls_type_violation,
    id_added_user integer not null
    constraint reg_person_violation_added_user_id_fk references cls_user,
    id_updated_user integer not null
    constraint reg_person_violation_updated_user_id_fk references cls_user,
    time_create timestamp not null,
    time_update timestamp not null,
    lastname varchar(100) not null,
    firstname varchar(100) not null,
    patronymic varchar(100),
    birthday date not null,
    place_birth text not null,
    registration_address text,
    residence_address text,
    passport_data text,
    place_work text,
    number_file varchar(100),
    date_file date,
    is_deleted boolean not null default false
);
