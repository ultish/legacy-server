
    alter table REVCHANGES
        drop constraint if exists FK_t69kea3hasj6uc6ddn5ck5r9y;

    alter table aud_chargecodes
        drop constraint if exists FK_n0x7anlgsrdh3dmgjk16lhtcm;

    alter table aud_taskcodes
        drop constraint if exists FK_73n565v3pr6j3sug32ri5g1nq;

    alter table aud_trackedtasks
        drop constraint if exists FK_sjtm4gun71fculslqyvqjymjm;

    alter table taskcodes
        drop constraint if exists FK_8q0mdns5dt9vhlp276he00yc6;

    alter table taskcodes
        drop constraint if exists FK_78apgsurnsqowd5ahvw9vomxq;

    drop table REVCHANGES cascade;

    drop table REVINFO cascade;

    drop table aud_chargecodes cascade;

    drop table aud_taskcodes cascade;

    drop table aud_trackedtasks cascade;

    drop table chargecodes cascade;

    drop table taskcodes cascade;

    drop table trackedtasks cascade;

    drop sequence chargecodes_id_seq;

    drop sequence hibernate_sequence;

    drop sequence trackedtasks_id_seq;

    create table REVCHANGES (
        rev int4 not null,
        entityname varchar(255)
    );

    create table REVINFO (
        rev int4 not null,
        revtstmp int8,
        primary key (rev)
    );

    create table aud_chargecodes (
        id int4 not null,
        rev int4 not null,
        revtype int2,
        code varchar(255),
        code_mod boolean,
        "createdAt" timestamp,
        "createdAt_mod" boolean,
        description varchar(255),
        description_mod boolean,
        expired boolean,
        expired_mod boolean,
        name varchar(255),
        name_mod boolean,
        "updatedAt" timestamp,
        "updatedAt_mod" boolean,
        "trackedTasks_mod" boolean,
        primary key (id, rev)
    );

    create table aud_taskcodes (
        rev int4 not null,
        "trackedtaskid" int4 not null,
        "chargecodeid" int4 not null,
        revtype int2,
        primary key (rev, "trackedtaskid", "chargecodeid")
    );

    create table aud_trackedtasks (
        id int4 not null,
        rev int4 not null,
        revtype int2,
        "createdAt" timestamp,
        "createdAt_mod" boolean,
        notes varchar(255),
        notes_mod boolean,
        "overtimeEnabled" boolean,
        "overtimeEnabled_mod" boolean,
        "updatedAt" timestamp,
        "updatedAt_mod" boolean,
        "chargeCodes_mod" boolean,
        primary key (id, rev)
    );

    create table chargecodes (
        id int4 not null,
        code varchar(255),
        "createdAt" timestamp,
        description varchar(255),
        expired boolean not null,
        name varchar(255),
        "updatedAt" timestamp,
        primary key (id)
    );

    create table taskcodes (
        "trackedtaskId" int4 not null,
        "chargecodeId" int4 not null
    );

    create table trackedtasks (
        id int4 not null,
        "createdAt" timestamp,
        notes varchar(255),
        "overtimeEnabled" boolean,
        "updatedAt" timestamp,
        primary key (id)
    );

    alter table REVCHANGES
        add constraint FK_t69kea3hasj6uc6ddn5ck5r9y
        foreign key (rev)
        references REVINFO;

    alter table aud_chargecodes
        add constraint FK_n0x7anlgsrdh3dmgjk16lhtcm
        foreign key (rev)
        references REVINFO;

    alter table aud_taskcodes
        add constraint FK_73n565v3pr6j3sug32ri5g1nq
        foreign key (rev)
        references REVINFO;

    alter table aud_trackedtasks
        add constraint FK_sjtm4gun71fculslqyvqjymjm
        foreign key (rev)
        references REVINFO;

    alter table taskcodes
        add constraint FK_8q0mdns5dt9vhlp276he00yc6
        foreign key ("chargecodeId")
        references chargecodes;

    alter table taskcodes
        add constraint FK_78apgsurnsqowd5ahvw9vomxq
        foreign key ("trackedtaskId")
        references trackedtasks;

    create sequence chargecodes_id_seq start 1 increment 1;

    create sequence hibernate_sequence;

    create sequence trackedtasks_id_seq start 1 increment 1;
