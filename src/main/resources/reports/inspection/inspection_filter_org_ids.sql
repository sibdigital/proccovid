WITH
    tbl as (
        SELECT *
        FROM reg_organization_inspection
        WHERE date_of_inspection >= :min_date AND date_of_inspection <= :max_date
                AND id_organization in (:org_ids)
    ),
    tbl_with_cnt as (
        SELECT tbl.id_organization, tbl.id_control_authority, count(*) as cnt
        FROM tbl
        GROUP BY id_organization, id_control_authority
    ),
    res_tbl as (
        SELECT tbl.*
        FROM tbl
                 INNER JOIN tbl_with_cnt
                            ON tbl.id_organization = tbl_with_cnt.id_organization
                                AND tbl.id_control_authority = tbl_with_cnt.id_control_authority
                                AND tbl_with_cnt.cnt > :min_cnt
    ),
    total_organization as (
        SELECT id_organization, count(*) as total
        FROM res_tbl
        GROUP BY id_organization
    ),
    total_authority as (
        SELECT id_control_authority, count(*) as total
        FROM res_tbl
        GROUP BY id_control_authority
    )
SELECT res_tbl.*,
       total_organization.total              as total_organization,
       total_authority.total                 as total_authority
FROM res_tbl
         LEFT JOIN total_organization
                   ON res_tbl.id_organization = total_organization.id_organization
         LEFT JOIN total_authority
                   ON res_tbl.id_control_authority = total_authority.id_control_authority