CREATE DATABASE `daaexample`;

CREATE TABLE `daaexample`.`people` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) DEFAULT NULL,
	`surname` varchar(100) DEFAULT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `daaexample`.`users` (
	`login` varchar(100) NOT NULL,
	`password` varbinary(64) DEFAULT NULL,
	PRIMARY KEY (`login`)
);

GRANT ALL ON `daaexample`.* TO 'daa'@'localhost' IDENTIFIED BY 'daa';