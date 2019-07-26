package com.chao.cloud.generator.controller;

import java.util.List;

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
import com.chao.cloud.common.extra.token.annotation.FormToken;
import com.chao.cloud.generator.dal.entity.XcConfig;
import com.chao.cloud.generator.dal.entity.XcGroup;
import com.chao.cloud.generator.service.XcConfigService;
import com.chao.cloud.generator.service.XcGroupService;

import cn.hutool.core.collection.CollUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-07-24
 * @version 1.0.0
 */
@RequestMapping("/xc/config")
@Controller
@Validated
public class XcConfigController {

	@Autowired
	private XcConfigService xcConfigService;

	@Autowired
	private XcGroupService xcGroupService;

	@RequestMapping
	public String list() {
		return "xc/config/list";
	}

	@RequestMapping("/list")
	@ResponseBody
	public Response<IPage<XcConfig>> list(Page<XcConfig> page) { // 分页
		LambdaQueryWrapper<XcConfig> queryWrapper = Wrappers.lambdaQuery();
		return Response.ok(xcConfigService.page(page, queryWrapper));
	}

	@RequestMapping("/add")
	@FormToken(save = true)
	public String add(Model model) {
		// 分组集合
		List<XcGroup> list = xcGroupService.list();
		model.addAttribute("groups", list);
		return "xc/config/add";
	}

	@RequestMapping("/edit/{id}")
	@FormToken(save = true)
	public String edit(@PathVariable("id") Integer id, Model model) {
		XcConfig xcConfig = xcConfigService.getById(id);
		// 分组集合
		List<XcGroup> list = xcGroupService.list();
		model.addAttribute("groups", list);
		model.addAttribute("xcConfig", xcConfig);
		return "xc/config/edit";
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	@FormToken(remove = true)
	public Response<String> save(XcConfig xcConfig) {
		boolean result = xcConfigService.save(xcConfig);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@FormToken(remove = true)
	public Response<String> update(XcConfig xcConfig) {
		boolean result = xcConfigService.updateById(xcConfig);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/remove")
	@ResponseBody
	public Response<String> remove(@NotNull(message = "id 不能为空") Integer id) {
		boolean result = xcConfigService.removeById(id);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 批量删除
	 */
	@RequestMapping("/batchRemove")
	@ResponseBody
	public Response<String> batchRemove(
			@NotNull(message = "不能为空") @Size(min = 1, message = "请至少选择一个") @RequestParam("ids[]") Integer[] ids) {
		boolean result = xcConfigService.removeByIds(CollUtil.toList(ids));
		return result ? Response.ok() : Response.error();
	}

}