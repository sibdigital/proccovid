alter table reg_mailing_history
    add column if not exists id_mailing_list integer
        constraint reg_mailing_history_cls_mailing_list_id_fk
            references cls_mailing_list;

alter table reg_mailing_history
    add column if not exists id_mailing_message integer
        constraint reg_mailing_history_reg_mailing_message_id_fk
            references reg_mailing_message;