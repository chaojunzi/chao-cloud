package com.chao.cloud.generator.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chao.cloud.common.config.web.HealthController;
import com.chao.cloud.common.config.web.HealthController.CoreParam;
import com.chao.cloud.common.util.EntityUtil;
import com.chao.cloud.generator.constant.XcConstant;
import com.chao.cloud.generator.dal.entity.XcConfig;
import com.chao.cloud.generator.dal.entity.XcGroup;
import com.chao.cloud.generator.domain.dto.LoginDTO;
import com.chao.cloud.generator.domain.dto.MenuDTO;
import com.chao.cloud.generator.service.XcConfigService;
import com.chao.cloud.generator.service.XcGroupService;

import cn.hutool.core.collection.CollUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-07-23
 * @version 1.0.0
 */
@RequestMapping
@Controller
@Validated
public class IndexController extends BaseController {
	@Autowired
	private XcConfigService xcConfigService;
	@Autowired
	private XcGroupService xcGroupService;

	/**
	 * 主页面
	 * @return
	 */
	@RequestMapping("main")
	String main(Model m) {
		CoreParam core = HealthController.coreParam();
		m.addAttribute("core", core);
		return "main";
	}

	/**
	 * 首页
	 * @param m
	 * @param session
	 * @return
	 */
	@RequestMapping({ "", "/", "/index" })
	String generator(Model m, HttpSession session) {
		List<XcGroup> groups = xcGroupService.list();
		List<MenuDTO> list = EntityUtil.listConver(groups, MenuDTO.class);
		if (CollUtil.isNotEmpty(list)) {
			List<XcConfig> configs = xcConfigService.list();
			// 分组转换
			if (CollUtil.isNotEmpty(configs)) {
				Map<Integer, List<XcConfig>> map = configs.stream()
						.collect(Collectors.groupingBy(XcConfig::getGroupId));
				// 遍历list
				list.forEach(l -> l.setConfigs(EntityUtil.listConver(map.get(l.getId()), MenuDTO.Config.class)));
			}
		}
		m.addAttribute("menus", list);
		// 判断是否为管理员
		LoginDTO user = getUser(session);
		if (XcConstant.ADMIN_USER_NAME.contains(user.getUserName())) {
			m.addAttribute("admin_menu", XcConstant.ADMIN_MENU);
		}
		return "index";
	}

}