
    alter table REVCHANGES
        drop constraint FK_t69kea3hasj6uc6ddn5ck5r9y;

    alter table aud_chargecodes
        drop constraint FK_n0x7anlgsrdh3dmgjk16lhtcm;

    alter table aud_taskcodes
        drop constraint FK_73n565v3pr6j3sug32ri5g1nq;

    alter table aud_timeblocks
        drop constraint FK_nf6qwiuvo47atgevlxfs9lgir;

    alter table aud_trackedtasks
        drop constraint FK_sjtm4gun71fculslqyvqjymjm;

    alter table aud_users
        drop constraint FK_sjwky3yv34xor8kto7f7h349l;

    alter table taskcodes
        drop constraint FK_8q0mdns5dt9vhlp276he00yc6;

    alter table taskcodes
        drop constraint FK_78apgsurnsqowd5ahvw9vomxq;

    alter table timeblocks
        drop constraint FK_89abtyp15km4g7jyosk09jno5;

    alter table timeblocks
        drop constraint FK_lqv4dwvi2mh3g15fkliowj3yg;

    alter table trackedtasks
        drop constraint FK_pea5bxx131y77yoxet4vjxlfy;

    drop table if exists REVCHANGES cascade;

    drop table if exists REVINFO cascade;

    drop table if exists aud_chargecodes cascade;

    drop table if exists aud_taskcodes cascade;

    drop table if exists aud_timeblocks cascade;

    drop table if exists aud_trackedtasks cascade;

    drop table if exists aud_users cascade;

    drop table if exists chargecodes cascade;

    drop table if exists taskcodes cascade;

    drop table if exists timeblocks cascade;

    drop table if exists trackedtasks cascade;

    drop table if exists users cascade;

    drop sequence chargecodes_id_seq;

    drop sequence hibernate_sequence;

    drop sequence timeblocks_id_seq;

    drop sequence trackedtasks_id_seq;

    drop sequence users_id_seq;

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
        "createdAt" timestamp,
        description varchar(255),
        expired boolean,
        name varchar(255),
        "updatedAt" timestamp,
        primary key (id, rev)
    );

    create table aud_taskcodes (
        rev int4 not null,
        "trackedtaskid" int4 not null,
        "chargecodeid" int4 not null,
        revtype int2,
        primary key (rev, trackedtaskId, chargecodeId)
    );

    create table aud_timeblocks (
        id int4 not null,
        rev int4 not null,
        revtype int2,
        "startTime" timestamp,
        "trackedtaskid" int4,
        "userid" int4,
        primary key (id, rev)
    );

    create table aud_trackedtasks (
        id int4 not null,
        rev int4 not null,
        revtype int2,
        "createdAt" timestamp,
        notes varchar(255),
        "overtimeEnabled" boolean,
        "updatedAt" timestamp,
        "userid" int4,
        primary key (id, rev)
    );

    create table aud_users (
        id int4 not null,
        rev int4 not null,
        revtype int2,
        password varchar(255),
        username varchar(255),
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

    create table timeblocks (
        id int4 not null,
        "startTime" timestamp,
        "trackedTaskId" int4,
        "userId" int4,
        primary key (id)
    );

    create table trackedtasks (
        id int4 not null,
        "createdAt" timestamp,
        notes varchar(255),
        "overtimeEnabled" boolean,
        "updatedAt" timestamp,
        "userId" int4,
        primary key (id)
    );

    create table users (
        id int4 not null,
        password varchar(255),
        username varchar(255),
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

    alter table aud_timeblocks
        add constraint FK_nf6qwiuvo47atgevlxfs9lgir
        foreign key (rev)
        references REVINFO;

    alter table aud_trackedtasks
        add constraint FK_sjtm4gun71fculslqyvqjymjm
        foreign key (rev)
        references REVINFO;

    alter table aud_users
        add constraint FK_sjwky3yv34xor8kto7f7h349l
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

    alter table timeblocks
        add constraint FK_89abtyp15km4g7jyosk09jno5
        foreign key ("trackedTaskId")
        references trackedtasks;

    alter table timeblocks
        add constraint FK_lqv4dwvi2mh3g15fkliowj3yg
        foreign key ("userId")
        references users;

    alter table trackedtasks
        add constraint FK_pea5bxx131y77yoxet4vjxlfy
        foreign key ("userId")
        references users;

    create sequence chargecodes_id_seq start 1 increment 1;

    create sequence hibernate_sequence;

    create sequence timeblocks_id_seq start 1 increment 1;

    create sequence trackedtasks_id_seq start 1 increment 1;

    create sequence users_id_seq start 1 increment 1;
