-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: financial_management22
-- ------------------------------------------------------
-- Server version	8.0.19

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
-- Table structure for table `budgets`
--

DROP TABLE IF EXISTS `budgets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budgets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `budgetable_id` int NOT NULL,
  `budgetable_type` varchar(45) DEFAULT NULL,
  `started_at` datetime NOT NULL,
  `ended_at` datetime NOT NULL,
  `amount` float NOT NULL DEFAULT '0',
  `spent_amount` float NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bubudget_fk_idx` (`user_id`),
  CONSTRAINT `bubudget_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `budgets`
--

LOCK TABLES `budgets` WRITE;
/*!40000 ALTER TABLE `budgets` DISABLE KEYS */;
INSERT INTO `budgets` VALUES (13,68,204,'APP/CATEGORY','2020-06-01 00:00:00','2020-06-30 00:00:00',5000000,3450000,'2020-06-21 11:55:30','2020-06-22 14:42:22'),(18,69,204,'APP/CATEGORY','2020-06-01 00:00:00','2020-06-06 00:00:00',500000,0,'2020-06-21 16:20:12','2020-06-21 16:20:12'),(21,59,205,'APP/CATEGORY','2020-06-01 00:00:00','2020-06-30 00:00:00',600000,270000,'2020-06-21 23:24:07','2020-06-22 14:44:15'),(23,59,204,'APP/CATEGORY','2020-06-01 00:00:00','2020-06-30 00:00:00',5000000,2950000,'2020-06-22 03:32:43','2020-06-22 14:42:22'),(31,59,204,'APP/CATEGORY','2020-06-01 00:00:00','2020-06-26 00:00:00',5000000,3950000,'2020-06-22 04:27:49','2020-06-22 14:42:22'),(32,59,210,'APP/CATEGORY','2020-06-02 00:00:00','2020-06-17 00:00:00',500000,100000,'2020-06-22 05:12:32','2020-06-22 05:12:32'),(33,59,212,'APP/CATEGORY','2020-06-01 00:00:00','2020-06-30 00:00:00',5000000,60000,'2020-06-22 10:48:34','2020-06-22 10:51:05'),(34,59,205,'APP/CATEGORY','2020-07-01 00:00:00','2020-07-31 00:00:00',2000000,200000,'2020-06-22 14:51:15','2020-06-22 14:52:12'),(35,59,204,'APP/CATEGORY','2020-07-01 00:00:00','2020-07-31 00:00:00',2000000,2200000,'2020-07-01 14:59:43','2020-07-01 15:00:13');
/*!40000 ALTER TABLE `budgets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_id` int NOT NULL,
  `money_type` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `icon` varchar(45) DEFAULT 'null',
  PRIMARY KEY (`id`),
  KEY `type_idx` (`type_id`) /*!80000 INVISIBLE */,
  CONSTRAINT `categories_id_fk1` FOREIGN KEY (`type_id`) REFERENCES `types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (199,3,'debt','Debt','i_debt'),(200,3,'debt-collection','Debt Collection','i_debt-collection'),(201,3,'loan','Loan','i_loan'),(202,3,'loan-repayment','Repayment','i_repayment'),(203,4,'expense','Bills & Utilities','i_bill'),(204,4,'expense','Business','i_business'),(205,4,'expense','Education','i_education'),(206,4,'expense','Entertainment','i_entertainment'),(207,4,'expense','Family','i_family'),(208,4,'expense','Fees & Charges','i_fee'),(209,4,'expense','Food & Beverage','i_fee'),(210,4,'expense','Friends & Lover','i_friend'),(211,4,'expense','Gifts & Donations','i_gift'),(212,4,'expense','Health & Fitness','i_health'),(213,4,'expense','Insurances','i_insurances'),(214,4,'expense','Investment','i_investment'),(215,4,'expense','Others','i_others'),(216,4,'expense','Shopping','i_shopping'),(217,4,'expense','Transportation','i_transportation'),(218,4,'expense','Travel','i_travel'),(219,4,'expense','Withdrawal','i_withdrawal'),(220,5,'income','Award','i_award'),(221,5,'income','Gift','i_give'),(222,5,'income','Interest money','i_interest-money'),(223,5,'income','Others','i_others'),(224,5,'income','Salary','i_salary'),(225,5,'income','Selling','i_selling');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currencies`
--

DROP TABLE IF EXISTS `currencies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `currencies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `code` varchar(5) NOT NULL,
  `symbol` varchar(2) NOT NULL,
  `icon` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currencies`
--

LOCK TABLES `currencies` WRITE;
/*!40000 ALTER TABLE `currencies` DISABLE KEYS */;
INSERT INTO `currencies` VALUES (1,'Việt Nam Đồng','VND','₫','currency__vnd'),(2,'United States Dollar','USD','$','currency__usd'),(3,'Yen','JPY','¥','currency__jpy');
/*!40000 ALTER TABLE `currencies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sub_categories`
--

DROP TABLE IF EXISTS `sub_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_id` int NOT NULL,
  `category_id` int NOT NULL,
  `money_type` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `icon` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `type_idx` (`type_id`) /*!80000 INVISIBLE */,
  KEY `category_idx` (`category_id`),
  CONSTRAINT `sub_category_fk_w_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sub_category_fk_w_type` FOREIGN KEY (`type_id`) REFERENCES `types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_categories`
--

LOCK TABLES `sub_categories` WRITE;
/*!40000 ALTER TABLE `sub_categories` DISABLE KEYS */;
INSERT INTO `sub_categories` VALUES (121,4,203,'expense','Gas','i_bill_gas'),(122,4,203,'expense','Internet','i_bill_internet'),(123,4,203,'expense','Phone','i_bill_phone'),(124,4,203,'expense','Rentals','i_bill_rentals'),(125,4,203,'expense','Television','i_bill_television'),(126,4,203,'expense','Water','i_bill_water'),(127,4,205,'expense','Books','i_education_book'),(128,4,206,'expense','Games','i_entertainment_game'),(129,4,206,'expense','Movies','i_entertainment_movie'),(130,4,207,'expense','Children & Babies','i_family_children'),(131,4,207,'expense','Home Improvement','i_family_home-improvement'),(132,4,207,'expense','Home Services','i_family_home-services'),(133,4,207,'expense','Pets','i_family_pets'),(134,4,209,'expense','Café','i_food_cafe'),(135,4,209,'expense','Restaurants','i_food_restaurant'),(136,4,211,'expense','Charity','i_gift_charity'),(137,4,211,'expense','Funeral','i_gift_funeral'),(138,4,211,'expense','Marriage','i_gift_marriage'),(139,4,212,'expense','Doctor','i_health_doctor'),(140,4,212,'expense','Personal Care','i_health_personal-care'),(141,4,212,'expense','Pharmacy','i_health_pharmacy'),(142,4,212,'expense','Sports','i_health_sports'),(143,4,216,'expense','Accessories','i_shopping_accessory'),(144,4,216,'expense','Clothing','i_shopping_clothing'),(145,4,216,'expense','Electronics','i_shopping_electronic'),(146,4,216,'expense','Footwear','i_shopping_footwear'),(147,4,217,'expense','Maintenance','i_transportation_maintenance'),(148,4,217,'expense','Parking Fees','i_transportation_parking-fee'),(149,4,217,'expense','Petrol','i_transportation_petrol'),(150,4,217,'expense','Taxi','i_transportation_taxi');
/*!40000 ALTER TABLE `sub_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `wallet_id` int NOT NULL,
  `category_id` int NOT NULL,
  `sub_category_id` int DEFAULT NULL,
  `amount` float NOT NULL,
  `note` text,
  `transacted_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `categoris_idx` (`category_id`) /*!80000 INVISIBLE */,
  KEY `wallet_idx` (`wallet_id`) /*!80000 INVISIBLE */,
  KEY `subcategory_idx` (`sub_category_id`) /*!80000 INVISIBLE */,
  CONSTRAINT `transactions_idfk_w_categories` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `transactions_idfk_w_subcategories` FOREIGN KEY (`sub_category_id`) REFERENCES `sub_categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `transactions_idfk_w_wallet` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (54,1,203,121,-50000,'','2020-06-21 00:00:00'),(55,1,205,NULL,-40000,'Mua sach','2020-06-08 00:00:00'),(56,69,203,122,-50000,'Thang 5','2020-06-21 00:00:00'),(57,1,204,NULL,-500000,'','2020-06-21 00:00:00'),(58,69,205,NULL,-50000,'Mua sach','2020-06-21 00:00:00'),(59,1,206,NULL,-40000,'','2020-06-21 00:00:00'),(60,1,211,NULL,-60000,'Di sinh nhat','2020-06-11 00:00:00'),(61,1,205,127,-60000,'','2020-06-04 00:00:00'),(62,1,210,NULL,-100000,'','2020-06-07 00:00:00'),(63,1,223,NULL,1000000,'Me gui tien <3','2020-06-21 00:00:00'),(64,1,205,NULL,-60000,'','2020-06-21 00:00:00'),(65,69,205,NULL,-40000,'','2020-06-21 00:00:00'),(67,69,205,NULL,-100000,'','2020-06-22 00:00:00'),(69,1,204,NULL,-500000,'','2020-06-22 00:00:00'),(70,69,204,NULL,-500000,'','2020-06-22 00:00:00'),(73,1,212,NULL,-60000,'','2020-06-22 00:00:00'),(74,1,205,NULL,-50000,'Mua sach','2020-05-02 00:00:00'),(75,1,224,NULL,500000,'Tien cong','2020-05-08 00:00:00'),(76,1,210,NULL,-500000,'Di sinh nhat ban ABC','2020-07-16 00:00:00'),(77,1,211,NULL,-1000000,'Sinh nhat em gai <3','2020-09-20 00:00:00'),(94,1,205,NULL,-50000,'Mua dung cu hoc tap','2020-06-20 00:00:00'),(95,1,211,NULL,-200000,'Dam cuoi ban Hieu Le','2021-06-22 00:00:00'),(97,69,205,NULL,-200000,'','2020-07-03 00:00:00'),(98,1,204,NULL,-200000,'','2020-07-01 00:00:00'),(99,1,204,NULL,-2000000,'','2020-07-01 00:00:00');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `types`
--

DROP TABLE IF EXISTS `types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `money_type` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `types`
--

LOCK TABLES `types` WRITE;
/*!40000 ALTER TABLE `types` DISABLE KEYS */;
INSERT INTO `types` VALUES (3,'debt-loan','Debt/Loan'),(4,'expense','Expense'),(5,'income','Income');
/*!40000 ALTER TABLE `types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `created` datetime DEFAULT NULL,
  `delete` datetime DEFAULT NULL,
  `status` int DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (58,'nvan','nguyenvinhanchelseafc@gmail.com','an123456','2020-05-29 23:28:55',NULL,1),(59,'an222','nguyenvinhanchelseafc030499@gmail.com','an1234567','2020-05-29 23:30:02',NULL,1),(60,'nvan1','nvan1@gmail.com','an123456','2020-06-06 14:39:05',NULL,1),(61,'nvan2','nvan2@gmail.com','an123456','2020-06-07 13:19:01',NULL,1),(62,'nvan9','nvan9@gmail.com','an123456','2020-06-08 03:16:40',NULL,1),(64,'nvan999','nvan999@gmail.com','an12345678','2020-06-09 23:42:09',NULL,1),(65,'nvan001','nvan001@gmail.com','an12345678','2020-06-10 01:32:30',NULL,1),(66,'nvan555','an555@gmail.com','an1234567890','2020-06-10 14:47:48',NULL,1),(67,'an1234','nguyenvinhanchelseafc111@gmail.com','an179902','2020-06-11 10:43:31',NULL,1),(68,'an111','an111@gmail.com','an123456','2020-06-21 11:54:50',NULL,1),(69,'an333','an333@gmail.com','an123456','2020-06-21 16:19:03',NULL,1),(70,'an444','an444@gmail.com','an123456','2020-06-22 12:19:07',NULL,1),(71,'an999','an999@gmail.com','an123456','2020-06-22 14:40:21',NULL,1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallets`
--

DROP TABLE IF EXISTS `wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallets` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(45) DEFAULT 'null',
  `currency_id` int NOT NULL,
  `amount` float NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_idx` (`user_id`) /*!80000 INVISIBLE */,
  KEY `currency_idx` (`currency_id`),
  CONSTRAINT `wallets_idfk_w_currency` FOREIGN KEY (`currency_id`) REFERENCES `currencies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `wallets_idfk_w_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
INSERT INTO `wallets` VALUES (1,59,'Test 2',1,-2790210),(69,59,'Test',1,6460000),(75,58,'bbbb',1,4000),(77,58,'1',1,1),(78,58,'tttt',1,2),(79,61,'2',1,2),(80,60,'1',1,1),(81,62,'Test',1,500000),(83,64,'Ready Cash 2',1,2000000),(86,66,'Ready Cash 1',1,2000000),(88,67,'Ready Cash',1,1800000),(89,67,'Test',2,0),(90,68,'Test',1,2000000),(91,69,'Test',1,6000000),(97,70,'Ready Cash 2',1,5000000);
/*!40000 ALTER TABLE `wallets` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-22 17:30:40
