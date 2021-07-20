alter table subs.tp_required_subsidy_file
    add column if not exists weight integer default 0; -- порядок сортировки
alter table okved
    add column if not exists id_parent uuid; --добавим родителя в ОКВЭД

alter table cls_organization rename column is_expiremental to is_experimental;

INSERT INTO public.cls_settings (status, key, value, string_value)
VALUES
(1, 'enableExperimentalFeatures', null, 'false')
;