alter table subs.cls_subsidy_request_status
    add column if not exists is_block_request boolean default true;

update subs.cls_subsidy_request_status set is_block_request = false where id = 1;
update subs.cls_subsidy_request_status set is_block_request = true where id <> 1;