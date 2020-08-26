alter table if exists dep_user
	add column if not exists is_admin boolean default false;
