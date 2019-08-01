package com.chao.cloud.common.extra.ftp.annotation;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.ftp.IFileOperation;
import com.chao.cloud.common.extra.ftp.impl.FileOperationImpl;
import com.chao.cloud.common.extra.ftp.pool.FtpClientFactory;
import com.chao.cloud.common.extra.ftp.pool.FtpClientPool;
import com.chao.cloud.common.extra.ftp.pool.FtpClientProxy;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * ftp 配置
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.ftp.config")
public class FtpConfig implements InitializingBean {
	// 扩展设置
	private boolean local = false;// 默认是ftp
	private String prefix;// 返回时去掉的路径
	private String path;// path-根目录
	private String logo;// logo-水印
	private float alpha = 0.1F;// 透明度
	private String realm = StrUtil.EMPTY;// 域名->默认为空
	// 基础设置
	private String host;
	private int port;
	private String username;
	private String password;
	private boolean passiveMode = true;// 被动模式
	private String encoding = CharsetUtil.UTF_8; // 编码
	private int clientTimeout = 6000;// 超时时间
	private int threadNum = 3;// 线程数
	private int transferFileType = 2;// 0=ASCII_FILE_TYPE（ASCII格式） 1=EBCDIC_FILE_TYPE 2=LOCAL_FILE_TYPE（二进制文件）
	private boolean renameUploaded = true;// 是否重命名
	private int retryTimes = 1200;// 重新连接时间（秒）
	private int bufferSize = 1024;// 缓存大小
	private String workingDirectory = StrUtil.EMPTY;// 默认路径
	// 线程池
	private boolean blockWhenExhausted = true;// 池对象耗尽之后是否阻塞,maxWait<0时一直等待
	private long maxWaitMillis = 3000; // 最大等待时间(毫秒)
	private int minIdle = 1; // 最小空闲
	private int maxIdle = 3; // 最大空闲
	private int maxTotal = 10; // 最大连接数
	private boolean testOnBorrow = true; // 取对象时验证
	private boolean testOnReturn = true; // 回收验证
	private boolean testOnCreate = true; // 创建时验证
	private boolean testWhileIdle = false; // 空闲验证
	private boolean lifo = false; // 后进先出

	@Bean
	public IFileOperation fileOperation(FtpConfig ftpConfig) {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setBlockWhenExhausted(ftpConfig.blockWhenExhausted);
		poolConfig.setMaxWaitMillis(ftpConfig.maxWaitMillis);
		poolConfig.setMinIdle(ftpConfig.minIdle);
		poolConfig.setMaxIdle(ftpConfig.maxIdle);
		poolConfig.setMaxTotal(ftpConfig.maxTotal);
		poolConfig.setTestOnBorrow(ftpConfig.testOnBorrow);
		poolConfig.setTestOnReturn(ftpConfig.testOnReturn);
		poolConfig.setTestOnCreate(ftpConfig.testOnCreate);
		poolConfig.setTestWhileIdle(ftpConfig.testWhileIdle);
		poolConfig.setLifo(ftpConfig.lifo);
		// 注入对象
		FtpClientFactory factory = new FtpClientFactory(ftpConfig);
		FtpClientPool ftpClientPool = new FtpClientPool(new GenericObjectPool(factory, poolConfig));
		FtpClientProxy ftpClientProxy = new FtpClientProxy(ftpClientPool);
		FileOperationImpl operation = new FileOperationImpl();
		operation.setFtpConfig(ftpConfig);
		operation.setFtpClientProxy(ftpClientProxy);
		return operation;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (local && StrUtil.isBlank(path)) {// 若不设置上传地址->使用classpass下的 public img目录
			this.path = StrUtil.format("{}public/img", ClassUtil.getClassPath());
			this.prefix = StrUtil.removeSuffix(path, "/img");
		}
		if (StrUtil.isBlank(path)) {
			throw new BusinessException("请设置ftp: path");
		}
		if (StrUtil.isBlank(logo)) {
			logo = "chao";
		}
	}
}