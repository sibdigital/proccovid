select co.id, co.short_name as short_name_organization, co.inn as organization_inn,
       rpc.time_edit,
       coalesce(rpc.person_office_cnt, 0) + coalesce(rpc.person_remote_cnt, 0) as all_count,
       coalesce(rpc.person_office_cnt, 0) as office_cnt,
       coalesce(rpc.person_remote_cnt, 0) as remote_cnt
from public.reg_person_count as rpc
         inner join (
    select id_organization, max(time_edit) as time_edit
    from public.reg_person_count
    where time_edit <= :report_date
    group by id_organization
) as slice_rpc using(id_organization, time_edit)
         inner join public.cls_organization as co on rpc.id_organization = co.id
order by time_edit desc