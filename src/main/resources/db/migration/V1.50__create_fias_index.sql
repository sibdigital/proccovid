create index if not exists adm_hierarchy_item_parentobjid_index
    on fias.adm_hierarchy_item (parentobjid);

create index if not exists adm_hierarchy_item_objid_index
    on fias.adm_hierarchy_item (objectid);

create index if not exists addr_object_objid_index
    on fias.addr_object (objectid);
