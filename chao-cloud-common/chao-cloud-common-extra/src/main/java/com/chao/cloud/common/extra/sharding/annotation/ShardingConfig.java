package com.chao.cloud.common.extra.sharding.annotation;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.sharding.convert.HintShardingColumnProxy;
import com.chao.cloud.common.extra.sharding.convert.ShardingColumnConvert;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;

/**
 * shardingCode 自动转化配置
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Getter
@Configuration
public class ShardingConfig implements InitializingBean {

	@Bean
	@ConditionalOnMissingBean(HintShardingColumnProxy.class)
	HintShardingColumnProxy hintShardingColumnProxy(ShardingColumnConvert convert) {
		return new HintShardingColumnProxy(convert);
	}

	@Bean
	public ShardingProperties shardingProperties() {
		return new ShardingProperties();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, ShardingColumnConvert> shardingConvertMap = SpringUtil.getBeansOfType(ShardingColumnConvert.class);
		//
		if (MapUtil.isEmpty(shardingConvertMap)) {
			// 注册默认的列转换
			String beanName = StrUtil.lowerFirst(ShardingColumnConvert.class.getSimpleName());
			// 分片 orgCode 处理
			SpringUtil.registerBean(beanName, (ShardingColumnConvert) ShardingColumnConvert::buildShardingColumnModel);
		}
	}
}
