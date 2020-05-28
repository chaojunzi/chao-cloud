/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 100313
Source Host           : localhost:3306
Source Database       : ds1

Target Server Type    : MYSQL
Target Server Version : 100313
File Encoding         : 65001

Date: 2020-05-28 15:08:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_202001
-- ----------------------------
DROP TABLE IF EXISTS `user_202001`;
CREATE TABLE `user_202001` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_202001
-- ----------------------------
INSERT INTO `user_202001` VALUES ('1', '1100', '2020-02-01 18:02:47');

-- ----------------------------
-- Table structure for user_202002
-- ----------------------------
DROP TABLE IF EXISTS `user_202002`;
CREATE TABLE `user_202002` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_202002
-- ----------------------------
INSERT INTO `user_202002` VALUES ('1', '1100', '2020-04-01 18:02:47');

-- ----------------------------
-- Table structure for user_202003
-- ----------------------------
DROP TABLE IF EXISTS `user_202003`;
CREATE TABLE `user_202003` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_202003
-- ----------------------------
INSERT INTO `user_202003` VALUES ('1', '1100', '2020-06-01 18:02:47');

-- ----------------------------
-- Table structure for user_202004
-- ----------------------------
DROP TABLE IF EXISTS `user_202004`;
CREATE TABLE `user_202004` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_202004
-- ----------------------------

-- ----------------------------
-- Table structure for user_202005
-- ----------------------------
DROP TABLE IF EXISTS `user_202005`;
CREATE TABLE `user_202005` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_202005
-- ----------------------------
INSERT INTO `user_202005` VALUES ('1', '1100', '2020-10-01 18:02:47');
INSERT INTO `user_202005` VALUES ('2', '1100', '2020-09-25 18:02:47');

-- ----------------------------
-- Table structure for user_202006
-- ----------------------------
DROP TABLE IF EXISTS `user_202006`;
CREATE TABLE `user_202006` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_202006
-- ----------------------------

-- ----------------------------
-- Table structure for user_item_202001
-- ----------------------------
DROP TABLE IF EXISTS `user_item_202001`;
CREATE TABLE `user_item_202001` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_item_202001
-- ----------------------------
INSERT INTO `user_item_202001` VALUES ('1', '1100', '2020-05-28 11:23:28');
INSERT INTO `user_item_202001` VALUES ('2', '1100', '2020-02-01 18:02:47');

-- ----------------------------
-- Table structure for user_item_202002
-- ----------------------------
DROP TABLE IF EXISTS `user_item_202002`;
CREATE TABLE `user_item_202002` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_item_202002
-- ----------------------------
INSERT INTO `user_item_202002` VALUES ('1', '1100', '2020-04-01 18:02:47');

-- ----------------------------
-- Table structure for user_item_202003
-- ----------------------------
DROP TABLE IF EXISTS `user_item_202003`;
CREATE TABLE `user_item_202003` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_item_202003
-- ----------------------------
INSERT INTO `user_item_202003` VALUES ('1', '1100', '2020-06-01 18:02:47');

-- ----------------------------
-- Table structure for user_item_202004
-- ----------------------------
DROP TABLE IF EXISTS `user_item_202004`;
CREATE TABLE `user_item_202004` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_item_202004
-- ----------------------------

-- ----------------------------
-- Table structure for user_item_202005
-- ----------------------------
DROP TABLE IF EXISTS `user_item_202005`;
CREATE TABLE `user_item_202005` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_item_202005
-- ----------------------------
INSERT INTO `user_item_202005` VALUES ('1', '1100', '2020-05-22 18:02:47');

-- ----------------------------
-- Table structure for user_item_202006
-- ----------------------------
DROP TABLE IF EXISTS `user_item_202006`;
CREATE TABLE `user_item_202006` (
  `id` int(11) NOT NULL,
  `dqbm` varchar(255) DEFAULT '',
  `kprq` datetime DEFAULT NULL COMMENT '开票日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_item_202006
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
