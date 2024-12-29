DROP DATABASE
IF
	EXISTS `springBatch`;
	
CREATE DATABASE
IF
	NOT EXISTS `springBatch` CHARACTER 
	SET utf8mb4;
	
USE `springBatch`;

DROP TABLE IF EXISTS `people`;

CREATE TABLE `people` (
  `person_id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `first_name` varchar(255) DEFAULT NULL COMMENT 'first_name',
  `last_name` varchar(255) DEFAULT NULL COMMENT 'last_name',
  PRIMARY KEY (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='people';
