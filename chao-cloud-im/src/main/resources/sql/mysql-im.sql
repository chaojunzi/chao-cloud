/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-07-05 17:53:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for im_dept
-- ----------------------------
DROP TABLE IF EXISTS `im_dept`;
CREATE TABLE `im_dept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) DEFAULT '' COMMENT '部门名称',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '是否删除（0未删除1已删除）',
  `version` int(11) DEFAULT 0 COMMENT '乐观锁',
  `create_time` datetime DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='部门';

-- ----------------------------
-- Records of im_dept
-- ----------------------------
INSERT INTO `im_dept` VALUES ('1', '技术部', '0', '0', '2019-06-26 17:02:14');
INSERT INTO `im_dept` VALUES ('2', '测试部', '0', '0', '2019-06-26 17:02:14');
INSERT INTO `im_dept` VALUES ('3', '市场部', '0', '0', '2019-06-26 17:02:14');
INSERT INTO `im_dept` VALUES ('4', '财务部', '0', '0', '2019-06-26 17:02:14');
INSERT INTO `im_dept` VALUES ('5', 'AI部', '0', '0', '2019-07-03 14:39:02');

-- ----------------------------
-- Table structure for im_group
-- ----------------------------
DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '群id',
  `nick_name` varchar(30) DEFAULT '' COMMENT '昵称',
  `head_img` varchar(150) DEFAULT '' COMMENT '头像',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '是否删除（0未删除1已删除）',
  `version` int(11) DEFAULT 0 COMMENT '乐观锁',
  `create_time` datetime DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='用户帐号';

-- ----------------------------
-- Records of im_group
-- ----------------------------
INSERT INTO `im_group` VALUES ('1', '君临天下', 'https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2260373675,224366072&fm=27&gp=0.jpg', '0', '0', '2019-06-26 16:48:59');

-- ----------------------------
-- Table structure for im_group_user
-- ----------------------------
DROP TABLE IF EXISTS `im_group_user`;
CREATE TABLE `im_group_user` (
  `group_id` int(11) NOT NULL COMMENT '群id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`user_id`,`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-群';

-- ----------------------------
-- Records of im_group_user
-- ----------------------------
INSERT INTO `im_group_user` VALUES ('1', '1');
INSERT INTO `im_group_user` VALUES ('1', '2');

-- ----------------------------
-- Table structure for im_msg
-- ----------------------------
DROP TABLE IF EXISTS `im_msg`;
CREATE TABLE `im_msg` (
  `msg_id` int(11) NOT NULL AUTO_INCREMENT,
  `fromid` int(11) DEFAULT NULL COMMENT '发送人',
  `toid` int(11) DEFAULT NULL COMMENT '接收人',
  `id` int(11) DEFAULT NULL COMMENT 'group=群id    friend=发送者id',
  `username` varchar(30) DEFAULT '' COMMENT '用户名',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像',
  `content` text DEFAULT NULL COMMENT '消息内容',
  `type` varchar(11) DEFAULT NULL COMMENT '类型   group friend',
  `version` int(11) DEFAULT 0 COMMENT '乐观锁',
  `create_time` datetime DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`msg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8 COMMENT='消息表';

-- ----------------------------
-- Records of im_msg
-- ----------------------------
INSERT INTO `im_msg` VALUES ('173', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'niad', 'group', '0', '2019-06-28 15:13:31');
INSERT INTO `im_msg` VALUES ('174', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'asdas', 'group', '0', '2019-06-28 15:13:33');
INSERT INTO `im_msg` VALUES ('175', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:34');
INSERT INTO `im_msg` VALUES ('176', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'a', 'group', '0', '2019-06-28 15:13:34');
INSERT INTO `im_msg` VALUES ('177', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'd', 'group', '0', '2019-06-28 15:13:35');
INSERT INTO `im_msg` VALUES ('178', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'f', 'group', '0', '2019-06-28 15:13:36');
INSERT INTO `im_msg` VALUES ('179', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'fdsg', 'group', '0', '2019-06-28 15:13:36');
INSERT INTO `im_msg` VALUES ('180', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'dgfds', 'group', '0', '2019-06-28 15:13:37');
INSERT INTO `im_msg` VALUES ('181', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'dasfdsg', 'group', '0', '2019-06-28 15:13:38');
INSERT INTO `im_msg` VALUES ('182', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'dsg', 'group', '0', '2019-06-28 15:13:38');
INSERT INTO `im_msg` VALUES ('183', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'h', 'group', '0', '2019-06-28 15:13:39');
INSERT INTO `im_msg` VALUES ('184', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:39');
INSERT INTO `im_msg` VALUES ('185', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg` VALUES ('186', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg` VALUES ('187', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg` VALUES ('188', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg` VALUES ('189', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg` VALUES ('190', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:41');
INSERT INTO `im_msg` VALUES ('191', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:41');
INSERT INTO `im_msg` VALUES ('192', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'a', 'group', '0', '2019-06-28 15:52:56');
INSERT INTO `im_msg` VALUES ('193', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'v', 'group', '0', '2019-06-28 15:52:58');
INSERT INTO `im_msg` VALUES ('194', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'h', 'group', '0', '2019-06-28 15:52:59');
INSERT INTO `im_msg` VALUES ('195', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'k', 'group', '0', '2019-06-28 15:53:01');
INSERT INTO `im_msg` VALUES ('196', '1', '2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', '阿迪', 'friend', '0', '2019-07-03 15:23:10');
INSERT INTO `im_msg` VALUES ('197', '1', '3', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'asd', 'friend', '0', '2019-07-03 19:19:02');

-- ----------------------------
-- Table structure for im_msg_his
-- ----------------------------
DROP TABLE IF EXISTS `im_msg_his`;
CREATE TABLE `im_msg_his` (
  `msg_id` int(11) NOT NULL AUTO_INCREMENT,
  `from_to` varchar(11) DEFAULT '' COMMENT '发送人->接收人 如   1-2',
  `id` int(11) DEFAULT NULL COMMENT 'group=群id    friend=发送者id',
  `username` varchar(30) DEFAULT '' COMMENT '用户名',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像',
  `content` text DEFAULT NULL COMMENT '消息内容',
  `type` varchar(11) DEFAULT NULL COMMENT '类型   group friend',
  `version` int(11) DEFAULT 0 COMMENT '乐观锁',
  `create_time` datetime DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`msg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8 COMMENT='消息表';

-- ----------------------------
-- Records of im_msg_his
-- ----------------------------
INSERT INTO `im_msg_his` VALUES ('173', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'nihao', 'friend', '0', '2019-06-28 15:10:37');
INSERT INTO `im_msg_his` VALUES ('174', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'enen', 'friend', '0', '2019-06-28 15:13:09');
INSERT INTO `im_msg_his` VALUES ('175', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'niad', 'group', '0', '2019-06-28 15:13:31');
INSERT INTO `im_msg_his` VALUES ('176', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'asdas', 'group', '0', '2019-06-28 15:13:33');
INSERT INTO `im_msg_his` VALUES ('177', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:34');
INSERT INTO `im_msg_his` VALUES ('178', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'a', 'group', '0', '2019-06-28 15:13:34');
INSERT INTO `im_msg_his` VALUES ('179', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'd', 'group', '0', '2019-06-28 15:13:35');
INSERT INTO `im_msg_his` VALUES ('180', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'f', 'group', '0', '2019-06-28 15:13:36');
INSERT INTO `im_msg_his` VALUES ('181', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'fdsg', 'group', '0', '2019-06-28 15:13:36');
INSERT INTO `im_msg_his` VALUES ('182', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'dgfds', 'group', '0', '2019-06-28 15:13:37');
INSERT INTO `im_msg_his` VALUES ('183', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'dasfdsg', 'group', '0', '2019-06-28 15:13:38');
INSERT INTO `im_msg_his` VALUES ('184', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'dsg', 'group', '0', '2019-06-28 15:13:38');
INSERT INTO `im_msg_his` VALUES ('185', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'h', 'group', '0', '2019-06-28 15:13:39');
INSERT INTO `im_msg_his` VALUES ('186', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:39');
INSERT INTO `im_msg_his` VALUES ('187', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg_his` VALUES ('188', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg_his` VALUES ('189', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg_his` VALUES ('190', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg_his` VALUES ('191', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:40');
INSERT INTO `im_msg_his` VALUES ('192', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'j', 'group', '0', '2019-06-28 15:13:41');
INSERT INTO `im_msg_his` VALUES ('193', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', '9', 'group', '0', '2019-06-28 15:13:41');
INSERT INTO `im_msg_his` VALUES ('194', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'a', 'group', '0', '2019-06-28 15:52:56');
INSERT INTO `im_msg_his` VALUES ('195', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'v', 'group', '0', '2019-06-28 15:52:58');
INSERT INTO `im_msg_his` VALUES ('196', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'h', 'group', '0', '2019-06-28 15:52:59');
INSERT INTO `im_msg_his` VALUES ('197', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'k', 'group', '0', '2019-06-28 15:53:01');
INSERT INTO `im_msg_his` VALUES ('198', '1-1', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'nishi', 'friend', '0', '2019-07-03 15:17:05');
INSERT INTO `im_msg_his` VALUES ('199', '1-2', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', '阿迪', 'friend', '0', '2019-07-03 15:23:10');
INSERT INTO `im_msg_his` VALUES ('200', '1-3', '1', '薛超', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', 'asd', 'friend', '0', '2019-07-03 19:19:02');

-- ----------------------------
-- Table structure for im_user
-- ----------------------------
DROP TABLE IF EXISTS `im_user`;
CREATE TABLE `im_user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(30) DEFAULT '' COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `nick_name` varchar(30) DEFAULT '' COMMENT '昵称',
  `sign` varchar(255) DEFAULT '' COMMENT '签名',
  `head_img` varchar(150) DEFAULT '' COMMENT '头像',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门id',
  `dept_name` varchar(255) DEFAULT '' COMMENT '部门名称',
  `status` int(11) DEFAULT 1 COMMENT '状态 0.冻结 1.正常',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '是否删除（0未删除1已删除）',
  `version` int(11) DEFAULT 0 COMMENT '乐观锁',
  `create_time` datetime DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='用户帐号';

-- ----------------------------
-- Records of im_user
-- ----------------------------
INSERT INTO `im_user` VALUES ('1', 'xuechao', '1', '薛超', '玉汝于成', 'https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoSLMMV0Gic9Zy3Nk56dZfkdm9DB5vWYQyucYOon0BG1vfVaz2d8kcsnetwM2eVzXgTJsvN2Pq45Fg/132', '1', '技术部', '1', '0', '0', '2019-06-26 16:48:21');
INSERT INTO `im_user` VALUES ('2', 'chaojunzi', '1', '超君子', '君子如玉', 'https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2260373675,224366072&fm=27&gp=0.jpg', '1', '技术部', '1', '0', '0', '2019-06-26 16:48:59');
INSERT INTO `im_user` VALUES ('3', 'xuexue', '0', '小薛', '玉不琢，不成器', 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=844831047,446771757&fm=26&gp=0.jpg', '5', 'AI部', '1', '0', '0', '2019-07-03 14:39:31');
INSERT INTO `im_user` VALUES ('4', 'chaochao', '0', '小超', '超越极限', 'https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3166569812,977304273&fm=26&gp=0.jpg', '5', 'AI部', '1', '0', '0', '2019-07-03 19:05:10');
