create index if not exists adr_object_level_index
    on fias.addr_object (level DESC);

create index if not exists adr_object_name_index
    on fias.addr_object (name);

create index if not exists adm_hierarchy_item_regioncode_index
    on fias.adm_hierarchy_item (regioncode);

create index if not exists adm_hierarchy_item_areacode_index
    on fias.adm_hierarchy_item (areacode);

create index if not exists adm_hierarchy_item_citycode_index
    on fias.adm_hierarchy_item (citycode);