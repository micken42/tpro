
   
 -- CREATE SCRIPT FOR BOOKSTORE PLUGIN
 
create table BookstoreBook (
    id integer not null auto_increment,
    title varchar(255) not null,
    author varchar(255) not null,
    primary key (id)
) engine=InnoDB;

alter table BookstoreBook 
   add constraint book_unique_title unique (title);


