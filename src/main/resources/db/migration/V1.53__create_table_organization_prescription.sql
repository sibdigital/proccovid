create table if not exists reg_organization_prescription
(
    id                    serial  not null
    constraint reg_organization_prescription_pkey primary key,
    id_organization       integer not null
    constraint fk_cls_organization references cls_organization,
    id_prescription       integer not null
    constraint fk_cls_prescription references cls_prescription,
    additional_attributes jsonb
);
