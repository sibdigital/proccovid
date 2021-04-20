WITH
    checked_okveds AS (
        SELECT *, length(ltree2text(okved.path)) as path_len
        FROM okved
        WHERE CAST(okved.path AS text) in (:okved_paths)
    ),
    child_checked_okveds AS (
        SELECT okved.*
        FROM checked_okveds
                 LEFT JOIN okved
                           ON index(okved.path, checked_okveds.path, 0) = 0
        GROUP BY okved.id
    )
SELECT reg_organization_okved.*
FROM child_checked_okveds
     INNER JOIN reg_organization_okved
                ON child_checked_okveds.id = reg_organization_okved.id_okved AND reg_organization_okved.is_main = :is_main
