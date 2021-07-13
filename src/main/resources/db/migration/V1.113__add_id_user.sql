alter table subs.reg_verification_signature_file
    add column if not exists is_deleted boolean default false;
alter table subs.reg_verification_signature_file
    add column if not exists id_principal integer;
alter table subs.reg_verification_signature_file
    add column if not exists id_user integer;

alter table subs.reg_verification_signature_file
    add constraint reg_verification_signature_file_cls_principal_id_fk
        foreign key (id_principal) references cls_principal;

alter table subs.reg_verification_signature_file
    add constraint reg_verification_signature_file_cls_user_id_fk
        foreign key (id_user) references cls_user;