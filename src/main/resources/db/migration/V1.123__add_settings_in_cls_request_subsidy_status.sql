alter table subs.cls_subsidy_request_status
    add column if not exists settings jsonb; -- настройки
