drop view IF EXISTS v_doc_person_and_org_info
;
create or replace view v_doc_person_and_org_info
            (id, id_request, lastname, firstname, patronymic, status_import, time_import, inn, short_name,
             begin_registration, end_registration)
as
SELECT pers.id,
       org.id_request,
       pers.lastname,
       pers.firstname,
       pers.patronymic,
       pers.status_import,
       pers.time_import,
       org.inn,
       org.short_name,
       org.begin_registration,
       org.end_registration
FROM (SELECT dp.id,
             dp.id_request,
             dp.lastname,
             dp.firstname,
             dp.patronymic,
             dp.status_import,
             dp.time_import,
             de.id as id_employee
      FROM doc_person dp
               JOIN doc_employee de on dp.id = de.id_person
      where dp.is_deleted = false and de.is_deleted = false
     ) pers
         JOIN (SELECT dr.id AS id_request,
                      co.inn,
                      co.short_name,
                      ctr.begin_registration,
                      ctr.end_registration,
                      rdre.id_employee
               FROM doc_request dr
                        JOIN cls_organization co ON dr.id_organization = co.id
                        JOIN cls_type_request ctr ON dr.id_type_request = ctr.id
                        JOIN reg_doc_request_employee rdre on dr.id = rdre.id_request
               WHERE co.is_deleted = false and (dr.status_review = 1 OR dr.status_review = 4)
                 AND dr.status_pause = 0) org
              USING (id_employee)