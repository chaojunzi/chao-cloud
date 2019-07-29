package com.chao.cloud.common.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.ReflectUtil;

/**
 * 透明验证码 
 * @功能：
 * @author： 薛超
 * @时间： 2019年7月29日
 * @version 1.0.3
 */
public class HyalineCaptchaUtil {
	/**
	 * 透明圆形验证码
	 * @param width
	 * @param height
	 * @param codeCount
	 * @param circleCount
	 * @return
	 */
	public static HyalineCircleCaptcha createCircleCaptcha(int width, int height, int codeCount, int circleCount) {
		return new HyalineCircleCaptcha(width, height, codeCount, circleCount);
	}

	public static class HyalineCircleCaptcha extends CircleCaptcha {
		private static final long serialVersionUID = 1L;

		public HyalineCircleCaptcha(int width, int height, int codeCount, int interfereCount) {
			super(width, height, codeCount, interfereCount);
		}

		@Override
		public Image createImage(String code) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			// 这里是关键部分->透明背景
			Graphics2D g = image.createGraphics();
			image = g.getDeviceConfiguration().createCompatibleImage(image.getWidth(null), image.getHeight(null),
					Transparency.TRANSLUCENT);
			g = image.createGraphics();
			// 随机画干扰圈圈
			ReflectUtil.invoke(this, "drawInterfere", g);
			ReflectUtil.invoke(this, "drawString", g, code);
			return image;
		}
	}
}