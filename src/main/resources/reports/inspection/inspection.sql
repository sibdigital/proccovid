WITH
    tbl as (
        SELECT roi.*
        FROM reg_organization_inspection roi
        INNER JOIN cls_control_authority cca
            ON roi.id_control_authority = cca.id and cca.is_deleted = false
        WHERE roi.date_of_inspection >= :min_date AND roi.date_of_inspection <= :max_date
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
        WHERE tbl_with_cnt.cnt >= :min_cnt
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
SELECT
       res_tbl.id as id,
       res_tbl.date_of_inspection as date_of_inspection,
       res_tbl.id_organization as id_organization,
       res_tbl.id_control_authority as id_authority,
       res_tbl.comment as comment,
       cir.name as inspection_result,
       cls_organization.name   as name_organization,
       cls_organization.short_name as short_name_organization,
       cls_organization.inn as inn_organization,
       cls_control_authority.name as name_authority,
       cls_control_authority.short_name as short_name_authority,
       total_organization.total              as total_organization,
       total_authority.total                 as total_authority
FROM res_tbl
         LEFT JOIN total_organization
                   ON res_tbl.id_organization = total_organization.id_organization
         LEFT JOIN total_authority
                   ON res_tbl.id_control_authority = total_authority.id_control_authority
         LEFT JOIN cls_organization
                   ON res_tbl.id_organization = cls_organization.id
         LEFT JOIN cls_control_authority
                   ON res_tbl.id_control_authority = cls_control_authority.id
         LEFT JOIN cls_inspection_result cir
                   ON res_tbl.id_inspection_result = cir.id