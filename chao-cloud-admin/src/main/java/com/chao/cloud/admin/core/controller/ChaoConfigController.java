package com.chao.cloud.admin.core.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.chao.cloud.admin.core.dal.entity.ChaoConfig;
import com.chao.cloud.admin.core.service.ChaoConfigService;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.extra.token.annotation.FormToken;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-24
 * @version 1.0.0
 */
@Controller
@Validated
@RequestMapping("chao/config")
public class ChaoConfigController {

	@Autowired
	private ChaoConfigService chaoConfigService;

	@RequestMapping
	@RequiresPermissions("chaoConfig:list")
	public String list() {
		return "chao/config/list";
	}

	@ResponseBody
	@RequestMapping("/list")
	@RequiresPermissions("chaoConfig:list")
	public Response<IPage<ChaoConfig>> list(Page<ChaoConfig> page //
			, String name, String val) { // 分页
		LambdaQueryWrapper<ChaoConfig> queryWrapper = Wrappers.lambdaQuery();
		if (StrUtil.isNotBlank(name)) {
			queryWrapper.like(ChaoConfig::getName, name);
		}
		if (StrUtil.isNotBlank(val)) {
			queryWrapper.like(ChaoConfig::getVal, val);
		}
		return ResponseResult.getResponseResult(chaoConfigService.page(page, queryWrapper));
	}

	@RequestMapping("/add")
	@RequiresPermissions("chaoConfig:add")
	@FormToken(save = true)
	public String add() {
		return "chao/config/add";
	}

	@RequestMapping("/edit/{id}")
	@RequiresPermissions("chaoConfig:edit")
	@FormToken(save = true)
	public String edit(@PathVariable("id") Integer id, Model model) {
		ChaoConfig chaoConfig = chaoConfigService.getById(id);
		model.addAttribute("chaoConfig", chaoConfig);
		return "chao/config/edit";
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	@RequiresPermissions("chaoConfig:add")
	@FormToken(remove = true)
	public Response<String> save(ChaoConfig chaoConfig) {
		boolean result = chaoConfigService.save(chaoConfig);
		return result ? ResponseResult.ok() : ResponseResult.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("chaoConfig:edit")
	@FormToken(remove = true)
	public Response<String> update(ChaoConfig chaoConfig) {
		boolean result = chaoConfigService.updateById(chaoConfig);
		return result ? ResponseResult.ok() : ResponseResult.error();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/remove")
	@ResponseBody
	@RequiresPermissions("chaoConfig:remove")
	public Response<String> remove(@NotNull(message = "id 不能为空") Integer id) {
		boolean result = chaoConfigService.removeById(id);
		return result ? ResponseResult.ok() : ResponseResult.error();
	}

	/**
	 * 批量删除
	 */
	@RequestMapping("/batchRemove")
	@ResponseBody
	@RequiresPermissions("chaoConfig:batchRemove")
	public Response<String> batchRemove(
			@NotNull(message = "不能为空") @Size(min = 1, message = "请至少选择一个") @RequestParam("ids[]") Integer[] ids) {
		boolean result = chaoConfigService.removeByIds(CollUtil.toList(ids));
		return result ? ResponseResult.ok() : ResponseResult.error();
	}

}