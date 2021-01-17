create or replace function get_request_slice(p_status int)
    returns table(
                     id integer,
                     id_organization integer,
                     id_type_request integer,
                     time_create timestamp,
                     time_review timestamp
                 )
    language plpgsql as
$$
begin
    return query
        with org_requests as(
            select co.inn, dr.* from doc_request as dr
                                         inner join (select *
                                                     from cls_organization as co
            ) as co on dr.id_organization = co.id
            where dr.status_review = p_status
        ),
             slice_doc_request as(
                 select ore.inn,  max(ore.time_create) as time_create
                 from org_requests as ore
                 group by ore.inn
             )
        select dr.id, dr.id_organization, dr.id_type_request, dr.time_create, dr.time_review
        from slice_doc_request as sdr
                 inner join (select co.inn, dr.* from doc_request as dr
                                                          inner join cls_organization as co on dr.id_organization = co.id
        )as dr
                            on (sdr.inn, sdr.time_create) = (dr.inn, dr.time_create)
        order by dr.id desc
    ;
end;
$$;

