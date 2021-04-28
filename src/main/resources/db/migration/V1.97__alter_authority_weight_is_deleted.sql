alter table cls_control_authority
    add if not exists is_deleted boolean;

alter table cls_control_authority
    add if not exists weight int;

update cls_control_authority set is_deleted = false;