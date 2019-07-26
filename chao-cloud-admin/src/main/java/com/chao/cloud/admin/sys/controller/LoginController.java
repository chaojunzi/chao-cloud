package com.chao.cloud.admin.sys.controller;

import java.io.File;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.shiro.ShiroUtils;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.util.HyalineCaptchaUtil;
import com.chao.cloud.common.util.HyalineCaptchaUtil.HyalineCircleCaptcha;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@Validated
public class LoginController extends BaseController {

	@RequestMapping({ "/", "" })
	public String welcome(Model model) {
		return "redirect:/index";
	}

	@RequestMapping({ "/index" })
	public String index(Model model) {
		model.addAttribute("name", getUser().getName());
		model.addAttribute("username", getUser().getUsername());
		return "index";
	}

	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}

	@AdminLog("登录")
	@PostMapping("/login")
	@ResponseBody
	public Response<String> ajaxLogin(@NotBlank(message = "请输入用户名") String username, //
			@NotBlank(message = "请输入密码") String password, //
			@NotBlank(message = "请输入验证码") String verify, //
			HttpServletRequest request) {
		String random = (String) request.getSession().getAttribute(RANDOMCODEKEY);
		if (StrUtil.isBlank(random)) {
			throw new BusinessException("请刷新验证码");
		}
		if (!verify.equals(random)) {
			throw new BusinessException("请输入正确的验证码");
		}
		// 摘要算法
		password = DigestUtil.md5Hex(username + password);
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
			return Response.ok();
		} catch (AuthenticationException e) {
			throw new BusinessException(e.getMessage());
		}
	}

	@RequestMapping("/logout")
	public String logout() {
		ShiroUtils.logout();
		return "redirect:/login";
	}

	@RequestMapping("/main")
	public String main(Model model) {
		int scale = 2;
		RuntimeInfo runtime = SystemUtil.getRuntimeInfo();
		// 分配内存百分比
		double distribute = NumberUtil.div(runtime.getFreeMemory(), runtime.getTotalMemory(), scale);
		// 物理内存
		double physics = NumberUtil.div(runtime.getUsableMemory(), runtime.getMaxMemory(), scale);
		model.addAttribute("distribute", NumberUtil.formatPercent(distribute, scale));
		model.addAttribute("physics", NumberUtil.formatPercent(physics, scale));
		// 内存详情
		model.addAttribute("totalMemory", FileUtil.readableFileSize(runtime.getTotalMemory()));
		model.addAttribute("freeMemory", FileUtil.readableFileSize(runtime.getFreeMemory()));
		model.addAttribute("maxMemory", FileUtil.readableFileSize(runtime.getMaxMemory()));
		model.addAttribute("usableMemory", FileUtil.readableFileSize(runtime.getUsableMemory()));
		model.addAttribute("threadCount", SystemUtil.getTotalThreadCount());
		return "main";
	}

	/**
	 * 生成验证码
	 */
	@RequestMapping(value = "/getVerify")
	public void getVerify(HttpServletRequest request, HttpServletResponse response) {
		try (OutputStream os = response.getOutputStream()) {
			response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
			response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			// 制作验证码
			HyalineCircleCaptcha captcha = HyalineCaptchaUtil.createCircleCaptcha(100, 42, 4, 3);
			String code = captcha.getCode();
			log.info("[验证码: {}]", code);
			// 存入session
			HttpSession session = request.getSession();
			// 将生成的随机字符串保存到session中
			session.removeAttribute(RANDOMCODEKEY);
			session.setAttribute(RANDOMCODEKEY, code);
			// 输出
			captcha.write(os);
		} catch (Exception e) {
			log.error("获取验证码失败>>>> ", e);
		}
	}

	public static void main(String[] args) {
		ImgUtil.scale(new File("D:/user.png"), new File("D:/a.png"), 0.5F);
	}

}