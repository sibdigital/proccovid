create index reg_egrip_okved_id_egrip_index
    on reg_egrip_okved (id_egrip)
;
create index reg_egrul_okved_id_egrul_index
    on reg_egrul_okved (id_egrul)
;
alter table reg_organization_address_fact drop column  fias_objectguid
;
alter table reg_organization_address_fact drop column  fias_region_objectguid
;
alter table reg_organization_address_fact drop column  fias_raion_objectguid
;
alter table reg_organization_address_fact add column  fias_objectid bigint
;
alter table reg_organization_address_fact add column  fias_region_objectid bigint
;
alter table reg_organization_address_fact add column  fias_raion_objectid bigint
;
alter table reg_organization_address_fact add column  fias_city_objectid bigint
;
alter table reg_organization_address_fact add column  fias_house_objectid bigint
;
alter table reg_organization_address_fact add column  fias_street_objectid bigint
;
alter table reg_organization_address_fact add column  street_hand text
;
alter table reg_organization_address_fact add column  house_hand text
;
alter table reg_organization_address_fact add column  apartment_hand text
;
