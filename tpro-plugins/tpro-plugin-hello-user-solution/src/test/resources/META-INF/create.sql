
 -- CREATE SCRIPT FUER DIE ANLAGE DES DATENBANK SCHEMAS DER VON DEM 
 -- PLUGIN VERWENDETEN DATENBANK (ABFOLGE NATIVER SQL ABFRAGEN)

create table Visitor (
    id integer not null auto_increment,
    fullname varchar(255) not null,
    role varchar(255) not null,
    primary key (id)
) engine=InnoDB;

alter table Visitor 
   add constraint visitor_unique_fullname unique (fullname);