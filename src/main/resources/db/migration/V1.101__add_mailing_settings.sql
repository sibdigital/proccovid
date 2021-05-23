alter table cls_mailing_list add column is_for_principal boolean default false;
alter table reg_mailing_list_follower add column id_organization integer;
alter table reg_mailing_list_follower
    add constraint reg_mailing_list_follower_organization_fk
        foreign key (id_organization) references cls_organization (id);