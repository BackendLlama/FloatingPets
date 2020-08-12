create table fp_locale (
    recordId int auto_increment
        primary key,
    l_key    text null,
    value    text not null
);
create table fp_pet (
    recordId int auto_increment
        primary key,
    uniqueId text not null,
    owner    text not null,
    type     text not null,
    name     text not null,
    skills   text not_null,
    particle text not null
);
create table fp_type (
    recordId int auto_increment
        primary key,
    uniqueId text not null,
    name     text not null,
    texture  text not null,
    price    text not null,
    category text not_null,
);
create table fp_misc (
    recordId int auto_increment
        primary key,
    material text   not null,
    amount   int    not null,
    value    double not null
);

