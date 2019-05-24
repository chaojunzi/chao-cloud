/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : admin

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-05-24 19:19:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for chao_config
-- ----------------------------
DROP TABLE IF EXISTS `chao_config`;
CREATE TABLE `chao_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) DEFAULT '' COMMENT '名称',
  `val` varchar(255) DEFAULT '' COMMENT '值',
  `version` int(11) DEFAULT 0 COMMENT '乐观锁',
  `create_time` timestamp NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='like=name,val';

-- ----------------------------
-- Records of chao_config
-- ----------------------------
INSERT INTO `chao_config` VALUES ('8', 'xuechao', '啊', '0', '2019-05-23 12:26:26');
INSERT INTO `chao_config` VALUES ('9', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('10', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('11', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('12', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('13', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('14', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('15', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('16', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('17', 'xuechao', null, '0', null);
INSERT INTO `chao_config` VALUES ('18', '测试', '爱上阿萨德', '0', '2019-05-22 00:00:00');
INSERT INTO `chao_config` VALUES ('19', 'das', 'asdas', '0', '2019-05-22 00:00:00');
INSERT INTO `chao_config` VALUES ('20', '啊', '啊', '0', '2019-05-28 00:00:00');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级部门ID，一级部门为0',
  `name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `order_num` int(11) DEFAULT 0 COMMENT '排序',
  `del_flag` tinyint(4) DEFAULT 1 COMMENT '是否删除  0：已删除  1：正常',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='部门管理';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('6', '0', '研发部', '1', '1');
INSERT INTO `sys_dept` VALUES ('9', '0', '市场部', '0', '1');
INSERT INTO `sys_dept` VALUES ('11', '0', '运营部', '5', '1');
INSERT INTO `sys_dept` VALUES ('16', '0', '产品部', '4', '1');
INSERT INTO `sys_dept` VALUES ('18', '0', '测试部', '5', '1');
INSERT INTO `sys_dept` VALUES ('20', '18', '测试1部', '1', '1');
INSERT INTO `sys_dept` VALUES ('21', '20', '测试2部', '1', '1');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `time` int(11) DEFAULT NULL COMMENT '响应时间',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=258 DEFAULT CHARSET=utf8 COMMENT='系统日志';

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES ('1', '0', 'admin', '登录', '188', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"n4ek\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-16 17:03:49');
INSERT INTO `sys_log` VALUES ('2', '0', 'admin', '请求更改用户密码', '0', 'com.chao.cloud.admin.system.controller.UserController.resetPwd()', '[]', '0:0:0:0:0:0:0:1', '2019-05-16 17:04:10');
INSERT INTO `sys_log` VALUES ('3', '0', 'admin', '编辑菜单', '17', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":91,\"pName\":\"系统监控\",\"menu\":{\"name\":\"系统日志\",\"icon\":\"layui-icon layui-ic...\",\"type\":1,\"gmtCreate\":1502719913000,\"parentId\":91,\"url\":\"common/log\"}},27]', '0:0:0:0:0:0:0:1', '2019-05-16 17:05:53');
INSERT INTO `sys_log` VALUES ('4', '0', 'admin', '编辑菜单', '12', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":91,\"pName\":\"系统监控\",\"menu\":{\"name\":\"系统日志\",\"icon\":\"layui-icon layui-ic...\",\"type\":1,\"gmtCreate\":1502719913000,\"parentId\":91,\"url\":\"common/log\"}},27]', '0:0:0:0:0:0:0:1', '2019-05-16 17:06:16');
INSERT INTO `sys_log` VALUES ('5', '0', 'admin', '更新菜单', '35', 'com.chao.cloud.admin.system.controller.MenuController.update()', '[{\"name\":\"系统日志\",\"icon\":\"layui-icon layui-icon-list\",\"menuId\":27,\"orderNum\":0,\"perms\":\"common:log\",\"type\":1,\"parentId\":91,\"url\":\"sys/log\"}]', '0:0:0:0:0:0:0:1', '2019-05-16 17:06:28');
INSERT INTO `sys_log` VALUES ('6', '0', 'admin', '编辑菜单', '8', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":91,\"pName\":\"系统监控\",\"menu\":{\"name\":\"系统日志\",\"icon\":\"layui-icon layui-ic...log\",\"type\":1,\"gmtCreate\":1502719913000,\"parentId\":91,\"url\":\"sys/log\"}},27]', '0:0:0:0:0:0:0:1', '2019-05-16 17:06:47');
INSERT INTO `sys_log` VALUES ('7', '0', 'admin', '更新菜单', '41', 'com.chao.cloud.admin.system.controller.MenuController.update()', '[{\"name\":\"系统日志\",\"icon\":\"layui-icon layui-icon-list\",\"menuId\":27,\"orderNum\":0,\"perms\":\"sys:log\",\"type\":1,\"parentId\":91,\"url\":\"sys/log\"}]', '0:0:0:0:0:0:0:1', '2019-05-16 17:06:55');
INSERT INTO `sys_log` VALUES ('8', '0', 'admin', '编辑菜单', '10', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":77,\"pName\":\"系统工具\",\"menu\":{\"name\":\"计划任务\",\"icon\":\"layui-icon layui-ic...s\":\"common:taskScheduleJob\",\"type\":1,\"parentId\":77,\"url\":\"common/job\"}},72]', '0:0:0:0:0:0:0:1', '2019-05-16 17:07:08');
INSERT INTO `sys_log` VALUES ('9', '0', 'admin', '更新菜单', '38', 'com.chao.cloud.admin.system.controller.MenuController.update()', '[{\"name\":\"计划任务\",\"icon\":\"layui-icon layui-icon-table\",\"menuId\":72,\"orderNum\"...m\":4,\"perms\":\"sys:taskScheduleJob\",\"type\":1,\"parentId\":77,\"url\":\"sys/job\"}]', '0:0:0:0:0:0:0:1', '2019-05-16 17:07:14');
INSERT INTO `sys_log` VALUES ('10', '0', 'admin', '编辑菜单', '6', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":77,\"pName\":\"系统工具\",\"menu\":{\"name\":\"代码生成\",\"icon\":\"layui-icon layui-ic...s\":\"common:generator\",\"type\":1,\"parentId\":77,\"url\":\"common/generator\"}},48]', '0:0:0:0:0:0:0:1', '2019-05-16 17:07:21');
INSERT INTO `sys_log` VALUES ('11', '0', 'admin', '更新菜单', '39', 'com.chao.cloud.admin.system.controller.MenuController.update()', '[{\"name\":\"代码生成\",\"icon\":\"layui-icon layui-icon-fonts-code\",\"menuId\":48,\"orde...m\":3,\"perms\":\"sys:generator\",\"type\":1,\"parentId\":77,\"url\":\"sys/generator\"}]', '0:0:0:0:0:0:0:1', '2019-05-16 17:07:26');
INSERT INTO `sys_log` VALUES ('12', '0', 'admin', '编辑菜单', '8', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":0,\"pName\":\"根目录\",\"menu\":{\"name\":\"系统工具\",\"icon\":\"layui-icon layui-icon...l\",\"menuId\":77,\"orderNum\":3,\"perms\":\"\",\"type\":0,\"parentId\":0,\"url\":\"\"}},77]', '0:0:0:0:0:0:0:1', '2019-05-16 17:07:29');
INSERT INTO `sys_log` VALUES ('13', '-1', '获取用户信息为空', '登录', '0', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"ho8s\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-16 18:22:07');
INSERT INTO `sys_log` VALUES ('14', '0', 'admin', '登录', '467', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"h08s\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-16 18:22:11');
INSERT INTO `sys_log` VALUES ('15', '0', 'admin', '登录', '787', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"7frk\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-16 20:16:26');
INSERT INTO `sys_log` VALUES ('16', '0', 'admin', '登录', '538', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"v6rv\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 09:50:57');
INSERT INTO `sys_log` VALUES ('17', '0', 'admin', '添加菜单', '0', 'com.chao.cloud.admin.system.controller.MenuController.add()', '[{\"pId\":0,\"pName\":\"根目录\"},0]', '0:0:0:0:0:0:0:1', '2019-05-17 10:13:39');
INSERT INTO `sys_log` VALUES ('18', '0', 'admin', '保存菜单', '40', 'com.chao.cloud.admin.system.controller.MenuController.save()', '[{\"name\":\"Echarts图表\",\"icon\":\"layui-icon layui-icon-chart\",\"menuId\":171,\"orderNum\":5,\"perms\":\"\",\"type\":0,\"parentId\":0,\"url\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 10:16:14');
INSERT INTO `sys_log` VALUES ('19', '0', 'admin', '添加菜单', '8', 'com.chao.cloud.admin.system.controller.MenuController.add()', '[{\"pId\":171,\"pName\":\"Echarts图表\"},171]', '0:0:0:0:0:0:0:1', '2019-05-17 10:16:42');
INSERT INTO `sys_log` VALUES ('20', '0', 'admin', '保存菜单', '37', 'com.chao.cloud.admin.system.controller.MenuController.save()', '[{\"name\":\"3D地球\",\"icon\":\"layui-icon layui-icon-website\",\"menuId\":172,\"orderN...m\":1,\"perms\":\"\",\"type\":1,\"parentId\":171,\"url\":\"/echarts/world/world.html\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 10:17:51');
INSERT INTO `sys_log` VALUES ('21', '0', 'admin', '编辑菜单', '12', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":91,\"pName\":\"系统监控\",\"menu\":{\"name\":\"运行监控\",\"icon\":\"layui-icon layui-ic...derNum\":1,\"perms\":\"\",\"type\":1,\"parentId\":91,\"url\":\"/druid/index.html\"}},57]', '0:0:0:0:0:0:0:1', '2019-05-17 10:18:29');
INSERT INTO `sys_log` VALUES ('22', '0', 'admin', '更新菜单', '37', 'com.chao.cloud.admin.system.controller.MenuController.update()', '[{\"name\":\"运行监控\",\"icon\":\"layui-icon layui-icon-vercode\",\"menuId\":57,\"orderNum\":1,\"perms\":\"\",\"type\":1,\"parentId\":91,\"url\":\"/druid/index.html\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 10:18:39');
INSERT INTO `sys_log` VALUES ('23', '-1', '获取用户信息为空', '登录', '0', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 11:29:20');
INSERT INTO `sys_log` VALUES ('24', '0', 'admin', '登录', '10', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"039r\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 11:30:58');
INSERT INTO `sys_log` VALUES ('25', '0', 'admin', '登录', '20', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"w83j\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 12:21:40');
INSERT INTO `sys_log` VALUES ('26', '0', 'admin', '登录', '43', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"hhk2\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 13:28:23');
INSERT INTO `sys_log` VALUES ('27', '0', 'admin', '登录', '46', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"vmqn\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 14:13:47');
INSERT INTO `sys_log` VALUES ('28', '0', 'admin', '登录', '580779', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"upxf\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:21');
INSERT INTO `sys_log` VALUES ('29', '0', 'admin', '数据表', '134446', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:31');
INSERT INTO `sys_log` VALUES ('30', '0', 'admin', '任务列表', '17420', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:32');
INSERT INTO `sys_log` VALUES ('31', '0', 'admin', '在线用户列表', '349', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:35');
INSERT INTO `sys_log` VALUES ('32', '0', 'admin', '用户列表', '13498', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:37');
INSERT INTO `sys_log` VALUES ('33', '0', 'admin', '权限列表', '15385', 'com.chao.cloud.admin.system.controller.RoleController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:37');
INSERT INTO `sys_log` VALUES ('34', '0', 'admin', '菜单列表', '13767', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:38');
INSERT INTO `sys_log` VALUES ('35', '0', 'admin', '部门列表', '8005', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:21:38');
INSERT INTO `sys_log` VALUES ('36', '0', 'admin', '登录', '195166', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"remc\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:44');
INSERT INTO `sys_log` VALUES ('37', '0', 'admin', 'stat@用户列表', '37991', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:47');
INSERT INTO `sys_log` VALUES ('38', '0', 'admin', 'stat@权限列表', '16844', 'com.chao.cloud.admin.system.controller.RoleController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:48');
INSERT INTO `sys_log` VALUES ('39', '0', 'admin', 'stat@菜单列表', '11928', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:48');
INSERT INTO `sys_log` VALUES ('40', '0', 'admin', 'stat@部门列表', '6517', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:49');
INSERT INTO `sys_log` VALUES ('41', '0', 'admin', 'stat@在线用户列表', '275', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:52');
INSERT INTO `sys_log` VALUES ('42', '0', 'admin', 'stat@数据表', '4053', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:57');
INSERT INTO `sys_log` VALUES ('43', '0', 'admin', 'stat@任务列表', '9721', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:30:57');
INSERT INTO `sys_log` VALUES ('44', '0', 'admin', 'stat@任务列表', '7674', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"jobName\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:05');
INSERT INTO `sys_log` VALUES ('45', '0', 'admin', 'stat@数据表', '7042', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:08');
INSERT INTO `sys_log` VALUES ('46', '0', 'admin', 'stat@在线用户列表', '40', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:09');
INSERT INTO `sys_log` VALUES ('47', '0', 'admin', 'stat@部门列表', '7467', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:11');
INSERT INTO `sys_log` VALUES ('48', '0', 'admin', 'stat@菜单列表', '16605', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:13');
INSERT INTO `sys_log` VALUES ('49', '0', 'admin', 'stat@用户列表', '14834', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:17');
INSERT INTO `sys_log` VALUES ('50', '0', 'admin', 'stat@用户列表', '11408', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:19');
INSERT INTO `sys_log` VALUES ('51', '0', 'admin', 'stat@用户列表', '4708', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:19');
INSERT INTO `sys_log` VALUES ('52', '0', 'admin', 'stat@用户列表', '7444', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:19');
INSERT INTO `sys_log` VALUES ('53', '0', 'admin', 'stat@用户列表', '5027', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('54', '0', 'admin', 'stat@用户列表', '4986', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('55', '0', 'admin', 'stat@用户列表', '5304', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('56', '0', 'admin', 'stat@用户列表', '5562', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('57', '0', 'admin', 'stat@用户列表', '5998', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('58', '0', 'admin', 'stat@用户列表', '4865', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('59', '0', 'admin', 'stat@用户列表', '5754', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('60', '0', 'admin', 'stat@用户列表', '4344', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:20');
INSERT INTO `sys_log` VALUES ('61', '0', 'admin', 'stat@用户列表', '4355', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:21');
INSERT INTO `sys_log` VALUES ('62', '0', 'admin', 'stat@权限列表', '5392', 'com.chao.cloud.admin.system.controller.RoleController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:28');
INSERT INTO `sys_log` VALUES ('63', '0', 'admin', 'stat@权限列表', '4689', 'com.chao.cloud.admin.system.controller.RoleController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:32');
INSERT INTO `sys_log` VALUES ('64', '0', 'admin', 'stat@权限列表', '6789', 'com.chao.cloud.admin.system.controller.RoleController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:37');
INSERT INTO `sys_log` VALUES ('65', '0', 'admin', 'stat@菜单列表', '20334', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:41');
INSERT INTO `sys_log` VALUES ('66', '0', 'admin', 'stat@菜单列表', '11148', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:42');
INSERT INTO `sys_log` VALUES ('67', '0', 'admin', 'stat@数据表', '3956', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:58');
INSERT INTO `sys_log` VALUES ('68', '0', 'admin', 'stat@数据表', '6992', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:59');
INSERT INTO `sys_log` VALUES ('69', '0', 'admin', 'stat@数据表', '4824', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:59');
INSERT INTO `sys_log` VALUES ('70', '0', 'admin', 'stat@数据表', '2796', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:31:59');
INSERT INTO `sys_log` VALUES ('71', '0', 'admin', 'stat@任务列表', '8626', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:01');
INSERT INTO `sys_log` VALUES ('72', '0', 'admin', 'stat@任务列表', '8447', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"jobName\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:01');
INSERT INTO `sys_log` VALUES ('73', '0', 'admin', 'stat@任务列表', '3426', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"jobName\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:01');
INSERT INTO `sys_log` VALUES ('74', '0', 'admin', 'stat@任务列表', '4951', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"jobName\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:01');
INSERT INTO `sys_log` VALUES ('75', '0', 'admin', 'stat@任务列表', '4939', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"jobName\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:02');
INSERT INTO `sys_log` VALUES ('76', '0', 'admin', 'stat@任务列表', '4091', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"jobName\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:02');
INSERT INTO `sys_log` VALUES ('77', '0', 'admin', 'stat@任务列表', '4420', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"jobName\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:02');
INSERT INTO `sys_log` VALUES ('78', '0', 'admin', 'stat@在线用户列表', '32', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:06');
INSERT INTO `sys_log` VALUES ('79', '0', 'admin', 'stat@在线用户列表', '63', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:07');
INSERT INTO `sys_log` VALUES ('80', '0', 'admin', 'stat@在线用户列表', '39', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:07');
INSERT INTO `sys_log` VALUES ('81', '0', 'admin', 'stat@在线用户列表', '38', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:07');
INSERT INTO `sys_log` VALUES ('82', '0', 'admin', 'stat@在线用户列表', '26', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:07');
INSERT INTO `sys_log` VALUES ('83', '0', 'admin', 'stat@在线用户列表', '26', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:07');
INSERT INTO `sys_log` VALUES ('84', '0', 'admin', 'stat@在线用户列表', '44', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:08');
INSERT INTO `sys_log` VALUES ('85', '0', 'admin', 'stat@在线用户列表', '26', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[\"\"]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:08');
INSERT INTO `sys_log` VALUES ('86', '0', 'admin', 'stat@用户列表', '5031', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:10');
INSERT INTO `sys_log` VALUES ('87', '0', 'admin', 'stat@用户列表', '5229', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:11');
INSERT INTO `sys_log` VALUES ('88', '0', 'admin', 'stat@用户列表', '4632', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:11');
INSERT INTO `sys_log` VALUES ('89', '0', 'admin', 'stat@用户列表', '8076', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\",\"username\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:11');
INSERT INTO `sys_log` VALUES ('90', '0', 'admin', 'stat@菜单列表', '3995', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:14');
INSERT INTO `sys_log` VALUES ('91', '0', 'admin', 'stat@部门列表', '4659', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:14');
INSERT INTO `sys_log` VALUES ('92', '0', 'admin', 'stat@部门列表', '4623', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:15');
INSERT INTO `sys_log` VALUES ('93', '0', 'admin', 'stat@部门列表', '1937', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:15');
INSERT INTO `sys_log` VALUES ('94', '0', 'admin', 'stat@部门列表', '1857', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:15');
INSERT INTO `sys_log` VALUES ('95', '0', 'admin', 'stat@部门列表', '2093', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:16');
INSERT INTO `sys_log` VALUES ('96', '0', 'admin', 'stat@部门列表', '1933', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:16');
INSERT INTO `sys_log` VALUES ('97', '0', 'admin', 'stat@部门列表', '1814', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:16');
INSERT INTO `sys_log` VALUES ('98', '0', 'admin', 'stat@菜单列表', '6630', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:17');
INSERT INTO `sys_log` VALUES ('99', '0', 'admin', 'stat@菜单列表', '3184', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:17');
INSERT INTO `sys_log` VALUES ('100', '0', 'admin', 'stat@菜单列表', '3213', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:17');
INSERT INTO `sys_log` VALUES ('101', '0', 'admin', 'stat@菜单列表', '3115', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:18');
INSERT INTO `sys_log` VALUES ('102', '0', 'admin', 'stat@菜单列表', '2925', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-17 16:32:18');
INSERT INTO `sys_log` VALUES ('103', '0', 'admin', '登录', '206457', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"v3cz\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 17:26:31');
INSERT INTO `sys_log` VALUES ('104', '0', 'admin', '登录', '177601', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"q3fd\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 17:31:57');
INSERT INTO `sys_log` VALUES ('105', '0', 'admin', '登录', '183836', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"xkjx\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 17:35:37');
INSERT INTO `sys_log` VALUES ('106', '0', 'admin', 'stat@在线用户列表', '48377', 'com.chao.cloud.admin.system.controller.SessionController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-17 17:45:57');
INSERT INTO `sys_log` VALUES ('107', '0', 'admin', 'stat@任务列表', '11250', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-17 17:52:25');
INSERT INTO `sys_log` VALUES ('108', '0', 'admin', '登录', '370706', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"dvzd\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-17 18:38:33');
INSERT INTO `sys_log` VALUES ('109', '0', 'admin', '登录', '563796', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"ooxd\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 14:09:59');
INSERT INTO `sys_log` VALUES ('110', '0', 'admin', 'stat@数据表', '27564', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 14:12:16');
INSERT INTO `sys_log` VALUES ('111', '0', 'admin', 'stat@部门列表', '7191', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 14:12:26');
INSERT INTO `sys_log` VALUES ('112', '0', 'admin', 'stat@部门列表', '3112', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 14:12:43');
INSERT INTO `sys_log` VALUES ('113', '0', 'admin', 'stat@部门列表', '2659', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 14:15:11');
INSERT INTO `sys_log` VALUES ('114', '0', 'admin', '登录', '227989', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"dpck\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 14:17:17');
INSERT INTO `sys_log` VALUES ('115', '0', 'admin', '登录', '244265', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"ojmz\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 14:23:21');
INSERT INTO `sys_log` VALUES ('116', '0', 'admin', '登录', '191733', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"l4p1\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 14:44:34');
INSERT INTO `sys_log` VALUES ('117', '0', 'admin', '登录', '213869', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"nv16\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 17:15:59');
INSERT INTO `sys_log` VALUES ('118', '0', 'admin', 'stat@数据表', '6384', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 17:16:04');
INSERT INTO `sys_log` VALUES ('119', '0', 'admin', 'stat@数据表', '6985', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 17:17:38');
INSERT INTO `sys_log` VALUES ('120', '0', 'admin', '登录', '185647', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"xzwk\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 17:30:21');
INSERT INTO `sys_log` VALUES ('121', '0', 'admin', 'stat@数据表', '10150', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 17:30:24');
INSERT INTO `sys_log` VALUES ('122', '0', 'admin', 'stat@数据表', '8977', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 17:42:46');
INSERT INTO `sys_log` VALUES ('123', '0', 'admin', 'stat@数据表', '6046', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 17:51:12');
INSERT INTO `sys_log` VALUES ('124', '0', 'admin', 'stat@数据表', '8294', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 18:00:58');
INSERT INTO `sys_log` VALUES ('125', '0', 'admin', '登录', '188444', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"0a6e\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 18:22:44');
INSERT INTO `sys_log` VALUES ('126', '0', 'admin', 'stat@数据表', '9467', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 18:22:47');
INSERT INTO `sys_log` VALUES ('127', '0', 'admin', '登录', '171003', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"ovgn\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 18:29:51');
INSERT INTO `sys_log` VALUES ('128', '0', 'admin', 'stat@数据表', '6612', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 18:29:54');
INSERT INTO `sys_log` VALUES ('129', '0', 'admin', '登录', '202075', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"ybf8\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 18:32:28');
INSERT INTO `sys_log` VALUES ('130', '0', 'admin', 'stat@数据表', '6485', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 18:32:30');
INSERT INTO `sys_log` VALUES ('131', '0', 'admin', '登录', '214325', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"goaw\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 19:38:56');
INSERT INTO `sys_log` VALUES ('132', '0', 'admin', 'stat@数据表', '6177', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 19:38:59');
INSERT INTO `sys_log` VALUES ('133', '0', 'admin', '登录', '183384', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"69l5\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 19:44:00');
INSERT INTO `sys_log` VALUES ('134', '0', 'admin', 'stat@数据表', '20019', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 19:44:02');
INSERT INTO `sys_log` VALUES ('135', '0', 'admin', '登录', '381523', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"a65h\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 19:50:47');
INSERT INTO `sys_log` VALUES ('136', '0', 'admin', 'stat@数据表', '6163', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 19:50:49');
INSERT INTO `sys_log` VALUES ('137', '0', 'admin', '登录', '191788', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"y58o\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 19:55:13');
INSERT INTO `sys_log` VALUES ('138', '0', 'admin', 'stat@数据表', '6438', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 19:55:16');
INSERT INTO `sys_log` VALUES ('139', '0', 'admin', '登录', '203793', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"4kdf\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 20:04:34');
INSERT INTO `sys_log` VALUES ('140', '0', 'admin', 'stat@数据表', '9907', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 20:04:36');
INSERT INTO `sys_log` VALUES ('141', '0', 'admin', '登录', '426051', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"dlld\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 20:17:13');
INSERT INTO `sys_log` VALUES ('142', '0', 'admin', 'stat@数据表', '7077', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 20:17:15');
INSERT INTO `sys_log` VALUES ('143', '0', 'admin', '登录', '227164', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"jnsz\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 20:27:13');
INSERT INTO `sys_log` VALUES ('144', '0', 'admin', 'stat@数据表', '5225', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-20 20:27:15');
INSERT INTO `sys_log` VALUES ('145', '0', 'admin', '登录', '197625', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"h9vc\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-20 20:31:32');
INSERT INTO `sys_log` VALUES ('146', '0', 'admin', '登录', '413454', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"stq7\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 09:47:43');
INSERT INTO `sys_log` VALUES ('147', '0', 'admin', 'stat@数据表', '44615', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 09:47:50');
INSERT INTO `sys_log` VALUES ('148', '0', 'admin', '登录', '199809', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"oqii\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 11:03:30');
INSERT INTO `sys_log` VALUES ('149', '0', 'admin', '登录', '216364', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"5y7s\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 11:17:41');
INSERT INTO `sys_log` VALUES ('150', '0', 'admin', '登录', '228432', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"likd\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 11:25:13');
INSERT INTO `sys_log` VALUES ('151', '0', 'admin', '登录', '30112', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"rxh3\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 12:08:52');
INSERT INTO `sys_log` VALUES ('152', '-1', '获取用户信息为空', '登录', '195', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"rtvn\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 12:11:17');
INSERT INTO `sys_log` VALUES ('153', '0', 'admin', '登录', '209191', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"4k75\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 12:11:31');
INSERT INTO `sys_log` VALUES ('154', '0', 'admin', '登录', '203193', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"7hkb\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 12:21:50');
INSERT INTO `sys_log` VALUES ('155', '0', 'admin', '登录', '403274', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"4bxo\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 14:43:36');
INSERT INTO `sys_log` VALUES ('156', '0', 'admin', 'stat@用户列表', '15821', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:06');
INSERT INTO `sys_log` VALUES ('157', '0', 'admin', 'stat@权限列表', '30838', 'com.chao.cloud.admin.system.controller.RoleController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:06');
INSERT INTO `sys_log` VALUES ('158', '0', 'admin', 'stat@菜单列表', '10509', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:08');
INSERT INTO `sys_log` VALUES ('159', '0', 'admin', '添加菜单', '8102', 'com.chao.cloud.admin.system.controller.MenuController.add()', '[{\"pId\":171,\"pName\":\"Echarts图表\"},171]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:10');
INSERT INTO `sys_log` VALUES ('160', '0', 'admin', '编辑角色', '13918', 'com.chao.cloud.admin.system.controller.RoleController.edit()', '[64,{\"role\":{\"roleId\":64,\"roleName\":\"测试4号\",\"remark\":\"1\",\"menuIds\":[91,92,57,27,30,29,28]}}]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:20');
INSERT INTO `sys_log` VALUES ('161', '0', 'admin', 'stat@菜单列表', '8717', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:20');
INSERT INTO `sys_log` VALUES ('162', '0', 'admin', '编辑用户', '22182', 'com.chao.cloud.admin.system.controller.UserController.edit()', '[{\"user\":{\"deptName\":\"测试1部\",\"password\":\"23d3e9a8eba3160054450a5851e8708c\",\"...\"remark\":\"测试\"},{\"roleId\":59,\"roleName\":\"研发管理员\",\"remark\":\"代码生成-计划任务\"}]},147]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:31');
INSERT INTO `sys_log` VALUES ('163', '0', 'admin', 'stat@部门列表', '11886', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 14:48:33');
INSERT INTO `sys_log` VALUES ('164', '0', 'admin', 'stat@数据表', '211817', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 15:27:23');
INSERT INTO `sys_log` VALUES ('165', '0', 'admin', '登录', '232462', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"px3h\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 16:54:43');
INSERT INTO `sys_log` VALUES ('166', '0', 'admin', '登录', '10246', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"yht9\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 17:25:42');
INSERT INTO `sys_log` VALUES ('167', '0', 'admin', 'stat@数据表', '13976', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:25:50');
INSERT INTO `sys_log` VALUES ('168', '0', 'admin', '登录', '576910', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"8m6q\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 17:30:54');
INSERT INTO `sys_log` VALUES ('169', '0', 'admin', 'stat@数据表', '5732', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:31:01');
INSERT INTO `sys_log` VALUES ('170', '0', 'admin', 'stat@数据表', '5375', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:32:07');
INSERT INTO `sys_log` VALUES ('171', '0', 'admin', 'stat@数据表', '29107', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:32:24');
INSERT INTO `sys_log` VALUES ('172', '0', 'admin', 'stat@数据表', '9879', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:33:42');
INSERT INTO `sys_log` VALUES ('173', '0', 'admin', 'stat@数据表', '19572', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:36:58');
INSERT INTO `sys_log` VALUES ('174', '0', 'admin', '登录', '194127', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"q069\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 17:39:33');
INSERT INTO `sys_log` VALUES ('175', '0', 'admin', 'stat@数据表', '8188', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:39:36');
INSERT INTO `sys_log` VALUES ('176', '0', 'admin', 'stat@数据表', '5856', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 17:46:12');
INSERT INTO `sys_log` VALUES ('177', '0', 'admin', '登录', '272893', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"r1x8\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 18:13:46');
INSERT INTO `sys_log` VALUES ('178', '0', 'admin', 'stat@数据表', '9762', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:13:49');
INSERT INTO `sys_log` VALUES ('179', '0', 'admin', 'stat@数据表', '5703', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:14:19');
INSERT INTO `sys_log` VALUES ('180', '0', 'admin', 'stat@数据表', '14984', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:15:05');
INSERT INTO `sys_log` VALUES ('181', '0', 'admin', 'stat@数据表', '5192', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:17:59');
INSERT INTO `sys_log` VALUES ('182', '0', 'admin', 'stat@数据表', '4600', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:18:18');
INSERT INTO `sys_log` VALUES ('183', '0', 'admin', 'stat@数据表', '12109', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:18:36');
INSERT INTO `sys_log` VALUES ('184', '0', 'admin', 'stat@数据表', '24351', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:20:24');
INSERT INTO `sys_log` VALUES ('185', '0', 'admin', '请求更改用户密码', '34', 'com.chao.cloud.admin.system.controller.UserController.resetPwd()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:20:51');
INSERT INTO `sys_log` VALUES ('186', '0', 'admin', 'stat@数据表', '5329', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:21:31');
INSERT INTO `sys_log` VALUES ('187', '0', 'admin', 'stat@数据表', '6161', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:21:51');
INSERT INTO `sys_log` VALUES ('188', '0', 'admin', 'stat@数据表', '4539', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:22:09');
INSERT INTO `sys_log` VALUES ('189', '0', 'admin', 'stat@数据表', '3922', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:24:00');
INSERT INTO `sys_log` VALUES ('190', '0', 'admin', 'stat@任务列表', '9392', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-21 18:29:58');
INSERT INTO `sys_log` VALUES ('191', '0', 'admin', 'stat@部门列表', '22457', 'com.chao.cloud.admin.system.controller.DeptController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:30:08');
INSERT INTO `sys_log` VALUES ('192', '0', 'admin', 'stat@菜单列表', '8661', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-21 18:30:10');
INSERT INTO `sys_log` VALUES ('193', '0', 'admin', 'stat@权限列表', '13647', 'com.chao.cloud.admin.system.controller.RoleController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-21 18:30:12');
INSERT INTO `sys_log` VALUES ('194', '0', 'admin', 'stat@用户列表', '22759', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-21 18:30:20');
INSERT INTO `sys_log` VALUES ('195', '0', 'admin', '登录', '189587', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"nsce\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 18:56:09');
INSERT INTO `sys_log` VALUES ('196', '0', 'admin', 'stat@数据表', '6758', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 18:56:11');
INSERT INTO `sys_log` VALUES ('197', '0', 'admin', '登录', '195136', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"lfum\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 19:05:37');
INSERT INTO `sys_log` VALUES ('198', '0', 'admin', 'stat@数据表', '5604', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 19:05:39');
INSERT INTO `sys_log` VALUES ('199', '0', 'admin', '登录', '17644', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"1bes\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-21 19:22:15');
INSERT INTO `sys_log` VALUES ('200', '0', 'admin', 'stat@数据表', '6407', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-21 19:22:19');
INSERT INTO `sys_log` VALUES ('201', '0', 'admin', '登录', '543325', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"lqkd\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 10:36:10');
INSERT INTO `sys_log` VALUES ('202', '0', 'admin', 'stat@数据表', '9575', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-22 10:36:12');
INSERT INTO `sys_log` VALUES ('203', '0', 'admin', '登录', '204443', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"jlfu\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 11:04:50');
INSERT INTO `sys_log` VALUES ('204', '0', 'admin', 'stat@数据表', '5900', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-22 11:04:52');
INSERT INTO `sys_log` VALUES ('205', '0', 'admin', 'stat@任务列表', '16397', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-22 11:24:23');
INSERT INTO `sys_log` VALUES ('206', '0', 'admin', '登录', '191340', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"qj3e\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 11:27:26');
INSERT INTO `sys_log` VALUES ('207', '0', 'admin', 'stat@数据表', '6961', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-22 11:27:29');
INSERT INTO `sys_log` VALUES ('208', '0', 'admin', '登录', '468224', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"i8gz\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 18:09:50');
INSERT INTO `sys_log` VALUES ('209', '0', 'admin', 'stat@数据表', '51413', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-22 18:09:53');
INSERT INTO `sys_log` VALUES ('210', '0', 'admin', '登录', '22938', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"2c5c\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 18:44:47');
INSERT INTO `sys_log` VALUES ('211', '0', 'admin', 'stat@数据表', '4945', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-22 18:44:50');
INSERT INTO `sys_log` VALUES ('212', '0', 'admin', '登录', '184920', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"p94s\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:02:49');
INSERT INTO `sys_log` VALUES ('213', '0', 'admin', 'stat@用户列表', '19866', 'com.chao.cloud.admin.system.controller.UserController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:02:52');
INSERT INTO `sys_log` VALUES ('214', '0', 'admin', 'stat@菜单列表', '12933', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:03:00');
INSERT INTO `sys_log` VALUES ('215', '0', 'admin', '添加菜单', '63', 'com.chao.cloud.admin.system.controller.MenuController.add()', '[{\"pId\":0,\"pName\":\"根目录\"},0]', '0:0:0:0:0:0:0:1', '2019-05-22 19:04:25');
INSERT INTO `sys_log` VALUES ('216', '0', 'admin', '保存菜单', '40758', 'com.chao.cloud.admin.system.controller.MenuController.save()', '[{\"name\":\"测试目录\",\"icon\":\"layui-icon layui-icon-login-qq\",\"menuId\":173,\"orderNum\":1,\"perms\":\"\",\"type\":1,\"parentId\":0,\"url\":\"\"}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:04:51');
INSERT INTO `sys_log` VALUES ('217', '0', 'admin', 'stat@菜单列表', '8553', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:04:52');
INSERT INTO `sys_log` VALUES ('218', '0', 'admin', 'stat@菜单列表', '7682', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:06:47');
INSERT INTO `sys_log` VALUES ('219', '0', 'admin', '编辑菜单', '9538', 'com.chao.cloud.admin.system.controller.MenuController.edit()', '[{\"pId\":0,\"pName\":\"根目录\",\"menu\":{\"name\":\"测试目录\",\"icon\":\"layui-icon layui-icon...,\"menuId\":173,\"orderNum\":1,\"perms\":\"\",\"type\":1,\"parentId\":0,\"url\":\"\"}},173]', '0:0:0:0:0:0:0:1', '2019-05-22 19:06:49');
INSERT INTO `sys_log` VALUES ('220', '0', 'admin', '更新菜单', '64951', 'com.chao.cloud.admin.system.controller.MenuController.update()', '[{\"name\":\"测试目录\",\"icon\":\"layui-icon layui-icon-login-qq\",\"menuId\":173,\"orderNum\":1,\"perms\":\"\",\"type\":1,\"parentId\":0,\"url\":\"chao/config\"}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:07:02');
INSERT INTO `sys_log` VALUES ('221', '0', 'admin', 'stat@菜单列表', '5922', 'com.chao.cloud.admin.system.controller.MenuController.list()', '[{}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:07:02');
INSERT INTO `sys_log` VALUES ('222', '0', 'admin', '登录', '199526', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"c0gh\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 19:38:45');
INSERT INTO `sys_log` VALUES ('223', '0', 'admin', 'stat@数据表', '8369', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-22 19:38:48');
INSERT INTO `sys_log` VALUES ('224', '0', 'admin', '登录', '198063', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"0ji7\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 20:01:14');
INSERT INTO `sys_log` VALUES ('225', '0', 'admin', '登录', '196969', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"fx01\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 20:06:42');
INSERT INTO `sys_log` VALUES ('226', '0', 'admin', '登录', '194710', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"k7mc\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 20:28:08');
INSERT INTO `sys_log` VALUES ('227', '0', 'admin', '登录', '214700', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"8d1y\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 20:33:02');
INSERT INTO `sys_log` VALUES ('228', '0', 'admin', '登录', '212725', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"bbjf\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-22 20:36:43');
INSERT INTO `sys_log` VALUES ('229', '0', 'admin', '登录', '2101739', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"b2dy\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 09:49:00');
INSERT INTO `sys_log` VALUES ('230', '0', 'admin', 'stat@数据表', '50253', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-23 09:50:13');
INSERT INTO `sys_log` VALUES ('231', '0', 'admin', 'stat@数据表', '15310', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[\"啊\"]', '0:0:0:0:0:0:0:1', '2019-05-23 09:50:15');
INSERT INTO `sys_log` VALUES ('232', '0', 'admin', 'stat@数据表', '13172', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[\"啊\"]', '0:0:0:0:0:0:0:1', '2019-05-23 09:50:28');
INSERT INTO `sys_log` VALUES ('233', '0', 'admin', '登录', '233426', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"tn2r\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 10:59:31');
INSERT INTO `sys_log` VALUES ('234', '0', 'admin', '登录', '222347', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"3wf0\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 12:11:28');
INSERT INTO `sys_log` VALUES ('235', '0', 'admin', '登录', '204928', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"r8wn\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 12:16:34');
INSERT INTO `sys_log` VALUES ('236', '0', 'admin', 'stat@数据表', '5655', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-23 12:17:45');
INSERT INTO `sys_log` VALUES ('237', '0', 'admin', '登录', '183988', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"7zeq\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 12:25:20');
INSERT INTO `sys_log` VALUES ('238', '0', 'admin', '登录', '196188', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"uiub\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 13:21:22');
INSERT INTO `sys_log` VALUES ('239', '0', 'admin', 'stat@数据表', '10260', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-23 13:39:40');
INSERT INTO `sys_log` VALUES ('240', '0', 'admin', '登录', '119246', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"xkga\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 13:59:43');
INSERT INTO `sys_log` VALUES ('241', '0', 'admin', '登录', '562786', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"74bt\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 16:11:18');
INSERT INTO `sys_log` VALUES ('242', '0', 'admin', '登录', '216212', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"z65k\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 16:22:49');
INSERT INTO `sys_log` VALUES ('243', '0', 'admin', '登录', '212104', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"eryi\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 16:37:18');
INSERT INTO `sys_log` VALUES ('244', '0', 'admin', '登录', '159030', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"ulix\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 17:01:53');
INSERT INTO `sys_log` VALUES ('245', '0', 'admin', '登录', '204288', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"2pvq\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 17:07:08');
INSERT INTO `sys_log` VALUES ('246', '0', 'admin', '登录', '120776', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"s19s\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 17:12:13');
INSERT INTO `sys_log` VALUES ('247', '0', 'admin', '登录', '260810', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"mq3z\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-23 17:22:59');
INSERT INTO `sys_log` VALUES ('248', '0', 'admin', '登录', '466760', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"dfc9\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-24 10:23:16');
INSERT INTO `sys_log` VALUES ('249', '0', 'admin', '登录', '168338', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"8wsh\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-24 11:04:08');
INSERT INTO `sys_log` VALUES ('250', '0', 'admin', '登录', '337805', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"xb4z\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-24 14:47:10');
INSERT INTO `sys_log` VALUES ('251', '0', 'admin', 'stat@数据表', '217853', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-24 14:47:19');
INSERT INTO `sys_log` VALUES ('252', '0', 'admin', '登录', '169998', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"19mp\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-24 17:39:35');
INSERT INTO `sys_log` VALUES ('253', '0', 'admin', 'stat@数据表', '7250', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-24 17:39:39');
INSERT INTO `sys_log` VALUES ('254', '0', 'admin', 'stat@数据表', '6175', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-24 17:53:48');
INSERT INTO `sys_log` VALUES ('255', '0', 'admin', '登录', '166674', 'com.chao.cloud.admin.system.controller.LoginController.ajaxLogin()', '[\"admin\",\"123456\",\"gg8m\",{\"request\":{},\"servletContext\":{},\"session\":{\"servletContext\":{},\"session\":{}},\"httpSessions\":false}]', '0:0:0:0:0:0:0:1', '2019-05-24 18:53:59');
INSERT INTO `sys_log` VALUES ('256', '0', 'admin', 'stat@数据表', '6452', 'com.chao.cloud.admin.system.controller.GeneratorController.list()', '[]', '0:0:0:0:0:0:0:1', '2019-05-24 18:54:03');
INSERT INTO `sys_log` VALUES ('257', '0', 'admin', 'stat@任务列表', '8362', 'com.chao.cloud.admin.system.controller.JobController.list()', '[{\"page\":\"1\",\"limit\":\"10\"}]', '0:0:0:0:0:0:0:1', '2019-05-24 18:54:03');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=utf8 COMMENT='菜单管理';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('2', '3', '系统菜单', 'sys/menu/', 'sys:menu:menu', '1', 'layui-icon layui-icon-senior', '2', '2017-08-09 22:55:15', null);
INSERT INTO `sys_menu` VALUES ('3', '0', '系统管理', '', '', '0', 'layui-icon layui-icon-set-sm', '1', '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `sys_menu` VALUES ('6', '3', '用户管理', 'sys/user/', 'sys:user:user', '1', 'layui-icon layui-icon-user', '0', '2017-08-10 14:12:11', null);
INSERT INTO `sys_menu` VALUES ('7', '3', '角色管理', 'sys/role', 'sys:role:role', '1', 'layui-icon layui-icon-friends', '1', '2017-08-10 14:13:19', null);
INSERT INTO `sys_menu` VALUES ('12', '6', '新增', '', 'sys:user:add', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 10:51:35', null);
INSERT INTO `sys_menu` VALUES ('13', '6', '编辑', '', 'sys:user:edit', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 10:52:06', null);
INSERT INTO `sys_menu` VALUES ('14', '6', '删除', null, 'sys:user:remove', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 10:52:24', null);
INSERT INTO `sys_menu` VALUES ('15', '7', '新增', '', 'sys:role:add', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 10:56:37', null);
INSERT INTO `sys_menu` VALUES ('20', '2', '新增', '', 'sys:menu:add', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 10:59:32', null);
INSERT INTO `sys_menu` VALUES ('21', '2', '编辑', '', 'sys:menu:edit', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 10:59:56', null);
INSERT INTO `sys_menu` VALUES ('22', '2', '删除', '', 'sys:menu:remove', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 11:00:26', null);
INSERT INTO `sys_menu` VALUES ('24', '6', '批量删除', '', 'sys:user:batchRemove', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 17:27:18', null);
INSERT INTO `sys_menu` VALUES ('25', '6', '停用', null, 'sys:user:disable', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 17:27:43', null);
INSERT INTO `sys_menu` VALUES ('26', '6', '重置密码', '', 'sys:user:resetPwd', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 17:28:34', null);
INSERT INTO `sys_menu` VALUES ('27', '91', '系统日志', 'sys/log', 'sys:log', '1', 'layui-icon layui-icon-list', '0', '2017-08-14 22:11:53', null);
INSERT INTO `sys_menu` VALUES ('28', '27', '刷新', null, 'sys:log:list', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 22:30:22', null);
INSERT INTO `sys_menu` VALUES ('29', '27', '删除', null, 'sys:log:remove', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 22:30:43', null);
INSERT INTO `sys_menu` VALUES ('30', '27', '清空', null, 'sys:log:clear', '2', 'layui-icon layui-icon-rate-half', '0', '2017-08-14 22:31:02', null);
INSERT INTO `sys_menu` VALUES ('48', '77', '代码生成', 'sys/generator', 'sys:generator', '1', 'layui-icon layui-icon-fonts-code', '3', null, null);
INSERT INTO `sys_menu` VALUES ('55', '7', '编辑', '', 'sys:role:edit', '2', 'layui-icon layui-icon-rate-half', null, null, null);
INSERT INTO `sys_menu` VALUES ('56', '7', '删除', '', 'sys:role:remove', '2', 'layui-icon layui-icon-rate-half', null, null, null);
INSERT INTO `sys_menu` VALUES ('57', '91', '运行监控', '/druid/index.html', '', '1', 'layui-icon layui-icon-vercode', '1', null, null);
INSERT INTO `sys_menu` VALUES ('61', '2', '批量删除', '', 'sys:menu:batchRemove', '2', 'layui-icon layui-icon-rate-half', null, null, null);
INSERT INTO `sys_menu` VALUES ('62', '7', '批量删除', '', 'sys:role:batchRemove', '2', 'layui-icon layui-icon-rate-half', null, null, null);
INSERT INTO `sys_menu` VALUES ('72', '77', '计划任务', 'sys/job', 'sys:taskScheduleJob', '1', 'layui-icon layui-icon-table', '4', null, null);
INSERT INTO `sys_menu` VALUES ('73', '3', '部门管理', '/sys/dept', 'system:sysDept:sysDept', '1', 'layui-icon layui-icon-group', '3', null, null);
INSERT INTO `sys_menu` VALUES ('74', '73', '增加', '/system/sysDept/add', 'system:sysDept:add', '2', 'layui-icon layui-icon-rate-half', '1', null, null);
INSERT INTO `sys_menu` VALUES ('75', '73', '刪除', 'system/sysDept/remove', 'system:sysDept:remove', '2', 'layui-icon layui-icon-rate-half', '2', null, null);
INSERT INTO `sys_menu` VALUES ('76', '73', '编辑', '/system/sysDept/edit', 'system:sysDept:edit', '2', 'layui-icon layui-icon-rate-half', '3', null, null);
INSERT INTO `sys_menu` VALUES ('77', '0', '系统工具', '', '', '0', 'layui-icon layui-icon-util', '3', null, null);
INSERT INTO `sys_menu` VALUES ('91', '0', '系统监控', '', '', '0', 'layui-icon layui-icon-video', '2', null, null);
INSERT INTO `sys_menu` VALUES ('92', '91', '在线用户', 'sys/online', '', '1', 'layui-icon layui-icon-username', '1', null, null);
INSERT INTO `sys_menu` VALUES ('171', '0', 'Echarts图表', '', '', '0', 'layui-icon layui-icon-chart', '5', null, null);
INSERT INTO `sys_menu` VALUES ('172', '171', '3D地球', '/echarts/world/world.html', '', '1', 'layui-icon layui-icon-website', '1', null, null);
INSERT INTO `sys_menu` VALUES ('173', '0', '测试目录', 'chao/config', '', '1', 'layui-icon layui-icon-login-qq', '1', null, null);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `role_sign` varchar(100) DEFAULT NULL COMMENT '角色标识',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `user_id_create` bigint(255) DEFAULT NULL COMMENT '创建用户id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8 COMMENT='角色';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('59', '研发管理员', null, '代码生成-计划任务', null, null, null);
INSERT INTO `sys_role` VALUES ('61', '研发1号', null, '测试', null, null, null);
INSERT INTO `sys_role` VALUES ('62', '测试1号', null, '1', null, null, null);
INSERT INTO `sys_role` VALUES ('63', '测试3号', null, '1', null, null, null);
INSERT INTO `sys_role` VALUES ('64', '测试4号', null, '1', null, null, null);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3369 DEFAULT CHARSET=utf8 COMMENT='角色与菜单对应关系';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('3180', '60', '130');
INSERT INTO `sys_role_menu` VALUES ('3181', '60', '129');
INSERT INTO `sys_role_menu` VALUES ('3182', '60', '128');
INSERT INTO `sys_role_menu` VALUES ('3183', '60', '127');
INSERT INTO `sys_role_menu` VALUES ('3184', '60', '126');
INSERT INTO `sys_role_menu` VALUES ('3185', '60', '124');
INSERT INTO `sys_role_menu` VALUES ('3186', '60', '122');
INSERT INTO `sys_role_menu` VALUES ('3187', '60', '121');
INSERT INTO `sys_role_menu` VALUES ('3188', '60', '111');
INSERT INTO `sys_role_menu` VALUES ('3189', '60', '110');
INSERT INTO `sys_role_menu` VALUES ('3190', '60', '109');
INSERT INTO `sys_role_menu` VALUES ('3191', '60', '108');
INSERT INTO `sys_role_menu` VALUES ('3192', '60', '76');
INSERT INTO `sys_role_menu` VALUES ('3193', '60', '75');
INSERT INTO `sys_role_menu` VALUES ('3194', '60', '74');
INSERT INTO `sys_role_menu` VALUES ('3195', '60', '62');
INSERT INTO `sys_role_menu` VALUES ('3196', '60', '56');
INSERT INTO `sys_role_menu` VALUES ('3197', '60', '55');
INSERT INTO `sys_role_menu` VALUES ('3198', '60', '15');
INSERT INTO `sys_role_menu` VALUES ('3199', '60', '26');
INSERT INTO `sys_role_menu` VALUES ('3200', '60', '25');
INSERT INTO `sys_role_menu` VALUES ('3201', '60', '24');
INSERT INTO `sys_role_menu` VALUES ('3202', '60', '14');
INSERT INTO `sys_role_menu` VALUES ('3203', '60', '13');
INSERT INTO `sys_role_menu` VALUES ('3204', '60', '12');
INSERT INTO `sys_role_menu` VALUES ('3205', '60', '123');
INSERT INTO `sys_role_menu` VALUES ('3206', '60', '107');
INSERT INTO `sys_role_menu` VALUES ('3207', '60', '73');
INSERT INTO `sys_role_menu` VALUES ('3208', '60', '7');
INSERT INTO `sys_role_menu` VALUES ('3209', '60', '6');
INSERT INTO `sys_role_menu` VALUES ('3210', '60', '156');
INSERT INTO `sys_role_menu` VALUES ('3211', '60', '155');
INSERT INTO `sys_role_menu` VALUES ('3212', '60', '113');
INSERT INTO `sys_role_menu` VALUES ('3213', '60', '154');
INSERT INTO `sys_role_menu` VALUES ('3214', '60', '153');
INSERT INTO `sys_role_menu` VALUES ('3215', '60', '152');
INSERT INTO `sys_role_menu` VALUES ('3216', '60', '150');
INSERT INTO `sys_role_menu` VALUES ('3217', '60', '149');
INSERT INTO `sys_role_menu` VALUES ('3218', '60', '148');
INSERT INTO `sys_role_menu` VALUES ('3219', '60', '147');
INSERT INTO `sys_role_menu` VALUES ('3220', '60', '146');
INSERT INTO `sys_role_menu` VALUES ('3221', '60', '145');
INSERT INTO `sys_role_menu` VALUES ('3222', '60', '144');
INSERT INTO `sys_role_menu` VALUES ('3223', '60', '143');
INSERT INTO `sys_role_menu` VALUES ('3224', '60', '112');
INSERT INTO `sys_role_menu` VALUES ('3225', '60', '131');
INSERT INTO `sys_role_menu` VALUES ('3226', '60', '164');
INSERT INTO `sys_role_menu` VALUES ('3227', '60', '163');
INSERT INTO `sys_role_menu` VALUES ('3228', '60', '162');
INSERT INTO `sys_role_menu` VALUES ('3229', '60', '161');
INSERT INTO `sys_role_menu` VALUES ('3230', '60', '160');
INSERT INTO `sys_role_menu` VALUES ('3231', '60', '159');
INSERT INTO `sys_role_menu` VALUES ('3232', '60', '142');
INSERT INTO `sys_role_menu` VALUES ('3233', '60', '141');
INSERT INTO `sys_role_menu` VALUES ('3234', '60', '140');
INSERT INTO `sys_role_menu` VALUES ('3235', '60', '139');
INSERT INTO `sys_role_menu` VALUES ('3236', '60', '138');
INSERT INTO `sys_role_menu` VALUES ('3237', '60', '137');
INSERT INTO `sys_role_menu` VALUES ('3238', '60', '151');
INSERT INTO `sys_role_menu` VALUES ('3239', '60', '135');
INSERT INTO `sys_role_menu` VALUES ('3240', '60', '134');
INSERT INTO `sys_role_menu` VALUES ('3241', '60', '133');
INSERT INTO `sys_role_menu` VALUES ('3242', '60', '158');
INSERT INTO `sys_role_menu` VALUES ('3243', '60', '157');
INSERT INTO `sys_role_menu` VALUES ('3244', '60', '136');
INSERT INTO `sys_role_menu` VALUES ('3245', '60', '132');
INSERT INTO `sys_role_menu` VALUES ('3246', '60', '-1');
INSERT INTO `sys_role_menu` VALUES ('3247', '60', '102');
INSERT INTO `sys_role_menu` VALUES ('3248', '60', '3');
INSERT INTO `sys_role_menu` VALUES ('3249', '61', '91');
INSERT INTO `sys_role_menu` VALUES ('3250', '61', '92');
INSERT INTO `sys_role_menu` VALUES ('3251', '61', '57');
INSERT INTO `sys_role_menu` VALUES ('3252', '61', '27');
INSERT INTO `sys_role_menu` VALUES ('3253', '61', '30');
INSERT INTO `sys_role_menu` VALUES ('3254', '61', '29');
INSERT INTO `sys_role_menu` VALUES ('3255', '61', '28');
INSERT INTO `sys_role_menu` VALUES ('3256', '62', '91');
INSERT INTO `sys_role_menu` VALUES ('3257', '62', '92');
INSERT INTO `sys_role_menu` VALUES ('3258', '62', '57');
INSERT INTO `sys_role_menu` VALUES ('3259', '62', '27');
INSERT INTO `sys_role_menu` VALUES ('3260', '62', '30');
INSERT INTO `sys_role_menu` VALUES ('3261', '62', '29');
INSERT INTO `sys_role_menu` VALUES ('3262', '62', '28');
INSERT INTO `sys_role_menu` VALUES ('3263', '62', '77');
INSERT INTO `sys_role_menu` VALUES ('3264', '62', '72');
INSERT INTO `sys_role_menu` VALUES ('3265', '62', '48');
INSERT INTO `sys_role_menu` VALUES ('3266', '62', '3');
INSERT INTO `sys_role_menu` VALUES ('3267', '62', '73');
INSERT INTO `sys_role_menu` VALUES ('3268', '62', '76');
INSERT INTO `sys_role_menu` VALUES ('3269', '62', '75');
INSERT INTO `sys_role_menu` VALUES ('3270', '62', '74');
INSERT INTO `sys_role_menu` VALUES ('3271', '62', '7');
INSERT INTO `sys_role_menu` VALUES ('3272', '62', '62');
INSERT INTO `sys_role_menu` VALUES ('3273', '62', '56');
INSERT INTO `sys_role_menu` VALUES ('3274', '62', '55');
INSERT INTO `sys_role_menu` VALUES ('3275', '62', '15');
INSERT INTO `sys_role_menu` VALUES ('3276', '62', '6');
INSERT INTO `sys_role_menu` VALUES ('3277', '62', '26');
INSERT INTO `sys_role_menu` VALUES ('3278', '62', '25');
INSERT INTO `sys_role_menu` VALUES ('3279', '62', '24');
INSERT INTO `sys_role_menu` VALUES ('3280', '62', '14');
INSERT INTO `sys_role_menu` VALUES ('3281', '62', '13');
INSERT INTO `sys_role_menu` VALUES ('3282', '62', '12');
INSERT INTO `sys_role_menu` VALUES ('3283', '62', '2');
INSERT INTO `sys_role_menu` VALUES ('3284', '62', '61');
INSERT INTO `sys_role_menu` VALUES ('3285', '62', '22');
INSERT INTO `sys_role_menu` VALUES ('3286', '62', '21');
INSERT INTO `sys_role_menu` VALUES ('3287', '62', '20');
INSERT INTO `sys_role_menu` VALUES ('3327', '59', '91');
INSERT INTO `sys_role_menu` VALUES ('3328', '59', '92');
INSERT INTO `sys_role_menu` VALUES ('3329', '59', '57');
INSERT INTO `sys_role_menu` VALUES ('3330', '59', '27');
INSERT INTO `sys_role_menu` VALUES ('3331', '59', '30');
INSERT INTO `sys_role_menu` VALUES ('3332', '59', '29');
INSERT INTO `sys_role_menu` VALUES ('3333', '59', '28');
INSERT INTO `sys_role_menu` VALUES ('3334', '59', '77');
INSERT INTO `sys_role_menu` VALUES ('3335', '59', '72');
INSERT INTO `sys_role_menu` VALUES ('3336', '59', '48');
INSERT INTO `sys_role_menu` VALUES ('3362', '64', '91');
INSERT INTO `sys_role_menu` VALUES ('3363', '64', '92');
INSERT INTO `sys_role_menu` VALUES ('3364', '64', '57');
INSERT INTO `sys_role_menu` VALUES ('3365', '64', '27');
INSERT INTO `sys_role_menu` VALUES ('3366', '64', '30');
INSERT INTO `sys_role_menu` VALUES ('3367', '64', '29');
INSERT INTO `sys_role_menu` VALUES ('3368', '64', '28');

-- ----------------------------
-- Table structure for sys_task
-- ----------------------------
DROP TABLE IF EXISTS `sys_task`;
CREATE TABLE `sys_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cron_expression` varchar(255) DEFAULT NULL COMMENT 'cron表达式',
  `method_name` varchar(255) DEFAULT NULL COMMENT '任务调用的方法名',
  `is_concurrent` varchar(255) DEFAULT NULL COMMENT '任务是否有状态',
  `description` varchar(255) DEFAULT NULL COMMENT '任务描述',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `bean_class` varchar(255) DEFAULT NULL COMMENT '任务执行时调用哪个类的方法 包名+类名',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `job_status` varchar(255) DEFAULT NULL COMMENT '任务状态',
  `job_group` varchar(255) DEFAULT NULL COMMENT '任务分组',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `spring_bean` varchar(255) DEFAULT NULL COMMENT 'Spring bean',
  `job_name` varchar(255) DEFAULT NULL COMMENT '任务名',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_task
-- ----------------------------
INSERT INTO `sys_task` VALUES ('2', '0/5 * * * * ?', 'run1', '1', '测试', '4028ea815a3d2a8c015a3d2f8d2a0002', 'com.chao.cloud.admin.system.task.WelcomeJob', '2017-05-19 18:30:56', '0', 'group1', '2017-05-19 18:31:07', null, '', 'welcomJob');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `name` varchar(100) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `dept_id` bigint(20) DEFAULT NULL,
  `dept_name` varchar(50) DEFAULT '',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) DEFAULT 1 COMMENT '0.禁用1.正常',
  `create_time` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('0', 'admin', '超级管理员', 'a66abb5684c45962d887564f08346e8d', '0', '顶级', 'admin@example.com', '17699999999', '1', '2019-05-10 15:52:03');
INSERT INTO `sys_user` VALUES ('146', 'xuechao', '薛超', 'c5881e3ceb092c3aebeb4153cf017914', '0', '顶级', 'asdada@qq.com', '15711066461', '1', '2019-05-10 15:52:05');
INSERT INTO `sys_user` VALUES ('147', 'chaojunzi', '超君子', '23d3e9a8eba3160054450a5851e8708c', '20', '测试1部', '15215@qq.com', '15711066462', '1', '2019-05-10 21:15:49');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8 COMMENT='用户与角色对应关系';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('73', '30', '48');
INSERT INTO `sys_user_role` VALUES ('74', '30', '49');
INSERT INTO `sys_user_role` VALUES ('75', '30', '50');
INSERT INTO `sys_user_role` VALUES ('76', '31', '48');
INSERT INTO `sys_user_role` VALUES ('77', '31', '49');
INSERT INTO `sys_user_role` VALUES ('78', '31', '52');
INSERT INTO `sys_user_role` VALUES ('79', '32', '48');
INSERT INTO `sys_user_role` VALUES ('80', '32', '49');
INSERT INTO `sys_user_role` VALUES ('81', '32', '50');
INSERT INTO `sys_user_role` VALUES ('82', '32', '51');
INSERT INTO `sys_user_role` VALUES ('83', '32', '52');
INSERT INTO `sys_user_role` VALUES ('84', '33', '38');
INSERT INTO `sys_user_role` VALUES ('85', '33', '49');
INSERT INTO `sys_user_role` VALUES ('86', '33', '52');
INSERT INTO `sys_user_role` VALUES ('87', '34', '50');
INSERT INTO `sys_user_role` VALUES ('88', '34', '51');
INSERT INTO `sys_user_role` VALUES ('89', '34', '52');
INSERT INTO `sys_user_role` VALUES ('124', null, '48');
INSERT INTO `sys_user_role` VALUES ('144', '146', '64');
INSERT INTO `sys_user_role` VALUES ('145', '146', '61');
INSERT INTO `sys_user_role` VALUES ('150', null, '64');
INSERT INTO `sys_user_role` VALUES ('151', null, '64');
INSERT INTO `sys_user_role` VALUES ('154', '147', '61');
