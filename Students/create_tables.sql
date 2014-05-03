-- this code can be loaded ot generate the requisite tables to use this software

-- First drop any existing conflicts to be safe.
--
drop table Students cascade constraints;
drop table Courses cascade constraints;
drop table Enrolled cascade constraints;
--
-- Now, add each table.
--
create table Students(
	sid integer primary key,
	sname varchar2(30)
	);

create table Courses(
	cid integer primary key,
	cname varchar2(40),
	credits integer
	);
create table Enrolled(
	sid integer,
	cid integer,
	primary key(sid,cid),
	foreign key(sid) references students,
	foreign key(cid) references courses
	);
	
--test data
/*	
insert into students values(1, 'Alice');
insert into students values(2, 'Bob');
insert into students values(3, 'Carl');
insert into students values(4, 'Denise');

insert into courses values(1, 'Java', 4);
insert into courses values(2, 'Micro', 3);
insert into courses values(3, 'C', 3);
insert into courses values(4, 'US Hist.', 4);

insert into enrolled values(1,1);
insert into enrolled values(1,2);
insert into enrolled values(2,2);
insert into enrolled values(2,4);
insert into enrolled values(3,1);
insert into enrolled values(3,3);
insert into enrolled values(4,4);
*/
