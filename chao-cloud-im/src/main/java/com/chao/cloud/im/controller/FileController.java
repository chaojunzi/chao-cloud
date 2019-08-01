package com.chao.cloud.im.controller;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.api.R;
import com.chao.cloud.common.extra.ftp.IFileOperation;
import com.chao.cloud.common.extra.ftp.annotation.FtpConfig;
import com.chao.cloud.im.domain.dto.LayFileDTO;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月30日
 * @version 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("file")
public class FileController {

	private static final String MESSAGE = "文件不能为空";

	@Autowired
	private IFileOperation fileOperation;

	@Autowired
	private FtpConfig ftpConfig;

	/**
	 * 图片上传
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "uploadImg")
	public R<LayFileDTO> uploadImg(@NotNull(message = MESSAGE) MultipartFile file, HttpServletRequest request)
			throws Exception {
		String uploadImg = fileOperation.uploadImg(file.getInputStream(), file.getOriginalFilename());
		// 获取域名
		String realm = ServletUtil.getHeaderIgnoreCase(request, "Origin");
		return R.ok(LayFileDTO.builder().src(realm + uploadImg).domain(ftpConfig.getRealm()).build());
	}

	/**
	 * 上传文件
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "upload")
	public R<LayFileDTO> upload(@NotNull(message = MESSAGE) MultipartFile file, HttpServletRequest request)
			throws Exception {
		String name = file.getOriginalFilename();
		String uploadImg = fileOperation.uploadInputStream(file.getInputStream(), name);
		// 获取域名
		String realm = ServletUtil.getHeaderIgnoreCase(request, "Origin");
		return R.ok(LayFileDTO.builder().src(realm + uploadImg).domain(ftpConfig.getRealm()).name(name).build());
	}

	/**
	 * 文件下载
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "downLoad")
	public void downLoad(@NotEmpty(message = MESSAGE) String fileName, HttpServletResponse response) throws Exception {
		String finalName = fileName.substring(fileName.lastIndexOf("/"));
		try (OutputStream os = new BufferedOutputStream(response.getOutputStream())) {
			byte[] downLoad = null;
			response.reset();
			response.setContentType("application/x-msdownload"); // 设置返回的文件类型
			response.setHeader("Content-Disposition", "attachment;filename=" + finalName);
			os.write(downLoad);
			os.flush();
		} catch (Exception e) {
			log.info("[下载异常信息：fileName={},error={}]", fileName, e.getMessage());
			log.error("[下载异常{}]", e);
		}

	}

}
