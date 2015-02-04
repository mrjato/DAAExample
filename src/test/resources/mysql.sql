CREATE DATABASE `daaexampletest`;

CREATE TABLE `daaexampletest`.`people` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(50) DEFAULT NULL,
	`surname` varchar(100) DEFAULT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `daaexampletest`.`users` (
	`login` varchar(100) NOT NULL,
	`password` varbinary(64) DEFAULT NULL,
	PRIMARY KEY (`login`)
);

GRANT ALL ON `daaexampletest`.* TO 'daa'@'localhost' IDENTIFIED BY 'daa';