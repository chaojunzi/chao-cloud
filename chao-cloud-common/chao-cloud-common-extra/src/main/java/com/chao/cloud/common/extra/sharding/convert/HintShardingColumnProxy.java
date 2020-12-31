package com.chao.cloud.common.extra.sharding.convert;

import java.lang.reflect.Method;
import java.util.function.Function;

import org.apache.shardingsphere.api.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.sharding.annotation.ShardingColumn;
import com.chao.cloud.common.extra.sharding.annotation.ShardingExtraConfig;
import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;
import com.chao.cloud.common.extra.sharding.constant.ColumnType;
import com.chao.cloud.common.extra.sharding.constant.ShardingConstant;
import com.chao.cloud.common.extra.sharding.convert.ShardingModel.ShardingJson;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
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
	 * @param <T>          返回对象类型
	 * @param shardingCode 分库标识
	 * @param function     对shardingCode 操作处理
	 * @return 返回对象
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
	 * 环绕拦截
	 * 
	 * @param pdj 切点对象
	 * @return 调用方法结果
	 * @throws Throwable 异常
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
		if (shardingColumn == null || !shardingColumn.shardingCode()) {
			return null;
		}
		// 解析model
		if (shardingColumn.type() == ColumnType.MODEL) {
			Object model = ArrayUtil.firstNonNull(args);
			if (model instanceof ShardingModel) {
				return this.columnModel((ShardingModel) model, shardingColumn.defaultSet());
			}
			throw new BusinessException("参数 args[0] 未继承 ShardingModel");

		}
		// 解析json
		if (shardingColumn.type() == ColumnType.JSON) {
			return this.columnJson(args);
		}
		return null;
	}

	// orgCode 转shardingCode
	private String columnModel(ShardingModel model, boolean defaultSet) { // 方法参数处理
		String shardingCode = this.shardingCodeThrow(model.getCode());
		if (defaultSet) { // 设置默认值
			model.setShardingCode(shardingCode);
		}
		return shardingCode;
	}

	private String shardingCodeThrow(String code) {
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		String shardingCode = convert.getShardingCode(code);
		// 匹配数据源
		boolean matches = true;
		if (StrUtil.isBlank(shardingCode)) {
			matches = false;
		} else if (prop.isEnable()) {// 判断shardingCode 是否包含符合的数据库
			String ds = SpringUtil.getBean(ShardingExtraConfig.class).getDsByColumnValue(shardingCode);
			if (StrUtil.isBlank(ds)) {
				matches = false;
				shardingCode = null;
			}
		}
		// 匹配失败
		Assert.isFalse(!matches && prop.getDsNum() > 1, "[sharding]未定义code:{}", code);
		// 设置默认值
		if (StrUtil.isBlank(shardingCode)) {
			shardingCode = ShardingConstant.DEFAULT_VALUE;
		}
		return shardingCode;
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