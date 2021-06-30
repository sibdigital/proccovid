create index if not exists sv_status_id_egrip_index
    on egr.sv_status (id_egrip)
;

create index if not exists sv_status_id_egrul_index
    on egr.sv_status (id_egrul)
;

create index if not exists sv_record_egr_id_egrip_index
    on egr.sv_record_egr (id_egrip)
;

create index if not exists sv_record_egr_id_egrul_index
    on egr.sv_record_egr (id_egrul)
;