CREATE TABLE student (
                         id serial primary key not null ,
                         last_name varchar(30),
                         first_name varchar(30),
                         middle_name varchar(30),
                         grp varchar(10),
                         programme varchar(100)
);

CREATE TABLE teacher (
                         id serial primary key not null ,
                         last_name varchar(30),
                         first_name varchar(30),
                         middle_name varchar(30),
                         dept varchar(50),
                         grade varchar(10),
                         experience int
);