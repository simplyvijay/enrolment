drop table if exists STUDENT;
drop table if exists COURSE;

create table COURSE (
    id int not null,
    name varchar(30) not null,
    primary key (id)
) engine=INNODB;

create table STUDENT (
    email varchar(60) not null,
    first_name varchar(40),
    last_name varchar(60),
    dob varchar(10),
    location varchar(30),
    course_id int not null,
    foreign key (course_id) references COURSE(id),
    primary key (email)
) engine=INNODB;

insert into COURSE (id, name) values (1001, 'Cloud'), (1002, 'Java'), (1003, '.Net'), (1004, 'ReactJS'), (1005, 'Python');
