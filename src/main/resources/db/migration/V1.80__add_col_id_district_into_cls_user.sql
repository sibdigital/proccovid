alter table cls_user
    add column if not exists id_district integer
        constraint cls_user_cls_district_id_fk references cls_district;