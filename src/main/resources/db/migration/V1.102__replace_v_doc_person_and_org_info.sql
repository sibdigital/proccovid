drop view v_doc_person_and_org_info
;
create or replace view v_doc_person_and_org_info
            (id, id_request, lastname, firstname, patronymic, status_import, time_import, inn, short_name,
             begin_registration, end_registration)
as
SELECT pers.id,
       pers.id_request,
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
             dp.time_import
      FROM doc_person dp) pers
         JOIN (SELECT dr.id AS id_request,
                      co.inn,
                      co.short_name,
                      ctr.begin_registration,
                      ctr.end_registration
               FROM doc_request dr
                        JOIN cls_organization co ON dr.id_organization = co.id
                        JOIN cls_type_request ctr ON dr.id_type_request = ctr.id
               WHERE (dr.status_review = 1 OR dr.status_review = 4)
                 AND dr.status_pause = 0) org USING (id_request)
UNION
SELECT pers.id,
       pers.id_request,
       pers.lastname,
       pers.firstname,
       pers.patronymic,
       pers.status_import,
       pers.time_import,
       org.inn,
       org.short_name,
       '1970-01-01 00:00:00'::timestamp without time zone AS begin_registration,
       '9999-12-31 23:59:59'::timestamp without time zone AS end_registration
FROM (SELECT de_1.id,
             de_1.id_organization,
             de_1.id_person,
             de_1.is_vaccinated_flu,
             de_1.is_vaccinated_covid,
             de_1.is_deleted
      FROM doc_employee de_1
      WHERE de_1.is_deleted = false) de
         JOIN doc_person pers ON de.id_person = pers.id
         JOIN cls_organization org ON de.id_organization = org.id
WHERE pers.is_deleted = false
  AND org.is_deleted = false;