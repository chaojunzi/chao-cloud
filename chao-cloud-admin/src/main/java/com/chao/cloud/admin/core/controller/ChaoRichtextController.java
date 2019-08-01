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
import com.chao.cloud.admin.core.dal.entity.ChaoRichtext;
import com.chao.cloud.admin.core.service.ChaoRichtextService;
import com.chao.cloud.admin.sys.config.GlobleConfig;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.extra.ftp.IFileOperation;
import com.chao.cloud.common.extra.ftp.annotation.FtpConfig;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuEnum;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;
import com.chao.cloud.common.extra.token.annotation.FormToken;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-06-06
 * @version 1.0.0
 */
@RequestMapping("/chao/richtext")
@Controller
@Validated
@MenuMapping
public class ChaoRichtextController {

	@Autowired
	private GlobleConfig globleConfig;
	@Autowired
	private FtpConfig ftpConfig;

	@Autowired
	private ChaoRichtextService chaoRichtextService;

	@MenuMapping(value = "富文本管理", type = MenuEnum.MENU)
	@RequiresPermissions("chao:richtext:list")
	@RequestMapping
	public String list() {
		return "chao/richtext/list";
	}

	@MenuMapping("列表")
	@RequiresPermissions("chao:richtext:list")
	@RequestMapping("/list")
	@ResponseBody
	public Response<IPage<ChaoRichtext>> list(Page<ChaoRichtext> page //
			, String title) { // 分页
		LambdaQueryWrapper<ChaoRichtext> queryWrapper = Wrappers.lambdaQuery();
		if (StrUtil.isNotBlank(title)) {
			queryWrapper.like(ChaoRichtext::getTitle, title);
		}
		return Response.ok(chaoRichtextService.page(page, queryWrapper));
	}

	/**
	 * 预览
	 * @param id
	 * @return
	 */
	@RequestMapping("/preview/{id}")
	public String preview(@PathVariable("id") Integer id, Model model) {
		ChaoRichtext chaoRichtext = chaoRichtextService.getById(id);
		String content = "";
		// 读取富文本编辑器的内容
		if (StrUtil.isNotBlank(chaoRichtext.getContent()) && FileUtil.exist(chaoRichtext.getContent())) {
			content = FileUtil.readUtf8String(chaoRichtext.getContent());
		}
		model.addAttribute("content", content);
		return "chao/richtext/preview";
	}

	@MenuMapping("增加")
	@RequiresPermissions("chao:richtext:add")
	@RequestMapping("/add")
	@FormToken(save = true)
	public String add() {
		return "chao/richtext/add";
	}

	@MenuMapping("编辑")
	@RequiresPermissions("chao:richtext:edit")
	@RequestMapping("/edit/{id}")
	@FormToken(save = true)
	public String edit(@PathVariable("id") Integer id, Model model) {
		ChaoRichtext chaoRichtext = chaoRichtextService.getById(id);
		// 读取富文本编辑器的内容
		String html = "";
		if (StrUtil.isNotBlank(chaoRichtext.getContent()) && FileUtil.exist(chaoRichtext.getContent())) {
			html = FileUtil.readUtf8String(chaoRichtext.getContent());
		}
		chaoRichtext.setContent(html);// 还原
		model.addAttribute("domain", ftpConfig.getRealm());
		model.addAttribute("chaoRichtext", chaoRichtext);
		return "chao/richtext/edit";
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@RequestMapping("/save")
	@RequiresPermissions("chao:richtext:add")
	@FormToken(remove = true)
	public Response<String> save(ChaoRichtext chaoRichtext) {
		// 富文本编辑器转义
		if (StrUtil.isNotBlank(chaoRichtext.getContent())) {
			// 生成文件名
			String fileName = getHtmlFileName();
			// 字符串转文件
			FileUtil.writeUtf8String(chaoRichtext.getContent(), fileName);
			chaoRichtext.setContent(fileName);
		}
		boolean result = chaoRichtextService.save(chaoRichtext);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("chao:richtext:edit")
	@FormToken(remove = true)
	public Response<String> update(ChaoRichtext chaoRichtext) {
		// 富文本编辑器转义
		if (StrUtil.isNotBlank(chaoRichtext.getContent())) {
			// 获取文件名
			String fileName = getHtmlFileName();
			FileUtil.writeUtf8String(chaoRichtext.getContent(), fileName);
			chaoRichtext.setContent(fileName);
		}
		boolean result = chaoRichtextService.updateById(chaoRichtext);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 删除
	 */
	@MenuMapping("删除")
	@RequiresPermissions("chao:richtext:remove")
	@RequestMapping("/remove")
	@ResponseBody
	public Response<String> remove(@NotNull(message = "id 不能为空") Integer id) {
		boolean result = chaoRichtextService.removeById(id);
		return result ? Response.ok() : Response.error();
	}

	/**
	 * 批量删除
	 */
	@MenuMapping("批量删除")
	@RequiresPermissions("chao:richtext:batchRemove")
	@RequestMapping("/batchRemove")
	@ResponseBody
	public Response<String> batchRemove(
			@NotNull(message = "不能为空") @Size(min = 1, message = "请至少选择一个") @RequestParam("ids[]") Integer[] ids) {
		boolean result = chaoRichtextService.removeByIds(CollUtil.toList(ids));
		return result ? Response.ok() : Response.error();
	}

	private String getHtmlFileName() {
		return IFileOperation.genRandomFullFileName(globleConfig.getRichPath(), ".html");
	}

}