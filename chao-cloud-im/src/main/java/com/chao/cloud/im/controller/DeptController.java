package com.chao.cloud.im.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.im.dal.entity.ImDept;
import com.chao.cloud.im.service.ImDeptService;

import cn.hutool.core.collection.CollUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-06-26
 * @version 1.0.0
 */
@RequestMapping("/im/dept")
@RestController
@Validated
public class DeptController {

	@Autowired
	private ImDeptService imDeptService;

	@RequestMapping("/list")
	public Response<IPage<ImDept>> list(Page<ImDept> page //
	) { // 分页
		LambdaQueryWrapper<ImDept> queryWrapper = Wrappers.lambdaQuery();
		return Response.ok(imDeptService.page(page, queryWrapper));
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public Response<String> save(ImDept imDept) {
		boolean result = imDeptService.save(imDept);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public Response<String> update(ImDept imDept) {
		boolean result = imDeptService.updateById(imDept);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/remove")
	public Response<String> remove(@NotNull(message = "id 不能为空") Integer id) {
		boolean result = imDeptService.removeById(id);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 批量删除
	 */
	@RequestMapping("/batchRemove")
	public Response<String> batchRemove(
			@NotNull(message = "不能为空") @Size(min = 1, message = "请至少选择一个") @RequestParam("ids[]") Integer[] ids) {
		boolean result = imDeptService.removeByIds(CollUtil.toList(ids));
		return result ? Response.ok() : Response.error();
	}

}