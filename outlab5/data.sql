insert into users values ('1', 'A', '123');
insert into users values ('2', 'B', '234');
insert into users values ('3', 'C', '345');
insert into users values ('4', 'D', '456');
insert into password select uid, name from users;
-- The password is the name of the users