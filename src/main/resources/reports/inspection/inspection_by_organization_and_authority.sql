WITH
res_tbl as (
    SELECT roi.id as id,
       roi.date_of_inspection as date_of_inspection,
       roi.id_organization as id_organization,
       roi.id_control_authority as id_authority,
       roi.comment as comment,
       cir.name as inspection_result,
       cls_organization.name   as name_organization,
       cls_organization.short_name as short_name_organization,
       cls_organization.inn as inn_organization,
       cls_control_authority.name as name_authority,
       cls_control_authority.short_name as short_name_authority,
       1              as total_organization,
       1              as total_authority
    FROM reg_organization_inspection roi
            LEFT JOIN cls_organization
                ON roi.id_organization = cls_organization.id
            LEFT JOIN cls_control_authority
                ON roi.id_control_authority = cls_control_authority.id
            LEFT JOIN cls_inspection_result cir
                ON roi.id_inspection_result = cir.id
    WHERE date_of_inspection >= :min_date AND date_of_inspection <= :max_date
            AND id_organization =:org_id AND id_control_authority =:auth_id),
not_group_file_tbl as (
    SELECT res_tbl.id as id_inspection,
           CASE WHEN roif.id is null THEN false
                ELSE true
           END as files_attached
    FROM res_tbl
    LEFT JOIN reg_organization_inspection_file roif
           ON res_tbl.id = roif.id_organization_inspection
                AND roif.is_deleted = false
),
file_tbl as (
    SELECT id_inspection, files_attached
    FROM not_group_file_tbl
    GROUP BY id_inspection, files_attached
)
SELECT res_tbl.*, file_tbl.files_attached
FROM res_tbl
LEFT JOIN file_tbl
    ON res_tbl.id = file_tbl.id_inspection
ORDER BY res_tbl.date_of_inspection