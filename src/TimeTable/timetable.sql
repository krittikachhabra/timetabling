-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 01, 2018 at 06:18 PM
-- Server version: 5.7.19
-- PHP Version: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `timetable`
--

-- --------------------------------------------------------

--
-- Table structure for table `backlog`
--

DROP TABLE IF EXISTS `backlog`;
CREATE TABLE IF NOT EXISTS `backlog` (
  `AttendingEventID` int(11) NOT NULL,
  `LID` int(11) NOT NULL,
  `PID` int(11) NOT NULL,
  `TID` int(11) NOT NULL,
  PRIMARY KEY (`AttendingEventID`,`LID`,`PID`,`TID`),
  KEY `fk2` (`LID`),
  KEY `fk3` (`PID`),
  KEY `fk4` (`TID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
CREATE TABLE IF NOT EXISTS `courses` (
  `Course_ID` int(11) NOT NULL,
  `No_lectures` int(11) NOT NULL,
  `No_tuts` int(11) NOT NULL,
  `No_practicals` int(11) NOT NULL,
  `Course_name` varchar(30) NOT NULL,
  `Dept_ID` int(11) NOT NULL,
  PRIMARY KEY (`Course_ID`),
  KEY `fk1` (`Dept_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
CREATE TABLE IF NOT EXISTS `department` (
  `Dept_ID` int(11) NOT NULL,
  `No_rooms` int(11) NOT NULL,
  `No_teachers` int(11) NOT NULL,
  `No_courses` int(11) NOT NULL,
  `No_students` int(11) NOT NULL,
  PRIMARY KEY (`Dept_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
CREATE TABLE IF NOT EXISTS `event` (
  `EID` int(11) NOT NULL,
  `Course_ID` int(11) NOT NULL,
  `E_type` varchar(1) NOT NULL,
  `Student_group_ID` int(11) NOT NULL,
  `Teacher_ID` int(11) NOT NULL,
  `Room_ID` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  `no_timeslots` int(11) NOT NULL,
  PRIMARY KEY (`EID`),
  KEY `fk2` (`Course_ID`),
  KEY `fk1` (`Student_group_ID`),
  KEY `fk3` (`Teacher_ID`),
  KEY `fk4` (`Room_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
CREATE TABLE IF NOT EXISTS `rooms` (
  `Dept_ID` int(11) NOT NULL,
  `Room_no` int(11) NOT NULL,
  `Capacity` int(11) NOT NULL,
  PRIMARY KEY (`Dept_ID`,`Room_no`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
CREATE TABLE IF NOT EXISTS `student` (
  `LID` int(11) NOT NULL,
  `PID` int(11) NOT NULL,
  `TID` int(11) NOT NULL,
  `Sname` varchar(30) NOT NULL,
  `Dept_ID` int(11) NOT NULL,
  PRIMARY KEY (`LID`,`PID`,`TID`),
  KEY `fk1` (`Dept_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `student_groups`
--

DROP TABLE IF EXISTS `student_groups`;
CREATE TABLE IF NOT EXISTS `student_groups` (
  `Group_ID` int(11) NOT NULL,
  `LID` int(11) NOT NULL,
  `PID` int(11) NOT NULL,
  `TID` int(11) NOT NULL,
  PRIMARY KEY (`Group_ID`,`LID`,`PID`,`TID`),
  KEY `fk1` (`LID`),
  KEY `fk2` (`PID`),
  KEY `fk3` (`TID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
CREATE TABLE IF NOT EXISTS `teacher` (
  `Teacher_ID` int(11) NOT NULL,
  `Teacher_name` varchar(30) NOT NULL,
  `Dept_ID` int(11) NOT NULL,
  PRIMARY KEY (`Teacher_ID`),
  KEY `fk1` (`Dept_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `university`
--

DROP TABLE IF EXISTS `university`;
CREATE TABLE IF NOT EXISTS `university` (
  `Dept_ID` int(11) NOT NULL,
  `Dept_Name` varchar(30) NOT NULL,
  PRIMARY KEY (`Dept_ID`),
  UNIQUE KEY `UNIQUE` (`Dept_Name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
