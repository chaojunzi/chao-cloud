package com.chao.cloud.generator.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;
import com.chao.cloud.common.extra.token.annotation.FormToken;
import com.chao.cloud.generator.dal.entity.XcGroup;
import com.chao.cloud.generator.service.XcGroupService;

import cn.hutool.core.collection.CollUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-07-24
 * @version 1.0.0
 */
@RequestMapping("/xc/group")
@Controller
@Validated
public class XcGroupController {

	@Autowired
	private XcGroupService xcGroupService;

	@RequestMapping
	public String list() {
		return "xc/group/list";
	}

	@RequestMapping("/list")
	@ResponseBody
	public Response<IPage<XcGroup>> list(Page<XcGroup> page) { // 分页
		LambdaQueryWrapper<XcGroup> queryWrapper = Wrappers.lambdaQuery();
		return Response.ok(xcGroupService.page(page, queryWrapper));
	}

	@MenuMapping("增加")
	@RequestMapping("/add")
	@FormToken(save = true)
	public String add() {
		return "xc/group/add";
	}

	@RequestMapping("/edit/{id}")
	@FormToken(save = true)
	public String edit(@PathVariable("id") Integer id, Model model) {
		XcGroup xcGroup = xcGroupService.getById(id);
		model.addAttribute("xcGroup", xcGroup);
		return "xc/group/edit";
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	@FormToken(remove = true)
	public Response<String> save(XcGroup xcGroup) {
		boolean result = xcGroupService.save(xcGroup);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@FormToken(remove = true)
	public Response<String> update(XcGroup xcGroup) {
		boolean result = xcGroupService.updateById(xcGroup);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/remove")
	@ResponseBody
	public Response<String> remove(@NotNull(message = "id 不能为空") Integer id) {
		boolean result = xcGroupService.removeById(id);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 批量删除
	 */
	@RequestMapping("/batchRemove")
	@ResponseBody
	public Response<String> batchRemove(
			@NotNull(message = "不能为空") @Size(min = 1, message = "请至少选择一个") @RequestParam("ids[]") Integer[] ids) {
		boolean result = xcGroupService.removeByIds(CollUtil.toList(ids));
		return result ? Response.ok() : Response.error();
	}

}