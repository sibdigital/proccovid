alter table okved add column if not exists section_code varchar(2);

-- update okved
--     set section_code = case
--         when class_code in ('01','02','03') then '01'
--         when class_code in ('05','06','07','08','09') then '02'
--         when class_code in ('10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30','31','32','33') then '03'
--         when class_code in ('35') then '04'
--         when class_code in ('36','37','38','39') then '05'
--         when class_code in ('41','42','43') then '06'
--         when class_code in ('45','46','47') then '07'
--         when class_code in ('49','50','51','52','53') then '08'
--         when class_code in ('55','56') then '09'
--         when class_code in ('58','59','60','61','62','63') then '10'
--         when class_code in ('64','65','66') then '11'
--         when class_code in ('68') then '12'
--         when class_code in ('69','70','71','72','73','74','75') then '13'
--         when class_code in ('77','78','79','80','81','82') then '14'
--         when class_code in ('84') then '15'
--         when class_code in ('85') then '16'
--         when class_code in ('86','87','88') then '17'
--         when class_code in ('90','91','92','93') then '18'
--         when class_code in ('94','95','96') then '19'
--         when class_code in ('97','98') then '20'
--         when class_code in ('99') then '21'
--         else section_code
--     end;
--
-- update okved set path = concat(version,'.',section_code,'.',kind_code)::ltree where kind_code is not null;
--
-- insert into okved
--     (id,section_code,type_code,status,kind_name,version,path)
--     values
--         (uuid_generate_v4(), '01', 0, 1, 'Сельское, лесное хозяйство, охота, рыболовство и рыбоводство', '2014', '2014.01'::ltree),
--         (uuid_generate_v4(), '02', 0, 1, 'Добыча полезных ископаемых', '2014', '2014.02'::ltree),
--         (uuid_generate_v4(), '03', 0, 1, 'Обрабатывающие производства', '2014', '2014.03'::ltree),
--         (uuid_generate_v4(), '04', 0, 1, 'Обеспечение электрической энергией, газом и паром; кондиционирование воздуха', '2014', '2014.04'::ltree),
--         (uuid_generate_v4(), '05', 0, 1, 'Водоснабжение; водоотведение, организация сбора и утилизации отходов, деятельность по ликвидации загрязнений', '2014', '2014.05'::ltree),
--         (uuid_generate_v4(), '06', 0, 1, 'Строительство', '2014', '2014.06'::ltree),
--         (uuid_generate_v4(), '07', 0, 1, 'Торговля оптовая и розничная; ремонт автотранспортных средств и мотоциклов', '2014', '2014.07'::ltree),
--         (uuid_generate_v4(), '08', 0, 1, 'Транспортировка и хранение', '2014', '2014.08'::ltree),
--         (uuid_generate_v4(), '09', 0, 1, 'Деятельность гостиниц и предприятий общественного питания', '2014', '2014.09'::ltree),
--         (uuid_generate_v4(), '10', 0, 1, 'Деятельность в области информации и связи', '2014', '2014.10'::ltree),
--         (uuid_generate_v4(), '11', 0, 1, 'Деятельность финансовая и страховая', '2014', '2014.11'::ltree),
--         (uuid_generate_v4(), '12', 0, 1, 'Деятельность по операциям с недвижимым имуществом', '2014', '2014.12'::ltree),
--         (uuid_generate_v4(), '13', 0, 1, 'Деятельность профессиональная, научная и техническая', '2014', '2014.13'::ltree),
--         (uuid_generate_v4(), '14', 0, 1, 'Деятельность административная и сопутствующие дополнительные услуги', '2014', '2014.14'::ltree),
--         (uuid_generate_v4(), '15', 0, 1, 'Государственное управление и обеспечение военной безопасности; социальное обеспечение', '2014', '2014.15'::ltree),
--         (uuid_generate_v4(), '16', 0, 1, 'Образование', '2014', '2014.16'::ltree),
--         (uuid_generate_v4(), '17', 0, 1, 'Деятельность в области здравоохранения и социальных услуг', '2014', '2014.17'::ltree),
--         (uuid_generate_v4(), '18', 0, 1, 'Деятельность в области культуры, спорта, организации досуга и развлечений', '2014', '2014.18'::ltree),
--         (uuid_generate_v4(), '19', 0, 1, 'Предоставление прочих видов услуг', '2014', '2014.19'::ltree),
--         (uuid_generate_v4(), '20', 0, 1, 'Деятельность домашних хозяйств как работодателей; недифференцированная деятельность частных домашних хозяйств по производству товаров и оказанию услуг для собственного потребления', '2014', '2014.20'::ltree),
--         (uuid_generate_v4(), '21', 0, 1, 'Деятельность экстерриториальных организаций и органов', '2014', '2014.21'::ltree),
--         (uuid_generate_v4(), '01', 0, 1, 'Сельское, лесное хозяйство, охота, рыболовство и рыбоводство', '2001', '2001.01'::ltree),
--         (uuid_generate_v4(), '02', 0, 1, 'Добыча полезных ископаемых', '2001', '2001.02'::ltree),
--         (uuid_generate_v4(), '03', 0, 1, 'Обрабатывающие производства', '2001', '2001.03'::ltree),
--         (uuid_generate_v4(), '04', 0, 1, 'Обеспечение электрической энергией, газом и паром; кондиционирование воздуха', '2001', '2001.04'::ltree),
--         (uuid_generate_v4(), '05', 0, 1, 'Водоснабжение; водоотведение, организация сбора и утилизации отходов, деятельность по ликвидации загрязнений', '2001', '2001.05'::ltree),
--         (uuid_generate_v4(), '06', 0, 1, 'Строительство', '2001', '2001.06'::ltree),
--         (uuid_generate_v4(), '07', 0, 1, 'Торговля оптовая и розничная; ремонт автотранспортных средств и мотоциклов', '2001', '2001.07'::ltree),
--         (uuid_generate_v4(), '08', 0, 1, 'Транспортировка и хранение', '2001', '2001.08'::ltree),
--         (uuid_generate_v4(), '09', 0, 1, 'Деятельность гостиниц и предприятий общественного питания', '2001', '2001.09'::ltree),
--         (uuid_generate_v4(), '10', 0, 1, 'Деятельность в области информации и связи', '2001', '2001.10'::ltree),
--         (uuid_generate_v4(), '11', 0, 1, 'Деятельность финансовая и страховая', '2001', '2001.11'::ltree),
--         (uuid_generate_v4(), '12', 0, 1, 'Деятельность по операциям с недвижимым имуществом', '2001', '2001.12'::ltree),
--         (uuid_generate_v4(), '13', 0, 1, 'Деятельность профессиональная, научная и техническая', '2001', '2001.13'::ltree),
--         (uuid_generate_v4(), '14', 0, 1, 'Деятельность административная и сопутствующие дополнительные услуги', '2001', '2001.14'::ltree),
--         (uuid_generate_v4(), '15', 0, 1, 'Государственное управление и обеспечение военной безопасности; социальное обеспечение', '2001', '2001.15'::ltree),
--         (uuid_generate_v4(), '16', 0, 1, 'Образование', '2001', '2001.16'::ltree),
--         (uuid_generate_v4(), '17', 0, 1, 'Деятельность в области здравоохранения и социальных услуг', '2001', '2001.17'::ltree),
--         (uuid_generate_v4(), '18', 0, 1, 'Деятельность в области культуры, спорта, организации досуга и развлечений', '2001', '2001.18'::ltree),
--         (uuid_generate_v4(), '19', 0, 1, 'Предоставление прочих видов услуг', '2001', '2001.19'::ltree),
--         (uuid_generate_v4(), '20', 0, 1, 'Деятельность домашних хозяйств как работодателей; недифференцированная деятельность частных домашних хозяйств по производству товаров и оказанию услуг для собственного потребления', '2001', '2001.20'::ltree),
--         (uuid_generate_v4(), '21', 0, 1, 'Деятельность экстерриториальных организаций и органов', '2001', '2001.21'::ltree);