alter table subs.doc_request_subsidy
    add column if not exists time_send timestamp;