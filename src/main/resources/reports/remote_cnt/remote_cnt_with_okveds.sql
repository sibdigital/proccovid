WITH
    checked_okveds AS (
        SELECT *, length(ltree2text(okved.path)) as path_len
        FROM okved
        WHERE CAST(okved.path AS text) in (:okved_paths)
    ), -- Нашли все ОКВЭД по paths
    child_checked_okveds AS (
        SELECT okved.*
        FROM checked_okveds
                 LEFT JOIN okved
                           ON index(okved.path, checked_okveds.path, 0) = 0
        GROUP BY okved.id
    ), -- Нашли все дочерние ОКВЭД
    org_ids_by_checked_okveds_in_main AS (
        SELECT reg_organization_okved.id_organization
        FROM child_checked_okveds
                 INNER JOIN reg_organization_okved
                            ON child_checked_okveds.id = reg_organization_okved.id_okved AND reg_organization_okved.is_main = true
        GROUP BY id_organization
    ), -- Нашли все id организаций по ОКВЭДам в основных
    org_ids_by_checked_okveds_in_additional AS (
        SELECT reg_organization_okved.id_organization
        FROM child_checked_okveds
                 INNER JOIN reg_organization_okved
                            ON child_checked_okveds.id = reg_organization_okved.id_okved AND reg_organization_okved.is_main = false
        GROUP BY id_organization
    ), -- Нашли все id организаций по ОКВЭДам в доп-ых
    org_ids_by_checked_okveds AS (
        SELECT id_organization
        FROM (
                 SELECT id_organization
                 FROM org_ids_by_checked_okveds_in_main

                 UNION

                 SELECT id_organization
                 FROM org_ids_by_checked_okveds_in_additional
             ) AS ids_by_checked_okveds
        GROUP BY id_organization
    ), -- Объединили все найденные организации
    doc_employee_cnt AS (
        SELECT doc_employee.id_organization as id_organization,
               count(doc_employee.id) as cnt_by_doc_employee
        FROM doc_employee
                 INNER JOIN org_ids_by_checked_okveds oibco
                            ON doc_employee.id_organization = oibco.id_organization AND doc_employee.is_deleted = false
        GROUP BY doc_employee.id_organization
    ), -- Нашли кол-во работников по doc_employee
    max_rpc AS (
        select id_organization, max(time_edit) as time_edit
        from public.reg_person_count
        group by id_organization
    ),
    reg_person_cnt AS (
        select rpc.id_organization,
               coalesce(rpc.person_office_cnt, 0) + coalesce(rpc.person_remote_cnt, 0) as all_count,
               coalesce(rpc.person_office_cnt, 0) as office_cnt,
               coalesce(rpc.person_remote_cnt, 0) as remote_cnt
        from public.reg_person_count as rpc
                 inner join max_rpc on max_rpc.time_edit = rpc.time_edit and max_rpc.id_organization = rpc.id_organization
                 inner join org_ids_by_checked_okveds on rpc.id_organization = org_ids_by_checked_okveds.id_organization
    ), -- Нашли кол-во работников по reg_person_count
    main_okved_ids AS (
        SELECT oibco.id_organization,
               roo.id_okved
        FROM org_ids_by_checked_okveds oibco
                 INNER JOIN reg_organization_okved roo
                            ON oibco.id_organization = roo.id_organization AND roo.is_main = true
    ), -- Нашли все осн. оквэды организаций
    main_okveds AS (
        SELECT moi.id_organization,
               string_agg(ltree2text(okved.path), ', ') as names
        FROM main_okved_ids moi
                 INNER JOIN okved
                            ON moi.id_okved = okved.id
        GROUP BY moi.id_organization
    ), -- Получили все коды осн. оквэдов через запятую
    additional_okved_ids AS (
        SELECT oibco.id_organization,
               roo.id_okved
        FROM org_ids_by_checked_okveds oibco
                 INNER JOIN reg_organization_okved roo
                            ON oibco.id_organization = roo.id_organization AND roo.is_main = false
    ), -- Нашли все доп. оквэды организаций
    additional_okveds AS (
        SELECT aoi.id_organization,
               string_agg(ltree2text(okved.path), ', ') as names
        FROM additional_okved_ids aoi
                 INNER JOIN okved
                            ON aoi.id_okved = okved.id
        GROUP BY aoi.id_organization
    ) -- Получили все коды доп. оквэдов через запятую
SELECT co.id, co.short_name as short_name_organization, co.inn as organization_inn,
       coalesce(dec.cnt_by_doc_employee, 0) as cnt_by_doc_employee,
       coalesce(rpc.all_count,0) as all_count,
       coalesce(rpc.office_cnt, 0) as office_cnt,
       coalesce(rpc.remote_cnt, 0) as remote_cnt,
       coalesce(main_okveds.names, '') as main_okveds,
       coalesce(additional_okveds.names, '') as additional_okveds,
       CASE WHEN oibcoim.id_organization IS NOT NULL THEN 'основному ОКВЭД'
            ELSE CASE WHEN oibcoia.id_organization IS NOT NULL THEN 'доп. ОКВЭД'
                      ELSE 'Ошибка'
                END
           END as by_what_okved_type
FROM org_ids_by_checked_okveds oibco
         INNER JOIN cls_organization co
                    ON oibco.id_organization = co.id
         LEFT JOIN doc_employee_cnt dec
                   ON co.id = dec.id_organization
         LEFT JOIN reg_person_cnt rpc
                   ON oibco.id_organization = rpc.id_organization
         LEFT JOIN main_okveds
                   ON oibco.id_organization = main_okveds.id_organization
         LEFT JOIN additional_okveds
                   ON oibco.id_organization = additional_okveds.id_organization
         LEFT JOIN org_ids_by_checked_okveds_in_main oibcoim
                   ON oibco.id_organization = oibcoim.id_organization
         LEFT JOIN org_ids_by_checked_okveds_in_additional oibcoia
                   ON oibco.id_organization = oibcoia.id_organization