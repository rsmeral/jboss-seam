alter table Booking drop constraint FK_booking_hotel;
alter table Booking drop constraint FK_booking_user;

drop table Booking if exists;
drop table Customer if exists;
drop table Hotel if exists;

drop sequence hibernate_sequence;

create table Booking (
    id bigint not null,
    beds integer not null,
    checkinDate date not null,
    checkoutDate date not null,
    creditCard varchar(16) not null,
    creditCardExpiryMonth integer not null,
    creditCardExpiryYear integer not null,
    creditCardName varchar(70) not null,
    smoking boolean not null,
    hotel_id bigint not null,
    user_username varchar(15) not null,
    primary key (id)
);

create table Customer (
    username varchar(15) not null,
    name varchar(100) not null,
    password varchar(15) not null,
    primary key (username)
);

create table Hotel (
    id bigint not null,
    address varchar(100) not null,
    city varchar(40) not null,
    country varchar(40) not null,
    name varchar(50) not null,
    price decimal(6,2),
    state varchar(10) not null,
    zip varchar(6) not null,
    primary key (id)
);

alter table Booking add constraint FK_booking_hotel foreign key (hotel_id) references Hotel;
alter table Booking add constraint FK_booking_user foreign key (user_username) references Customer;

create sequence hibernate_sequence start with 1 increment by 1;


insert into Customer (username, password, name) values ('demo1', 'demodemo', 'Demo User');
insert into Customer (username, password, name) values ('demo2', 'demodemo', 'Demo User');
insert into Customer (username, password, name) values ('demo3', 'demodemo', 'Demo User');
insert into Customer (username, password, name) values ('demo4', 'demodemo', 'Demo User');
insert into Hotel (id, price, name, address, city, state, zip, country) values (1, 120, 'Marriott Courtyard', 'Tower Place, Buckhead', 'Atlanta', 'GA', '30305', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (2, 180, 'Doubletree', 'Tower Place, Buckhead', 'Atlanta', 'GA', '30305', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (3, 450, 'W Hotel', 'Union Square, Manhattan', 'NY', 'NY', '10011', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (4, 450, 'W Hotel', 'Lexington Ave, Manhattan', 'NY', 'NY', '10011', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (5, 250, 'Hotel Rouge', '1315 16th Street NW', 'Washington', 'DC', '20036', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (6, 300, '70 Park Avenue Hotel', '70 Park Avenue', 'NY', 'NY', '10011', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (8, 300, 'Conrad Miami', '1395 Brickell Ave', 'Miami', 'FL', '33131', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (9, 80, 'Sea Horse Inn', '2106 N Clairemont Ave', 'Eau Claire', 'WI', '54703', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (10, 90, 'Super 8 Eau Claire Campus Area', '1151 W Macarthur Ave', 'Eau Claire', 'WI', '54701', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (11, 160, 'Marriott Downtown', '55 Fourth Street', 'San Francisco', 'CA', '94103', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (12, 200, 'Hilton Diagonal Mar', 'Passeig del Taulat 262-264', 'Barcelona', 'Catalunya', '08019', 'Spain');
insert into Hotel (id, price, name, address, city, state, zip, country) values (13, 210, 'Hilton Tel Aviv', 'Independence Park', 'Tel Aviv', '', '63405', 'Israel');
insert into Hotel (id, price, name, address, city, state, zip, country) values (14, 240, 'InterContinental Tokyo Bay', 'Takeshiba Pier', 'Tokyo', '', '105', 'Japan');
insert into Hotel (id, price, name, address, city, state, zip, country) values (15, 130, 'Hotel Beaulac', ' Esplanade Léopold-Robert 2', 'Neuchatel', '', '2000', 'Switzerland');
insert into Hotel (id, price, name, address, city, state, zip, country) values (16, 140, 'Conrad Treasury Place', 'William & George Streets', 'Brisbane', 'QLD', '4001', 'Australia');
insert into Hotel (id, price, name, address, city, state, zip, country) values (17, 230, 'Ritz Carlton', '1228 Sherbrooke St', 'West Montreal', 'Quebec', 'H3G1H6', 'Canada');
insert into Hotel (id, price, name, address, city, state, zip, country) values (18, 460, 'Ritz Carlton', 'Peachtree Rd, Buckhead', 'Atlanta', 'GA', '30326', 'USA');
insert into Hotel (id, price, name, address, city, state, zip, country) values (19, 220, 'Swissotel', '68 Market Street', 'Sydney', 'NSW', '2000', 'Australia');
insert into Hotel (id, price, name, address, city, state, zip, country) values (20, 250, 'Meliá White House', 'Albany Street', 'Regents Park London', '', 'NW13UP', 'Great Britain');
insert into Hotel (id, price, name, address, city, state, zip, country) values (21, 210, 'Hotel Allegro', '171 West Randolph Street', 'Chicago', 'IL', '60601', 'USA');
