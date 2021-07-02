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
    ), -- Объединили все найденные организации
    count_org_empls AS (
        SELECT oidbco.id_organization, count(distinct rdre.id_employee) as count_empl
        FROM reg_doc_request_employee AS rdre
        INNER JOIN doc_employee de ON rdre.id_employee = de.id
        INNER JOIN org_ids_by_checked_okveds oidbco on de.id_organization = oidbco.id_organization
        INNER JOIN doc_request dr ON rdre.id_request = dr.id
        WHERE de.is_deleted = false
           AND dr.status_pause = 0 AND  dr.status_activity = 1
           AND dr.status_review = :status_review
          AND dr.time_create > :time_create
        GROUP BY oidbco.id_organization
    ), -- Кол-во работников (из готового запроса)
    empl_okveds AS (
        SELECT count_org_empls.*, roo.id_okved, roo.is_main
        FROM count_org_empls
             INNER JOIN reg_organization_okved as roo using(id_organization)
             INNER JOIN child_checked_okveds on child_checked_okveds.id = roo.id_okved
    ),
    empl_okveds_m_a AS (
        SELECT count_org_empls.id_organization, count_org_empls.count_empl,
               (
                   SELECT eo.id_okved FROM empl_okveds AS eo
                   WHERE (eo.is_main, eo.id_organization) = (true, count_org_empls.id_organization)
                   FETCH FIRST 1 ROW ONLY
               ) AS main_okved,
               (
                   SELECT eo.id_okved FROM empl_okveds AS eo
                   WHERE (eo.is_main, eo.id_organization) = (false, count_org_empls.id_organization)
                   FETCH FIRST 1 ROW ONLY
               ) AS add_okved
        FROM count_org_empls
    ),
    org_okved AS (
        SELECT id_organization, coalesce(count_empl, 0) as count_empl, coalesce(main_okved, add_okved) AS id_okved
        FROM empl_okveds_m_a
        WHERE coalesce(main_okved, add_okved) IS NOT NULL
    ),
     final_table AS (
         SELECT cco.id,
                cco.kind_code,
                cco.kind_name,
                sum(oo.count_empl)        as employee_count,
                count(oo.id_organization) as organization_count
         FROM child_checked_okveds cco
                  LEFT JOIN org_okved as oo on cco.id = oo.id_okved
         GROUP BY cco.id, cco.kind_code, cco.kind_name
     )
SELECT id, kind_code, kind_name, coalesce(employee_count, 0) as employee_count, coalesce(organization_count, 0) as organization_count
FROM final_table
ORDER BY kind_code, kind_name;