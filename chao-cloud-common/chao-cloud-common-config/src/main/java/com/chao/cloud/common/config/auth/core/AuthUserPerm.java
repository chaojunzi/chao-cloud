package com.chao.cloud.common.config.auth.core;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.chao.cloud.common.config.auth.annotation.ResolverEnum;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 
 * @功能：权限配置
 * @author： 薛超
 * @时间： 2019年7月30日
 * @version 1.0.3
 */
@Data
public class AuthUserPerm implements InitializingBean {

	private final String errorPermName = "ERROR_PERM";

	private Set<?> errorPerm;

	/**
	 * token 获取类型
	 */
	private Integer resolverType = ResolverEnum.PARAM.type;

	/**
	 * 用户类型 枚举
	 */
	private String type;
	/**
	 * 用户状态 枚举
	 */
	private String status;
	/**
	 * 用户权限接口类
	 */
	private String perm;

	private Map<Object, String> typeMap;
	private Map<Object, String> statusMap;
	private Map<String, Integer> permMap;
	private Class<?> permClass;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.check();
		// 初始化枚举
		typeMap = getNameFieldMap(ClassUtil.loadClass(type), "type");
		Assert.notNull(typeMap, "请在枚举类中设置用户类型:{}", type);
		statusMap = getNameFieldMap(ClassUtil.loadClass(status), "status");
		Assert.notNull(statusMap, "请在枚举类中设置用户状态:{}", status);
		// 权限组合
		Field[] fields = ReflectUtil.getFields(permClass);
		permMap = MapUtil.newHashMap(fields.length);
		for (Field field : fields) {
			Object obj = field.get(permClass);
			if (obj instanceof Integer) {
				permMap.put(field.getName(), (Integer) obj);
			}
		}
	}

	private void check() throws Exception {
		Assert.notBlank(type, "请配置用户类型枚举类全路径:type={}", type);
		Assert.notBlank(status, "请配置用户状态枚举类全路径:status={}", status);
		Assert.notBlank(perm, "请配置用户权限码全路径:perm={}", perm);
		// 接口
		permClass = ClassUtil.loadClass(perm);
		Assert.notNull(permClass, "请在接口中设置权限码:{}", perm);
		// 判断接口是否包含规定的属性
		Object errorPerm = ReflectUtil.getField(permClass, errorPermName).get(permClass);
		if (errorPerm instanceof Set) {
			this.errorPerm = (Set<?>) errorPerm;
		} else {
			throw new BusinessException(StrUtil.format("请在[{}]设置常量[Set<Integer> {}], 并初始化", perm, errorPermName));
		}

	}

	private Map<Object, String> getNameFieldMap(Class<? extends Enum<?>> clazz, String fieldName) {
		final Enum<?>[] enums = clazz.getEnumConstants();
		if (null == enums) {
			return null;
		}
		final Map<Object, String> map = MapUtil.newHashMap(enums.length);
		for (Enum<?> e : enums) {
			map.put(ReflectUtil.getFieldValue(e, fieldName), e.name());
		}
		return map;
	}

}
