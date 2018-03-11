CREATE DATABASE claimsDb;
USE claimsDb;

CREATE TABLE claims (
    id smallint unsigned not null auto_increment,
    ClaimId int,
    InsuredId int,
    Amount int,
    PatientName varchar(60),
    constraint pk_example primary key (id)
);
commit;

 