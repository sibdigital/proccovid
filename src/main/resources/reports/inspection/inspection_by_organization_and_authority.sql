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
        AND id_organization =:org_id AND id_control_authority =:auth_id
ORDER BY date_of_inspection