alter table subs.tp_required_subsidy_file
    add column if not exists weight integer default 0; -- порядок сортировки
alter table okved
    add column if not exists id_parent uuid; --добавим родителя в ОКВЭД