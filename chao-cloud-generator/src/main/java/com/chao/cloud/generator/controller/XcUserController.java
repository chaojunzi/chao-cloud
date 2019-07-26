package com.chao.cloud.generator.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuEnum;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;
import com.chao.cloud.common.extra.token.annotation.FormToken;
import com.chao.cloud.generator.dal.entity.XcUser;
import com.chao.cloud.generator.service.XcUserService;

import cn.hutool.core.lang.Assert;

/**
 * @功能：
 * @author： 薛超
 * @时间：2019-07-25
 * @version 1.0.0
 */
@RequestMapping("/xc/user")
@Controller
@Validated
@MenuMapping
public class XcUserController extends BaseController {

	@Autowired
	private XcUserService xcUserService;

	@MenuMapping(value = "${menuTitle}", type = MenuEnum.MENU)
	@RequestMapping
	public String list() {
		return "xc/user/list";
	}

	@RequestMapping("/list")
	@ResponseBody
	public Response<IPage<XcUser>> list(Page<XcUser> page //
	) { // 分页
		LambdaQueryWrapper<XcUser> queryWrapper = Wrappers.lambdaQuery();
		return Response.ok(xcUserService.page(page, queryWrapper));
	}

	@RequestMapping("/add")
	@FormToken(save = true)
	public String add() {
		return "xc/user/add";
	}

	@RequestMapping("/edit/{id}")
	@FormToken(save = true)
	public String edit(@PathVariable("id") Integer id, Model model) {
		XcUser xcUser = xcUserService.getById(id);
		model.addAttribute("xcUser", xcUser);
		return "xc/user/edit";
	}

	/**
	 * 用户信息
	 */
	@RequestMapping("/info")
	public String info() {
		return "xc/user/info";
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	@FormToken(remove = true)
	public Response<String> save(XcUser xcUser) {
		LambdaQueryWrapper<XcUser> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.eq(XcUser::getUserName, xcUser.getUserName());
		int count = xcUserService.count(queryWrapper);
		Assert.state(count < 1, "用户名已经存在");
		boolean result = xcUserService.save(xcUser);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@FormToken(remove = true)
	public Response<String> update(XcUser xcUser) {
		boolean result = xcUserService.updateById(xcUser);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 删除
	 */
	@ResponseBody
	@RequestMapping("/remove")
	public Response<String> remove(@NotNull Integer id) {
		boolean result = xcUserService.removeById(id);
		return result ? Response.ok() : Response.error();
	}
}