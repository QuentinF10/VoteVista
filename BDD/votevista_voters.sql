-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: votevista
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `voters`
--

DROP TABLE IF EXISTS `voters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voters` (
  `VoterID` int NOT NULL AUTO_INCREMENT,
  `LastName` varchar(255) NOT NULL,
  `FirstName` varchar(255) NOT NULL,
  `DateOfBirth` date DEFAULT NULL,
  PRIMARY KEY (`VoterID`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voters`
--

LOCK TABLES `voters` WRITE;
/*!40000 ALTER TABLE `voters` DISABLE KEYS */;
INSERT INTO `voters` VALUES (1,'Cracie','Bernarr','1957-01-31'),(2,'Jowsey','Cristine','1958-01-11'),(3,'Leverton','Conrade','1979-07-05'),(4,'Ailward','Robenia','1988-01-05'),(5,'Peniman','Maggi','1993-01-16'),(6,'Defty','Alicea','1971-04-23'),(7,'Grinston','Charleen','1986-12-12'),(8,'Elix','Felix','2000-02-02'),(9,'Ingrem','Nappy','1978-12-19'),(10,'Wrintmore','Gasper','1998-03-19'),(11,'Fortesquieu','Harriott','1981-07-19'),(12,'Ketchaside','Hunt','1982-05-17'),(13,'Chatenet','Sheff','2003-08-13'),(14,'Trahar','Rafaelia','1999-04-29'),(15,'Labrow','Reece','1987-12-22'),(16,'Dacks','Rupert','1998-05-23'),(17,'Loggie','Nels','2001-01-30'),(18,'Wildbore','Gayelord','1962-05-30'),(19,'Okill','Hedvig','1987-10-17'),(20,'Olyunin','Robinet','1987-08-03'),(21,'Coton','Loralee','2000-12-07'),(22,'Allcorn','Antonia','1993-03-24'),(23,'Kilmurry','Arni','1998-04-24'),(24,'Wetherald','Taffy','1962-02-08'),(25,'Summerlie','Quintilla','1997-08-25'),(26,'Casserly','Keane','1961-03-17'),(27,'Farens','Bradley','1963-08-18'),(28,'Rushbrook','Cole','1984-07-24'),(29,'Parish','Si','1970-06-18'),(30,'Rosenkranc','Reilly','1962-03-10'),(31,'Pennock','Ilaire','1973-08-08'),(32,'Bolgar','Kellina','1970-09-19'),(33,'Widmoor','Oswell','1988-08-24'),(34,'Tidgewell','Nefen','1980-01-21'),(35,'Yakubovics','Jo-anne','1998-07-23'),(36,'Crimin','Karyl','1998-05-12'),(37,'Housen','Mellie','2002-07-04'),(38,'Bricklebank','Dorree','1997-07-04'),(39,'MattiCCI','Tanner','1979-06-23'),(40,'Gerrett','Penelopa','1966-07-10'),(41,'Sharples','Ellissa','1963-03-25'),(42,'Aylott','Yard','1968-06-13'),(43,'Kwietek','Salli','1969-01-20'),(44,'Lawleff','Cordelie','1964-12-08'),(45,'Sharper','Bogey','1990-11-07'),(46,'Melloi','Phillipe','1973-03-09'),(47,'Glendining','Brent','1988-02-20'),(48,'Jencey','Katee','1977-08-19'),(49,'Jencken','Gunner','1979-06-11'),(50,'Hiscoke','De witt','1999-03-25'),(51,'Jaggi','Bride','1986-08-22'),(52,'Tunnacliffe','Lorne','1977-12-02'),(53,'Bendon','Merle','1968-09-27'),(54,'Balkwill','Genvieve','1994-07-10'),(55,'Leaman','Ariel','2003-03-01'),(56,'Trussell','Duke','1993-03-21'),(57,'Goodenough','Jolee','2000-03-24'),(58,'Timoney','Herrick','1994-07-08'),(59,'Haukey','Catriona','1965-10-05'),(60,'Shepstone','Brita','1989-03-09'),(61,'Mogridge','Vivianna','1980-06-26'),(62,'Savoury','Elset','1961-02-23'),(63,'Skerm','Bendix','1971-05-28'),(64,'Lestor','Jaye','1990-05-14'),(65,'Prahm','Rawley','1978-10-06'),(66,'Ruddiforth','Letizia','1983-10-01'),(67,'Leverson','Muire','2000-11-22'),(68,'Sainz','Amby','1998-10-19'),(69,'Polkinhorn','Opal','1969-02-06'),(70,'Metheringham','Hamil','1979-03-13'),(71,'Kett','Adrianne','1994-08-27'),(72,'Livick','Vidovic','1962-03-26'),(73,'Hartus','Kristofer','1991-10-29'),(74,'Goadbie','Fallon','2000-01-08'),(75,'Galletly','Sigismund','1998-07-17'),(76,'Scrivin','Willa','1999-06-05'),(77,'Sapir','Lishe','1985-10-24'),(78,'Bransgrove','Danny','1997-04-09'),(79,'Reddel','Debor','1969-04-18'),(80,'Ridd','Nils','1999-05-28'),(81,'Abrey','Justinian','1983-06-15'),(82,'Rottger','Alfie','2000-11-20'),(83,'Ramalhete','Griffy','1995-03-14'),(84,'O\'Downe','Simonette','1964-02-12'),(85,'Vedstra','Madison','1971-12-02'),(86,'Reddings','Bab','1987-05-15'),(87,'Ianno','Foss','1967-07-30'),(88,'Balchin','Harrison','1986-03-03'),(89,'Tredger','Josh','1965-05-03'),(90,'Kimmitt','Mimi','1989-08-05'),(91,'Walczak','Jase','1996-08-10'),(92,'Goscar','Shawn','1994-11-22'),(93,'Todari','Pet','1990-04-14'),(94,'Bodle','Pace','1965-12-09'),(95,'Garwill','Romeo','1997-03-12'),(96,'Toffoletto','Cathrin','1993-04-01'),(97,'Carnelley','Olenka','1995-06-02'),(98,'Wainscot','Saw','1972-06-01'),(99,'Pentland','Marion','2001-11-17'),(100,'Harman','Amy','1991-05-16');
/*!40000 ALTER TABLE `voters` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-12-04 22:59:52
