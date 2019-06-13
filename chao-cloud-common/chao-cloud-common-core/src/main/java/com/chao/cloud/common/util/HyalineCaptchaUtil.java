package com.chao.cloud.common.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.img.GraphicsUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * 透明验证码 
 * @功能：
 * @author： 薛超
 * @时间： 2019年6月13日
 * @version 1.0.0
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

	public static class HyalineCircleCaptcha extends AbstractCaptcha {

		private static final long serialVersionUID = -1635193165789675706L;

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
			drawInterfere(g);
			// 画字符串
			drawString(g, code);
			return image;
		}

		// -----------------------------------------------------------------------------------------------------
		// Private method start
		/**
		 * 绘制字符串
		 * 
		 * @param g {@link Graphics2D}画笔
		 * @param code 验证码
		 */
		private void drawString(Graphics2D g, String code) {
			// 抗锯齿
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// 创建字体
			g.setFont(this.font);

			// 文字
			int minY = GraphicsUtil.getMinY(g);
			if (minY < 0) {
				minY = this.height - 1;
			}

			final int len = this.generator.getLength();
			int charWidth = width / len;
			for (int i = 0; i < len; i++) {
				// 指定透明度
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				// 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
				g.setColor(ImgUtil.randomColor());
				g.drawString(String.valueOf(code.charAt(i)), i * charWidth, RandomUtil.randomInt(minY, this.height));
			}
		}

		/**
		 * 画随机干扰
		 * 
		 * @param g {@link Graphics2D}
		 */
		private void drawInterfere(Graphics2D g) {
			final ThreadLocalRandom random = RandomUtil.getRandom();

			for (int i = 0; i < this.interfereCount; i++) {
				g.setColor(ImgUtil.randomColor(random));
				g.drawOval(random.nextInt(width), random.nextInt(height), random.nextInt(height >> 1),
						random.nextInt(height >> 1));
			}
		}
	}

}