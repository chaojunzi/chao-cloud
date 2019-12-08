package com.chao.cloud.common.web.sign;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

import com.chao.cloud.common.base.BaseHttpServlet;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.web.config.SignConfig;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class SignInterceptor implements MethodInterceptor, BaseHttpServlet, Ordered {

	private int order;

	private SignConfig config;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object[] args = invocation.getArguments();
		Method method = invocation.getMethod();
		// 判断是否需要验签
		boolean needSign = this.needSign(method);
		if (needSign) {
			HttpServletRequest request = getRequest();
			// 获取签名Sign
			String sign = request.getHeader(Sign.SIGN);
			Assert.notBlank(sign, "Header[Sign]不能为空");
			// 获取时间戳
			Long timestamp = Convert.toLong(request.getHeader(Sign.TIMESTAMP));
			Assert.notNull(timestamp, "Header[Timestamp]不能为空");
			// 重放时间限制（单位）
			Long difference = DateUtil.between(DateUtil.date(), DateUtil.date(timestamp), DateUnit.SECOND);
			if (difference > config.getTimeout()) {
				log.info("前端时间戳：{},服务端时间戳：{}", DateUtil.date(timestamp), DateUtil.date());
				throw new BusinessException("已过期的签名");
			}
			Map<String, Object> signMap = this.buildSignMap(args, method.getParameters());
			// 验签
			this.checkSign(signMap, sign, timestamp);
		}
		return invocation.proceed();
	}

	/**
	 * 验签 自己拓展-唯一的流水号|单位时间内请求次数|签名密钥等等
	 * 
	 * @param signMap   签名参数Map
	 * @param sign      请求头带过来的签名
	 * @param timestamp
	 */
	protected void checkSign(Map<String, Object> signMap, String sign, Long timestamp) {
		SignTypeEnum type = SignTypeEnum.getByType(config.getSignType());
		String otherParams = StrUtil.format("{}{}", Sign.TIMESTAMP, timestamp);
		String signResult = null;
		switch (type) {
		case MD5:
			signResult = SecureUtil.signParamsMd5(signMap, otherParams);
			break;
		case SHA1:
			signResult = SecureUtil.signParamsSha1(signMap, otherParams);
			break;
		case SHA256:
			signResult = SecureUtil.signParamsSha256(signMap, otherParams);
			break;
		default:
			break;
		}
		Assert.isTrue(sign.equals(signResult), "签名参数校验失败");
	}

	/**
	 * 判断方法是否需要签名
	 * 
	 * @param method 方法
	 * @return true 为验签
	 */
	protected boolean needSign(Method method) {
		Parameter[] parameters = method.getParameters();
		if (ArrayUtil.isNotEmpty(parameters)) {
			for (Parameter parameter : parameters) {
				if (parameter.isAnnotationPresent(Sign.class)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 构造签名参数
	 * 
	 * @param args       参数列表
	 * @param parameters 参数类型
	 */
	protected Map<String, Object> buildSignMap(Object[] args, Parameter[] parameters) {
		// 获取参数map
		Map<String, Object> signMap = MapUtil.newHashMap();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			if (parameter.isAnnotationPresent(Sign.class)) {
				Object arg = args[i];
				// 简单值类型
				if (ClassUtil.isSimpleValueType(parameter.getType())) {
					signMap.put(parameter.getName(), arg);
					continue;
				}
				// map
				if (arg instanceof Map) {
					Map<Object, Object> map = (Map<Object, Object>) arg;
					map.forEach((k, v) -> {
						if (k instanceof String) {
							if (v != null && !ClassUtil.isSimpleValueType(v.getClass())) {
								return;
							}
							signMap.put(k.toString(), v);
						}
					});
					continue;
				}
				// bean
				if (BeanUtil.isBean(parameter.getType())) {
					Field[] fields = ReflectUtil.getFields(parameter.getType());
					for (Field field : fields) {
						if (field.isAnnotationPresent(Sign.class) //
								&& ClassUtil.isSimpleValueType(field.getType())) {
							signMap.put(field.getName(), ReflectUtil.getFieldValue(arg, field));
						}
					}
					continue;
				}
				// 其他------------------------------------------------------
				this.buildSignMapOthers(parameter, arg, signMap);
			}
		}
		return signMap;
	}

	/**
	 * 构造除了简单值-map-bean 之外的其他类型
	 * 
	 * @param parameter 参数类型
	 * @param arg       参数值
	 * @param signMap   签名参数Map
	 */
	private void buildSignMapOthers(Parameter parameter, Object arg, Map<String, Object> signMap) {
		// 自行拓展
	}

}