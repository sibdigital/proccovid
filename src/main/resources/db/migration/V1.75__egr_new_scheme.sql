alter table reg_egrul
    add if not exists full_name text;

alter table reg_egrul
    add if not exists short_name text;

--    Объединенная таблица СвОргПФ, СвОргФСС, атрибутов СвРегОрг - egr.sv_org
/*    СвОргПФ/ СвОргФСС/ СвРегОрг  -
        Сведения об органе ПФ/ ФСС/ налоговой
    СвОрг
    атрибуты:
    1) Код  - Код по справочнику СТОПФ/СТОФСС/СОУН
    2) Наим - наименование
    3*) Адр - адрес (только для СвРегОрг)
 */
CREATE TABLE if not exists egr.sv_org
(
    id      serial     not null
        constraint sv_org_pf_pkey
            primary key,
    type_org integer   not null, -- тип: ФСС/ПФ/Налоговая
    code    varchar(6) not null,
    name    text,
    adr     varchar(128)
);

--    Объединенная таблица СвРегПФ, СвРегФСС, СвРегОрг - egr.sv_reg
/*    СвРегПФ/СвРегФСС - Сведения о регистрации ЮЛ в качестве страхователя в ПФ/ФСС
      СвРегОрг - Сведения о регистрирующем органе по месту нахождения юридического лица
    СвРег
    <sequence>
        <element name="СвОрг"></element>        - Сведения об исполнительном органе ПФ/ФСС
        <element name="ГРНДата"></element>      - ГРН и дата внесения в ЕГРЮЛ записи
        <element name="ГРНДатаИспр"></element>  - ГРН и дата внесения в ЕГРЮЛ записи об исправлении технической ошибки
    </sequence>
    <attribute name="РегНом"></attribute>       - Регистрационный номер в ПФ/ФСС
    <attribute name="ДатаРег"></attribute>      - Дата регистрации
 */
CREATE TABLE if not exists egr.sv_reg
(
    id               serial      not null
        constraint sv_reg_pkey
            primary key,
    id_egrul         integer
        constraint fk_reg_egrul
            references reg_egrul,
    id_egrip         integer
        constraint fk_reg_egrip
            references reg_egrip,
    type_org integer   not null,            -- тип: ФСС/ПФ/Налоговая
    reg_num          varchar(15),           -- null для налоговой
    reg_date         date,                  -- null для налоговой
    id_sv_org        integer                -- элемент СвОрг - справочник с кодами органов
        constraint fk_sv_org
            references egr.sv_org,
    grn              varchar(15),
    record_date      date,
    grn_corr         varchar(15),           -- null для налоговой
    record_date_corr date                   -- null для налоговой
);

CREATE TABLE if not exists egr.opf(
    id                       serial not null
        constraint opf_pkey
            primary key,
    spr                      varchar(5),
    code                     varchar(5),
    full_name                text
);

ALTER TABLE reg_egrul add column if not exists id_opf integer
    constraint fk_opf
        references egr.opf;

ALTER TABLE egr.sv_status
    add column if not exists org_dos_sv smallint;

ALTER TABLE egr.sv_status
    add column if not exists org_dos_sv_record_date date;

ALTER TABLE egr.sv_status
    add column if not exists org_dos_sv_grn varchar(15);

ALTER TABLE egr.sv_status
    add column if not exists org_dos_sv_record_date_corr date;

ALTER TABLE egr.sv_status
    add column if not exists org_dos_sv_grn_corr varchar(15);
