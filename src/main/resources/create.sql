create table if not exists COURSE (
    id int not null,
    name varchar(30) not null,
    primary key (id)
) engine=INNODB;

create table if not exists STUDENT (
    email varchar(60) not null,
    first_name varchar(40),
    last_name varchar(60),
    dob varchar(10),
    location varchar(30),
    course_id int not null,
    foreign key (course_id) references COURSE(id),
    primary key (email)
) engine=INNODB;

insert ignore into COURSE (id, name) values (1001, 'Cloud'), (1002, 'Java'), (1003, '.Net'), (1004, 'ReactJS'), (1005, 'Python');
