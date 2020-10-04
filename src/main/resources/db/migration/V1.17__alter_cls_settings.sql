alter table cls_settings add column key varchar(100)
;
alter table cls_settings drop column messages
;
alter table cls_settings add column value jsonb
;
alter table cls_settings add column string_value text
;
truncate table cls_settings
;

insert into cls_settings(status, key, value) values (1, 'messages','{"actualization": {"requestNotFound": "<p><b>Заявка для указанного ИНН отсутствует в базе рассмотренных заявок на портале \"Работающая Бурятия\".</b></p><p>Проверьте правильность ввода ИНН и повторите поиск.</p><p>Если вы ранее не подавали заявку на портале \"Работающая Бурятия\" воспользуйтесь формой подачи заявки выбрав форму соответствующую вашему виду деятельности. <a href=\"/\">Подать новое заявление</a></p><p>Проверить ранее поданные заявки можно на сервисе <a href=\"http://cr.govrb.ru/org_check\">Проверка сведений</a> указав ИНН в форме ввода.</p>", "requestFound": "<p style=\"color: red\"><b>Если вы ранее подавали заявку используя форму (Общие основания (более 100 сотрудников)) \"Импорт Excel\" актуализируйте информацию в шаблоне и отправьте его используя форму <a href=\"/upload\">Общие основания (более 100 сотрудников)</a></b></p><p><b>Выберите для актуализации наиболее подходяющую для вашего вида деятельности форму заявки.</b></p>", "requestActualized": "Благодарим за сотрудничество!"}}')
;
insert into cls_settings(status, key, string_value) values (1, 'actualizeSubject', 'актуализируйте утвержденную заявку на портале Работающая Бурятия')
;
insert into cls_settings(status, key, string_value) values (1, 'actualizeFormAddr','http://rabota.govrb.ru/actualize_form')
;

