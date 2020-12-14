create table if not exists reg_help
(
    id serial not null
        constraint reg_help_pkey primary key,

    key varchar(36),
    name varchar(36),
    description text,
    time_create timestamp not null default current_timestamp
);

INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (6, 'TypeRequests', 'Предписания', 'Здесь отображаются информация по предписаниям', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (15, 'News', 'Новости', 'Здесь отображаются новости', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (9, 'Templates', 'Шаблоны сообщений', 'Шаблоны сообщений', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (11, 'Okveds', 'ОКВЭДы', 'Здесь отображаются ОКВЭДы (ОКВЭД - Общероссийский классификатор видов экономической деятельности)', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (10, 'Statistic', 'Статистика', 'Статистика', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (5, 'Requests', 'Заявки', 'Здесь отображаются информация по заявкам', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (24, 'Prescript', 'Предписания', 'Здесь отображаются информация по предписаниям', '2020-12-14 05:54:00.089726');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (19, 'CommonInfo', 'Общая информация', 'Здесь отображается информация об организации', '2020-12-14 10:16:35.472504');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (21, 'Settings', 'Настройки', 'Настройки', '2020-12-14 05:54:00.089726');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (23, 'Address', 'Фактические адреса', 'Здесь собраны фактические адреса организации', '2020-12-14 05:54:00.089726');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (22, 'Documents', 'Документы', 'Здесь отображается информация по документам', '2020-12-14 05:54:00.089726');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (3, 'DepartmentUsers', 'Пользователи подразделений', 'Здесь отображается информация по пользователям подразделениям', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (13, 'MailingMessages', 'Сообщения рассылок', 'Здесь отображаются сообщения рассылок', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (14, 'Fias', 'Загрузка ФИАС ЕГРЮЛ', 'Федеральная информационная адресная система<br>Единый государственный реестр юридических лиц', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (25, 'Contacts', 'Контакты', 'Здесь отображаются контактные номера ', '2020-12-14 05:54:00.089726');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (20, 'Employees', 'Сотрудники', 'Здесь отображается информация по сотрудникам', '2020-12-14 05:54:00.089726');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (12, 'Mailing', 'Типы рассылок', 'Здесь отображаются типы рассылок', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (2, 'Departments', 'Подразделения', 'Здесь отображается информация по подразделениям ', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (7, 'RestrictionTypes', 'Типы ограничений', 'Здесь отображаются информация по типам ограничений', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (4, 'Organizations', 'Организации', 'Здесь отображаются информация по организациям', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (8, 'Principals', 'Пользователи', 'Здесь отображаются информация по пользователям', '2020-12-14 01:37:00.327321');