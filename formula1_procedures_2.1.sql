-- This file includes the creation of MySQL procedures used in the final project.
use formula1;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to check qualifying results ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Check qualifying results if user doesn't specify the season, race, or driver. 
DROP PROCEDURE IF EXISTS check_qualifying;
DELIMITER //
CREATE PROCEDURE check_qualifying()
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali ORDER BY `year` DESC, round, driver_position;
END //
DELIMITER ;


-- Check qualifying results if the user specifies the season. Results ordered by races and driver positions in qualifying. 
DROP PROCEDURE IF EXISTS check_qualifying_season;
DELIMITER //
CREATE PROCEDURE check_qualifying_season(IN season INT)
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali WHERE `year` = season ORDER BY round, driver_position;
END //
DELIMITER ;


-- Check qualifying results if the user specifies the race. Results ordered by season and driver positions in qualifying. For example, will display all qualifying results of all Australian Grand Prix over the years. 
DROP PROCEDURE IF EXISTS check_qualifying_race;
DELIMITER //
CREATE PROCEDURE check_qualifying_race(IN race_name_p VARCHAR(255))
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali WHERE race_name = race_name_p ORDER BY `year`, driver_position;
END //
DELIMITER ;


-- Check qualifying results if the user specifies the driver. Results ordered by season and race rounds. For example, will display all qualifying results of Sebastian Vettel in all of his races in each season. 
DROP PROCEDURE IF EXISTS check_qualifying_driver;
DELIMITER //
CREATE PROCEDURE check_qualifying_driver(IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali WHERE first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC, round;
END //
DELIMITER ;


-- Check qualifying result if the user specifies the season and the race. Results ordered by driver positions after the qualifying. 
DROP PROCEDURE IF EXISTS check_qualifying_season_race;
DELIMITER //
CREATE PROCEDURE check_qualifying_season_race(IN season INT, IN race_name_p VARCHAR(255))
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali WHERE `year` = season AND race_name = race_name_p ORDER BY driver_position;
END //
DELIMITER ;


-- Check qualifying result if the user specifies the season and the driver. Results order by race rounds. For example, check all of Fernando Alonso's qualifying results in the 2008 season.
DROP PROCEDURE IF EXISTS check_qualifying_season_driver;
DELIMITER //
CREATE PROCEDURE check_qualifying_season_driver(IN season INT, IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali WHERE `year` = season AND first_name = first_name_p AND last_name = last_name_p ORDER BY round;
END //
DELIMITER ;


-- Check qualifying result if the user specifies the race and the driver. Results order by seasons. For example, check all of Daniel Ricciardo's Singapore Grand Prix results over the years.
DROP PROCEDURE IF EXISTS check_qualifying_race_driver;
DELIMITER //
CREATE PROCEDURE check_qualifying_race_driver(IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali WHERE race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC;
END //
DELIMITER ;


-- Check qualifying result if the user specifies the eason, the race, and the driver.
DROP PROCEDURE IF EXISTS check_qualifying_season_race_driver;
DELIMITER //
CREATE PROCEDURE check_qualifying_season_race_driver(IN season INT, IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_quali AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, q.car_number, c.`name` AS constructor_name, q.position AS driver_position, q.q1_session_time, q.q2_session_time, q.q3_session_time
		FROM
			qualifying_results AS q
		LEFT JOIN
			races AS r
				ON
			q.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			q.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			q.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
	)
    SELECT * FROM all_quali WHERE `year` = season AND race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC, round, driver_position;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to check race results ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------		
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Check race result if the user specifies none of season, race, or driver. Includes only the driver's data.
DROP PROCEDURE IF EXISTS check_race_d;
DELIMITER //
CREATE PROCEDURE check_race_d()
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d ORDER BY `year` DESC, round, final_order;
END //
DELIMITER ;


-- Check race result if the user specifies none of season, race, or constructor. Includes only the constructor's data.
DROP PROCEDURE IF EXISTS check_race_c;
DELIMITER //
CREATE PROCEDURE check_race_c()
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c ORDER BY `year` DESC, round, constructor_points DESC;
END //
DELIMITER ;


-- Check race result if the user specifies season. Show all races and all drivers in that season. 
DROP PROCEDURE IF EXISTS check_race_d_season;
DELIMITER //
CREATE PROCEDURE check_race_d_season(IN season INT)
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE `year` = season ORDER BY round, final_order;
END //
DELIMITER ;


-- Check race result if the user specifies season. Show all races and all constructors in that season.
DROP PROCEDURE IF EXISTS check_race_c_season;
DELIMITER //
CREATE PROCEDURE check_race_c_season(IN season INT)
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c WHERE `year` = season ORDER BY round, constructor_points DESC;
END //
DELIMITER ;


-- Check race result if the user specifies race. Show this race (for example, "Japanese Grand Prix") in all seasons and include all drivers. 
DROP PROCEDURE IF EXISTS check_race_d_race;
DELIMITER //
CREATE PROCEDURE check_race_d_race(IN race_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE race_name = race_name_p ORDER BY `year` DESC, final_order;
END //
DELIMITER ;


-- Check race result if the user specifies race. Show this race (for example, "Japanese Grand Prix") in all seasons and include all constructors. 
DROP PROCEDURE IF EXISTS check_race_c_race;
DELIMITER //
CREATE PROCEDURE check_race_c_race(IN race_name_p VARCHAR(255))
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c WHERE race_name = race_name_p ORDER BY `year`, constructor_points DESC;
END //
DELIMITER ;


-- Check race result if the user specifies driver. Show all seasons and all races for the driver.
DROP PROCEDURE IF EXISTS check_race_d_driver;
DELIMITER //
CREATE PROCEDURE check_race_d_driver(IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC, round;
END //
DELIMITER ;


-- Check race result if the user specifies constructor. Show all seasons and all races for the constructor.
DROP PROCEDURE IF EXISTS check_race_c_constructor;
DELIMITER //
CREATE PROCEDURE check_race_c_constructor(IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c WHERE constructor_name = constructor_name_p ORDER BY `year` DESC, round;
END //
DELIMITER ;


-- Check race result if the user specifies constructor. Show all seasons and all races for the drivers driving for this constructor.
DROP PROCEDURE IF EXISTS check_race_c_constructor_breakdown;
DELIMITER //
CREATE PROCEDURE check_race_c_constructor_breakdown(IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE constructor_name = constructor_name_p ORDER BY `year` DESC, round, final_order;
END //
DELIMITER ;


-- Check race result if the user specifies season and race. Show all drivers' results for that specific race. 
DROP PROCEDURE IF EXISTS check_race_d_season_race;
DELIMITER //
CREATE PROCEDURE check_race_d_season_race(IN season INT, IN race_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE `year` = season AND race_name = race_name_p ORDER BY final_order;
END //
DELIMITER ;


-- Check race result if the user specifies season and race. Show all constructors' results for that specific race. 
DROP PROCEDURE IF EXISTS check_race_c_season_race;
DELIMITER //
CREATE PROCEDURE check_race_c_season_race(IN season INT, IN race_name_p VARCHAR(255))
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c WHERE `year` = season AND race_name = race_name_p ORDER BY constructor_points DESC;
END //
DELIMITER ;


-- Check race result if the user specifies season and driver. Show that driver's race results for all races in that season.
DROP PROCEDURE IF EXISTS check_race_d_season_driver;
DELIMITER //
CREATE PROCEDURE check_race_d_season_driver(IN season INT, IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE `year` = season AND first_name = first_name_p AND last_name = last_name_p ORDER BY round;
END //
DELIMITER ;


-- Check race result if the user specifies season and constructor. Show that constructor's race results for all races in that season.
DROP PROCEDURE IF EXISTS check_race_c_season_constructor;
DELIMITER //
CREATE PROCEDURE check_race_c_season_constructor(IN season INT, IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c WHERE `year` = season AND constructor_name = constructor_name_p ORDER BY round;
END //
DELIMITER ;


-- Check race result if the user specifies season and constructor. Show results of drivers belonging to the constructor in that season.
DROP PROCEDURE IF EXISTS check_race_c_season_constructor_breakdown;
DELIMITER //
CREATE PROCEDURE check_race_c_season_constructor_breakdown(IN season INT, IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE `year` = season AND constructor_name = constructor_name_p ORDER BY round, final_order;
END //
DELIMITER ;


-- Check race result if the user specifies race and driver. Show that driver's results in that race for all seasons. 
DROP PROCEDURE IF EXISTS check_race_d_race_driver;
DELIMITER //
CREATE PROCEDURE check_race_d_race_driver(IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC;
END //
DELIMITER ;


-- Check race result if the user specifies race and constructor. Show that constructor's results in that race for all seasons. 
DROP PROCEDURE IF EXISTS check_race_c_race_constructor;
DELIMITER //
CREATE PROCEDURE check_race_c_race_constructor(IN race_name_p VARCHAR(255), IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c WHERE race_name = race_name_p AND constructor_name = constructor_name_p ORDER BY `year` DESC;
END //
DELIMITER ;


-- Check race result if the user specifies race and constructor. Show results of drivers belonging to that constructor in that race for all seasons. 
DROP PROCEDURE IF EXISTS check_race_c_race_constructor_breakdown;
DELIMITER //
CREATE PROCEDURE check_race_c_race_constructor_breakdown(IN race_name_p VARCHAR(255), IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE race_name = race_name_p AND constructor_name = constructor_name_p ORDER BY `year` DESC, final_order;
END //
DELIMITER ;


-- Check race result if the user specifies season, race and driver.
DROP PROCEDURE IF EXISTS check_race_d_season_race_driver;
DELIMITER //
CREATE PROCEDURE check_race_d_season_race_driver(IN season INT, IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE `year` = season AND race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p;
END //
DELIMITER ;


-- Check race result if the user specifies season, race and constructor.
DROP PROCEDURE IF EXISTS check_race_c_season_race_constructor;
DELIMITER //
CREATE PROCEDURE check_race_c_season_race_constructor(IN season INT, IN race_name_p VARCHAR(255), IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_c AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, c.`name` AS constructor_name, sum(rr.points) AS constructor_points
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
		GROUP BY 
			rr.race_id, rr.constructor_id
	)
    SELECT * FROM all_races_c WHERE `year` = season AND race_name = race_name_p AND constructor_name = constructor_name_p;
END //
DELIMITER ;


-- Check race result if the user specifies season, race and constructor. Show results of drivers belonging to that constructor in that specific race. 
DROP PROCEDURE IF EXISTS check_race_c_season_race_constructor_breakdown;
DELIMITER //
CREATE PROCEDURE check_race_c_season_race_constructor_breakdown(IN season INT, IN race_name_p VARCHAR(255), IN constructor_name_p VARCHAR(255))
BEGIN
	WITH all_races_d AS (
		SELECT
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, c.`name` AS constructor_name, rr.final_position AS final_result, rr.final_position_order AS final_order, rr.points, rr.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(rr.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, rr.fastest_lap AS fastest_lap_number, rr.fastest_lap_ranking, rr.fastest_lap_time, rr.fastest_lap_speed AS fastest_lap_average_speed, s.status_detail AS `status`
		FROM
			race_results AS rr
		LEFT JOIN
			races AS r
				ON
			rr.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			rr.driver_id = d.driver_id
		LEFT JOIN
			constructors AS c
				ON
			rr.constructor_id = c.constructor_id
		LEFT JOIN
			`status` AS s
				ON
			rr.status_id = s.status_id
	)
    SELECT * FROM all_races_d WHERE `year` = season AND race_name = race_name_p AND constructor_name = constructor_name_p ORDER BY final_order;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to check Sprint Race results ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Check sprint results if the user provides nothing. Order by year DESC, round, then final forder. 
DROP PROCEDURE IF EXISTS check_sprint;
DELIMITER //
CREATE PROCEDURE check_sprint()
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint ORDER BY `year` DESC, round, final_order;
END //
DELIMITER ;


-- Check sprint results if the user provides season. Order by race round and final order. 
DROP PROCEDURE IF EXISTS check_sprint_season;
DELIMITER //
CREATE PROCEDURE check_sprint_season(IN season INT)
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint WHERE `year` = season ORDER BY round, final_order;
END //
DELIMITER ;


-- Check sprint results if the user provides race. Order by year DESC and final order. 
DROP PROCEDURE IF EXISTS check_sprint_race;
DELIMITER //
CREATE PROCEDURE check_sprint_race(IN race_name_p VARCHAR(255))
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint WHERE race_name = race_name_p ORDER BY `year` DESC, final_order;
END //
DELIMITER ;


-- Check sprint results if the user provides race. Order by year DESC and round. 
DROP PROCEDURE IF EXISTS check_sprint_driver;
DELIMITER //
CREATE PROCEDURE check_sprint_driver(IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint WHERE first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC, round;
END //
DELIMITER ;


-- Check sprint results if the user provides season and race. Order by final order. 
DROP PROCEDURE IF EXISTS check_sprint_season_race;
DELIMITER //
CREATE PROCEDURE check_sprint_season_race(IN season INT, IN race_name_p VARCHAR(255))
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint WHERE `year` = season AND race_name = race_name_p ORDER BY final_order;
END //
DELIMITER ;


-- Check sprint results if the user provides season and driver. Order by round. 
DROP PROCEDURE IF EXISTS check_sprint_season_driver;
DELIMITER //
CREATE PROCEDURE check_sprint_season_driver(IN season INT, IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint WHERE `year` = season AND first_name = first_name_p AND last_name = last_name_p ORDER BY round;
END //
DELIMITER ;


-- Check sprint results if the user provides race and driver. Order by year DESC. 
DROP PROCEDURE IF EXISTS check_sprint_race_driver;
DELIMITER //
CREATE PROCEDURE check_sprint_race_driver(IN race_name_p INT, IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint WHERE race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC;
END //
DELIMITER ;


-- Check sprint results if the user provides season, race, and driver. Order by year DESC. 
DROP PROCEDURE IF EXISTS check_sprint_season_race_driver;
DELIMITER //
CREATE PROCEDURE check_sprint_season_race_driver(IN season INT, IN race_name_p INT, IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_sprint AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, s.car_number, c.`name` AS constructor_name, s.starting_grid_position AS starting_position, s.final_position AS final_position, s.final_position_order as final_order, s.points, s.laps_completed, CONCAT(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(s.time_in_milliseconds / 1000), '%f'), 1, 3)) AS `time`, s.fastest_lap, s.fastest_lap_time, sts.status_detail AS `status`
		FROM
			sprint_results AS s
		LEFT JOIN
			races AS r
				ON
			s.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			s.driver_id = d.driver_Id
		LEFT JOIN
			constructors AS c
				ON
			s.constructor_id = c.constructor_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			`status` AS sts
				ON
			s.status_id = sts.status_id
	)
    SELECT * FROM all_sprint WHERE `year` = season AND race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to check single laps ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Check lap results in races if the user provides season and race. Order by lap then position. 
DROP PROCEDURE IF EXISTS check_laps_season_race;
DELIMITER //
CREATE PROCEDURE check_laps_season_race(IN season INT, IN race_name_p VARCHAR(255))
BEGIN
	WITH all_laps AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, l.lap, l.position, CONCAT(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%f'), 1, 3)) AS `lap_time`
		FROM
			lap_times AS l
		LEFT JOIN
			races AS r
				ON
			l.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = l.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			l.race_id = rr.race_id AND l.driver_id = rr.driver_id
	)
    SELECT * FROM all_laps WHERE `year` = season AND race_name = race_name_p ORDER BY lap, position;
END //
DELIMITER ;


-- Check lap results in races if the user provides season and driver. Order by round then by lap. 
DROP PROCEDURE IF EXISTS check_laps_season_driver;
DELIMITER //
CREATE PROCEDURE check_laps_season_driver(IN season INT, IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_laps AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, l.lap, l.position, CONCAT(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%f'), 1, 3)) AS `lap_time`
		FROM
			lap_times AS l
		LEFT JOIN
			races AS r
				ON
			l.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = l.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			l.race_id = rr.race_id AND l.driver_id = rr.driver_id
	)
    SELECT * FROM all_laps WHERE `year` = season AND first_name = first_name_p AND last_name = last_name_p ORDER BY round, lap;
END //
DELIMITER ;


-- Check lap results in races if the user provides race and driver. Order by season in reverse then by lap. 
DROP PROCEDURE IF EXISTS check_laps_race_driver;
DELIMITER //
CREATE PROCEDURE check_laps_race_driver(IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_laps AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, l.lap, l.position, CONCAT(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%f'), 1, 3)) AS `lap_time`
		FROM
			lap_times AS l
		LEFT JOIN
			races AS r
				ON
			l.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = l.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			l.race_id = rr.race_id AND l.driver_id = rr.driver_id
	)
    SELECT * FROM all_laps WHERE race_name = race_name_P AND first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC, lap;
END //
DELIMITER ;


-- Check lap results in races if the user provides season, race and driver. Order by lap.
DROP PROCEDURE IF EXISTS check_laps_season_race_driver;
DELIMITER //
CREATE PROCEDURE check_laps_season_race_driver(IN season INT, IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_laps AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, l.lap, l.position, CONCAT(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(l.milliseconds / 1000), '%f'), 1, 3)) AS `lap_time`
		FROM
			lap_times AS l
		LEFT JOIN
			races AS r
				ON
			l.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = l.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			l.race_id = rr.race_id AND l.driver_id = rr.driver_id
	)
    SELECT * FROM all_laps WHERE `year` = season AND race_name = race_name_P AND first_name = first_name_p AND last_name = last_name_p ORDER BY lap;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to check pit stops ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Check pit stops in races if the user provides season and race. Order by driver's car number then pit stop order.
DROP PROCEDURE IF EXISTS check_pit_stop_season_race;
DELIMITER //
CREATE PROCEDURE check_pit_stop_season_race(IN season INT, IN race_name_p VARCHAR(255))
BEGIN
	WITH all_pit_stops AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, p.`stop` AS pit_stop_order, p.lap, p.local_time, CONCAT(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%f'), 1, 3)) AS `stop_duration`
		FROM
			pit_stops AS p
		LEFT JOIN
			races AS r
				ON
			p.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = p.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			p.race_id = rr.race_id AND p.driver_id = rr.driver_id
	)
    SELECT * FROM all_pit_stops WHERE `year` = season AND race_name = race_name_p ORDER BY car_number, pit_stop_order;
END //
DELIMITER ;


-- Check pit stops in races if the user provides season and driver. Order by race round then pit stop order (i.e. the nth pit stop in the race for this driver).
DROP PROCEDURE IF EXISTS check_pit_stop_season_driver;
DELIMITER //
CREATE PROCEDURE check_pit_stop_season_driver(IN season INT, IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_pit_stops AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, p.`stop` AS pit_stop_order, p.lap, p.local_time, CONCAT(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%f'), 1, 3)) AS `stop_duration`
		FROM
			pit_stops AS p
		LEFT JOIN
			races AS r
				ON
			p.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = p.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			p.race_id = rr.race_id AND p.driver_id = rr.driver_id
	)
    SELECT * FROM all_pit_stops WHERE `year` = season AND first_name = first_name_p AND last_name = last_name_p ORDER BY round, pit_stop_order;
END //
DELIMITER ;


-- Check pit stops in races if the user provides race and driver. Order by season in reverse then pit stop order (i.e. the nth pit stop in the race for this driver).
DROP PROCEDURE IF EXISTS check_pit_stop_race_driver;
DELIMITER //
CREATE PROCEDURE check_pit_stop_race_driver(IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_pit_stops AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, p.`stop` AS pit_stop_order, p.lap, p.local_time, CONCAT(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%f'), 1, 3)) AS `stop_duration`
		FROM
			pit_stops AS p
		LEFT JOIN
			races AS r
				ON
			p.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = p.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			p.race_id = rr.race_id AND p.driver_id = rr.driver_id
	)
    SELECT * FROM all_pit_stops WHERE race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p ORDER BY `year` DESC, pit_stop_order;
END //
DELIMITER ;


-- Check pit stops in races if the user provide season, race and driver.
DROP PROCEDURE IF EXISTS check_pit_stop_season_race_driver;
DELIMITER //
CREATE PROCEDURE check_pit_stop_season_race_driver(IN season INT, IN race_name_p VARCHAR(255), IN first_name_p VARCHAR(255), IN last_name_p VARCHAR(255))
BEGIN
	WITH all_pit_stops AS (
		SELECT 
			r.`year`, r.round, r.race_name, cct.`name` AS circuit_name, cct.location, cct.country, d.`code` AS driver_code, d.first_name, d.last_name, rr.car_number, p.`stop` AS pit_stop_order, p.lap, p.local_time, CONCAT(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%i:%s.'), SUBSTRING(TIME_FORMAT(SEC_TO_TIME(p.stop_duration_milliseconds / 1000), '%f'), 1, 3)) AS `stop_duration`
		FROM
			pit_stops AS p
		LEFT JOIN
			races AS r
				ON
			p.race_id = r.race_id
		LEFT JOIN
			circuits AS cct
				ON
			r.circuit_id = cct.circuit_id
		LEFT JOIN
			drivers AS d
				ON
			d.driver_id = p.driver_id
		LEFT JOIN
			race_results AS rr
				ON
			p.race_id = rr.race_id AND p.driver_id = rr.driver_id
	)
    SELECT * FROM all_pit_stops WHERE `year` = season AND race_name = race_name_p AND first_name = first_name_p AND last_name = last_name_p ORDER BY pit_stop_order;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to check end-of-season driver and constructor results  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Check the constructor standings by the end of a season given season. Order by the standings. 
DROP PROCEDURE IF EXISTS check_constructor_standing_season;
DELIMITER //
CREATE PROCEDURE check_constructor_standing_season(IN season INT)
BEGIN
	WITH all_constructor_standings AS (
		SELECT
			r.`year`, r.round, r.race_name, c.`name` AS constructor_name, cs.points AS cumulative_points, cs.position, cs.wins
		FROM
			constructor_standings AS cs
		LEFT JOIN
			races AS r
				ON
			cs.race_id = r.race_id
		LEFT JOIN
			constructors AS c
				ON
			cs.constructor_id = c.constructor_id
	)
    SELECT 
		`year`, constructor_name, cumulative_points, position, wins
	FROM
		all_constructor_standings
	WHERE
		`year` = season
			AND round >= ALL (SELECT 
				round
			FROM
				all_constructor_standings
			WHERE
				`year` = season)
	ORDER BY position;
END //
DELIMITER ;


-- Check the driver standings by the end of a season given season. Order by the standings. 
DROP PROCEDURE IF EXISTS check_driver_standing_season;
DELIMITER //
CREATE PROCEDURE check_driver_standing_season(IN season INT)
BEGIN
	WITH all_driver_standings AS (
		SELECT
			r.`year`, r.round, r.race_name, d.`code`, d.driver_number, d.first_name, d.last_name, ds.points AS cumulative_points, ds.position, ds.wins
		FROM
			driver_standings AS ds
		LEFT JOIN
			races AS r
				ON
			ds.race_id = r.race_id
		LEFT JOIN
			drivers AS d
				ON
			ds.driver_id = d.driver_id
	)
    SELECT 
		`year`, `code`, driver_number, first_name, last_name, cumulative_points, position, wins
	FROM
		all_driver_standings
	WHERE
		`year` = season
			AND round >= ALL (SELECT 
				round
			FROM
				all_driver_standings
			WHERE
				`year` = season)
	ORDER BY position;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to add, update, and delete favorite drivers for a user  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Add favorite driver.
DROP PROCEDURE IF EXISTS add_fav_driver;
DELIMITER //
CREATE PROCEDURE add_fav_driver(IN user_email_p VARCHAR(255), IN driver_id_p INT)
BEGIN
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
	END;
	
    START TRANSACTION;
    
    SELECT
		driver_id FROM favorite_drivers WHERE user_email = user_email_p AND driver_id = driver_id_p FOR UPDATE;
        
	IF ROW_COUNT() > 0 THEN
		SET @driver_id_var = (SELECT driver_id FROM favorite_drivers WHERE user_email = user_email_p AND driver_id = driver_id_p);
		IF @driver_id_var = driver_id_p THEN
			ROLLBACK;
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Driver already exists in the table';
		END IF;
	ELSE
		INSERT INTO favorite_drivers VALUES (user_email_p, driver_id_p);
		COMMIT;
	END IF;
END //

DELIMITER ;


-- Update favorite driver.
DROP PROCEDURE IF EXISTS update_fav_driver;
DELIMITER //
CREATE PROCEDURE update_fav_driver(IN user_email_p VARCHAR(255), IN old_driver_id INT, IN new_driver_id INT)
BEGIN
	UPDATE favorite_drivers 
	SET 
		driver_id = new_driver_id
	WHERE
		user_email = user_email_p
			AND driver_id = old_driver_id;
END //
DELIMITER ;


-- Delete favorite driver.
DROP PROCEDURE IF EXISTS delete_fav_driver;
DELIMITER //
CREATE PROCEDURE delete_fav_driver(IN user_email_p VARCHAR(255), IN driver_id_p INT)
BEGIN
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
		ROLLBACK;
	END;
    
    START TRANSACTION;
		SELECT COUNT(*) INTO @current_row_count FROM favorite_drivers WHERE driver_id = driver_id_p AND user_email = user_email_p FOR UPDATE;
		IF @current_row_count > 0 THEN
			DELETE FROM favorite_drivers WHERE driver_id = driver_id_p AND user_email = user_email_p;
			COMMIT;
		ELSE
			ROLLBACK;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No such record for the user and favorite driver match to be deleted';
		END IF;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures used to add, update, and delete favorite constructors for a user  -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Add favorite constructor.
DROP PROCEDURE IF EXISTS add_fav_constructor;
DELIMITER //
CREATE PROCEDURE add_fav_constructor(IN user_email_p VARCHAR(255), IN constructor_id_p INT)
BEGIN
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
	END;
	
    START TRANSACTION;
    
    SELECT
		constructor_id FROM favorite_constructors WHERE user_email = user_email_p AND constructor_id = constructor_id_p FOR UPDATE;
        
	IF ROW_COUNT() > 0 THEN
		SET @constructor_id_var = (SELECT constructor_id FROM favorite_constructors WHERE user_email = user_email_p AND constructor_id = constructor_id_p);
		IF @constructor_id_var = constructor_id_p THEN
			ROLLBACK;
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Constructor already exists in the table';
		END IF;
	ELSE
		INSERT INTO favorite_constructors VALUES (user_email_p, constructor_id_p);
		COMMIT;
	END IF;
END //

DELIMITER ;


-- Update favorite constructor.
DROP PROCEDURE IF EXISTS update_fav_constructor;
DELIMITER //
CREATE PROCEDURE update_fav_constructor(IN user_email_p VARCHAR(255), IN old_constructor_id INT, IN new_constructor_id INT)
BEGIN
	UPDATE favorite_constructors
	SET 
		constructor_id = new_constructor_id
	WHERE
		user_email = user_email_p
			AND constructor_id = old_constructor_id;
END //
DELIMITER ;


-- Delete favorite constructor.
DROP PROCEDURE IF EXISTS delete_fav_constructor;
DELIMITER //
CREATE PROCEDURE delete_fav_constructor(IN user_email_p VARCHAR(255), IN constructor_id_p INT)
BEGIN
	DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
		ROLLBACK;
	END;
    
    START TRANSACTION;
		SELECT COUNT(*) INTO @current_row_count FROM favorite_constructors WHERE constructor_id = constructor_id_p AND user_email = user_email_p FOR UPDATE;
		IF @current_row_count > 0 THEN
			DELETE FROM favorite_constructors WHERE constructor_id = constructor_id_p AND user_email = user_email_p;
			COMMIT;
		ELSE
			ROLLBACK;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No such record for the user and favorite constructor match to be deleted';
		END IF;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures for users to sign up and log in ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Sign up; will throw an SQLException with state "45000" to indicate duplicated email address. 
DROP PROCEDURE IF EXISTS sign_up;
DELIMITER //
CREATE PROCEDURE sign_up(IN email VARCHAR(255), IN pass VARCHAR(255), OUT success INT)
BEGIN
	DECLARE EXIT HANDLER FOR SQLSTATE '23000'
    BEGIN
		SET success = 0;
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User with this email already exists';
	END;
    INSERT INTO users VALUES (email, pass);
    SET success = 1;
END //
DELIMITER ;


DROP PROCEDURE IF EXISTS log_in;
DELIMITER //
CREATE PROCEDURE log_in(IN email_p VARCHAR(255), IN password_p VARCHAR(255), OUT outcome_p VARCHAR(255))
BEGIN
	DECLARE user_count INT DEFAULT 0;
	SELECT COUNT(*) INTO user_count FROM users WHERE user_email = email_p;
    
    IF user_count = 0 THEN
		SET outcome_p = "User not found";
	ELSE
		SELECT COUNT(*) INTO user_count FROM users WHERE user_email = email_p AND `password` = password_p;
        IF user_count = 0 THEN
			SET outcome_p = "Incorrect password";
		ELSE
			SET outcome_p = "Login successful";
		END IF;
	END IF;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures for setting up selectors for add and remove favorite drivers or constructors ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Selector items for adding favorite driver for a certain user.
DROP PROCEDURE IF EXISTS selector_add_fav_driver;
DELIMITER //
CREATE PROCEDURE selector_add_fav_driver(IN email VARCHAR(255))
BEGIN
	SELECT 
		first_name, last_name
	FROM
		drivers
	WHERE
		driver_id NOT IN (
			SELECT 
				driver_id
			FROM
				favorite_drivers
			WHERE
				user_email = email)
	ORDER BY 
		last_name, first_name;
END //
DELIMITER ;


-- Selector items for deleting favorite driver for a certain user.
DROP PROCEDURE IF EXISTS selector_delete_fav_driver;
DELIMITER //
CREATE PROCEDURE selector_delete_fav_driver(IN email VARCHAR(255))
BEGIN
	SELECT 
		first_name, last_name
	FROM
		drivers
	WHERE
		driver_id IN (
			SELECT 
				driver_id
			FROM
				favorite_drivers
			WHERE
				user_email = email)
	ORDER BY 
		last_name, first_name;
END //
DELIMITER ;


-- Selector items for adding favorite constructor for a certain user.
DROP PROCEDURE IF EXISTS selector_add_fav_constructor;
DELIMITER //
CREATE PROCEDURE selector_add_fav_constructor(IN email VARCHAR(255))
BEGIN
	SELECT 
		`name`
	FROM
		constructors
	WHERE
		constructor_id NOT IN (
			SELECT 
				constructor_id
			FROM
				favorite_constructors
			WHERE
				user_email = email)
	ORDER BY 
		`name`;
END //
DELIMITER ;


-- Selector items for deleting favorite constructor for a certain user.
DROP PROCEDURE IF EXISTS selector_delete_fav_constructor;
DELIMITER //
CREATE PROCEDURE selector_delete_fav_constructor(IN email VARCHAR(255))
BEGIN
	SELECT 
		`name`
	FROM
		constructors
	WHERE
		constructor_id IN (
			SELECT 
				constructor_id
			FROM
				favorite_constructors
			WHERE
				user_email = email)
	ORDER BY 
		`name`;
END //
DELIMITER ;



-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- -------------------------- Below are procedures for reading favorite drivers or constructors ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Get the fav drivers.
DROP PROCEDURE IF EXISTS get_fav_driver;
DELIMITER //
CREATE PROCEDURE get_fav_driver(IN email_p VARCHAR(255))
BEGIN
	SELECT 
		`code`, first_name, last_name, nationality, wiki
	FROM
		drivers
	WHERE
		driver_id IN (
			SELECT 
				driver_id
			FROM
				favorite_drivers
			WHERE
				user_email = email_p)
	ORDER BY last_name, first_name;
END //
DELIMITER ;


-- Get the fav constructors.
DROP PROCEDURE IF EXISTS get_fav_constructor;
DELIMITER //
CREATE PROCEDURE get_fav_constructor(IN email_p VARCHAR(255))
BEGIN
	SELECT 
		`name`, nationality, wiki
	FROM
		constructors
	WHERE
		constructor_id IN (
			SELECT 
				constructor_id
			FROM
				favorite_constructors
			WHERE
				user_email = email_p)
	ORDER BY `name`;
END //
DELIMITER ;




