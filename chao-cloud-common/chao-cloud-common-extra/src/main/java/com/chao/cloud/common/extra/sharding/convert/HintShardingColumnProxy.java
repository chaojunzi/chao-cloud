package com.chao.cloud.common.extra.sharding.convert;

import java.lang.reflect.Method;
import java.util.function.Function;

import org.apache.shardingsphere.infra.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.sharding.annotation.ShardingColumn;
import com.chao.cloud.common.extra.sharding.annotation.ShardingExtraConfig;
import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;
import com.chao.cloud.common.extra.sharding.constant.ShardingConstant;
import com.chao.cloud.common.extra.sharding.convert.ShardingModel.ShardingJson;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * 分片字段aop 处理
 * 
 * @author 薛超
 * @since 2020年12月2日
 * @version 1.0.0
 */
@Aspect
@AllArgsConstructor
public class HintShardingColumnProxy {

	private ShardingColumnConvert convert;

	/**
	 * 慎用：此方法会给ThreadLocal 存放变量<br>
	 * 注：请不要嵌套使用
	 * 
	 * @param <T>
	 * @param shardingCode
	 * @param function
	 * @return
	 */
	public static <T> T hintShardingCodeProxy(String shardingCode, Function<String, T> function) {
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		if (!prop.isEnable()) {
			return function.apply(shardingCode);
		}
		// 设置shardingCode
		if (StrUtil.isNotBlank(shardingCode)) {
			HintManager instance = HintManager.getInstance();
			instance.setDatabaseShardingValue(shardingCode);
		}
		try {
			return function.apply(shardingCode);
		} finally {
			HintManager.clear();
		}
	}

	/**
	 * @ShardingColumn 优先于 @Api
	 * @param pdj
	 * @return
	 * @throws Throwable
	 */
	@Around(value = ShardingConstant.SHARDING_PROXY)
	public Object around(ProceedingJoinPoint pdj) throws Throwable {
		Signature signature = pdj.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		// 获取shardingCode
		String shardingCode = this.getShardingCode(method, pdj.getArgs());
		return hintShardingCodeProxy(shardingCode, code -> {
			try {
				return pdj.proceed();
			} catch (Throwable e) {
				throw ExceptionUtil.wrapRuntime(e);
			}
		});
	}

	private String getShardingCode(Method method, Object[] args) {
		// 解析@ShardingColumn
		ShardingColumn shardingColumn = method.getAnnotation(ShardingColumn.class);
		if (shardingColumn != null && shardingColumn.shardingCode()) {
			switch (shardingColumn.type()) {
			case MODEL: // 解析model
				Object model = ArrayUtil.firstNonNull(args);
				if (model instanceof ShardingModel) {
					return this.columnModel((ShardingModel) model, //
							shardingColumn.defaultSet(), //
							shardingColumn.validateOrgCode());
				}
				throw new BusinessException("参数 args[0] 未继承 ShardingModel");
			case JSON:// 解析json
				return this.columnJson(args);
			default:
				break;
			}
			return null;
		}
		return null;
	}

	// orgCode 转shardingCode
	private String columnModel(ShardingModel model, boolean defaultSet, boolean validateOrgCode) { // 方法参数处理
		ShardingColumnModel m = this.shardingCodeThrow(model.getCode(), validateOrgCode);
		String shardingCode = m.getShardingCode();
		if (defaultSet) { // 设置默认值
			model.setShardingCode(shardingCode);
		}
		return shardingCode;
	}

	private ShardingColumnModel shardingCodeThrow(String orgCode, boolean validateOrgCode) {
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		ShardingColumnModel m = convert.getShardingColumnModel(orgCode);
		//
		String shardingCode = m.getShardingCode();
		// 匹配数据源
		boolean matches = true;
		if (StrUtil.isBlank(shardingCode)) {
			matches = false;
		} else if (prop.isEnable()) {// 判断shardingCode 是否包含符合的数据库
			String ds = SpringUtil.getBean(ShardingExtraConfig.class).getDsByColumnValue(shardingCode);
			if (StrUtil.isBlank(ds)) {
				matches = false;
			}
		}
		// 数据源大于1执行校验
		if (prop.getDsNum() > 1) {
			// 匹配失败
			if (!matches) {
				throw new BusinessException("未匹配到数据源");
			}
			if (validateOrgCode && !m.isOrgExist()) {
				throw new BusinessException("无效的code");
			}
		}
		// 设置默认值
		if (StrUtil.isBlank(shardingCode)) {
			shardingCode = ShardingConstant.DEFAULT_VALUE;
			m.setShardingCode(shardingCode);
		}
		return m;
	}

	// json直接获取
	private String columnJson(Object[] args) {
		Object json = ArrayUtil.firstNonNull(args);
		if (json instanceof String) {
			ShardingModel model = JSONUtil.toBean(json.toString(), ShardingJson.class);
			return model.getShardingCode();
		}
		return null;
	}

}