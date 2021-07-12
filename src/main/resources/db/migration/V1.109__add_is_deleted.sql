alter table subs.doc_request_subsidy
    add column if not exists is_deleted boolean default false;