alter table doc_request
    add column if not exists is_actualization boolean default false;

alter table doc_request
    add column if not exists id_actualized_request integer;

alter table doc_request
	add constraint doc_request_doc_request_id_fk
		foreign key (id_actualized_request) references doc_request;
