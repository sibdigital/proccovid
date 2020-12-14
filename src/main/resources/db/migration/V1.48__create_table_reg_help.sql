create table if not exists reg_help
(
    id serial not null
        constraint reg_help_pkey primary key,

    key varchar(36),
    name varchar(36),
    description text,
    time_create timestamp not null default current_timestamp
);

INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (1, 'Departments', 'Подразделения', 'Departments', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (2, 'DepartmentUsers', 'Пользователи подразделений', 'DepartmentUsers', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (3, 'Organizations', 'Организации', 'Organizations', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (4, 'Requests', 'Заявки', 'Requests', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (5, 'TypeRequests', 'Предписания', 'TypeRequests', '2020-12-14 09:25:48.067721');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (6, 'RestrictionTypes', 'Типы ограничений', 'RestrictionTypes', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (7, 'Principals', 'Пользователи', 'Principals', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (8, 'Templates', 'Шаблоны сообщений', 'Templates', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (9, 'Statistic', 'Статистика', 'Statistic', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (10, 'Okveds', 'ОКВЭДы', 'Okveds', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (11, 'Mailing', 'Типы рассылок', 'Mailing', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (12, 'MailingMessages', 'Сообщения рассылок', 'MailingMessages', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (13, 'Fias', 'Загрузка ФИАС ЕГРЮЛ', ' ЕГРЮЛ', '2020-12-14 01:37:00.327321');
INSERT INTO public.reg_help (id, key, name, description, time_create) VALUES (14, 'News', 'Новости', 'News', '2020-12-14 01:37:00.327321');