DROP VIEW IF EXISTS v_doc_person_and_org_info

CREATE VIEW v_doc_person_and_org_info
            (id, id_request, lastname, firstname, patronymic, status_import, time_import, inn, short_name,
             begin_registration, end_registration)
AS
SELECT person.id,
       person.id_request as id_request,
       person.lastname,
       person.firstname,
       person.patronymic,
       person.status_import,
       person.time_import,
       organization.inn,
       organization.short_name,
       request_type.begin_registration,
       request_type.end_registration
FROM reg_doc_request_employee rdre2
         JOIN doc_request dr ON rdre2.id_request = dr.id
         JOIN cls_organization organization ON dr.id_organization = organization.id
         JOIN cls_type_request request_type ON dr.id_type_request = request_type.id
         JOIN (
    SELECT dp.id,
           dp.id_request as id_request,
           dp.lastname,
           dp.firstname,
           dp.patronymic,
           dp.status_import,
           dp.time_import,
           de.id as emp_id
    FROM reg_doc_request_employee rdre2
             JOIN doc_employee de on rdre2.id_employee = de.id
             JOIN doc_person dp on de.id_person = dp.id
) as person on id_employee = person.emp_id
WHERE (dr.status_review = 1 OR dr.status_review = 4) AND dr.status_pause = 0 ORDER BY id;

alter table v_doc_person_and_org_info_v2
    owner to postgres;