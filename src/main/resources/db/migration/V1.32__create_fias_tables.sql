create schema if not exists fias;

create table if not exists fias.addr_obj_division_item
(
    id       bigint not null
        constraint addr_obj_division_item_pk
            primary key,
    parentid bigint,
    childid  bigint,
    changeid bigint
);

create table if not exists fias.addressobjecttype
(
    id         bigint not null
        constraint addressobjecttype_pk
            primary key,
    level      bigint,
    shortname  varchar(50),
    name       varchar(250),
    "desc"     varchar(250),
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactive   boolean
);

create table if not exists fias.adm_hierarchy_item
(
    id          bigint not null
        constraint adm_hierarchy_item_pk
            primary key,
    objectid    bigint,
    parentobjid bigint,
    changeid    bigint,
    regioncode  varchar(4),
    areacode    varchar(4),
    citycode    varchar(4),
    placecode   varchar(4),
    plancode    varchar(4),
    streetcode  varchar(4),
    previd      bigint,
    nextid      bigint,
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    isactive    smallint
);

create table if not exists fias.apartment
(
    id         bigint not null
        constraint apartment_pk
            primary key,
    objectid   bigint,
    objectguid varchar(36),
    changeid   bigint,
    number     varchar(50),
    aparttype  varchar(2),
    opertypeid varchar(2),
    previd     bigint,
    nextid     bigint,
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactual   smallint,
    isactive   smallint
);

create table if not exists fias.apartmenttype
(
    id         bigint not null
        constraint apartmenttype_pk
            primary key,
    name       varchar(50),
    shortname  varchar(50),
    "desc"     varchar(250),
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactive   boolean
);

create table if not exists fias.carplace
(
    id         bigint not null
        constraint carplace_pk
            primary key,
    objectid   bigint,
    objectguid varchar(36),
    changeid   bigint,
    number     varchar(50),
    opertypeid varchar(2),
    previd     bigint,
    nextid     bigint,
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactual   smallint,
    isactive   smallint
);

create table if not exists fias.change_history_item
(
    changeid    bigint not null
        constraint change_history_item_pk
            primary key,
    objectid    bigint,
    adrobjectid varchar(36),
    opertypeid  bigint,
    ndocid      bigint,
    changedate  timestamp
);

create table if not exists fias.house
(
    id         bigint not null
        constraint house_pk
            primary key,
    objectid   bigint,
    objectguid varchar(36),
    changeid   bigint,
    housenum   varchar(50),
    addnum1    varchar(50),
    addnum2    varchar(50),
    housetype  bigint,
    addtype1   bigint,
    addtype2   bigint,
    opertypeid bigint,
    previd     bigint,
    nextid     bigint,
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactual   smallint,
    isactive   smallint
);

create table if not exists fias.housetype
(
    id         bigint not null
        constraint housetype_pk
            primary key,
    name       varchar(50),
    shortname  varchar(50),
    "desc"     varchar(250),
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactive   boolean
);

create table if not exists fias.mun_hierarchy_item
(
    id          bigint not null
        constraint mun_hierarchy_item_pk
            primary key,
    objectid    bigint,
    parentobjid bigint,
    changeid    bigint,
    oktmo       varchar(11),
    previd      bigint,
    nextid      bigint,
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    isactive    smallint
);

create table if not exists fias.ndockind
(
    id   bigint not null
        constraint ndockind_pk
            primary key,
    name varchar(500)
);

create table if not exists fias.ndoctype
(
    id        bigint not null
        constraint ndoctype_pk
            primary key,
    name      varchar(500),
    startdate timestamp,
    enddate   timestamp
);

create table if not exists fias.normdoc
(
    id         bigint not null
        constraint normdoc_pk
            primary key,
    name       varchar(8000),
    date       timestamp,
    number     varchar(150),
    type       bigint,
    kind       bigint,
    updatedate timestamp,
    orgname    varchar(255),
    regnum     varchar(100),
    regdate    timestamp,
    accdate    timestamp,
    comment    varchar(8000)
);

create table if not exists fias.addr_object
(
    id         bigint not null
        constraint addr_object_pk
            primary key,
    objectid   bigint,
    objectguid varchar(36),
    changeid   bigint,
    name       varchar(250),
    typename   varchar(50),
    level      varchar(10),
    opertypeid bigint,
    previd     bigint,
    nextid     bigint,
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactual   smallint,
    isactive   smallint,
    createdate timestamp,
    levelid    bigint
);

create table if not exists fias.objectlevel
(
    level      integer not null
        constraint objectlevel_pk
            primary key,
    name       varchar(250),
    shortname  varchar(50),
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactive   boolean
);

create table if not exists fias.operationtype
(
    id         bigint not null
        constraint operationtype_pk
            primary key,
    name       varchar(100),
    shortname  varchar(100),
    "desc"     varchar(250),
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactive   boolean
);

create table if not exists fias.addr_obj_param
(
    id          bigint not null
        constraint addr_obj_param_pk
            primary key,
    objectid    bigint,
    changeid    bigint,
    typeid      integer,
    value       varchar(8000),
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    changeidend text
);

create table if not exists fias.houses_param
(
    id          bigint not null
        constraint houses_param_pk
            primary key,
    objectid    bigint,
    changeid    bigint,
    typeid      integer,
    value       varchar(8000),
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    changeidend text
);

create table if not exists fias.apartments_param
(
    id          bigint not null
        constraint apartments_param_pk
            primary key,
    objectid    bigint,
    changeid    bigint,
    typeid      integer,
    value       varchar(8000),
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    changeidend text
);

create table if not exists fias.rooms_param
(
    id          bigint not null
        constraint rooms_param_pk
            primary key,
    objectid    bigint,
    changeid    bigint,
    typeid      integer,
    value       varchar(8000),
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    changeidend text
);

create table if not exists fias.steads_param
(
    id          bigint not null
        constraint steads_param_pk
            primary key,
    objectid    bigint,
    changeid    bigint,
    typeid      integer,
    value       varchar(8000),
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    changeidend text
);

create table if not exists fias.carplaces_param
(
    id          bigint not null
        constraint carplaces_param_pk
            primary key,
    objectid    bigint,
    changeid    bigint,
    typeid      integer,
    value       varchar(8000),
    updatedate  timestamp,
    startdate   timestamp,
    enddate     timestamp,
    changeidend text
);

create table if not exists fias.paramtype
(
    id         bigint not null
        constraint paramtype_pk
            primary key,
    name       varchar(50),
    code       varchar(50),
    "desc"     varchar(120),
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactive   boolean
);

create table if not exists fias.room
(
    id         bigint not null
        constraint room_pk
            primary key,
    objectid   bigint,
    objectguid varchar(36),
    changeid   bigint,
    number     varchar(50),
    roomtype   varchar(1),
    opertypeid varchar(2),
    previd     bigint,
    nextid     bigint,
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactual   smallint,
    isactive   smallint
);

create table if not exists fias.roomtype
(
    id         bigint not null
        constraint roomtype_pk
            primary key,
    name       varchar(100),
    shortname  varchar(50),
    "desc"     varchar(250),
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactive   boolean
);

create table if not exists fias.stead
(
    id         bigint not null
        constraint stead_pk
            primary key,
    objectid   bigint,
    objectguid varchar(36),
    changeid   bigint,
    number     varchar(250),
    opertypeid varchar(2),
    previd     bigint,
    nextid     bigint,
    updatedate timestamp,
    startdate  timestamp,
    enddate    timestamp,
    isactual   smallint,
    isactive   smallint
);

create table if not exists fias.reestr_object
(
    objectid   bigint not null
        constraint reestr_object_pk
            primary key,
    createdate timestamp,
    changeid   bigint,
    levelid    bigint,
    updatedate timestamp,
    objectguid varchar(36),
    isactive   smallint
);

