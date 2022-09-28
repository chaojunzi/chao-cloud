package com.chao.cloud.common.extra.mybatis.processor;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.chao.cloud.common.extra.mybatis.common.DBConstant;
import com.chao.cloud.common.extra.mybatis.constant.DBContext;
import com.chao.cloud.common.util.ConfigUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 动态数据源配置处理器<br>
 * 请避免和mybatis-plus冲突<br>
 * {@link DataSourceProperties}
 * 
 * @author 薛超
 * @since 2022年6月1日
 * @version 1.0.0
 */
public class DataSourceProcessor implements EnvironmentPostProcessor {

	private final static String DS_URL_PROPERTY = "spring.datasource.url";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String url = null;
		for (PropertySource<?> ps : environment.getPropertySources()) {
			if (ps instanceof OriginTrackedMapPropertySource) {
				OriginTrackedMapPropertySource source = (OriginTrackedMapPropertySource) ps;
				Object str = source.getProperty(DS_URL_PROPERTY);
				if (str instanceof String) {
					url = (String) str;
					break;

				}
			}
		}
		if (StrUtil.isBlank(url)) {
			return;
		}
		Map<String, Object> map = MapUtil.newHashMap();
		DbType dbType = JdbcUtils.getDbType(url);
		String jarName = getJarName();
		String txt = StrUtil.EMPTY;
		switch (dbType) {
		case SQLITE:
			// 修改连接配置
			try {
				String dbPath = ConfigUtil.getMainFileName(url);
				if (StrUtil.isNotBlank(dbPath)) {
					DBContext.addDBFile(dbPath);
					url = StrUtil.format("jdbc:sqlite:{}", dbPath);
					map.put(DS_URL_PROPERTY, url);
					txt = StrUtil.format("{}={}", DS_URL_PROPERTY, url);
				}
			} catch (Exception e) {
				txt = StrUtil.format("【获取db文件失败:】[{}]: {}", url, e.getMessage());
				throw ExceptionUtil.wrapRuntime(e);
			}
			txt = StrUtil.format("[{}]: {}", DateUtil.now(), txt);
			if (StrUtil.isNotBlank(jarName)) {
				// 写入日志文件
				String dbInfo = FileUtil.normalize(StrUtil.format("{}/{}/db.txt", //
						ConfigUtil.getRootPath(), DBConstant.DB_PATH));
				FileUtil.writeUtf8String(txt, dbInfo);
			} else {
				Console.log(txt);
			}
			break;
		default:
			break;
		}
		// 将解密的数据放入环境变量，并处于第一优先级上
		if (CollUtil.isNotEmpty(map)) {
			environment.getPropertySources().addFirst(new MapPropertySource("local-ds-config", map));
		}
	}

	/**
	 * 获取jar包所在路径<br>
	 * 
	 * 1.（C）:/path/ukey-*.jar!/BOOT-INF/classes!/<br>
	 * 2.（C）:/path/ukey-*.jar<br>
	 * 
	 * @return jar包绝对路径
	 */
	static String getJarName() {
		String classPath = ClassUtil.getClassPath();
		return ReUtil.getGroup1("([\\w-]*\\.jar)", classPath);
	}

}
