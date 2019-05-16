package com.chao.cloud.admin.system.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chao.cloud.admin.system.annotation.AdminLog;
import com.chao.cloud.admin.system.service.MenuService;
import com.chao.cloud.admin.system.shiro.ShiroUtils;
import com.chao.cloud.admin.system.utils.R;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    MenuService menuService;

    @GetMapping({ "/", "" })
    String welcome(Model model) {
        return "redirect:/index";
    }

    @GetMapping({ "/index" })
    String index(Model model) {
        model.addAttribute("name", getUser().getName());
        model.addAttribute("username", getUser().getUsername());
        return "chao_index";
    }

    @GetMapping("/login")
    String login(Model model) {
        return "login";
    }

    @AdminLog("登录")
    @PostMapping("/login")
    @ResponseBody
    R ajaxLogin(String username, String password, String verify, HttpServletRequest request) {
        if (StrUtil.isBlank(verify)) {
            return R.error("请输入验证码");
        }
        String random = (String) request.getSession().getAttribute(RANDOMCODEKEY);
        if (StrUtil.isBlank(random)) {
            return R.error("服务验证码保存失败->请检查 并设置cookie");
        }
        if (!verify.equals(random)) {
            return R.error("请输入正确的验证码");
        }
        // 摘要算法
        password = DigestUtil.md5Hex(username + password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return R.ok();
        } catch (AuthenticationException e) {
            return R.error("用户或密码错误");
        }
    }

    @GetMapping("/logout")
    String logout() {
        ShiroUtils.logout();
        return "redirect:/login";
    }

    @GetMapping("/main")
    String main() {
        return "main";
    }

    /**
     * 生成验证码
     */
    @GetMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream()) {
            response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            // 制作验证码
            CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 3);
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
}