alter table cls_organization add column if not exists kpp varchar(9);

create table if not exists reg_organization_classifier
(
    id        serial not null
    constraint reg_organization_classifier_pkey
    primary key,
    id_egrul  integer
    constraint fk_reg_egrul references reg_egrul,
    id_egrip  integer
    constraint fk_reg_egrip references reg_egrip,
    id_filial integer
    constraint fk_reg_filial references reg_filial
);

alter table cls_organization
    add column if not exists id_organization_classifier integer
    constraint cls_organization_reg_orgnization_classifier_id_fk
    references reg_organization_classifier;
