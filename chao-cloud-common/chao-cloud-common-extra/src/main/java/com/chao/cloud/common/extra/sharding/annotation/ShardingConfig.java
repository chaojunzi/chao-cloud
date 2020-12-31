package com.chao.cloud.common.extra.sharding.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.sharding.convert.HintShardingColumnProxy;
import com.chao.cloud.common.extra.sharding.convert.ShardingColumnConvert;

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
public class ShardingConfig {

	@Bean
	@ConditionalOnMissingBean(SpringUtil.class)
	SpringUtil springUtil() {
		return new SpringUtil();
	}

	@Bean
	ShardingProperties shardingProperties() {
		return new ShardingProperties();
	}

	@Bean
	@ConditionalOnMissingBean(ShardingColumnConvert.class)
	ShardingColumnConvert shardingColumnConvert() {
		// 返回原字段
		return orgCode -> orgCode;
	}

	@Bean
	@ConditionalOnMissingBean(HintShardingColumnProxy.class)
	HintShardingColumnProxy hintShardingColumnProxy(ShardingColumnConvert convert) {
		return new HintShardingColumnProxy(convert);
	}
}
