package com.chao.cloud.generator.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.util.HyalineCaptchaUtil;
import com.chao.cloud.generator.constant.XcConstant;
import com.chao.cloud.generator.dal.entity.XcUser;
import com.chao.cloud.generator.domain.dto.LoginDTO;
import com.chao.cloud.generator.domain.vo.LoginVO;
import com.chao.cloud.generator.service.XcUserService;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@RequestMapping
@Controller
@Validated
@Slf4j
public class LoginController extends BaseController {

	@Autowired
	private XcUserService xcUserService;

	@GetMapping("/login")
	public String login(HttpSession session) {
		return isLogin(session) ? "redirect:/" : "login";
	}

	@PostMapping("/login")
	@ResponseBody
	public Response<String> ajaxLogin(@Valid LoginVO vo, HttpSession session) {
		String random = (String) session.getAttribute(randomCookie);
		if (StrUtil.isBlank(random)) {
			throw new BusinessException("请刷新验证码");
		}
		if (!vo.getVerify().equals(random)) {
			throw new BusinessException("请输入正确的验证码");
		}
		// 根据登录名获取用户
		XcUser user = xcUserService.getOne(Wrappers.<XcUser>lambdaQuery().eq(XcUser::getUserName, vo.getUserName()));
		if (BeanUtil.isEmpty(user)) {
			throw new BusinessException("用户不存在");
		}
		if (!vo.getPassword().equals(user.getPassword())) {
			throw new BusinessException("密码错误");
		}
		if (!XcConstant.Status.OK.equals(user.getStatus())) {
			throw new BusinessException("账号被冻结");
		}
		LoginDTO dto = BeanUtil.toBean(user, LoginDTO.class);
		session.setAttribute(token_key, dto);
		return Response.ok();

	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	/**
	 * 生成验证码
	 */
	@RequestMapping("/getVerify")
	public void getVerify(HttpServletRequest request, HttpServletResponse response) {
		try (OutputStream os = response.getOutputStream()) {
			response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
			response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			// 制作验证码
			CircleCaptcha captcha = HyalineCaptchaUtil.createCircleCaptcha(100, 42, 4, 3);
			String code = captcha.getCode();
			log.info("[验证码: {}]", code);
			// 存入session
			HttpSession session = request.getSession();
			// 将生成的随机字符串保存到session中
			session.removeAttribute(randomCookie);
			session.setAttribute(randomCookie, code);
			// 输出
			captcha.write(os);
		} catch (Exception e) {
			log.error("获取验证码失败--->{} ", e);
		}
	}

}