-- phpMyAdmin SQL Dump
-- version 4.0.4.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 26, 2013 at 07:04 
-- Server version: 5.6.12
-- PHP Version: 5.5.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `chatapplication`
--
CREATE DATABASE IF NOT EXISTS `chatapplication` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `chatapplication`;

-- --------------------------------------------------------

--
-- Table structure for table `server_settings`
--

CREATE TABLE IF NOT EXISTS `server_settings` (
  `server_id` int(11) NOT NULL,
  `server_name` varchar(25) NOT NULL,
  `server_version` varchar(15) NOT NULL DEFAULT '1.0.0',
  `server_auth_key` varchar(32) NOT NULL DEFAULT 'ed0f912fe5368e3b90b569d77275d838',
  `server_motd` varchar(1024) NOT NULL,
  `server_port` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `server_settings`
--

INSERT INTO `server_settings` (`server_id`, `server_name`, `server_version`, `server_auth_key`, `server_motd`, `server_port`) VALUES
(0, 'Simple Chat Application', '1.0.0', 'ed0f912fe5368e3b90b569d77275d838', 'Welcome to the Chat!\nYou can see your incoming private messages in the tabs above, if you want to message someone else, right-click in the list of online users to the right and select ''Send PM''', 13337);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `admin` tinyint(4) NOT NULL DEFAULT '0',
  `user_id` int(6) NOT NULL AUTO_INCREMENT,
  `username` varchar(25) NOT NULL,
  `password` varchar(32) NOT NULL,
  `timeRegisteredMillis` bigint(20) NOT NULL,
  `banned` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`admin`, `user_id`, `username`, `password`, `timeRegisteredMillis`, `banned`) VALUES
(1, 1, 'TestAdmin', 'password123', 0, 0),
(0, 2, 'TestUser', 'password', 0, 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

