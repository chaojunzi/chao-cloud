/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : admin

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-05-16 17:08:21
*/

SET FOREIGN_KEY_CHECKS=0;

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
INSERT INTO `sys_dept` VALUES ('9', '0', '市场部', '2', '1');
INSERT INTO `sys_dept` VALUES ('11', '0', '运营部', '3', '1');
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='系统日志';

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
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=utf8 COMMENT='菜单管理';

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
INSERT INTO `sys_menu` VALUES ('57', '91', '运行监控', '/druid/index.html', '', '1', 'layui-icon layui-icon-chart', '1', null, null);
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
