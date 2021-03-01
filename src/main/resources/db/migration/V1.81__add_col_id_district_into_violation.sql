alter table reg_violation
    add column if not exists id_district integer
        constraint reg_violation_cls_district_id_fk references cls_district;

alter table reg_person_violation
    add column if not exists id_district integer
        constraint reg_person_violation_cls_district_id_fk references cls_district;