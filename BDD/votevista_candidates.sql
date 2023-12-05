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
-- Table structure for table `candidates`
--

DROP TABLE IF EXISTS `candidates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `candidates` (
  `CandidateID` int NOT NULL AUTO_INCREMENT,
  `LastName` varchar(255) NOT NULL,
  `FirstName` varchar(255) NOT NULL,
  `PartyID` int DEFAULT NULL,
  `PositionID` int DEFAULT NULL,
  PRIMARY KEY (`CandidateID`),
  KEY `PartyID` (`PartyID`),
  KEY `fk_position_candidates` (`PositionID`),
  CONSTRAINT `candidates_ibfk_1` FOREIGN KEY (`PartyID`) REFERENCES `politicalparties` (`PartyID`),
  CONSTRAINT `fk_position_candidates` FOREIGN KEY (`PositionID`) REFERENCES `positions` (`PositionID`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `candidates`
--

LOCK TABLES `candidates` WRITE;
/*!40000 ALTER TABLE `candidates` DISABLE KEYS */;
INSERT INTO `candidates` VALUES (1,'Matthews','Lillie',1,1),(2,'Key','Douglas',2,1),(3,'Burch','Kyle',3,1),(4,'Tapia','David',4,1),(5,'Carter','Terri',5,1),(6,'Reed','Ben',1,3),(7,'Ortiz','Mari',2,3),(8,'Klein','Corey',3,3),(9,'Patterson','Carl',4,3),(10,'Francine','Bell',5,3),(11,'Parker','DeAngelo',1,4),(12,'Henderson','Steven',2,4),(13,'Franklin','Angie',3,4),(14,'Korver','Kathy',4,4),(15,'Smith','Theodore',5,4),(16,'Anders','Danielle',1,5),(17,'Morrison','Karen',2,5),(18,'Carver','Penny',3,5),(19,'Elliot','Evan',4,5),(20,'Cervantes','Arturo',5,5),(26,'Jordan','John',1,7),(27,'Hanks','Justin',2,7),(28,'Borchart','Bill',3,7),(29,'Harford','Tanner',4,7),(30,'Marion','Scott',5,7),(31,'Barker','Rhonda',1,8),(32,'Owens','Yvonne',2,8),(33,'Goodman','Jeremy',3,8),(34,'Crane','Simon',4,8),(35,'Scott','Antoine',5,8),(36,'McCoy','Louis',1,9),(37,'Francis','Patricia',2,9),(38,'Welch','Samuel',3,9),(39,'Sanchez','Leslie',4,9),(40,'Baker','Jerry',5,9);
/*!40000 ALTER TABLE `candidates` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-12-04 22:59:51
