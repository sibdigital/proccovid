CREATE OR REPLACE VIEW public.v_doc_person_and_org_info AS
	SELECT p.id,
    	req.id_request,
    	p.lastname,
    	p.firstname,
    	p.patronymic,
    	p.status_import,
    	p.time_import,
    	org.inn,
    	org.short_name,
    	tr.begin_registration,
    	tr.end_registration
	FROM reg_doc_request_employee req
		JOIN doc_employee AS emp ON emp.id = req.id_employee
		JOIN doc_person AS p ON p.id = emp.id_person
		JOIN cls_organization AS org ON org.id = emp.id_organization
		JOIN doc_request AS dr ON dr.id = req.id_request
		JOIN cls_type_request AS tr ON tr.id = dr.id_type_request
	WHERE dr.status_review IN (1, 4) AND dr.status_pause = 0;



