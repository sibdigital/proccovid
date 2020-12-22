-- ~50 minutes
ALTER TABLE IF EXISTS  fias.adm_hierarchy_item
    ALTER COLUMN regioncode SET DATA TYPE SMALLINT
USING regioncode::SMALLINT;

ALTER TABLE IF EXISTS  fias.adm_hierarchy_item
    ALTER COLUMN areacode SET DATA TYPE SMALLINT
USING areacode::SMALLINT;

ALTER TABLE IF EXISTS fias.adm_hierarchy_item
    ALTER COLUMN citycode SET DATA TYPE SMALLINT
USING citycode::SMALLINT;

