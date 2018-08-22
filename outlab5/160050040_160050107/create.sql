create table users(
    uid varchar(10) primary key, 
    name varchar(20), 
    phone varchar(10));

create table conversations(
     uid1 varchar(10) references users, 
     uid2 varchar(10) references users, 
     thread_id serial, 
     primary key (uid1, uid2),
     unique(thread_id),  
     check (uid1 < uid2));

create table posts (
    post_id serial primary key,
    thread_id integer references conversations(thread_id),
    uid varchar(10) references users,
    timestamp timestamp,
    text varchar(256)
);