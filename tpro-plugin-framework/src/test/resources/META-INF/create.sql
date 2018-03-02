
 -- CREATE SCRIPT FOR USER MANAGEMENT COMPONENT 
 
create table Context (
    id integer not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table `Group` (
    id integer not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table Group_Permission (
    group_id integer not null,
    permission_id integer not null,
    primary key (group_id, permission_id)
) engine=InnoDB;

create table Group_User (
    group_id integer not null,
    user_id integer not null,
    primary key (group_id, user_id)
) engine=InnoDB;

create table Permission (
    id integer not null auto_increment,
    name varchar(255) not null,
    context_id integer,
    primary key (id)
) engine=InnoDB;

create table User (
    id integer not null auto_increment,
    email varchar(255) not null,
    password varchar(255),
    prename varchar(255),
    surname varchar(255),
    username varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table User_Permission (
   user_id integer not null,
    permission_id integer not null,
    primary key (user_id, permission_id)
) engine=InnoDB;

alter table User 
   add constraint user_unique_username unique (username);

alter table User 
   add constraint user_unique_email unique (email);

alter table Context 
   add constraint context_unique_name unique (name);

alter table `Group` 
   add constraint group_unique_name unique (name);


