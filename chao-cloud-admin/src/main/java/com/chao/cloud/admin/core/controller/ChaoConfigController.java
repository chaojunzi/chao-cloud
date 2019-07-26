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
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuEnum;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;
import com.chao.cloud.common.extra.token.annotation.FormToken;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-30
 * @version 1.0.0
 */
@RequestMapping("/chao/config")
@Controller
@Validated
@MenuMapping
public class ChaoConfigController {

	@Autowired
	private ChaoConfigService chaoConfigService;

	@MenuMapping(value = "配置管理", type = MenuEnum.MENU)
	@RequiresPermissions("chao:config:list")
	@RequestMapping
	public String list() {
		return "chao/config/list";
	}

	@MenuMapping("列表")
	@RequiresPermissions("chao:config:list")
	@RequestMapping("/list")
	@ResponseBody
	public Response<IPage<ChaoConfig>> list(Page<ChaoConfig> page //
			, String name // 名称
			, String val // 值
	) { // 分页
		LambdaQueryWrapper<ChaoConfig> queryWrapper = Wrappers.lambdaQuery();
		if (StrUtil.isNotBlank(name)) {
			queryWrapper.like(ChaoConfig::getName, name);
		}
		if (StrUtil.isNotBlank(val)) {
			queryWrapper.like(ChaoConfig::getVal, val);
		}
		return Response.ok(chaoConfigService.page(page, queryWrapper));
	}

	@MenuMapping("增加")
	@RequiresPermissions("chao:config:add")
	@RequestMapping("/add")
	@FormToken(save = true)
	public String add() {
		return "chao/config/add";
	}

	@MenuMapping("编辑")
	@RequiresPermissions("chao:config:edit")
	@RequestMapping("/edit/{id}")
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
	@RequiresPermissions("chao:config:add")
	@FormToken(remove = true)
	public Response<String> save(ChaoConfig chaoConfig) {
		boolean result = chaoConfigService.save(chaoConfig);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("chao:config:edit")
	@FormToken(remove = true)
	public Response<String> update(ChaoConfig chaoConfig) {
		boolean result = chaoConfigService.updateById(chaoConfig);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 删除
	 */
	@MenuMapping("删除")
	@RequiresPermissions("chao:config:remove")
	@RequestMapping("/remove")
	@ResponseBody
	public Response<String> remove(@NotNull(message = "id 不能为空") Integer id) {
		boolean result = chaoConfigService.removeById(id);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 批量删除
	 */
	@MenuMapping("批量删除")
	@RequiresPermissions("chao:config:batchRemove")
	@RequestMapping("/batchRemove")
	@ResponseBody
	public Response<String> batchRemove(
			@NotNull(message = "不能为空") @Size(min = 1, message = "请至少选择一个") @RequestParam("ids[]") Integer[] ids) {
		boolean result = chaoConfigService.removeByIds(CollUtil.toList(ids));
		return result ? Response.ok() : Response.error();
	}

}