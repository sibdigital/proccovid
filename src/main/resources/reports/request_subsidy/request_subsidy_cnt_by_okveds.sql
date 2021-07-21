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
                 FROM org_ids_by_checked_okveds_in_main oibcoim
                          INNER JOIN cls_organization co ON oibcoim.id_organization = co.id
                 WHERE co.is_deleted = false

                 UNION

                 SELECT id_organization
                 FROM org_ids_by_checked_okveds_in_additional oibcoia
                          INNER JOIN cls_organization co2 ON oibcoia.id_organization = co2.id
                 WHERE co2.is_deleted = false
             ) AS ids_by_checked_okveds
        GROUP BY id_organization
    ), -- Объединили все найденные организации по ОКВЭД
    status_doc_request AS (
        SELECT sds.id_organization, sds.id, sds.id_subsidy_request_status, sds.time_send, sds.time_create
        FROM subs.doc_request_subsidy sds
                 INNER JOIN org_ids_by_checked_okveds oibco
                            ON sds.id_organization = oibco.id_organization AND sds.is_deleted = false AND sds.status_activity = 1 AND time_send >= :min_date AND time_send <= :max_date AND sds.id_subsidy_request_status != 1
    ), -- Исключаем статус "Создано", т.к. попасть е в этот отчет такой документ не может, только если он программно не переведен в статус "Создано".
    all_org_okveds AS (
        SELECT status_doc_request.*, roo.id_okved, roo.is_main
        FROM status_doc_request
            INNER JOIN reg_organization_okved as roo using(id_organization)
            INNER JOIN child_checked_okveds on child_checked_okveds.id = roo.id_okved
    ),
    tbl AS (
        SELECT status_doc_request.id_organization, status_doc_request.id as id_doc_request, status_doc_request.id_subsidy_request_status as id_request_status,
               status_doc_request.time_send as time_send, status_doc_request.time_create as time_create,
        (
            SELECT aoo.id_okved FROM all_org_okveds AS aoo
            WHERE (aoo.is_main, aoo.id_organization) = (true, status_doc_request.id_organization)
            FETCH FIRST 1 ROW ONLY
        ) AS main_okved,
        (
            SELECT aoo.id_okved FROM all_org_okveds AS aoo
            WHERE (aoo.is_main, aoo.id_organization) = (false, status_doc_request.id_organization)
            FETCH FIRST 1 ROW ONLY
        ) AS add_okved
        FROM status_doc_request
    ),
    main_tbl AS (
        SELECT id_organization, id_doc_request, id_request_status, time_send, time_create,
            coalesce(main_okved, add_okved) AS id_okved
        FROM tbl
        WHERE coalesce(main_okved, add_okved) IS NOT NULL
    )
SELECT mt.id_okved as id_okved,
       cco.kind_code, cco.kind_name,
       mt.id_organization, mt.id_doc_request, mt.id_doc_request as id, mt.id_request_status, mt.time_send, mt.time_create,
       co.name as organization_name, co.short_name as organization_short_name, co.inn as organization_inn,
       scrs.code as code_status, scrs.name as name_status, scrs.short_name as short_name_status
FROM  main_tbl mt
    LEFT JOIN child_checked_okveds cco  on cco.id = mt.id_okved
    LEFT JOIN cls_organization co on mt.id_organization = co.id
    LEFT JOIN subs.cls_subsidy_request_status scrs on mt.id_request_status = scrs.id
