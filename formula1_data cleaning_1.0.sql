use formula1;


-- Rename column names for imported tables. Change from "xxxYyy" format to "xxx_yyy" format.
-- (executed)
alter table circuits CHANGE circuitId circuit_id int;
alter table circuits change circuitRef circuit_reference_name varchar(255);
alter table circuits change lat latitude double;
alter table circuits change lng longitude double;
alter table circuits change alt altitude int;

alter table constructor_results change constructorResultsId constructor_results_id int;
alter table constructor_results change raceId race_id int;
alter table constructor_results change constructorId constructor_id int;

alter table constructors change constructorId constructor_id int;
alter table constructors change constructorRef constructor_reference_name varchar(255);

alter table constructor_standings change constructorStandingsId constructor_standings_id int;
alter table constructor_standings change raceId race_id int;
alter table constructor_standings change constructorId constructor_id int;

alter table drivers change driverId driver_id int;
alter table drivers change driverRef driver_reference_name varchar(255);
alter table drivers change forename first_name varchar(255);
alter table drivers change surname last_name varchar(255);
alter table drivers change dob date_of_birth date;
alter table drivers change `number` driver_number int;

alter table driver_standings change driverStandingsId driver_standings_id int;
alter table driver_standings change raceId race_id int;
alter table driver_standings change driverId driver_id int;

alter table lap_times change raceid race_id int;
alter table lap_times change driverId driver_id int;

alter table pit_stops change raceId race_id int;
alter table pit_stops change driverId driver_id int;

alter table qualifying change qualifyId qualifying_id int;
alter table qualifying change raceId race_id int;
alter table qualifying change driverId driver_id int;
alter table qualifying change constructorId constructor_id int;
alter table qualifying change `number` car_number int;
alter table qualifying change q1 q1_session_time varchar(50);
alter table qualifying change q2 q2_session_time varchar(50);
alter table qualifying change q3 q3_session_time varchar(50);

rename table qualifying TO qualifying_results;

alter table race_results change `number` car_number int;
alter table race_results change grid starting_grid_position int;
alter table race_results change position final_position varchar(50);
alter table race_results change position_order final_position_order int;
alter table race_results change laps laps_completed int;
alter table race_results change `rank` fastest_lap_ranking int;

alter table races change raceId race_id int;
alter table races change circuitId circuit_id int;
alter table races change `name` race_name varchar(255);
alter table races change `date` race_date date;
alter table races change `time` race_local_time time;
alter table races change fp1_date free_practice_1_date date;
alter table races change fp1_time free_practice_1_local_time time;
alter table races change fp2_date free_practice_2_date date;
alter table races change fp2_time free_practice_2_local_time time;
alter table races change fp3_date free_practice_3_date date;
alter table races change fp3_time free_practice_3_local_time time;
alter table races change quali_date qualifying_date date;
alter table races change quali_time qualifying_time time;
alter table races change sprint_date sprint_race_date date;
alter table races change sprint_time sprint_race_time time;

alter table sprint_results change result_id sprint_result_id int;
alter table sprint_results change `number` car_number int;
alter table sprint_results change grid starting_grid_position int;
alter table sprint_results change position final_position varchar(50);
alter table sprint_results change position_order final_position_order int;
alter table sprint_results change laps laps_completed int;

alter table `status` change statusId status_id int;
alter table `status` change `status` status_detail varchar(255); 


-- add primary key constraints for existing tables
-- (executed)
alter table circuits add constraint pk_circuit_id primary key (circuit_id);
alter table constructor_results add constraint pk_constructor_results_id primary key (constructor_results_id);
alter table constructors add constraint pk_constructor_id primary key (constructor_id);
alter table constructor_standings add constraint pk_constructor_standings_id primary key (constructor_standings_id);
alter table constructor_standings add constraint pk_constructor_standings_id primary key (constructor_standings_id);
alter table drivers add constraint pk_driver_id primary key (driver_id);
alter table driver_standings add constraint pk_driver_standings_id primary key (driver_standings_id);
alter table lap_times add constraint pk_race_id_driver_id_lap primary key (race_id, driver_id, lap);
alter table lap_times add constraint pk_race_id_driver_id_lap primary key (race_id, driver_id, lap);
alter table pit_stops add constraint pk_race_id_driver_id_stop primary key (race_id, driver_id, `stop`);
alter table qualifying_results add constraint pk_qualifying_id primary key (qualifying_id);
alter table race_results add constraint pk_result_id primary key (result_id);
alter table races add constraint pk_race_id primary key (race_id);
alter table sprint_results add constraint pk_sprint_result_id primary key (sprint_result_id);
alter table `status` add constraint pk_status_id primary key (status_id);


-- add tables for users and their favorite drivers and teams
-- (executed)
drop table if exists users;
create table users (
	user_email varchar(255) primary key,
    `password` varchar(255)
);

drop table if exists favorite_drivers;
create table favorite_drivers (
	user_email varchar(255),
    driver_id int,
    primary key (user_email, driver_id),
    foreign key (driver_id) references drivers (driver_id) on delete restrict on update restrict
);


drop table if exists favorite_constructors;
create table favorite_constructors (
	user_email varchar(255),
    constructor_id int,
    primary key (user_email, constructor_id),
    foreign key (constructor_id) references constructors (constructor_id) on delete restrict on update restrict
);


-- set up foreign key constraints for existing tables
-- (executed)
alter table constructor_results add constraint constructor_results_fk_race_id foreign key (race_id) references races (race_id) on delete restrict on update restrict;
alter table constructor_results add constraint constructor_results_fk_constructor_id foreign key (constructor_id) references constructors (constructor_id) on delete restrict on update restrict;

alter table constructor_standings add constraint constructor_standings_fk_race_id foreign key (race_id) references races (race_id) on delete restrict on update restrict;
alter table constructor_standings add constraint constructor_standings_fk_constructor_id foreign key (constructor_id) references races (race_id) on delete restrict on update restrict;

alter table lap_times add constraint lap_times_fk_race_id foreign key (race_id) references races (race_id) on delete restrict on update restrict;
alter table lap_times add constraint lap_times_fk_driver_id foreign key (driver_id) references drivers (driver_id) on delete restrict on update restrict;

alter table pit_stops add constraint pit_stops_fk_race_id foreign key (race_id) references races (race_id) on delete restrict on update restrict;
alter table pit_stops add constraint pit_stops_fk_driver_id foreign key (driver_id) references drivers (driver_id) on delete restrict on update restrict;

alter table qualifying_results add constraint qualifying_results_fk_race_id foreign key (race_id) references races (race_id) on delete restrict on update restrict;
alter table qualifying_results add constraint qualifying_results_fk_driver_id foreign key (driver_id) references drivers (driver_id) on delete restrict on update restrict;
alter table qualifying_results add constraint qualifying_results_fk_constructor_id foreign key (constructor_id) references constructors (constructor_id) on delete restrict on update restrict;

alter table race_results add constraint race_results_fk_race_id foreign key (race_id) references races (race_id) on delete restrict on update restrict;
alter table race_results add constraint race_results_fk_driver_id foreign key (driver_id) references drivers (driver_id) on delete restrict on update restrict;
alter table race_results add constraint race_results_fk_constructor_id foreign key (constructor_id) references constructors (constructor_id) on delete restrict on update restrict;
alter table race_results add constraint race_results_fk_status_id foreign key (status_id) references `status` (status_id) on delete restrict on update restrict;

alter table races add constraint races_fk_circuit_id foreign key (circuit_id) references circuits (circuit_id) on delete restrict on update restrict;

alter table sprint_results add constraint sprint_results_fk_race_id foreign key (race_id) references races (race_id) on delete restrict on update restrict;
alter table sprint_results add constraint sprint_results_fk_driver_id foreign key (driver_id) references drivers (driver_id) on delete restrict on update restrict;
alter table sprint_results add constraint sprint_results_fk_constructor_id foreign key (constructor_id) references constructors (constructor_id) on delete restrict on update restrict;
alter table sprint_results add constraint sprint_results_fk_status_id foreign key (status_id) references `status` (status_id) on delete restrict on update restrict;

-- In the constructors table, many of the urls are the same (i.e., leading to the same page) because some constructors are actually the same constructor with different names. So we add a 
-- column "actual url" to help identify these constructor rows. The method (i.e. function) used to identify the identical urls are written in Python file dataCleaning.py
alter table constructors add actual_url varchar(255);


-- Upon investigation specified in the log on 04.15.2023, drop the errorneous constructor_results table
drop table constructor_results;

-- Url column in constructors is now redundant after I ran the python program I wrote to extract the final redirected url for each constructor.
ALTER TABLE constructors DROP COLUMN url;

-- Rename two columns: url and actual_url to wiki (in table Drivers and Constructors, respectively)
ALTER TABLE constructors CHANGE COLUMN actual_url wiki VARCHAR(255);
ALTER TABLE drivers CHANGE COLUMN url wiki VARCHAR(255);







-- Below are some raw test codes I used to discover the problem. Don't worry if you don't understand. 
-- with table1 as (
-- select rr.race_id, rr.driver_id, rr.constructor_id, r.`year`, r.race_name, d.first_name, d.last_name, c.`name`, rr.points from race_results as rr left join races as r on rr.race_id = r.race_id left join drivers as d on rr.driver_id = d.driver_id left join constructors as c on rr.constructor_id = c.constructor_id),
-- table2 as (
-- select race_id, constructor_id, points as constructor_points from constructor_results),
-- calc_table as (
-- select race_id, constructor_id, `year`, race_name, `name`, sum(points) as calculated_constructor_points from table1 group by race_id, constructor_id),
-- merged as (
-- select t2.race_id, t2.constructor_id, t2.constructor_points, ct.`year`, ct.race_name, ct.`name` AS constructor_name, ct.calculated_constructor_points from table2 as t2 left join calc_table as ct on t2.race_id = ct.race_id and t2.constructor_id = ct.constructor_id)
-- select *, (calculated_constructor_points - constructor_points) AS diff from merged order by diff;

-- select * from race_results where race_id = 937;
-- select * from races where race_id = 937;
-- select * from constructors where constructor_id = 209;

-- select constructor_results.*, constructors.`name` from constructor_results left join constructors on constructor_results.constructor_id = constructors.constructor_id having race_id = 937 ;

-- select * from constructors where constructor_id = 34;
-- select * from races where race_id = 514 OR race_id = 522;
-- select * from constructors where `name` like "%Brabham%";
-- select * from race_results where constructor_id = 204 AND race_id = 514;

