create table users (
	uid varchar(10) primary key, 
	name varchar(20),
	phone varchar(10),
	class integer not null);

create table wallet (
	uid varchar(10) not null,
	amount numeric(8,2) check (amount >=0),
	foreign key (uid) references users
		on delete cascade,
	primary key (uid)
);

create table password (
	uid varchar(10) not null,
	password varchar(20) not null,
	foreign key (uid) references users
		on delete cascade,
	primary key (uid)
);

create table car (
	cid varchar(10) not null,
	password varchar(20),
	primary key (cid)
);

create table owns (
	cid varchar(10) not null,
	uid varchar(10) not null,
	foreign key (uid) references users on delete cascade,
	foreign key (cid) references car on delete cascade,
	primary key (uid, cid)
);

create table payer (
	cid varchar(10) not null,
	uid varchar(10) not null,
	start_time timestamp not null default now(),
	foreign key (cid) references users on delete restrict,
	foreign key (uid) references users on delete restrict,
	primary key (cid)
);

create table parking_mall (
	pid varchar(10) primary key,
	location varchar(20) not null,
	price numeric(6,2) not null);

create table parking_floor (
	pid varchar(10),
	floor_number varchar(10),
	total_space numeric(10),
	free_space numeric(10),
	primary key (pid, floor_number),
	foreign key (pid) references parking_mall
		on delete cascade);

create table parks (
	cid varchar(10) not null,
	pid varchar(10) not null,
	floor_number varchar(10),
	entry_time timestamp not null default now(),
	primary key (cid),
	foreign key (cid) references car on delete restrict,
	foreign key (pid, floor_number) references parking_floor on delete restrict
);