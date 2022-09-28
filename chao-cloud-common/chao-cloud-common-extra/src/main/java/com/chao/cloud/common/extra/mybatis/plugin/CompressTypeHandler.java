package com.chao.cloud.common.extra.mybatis.plugin;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.chao.cloud.common.extra.mybatis.config.MyDataSourceProperties;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据压缩
 * 
 * @author 薛超
 * @since 2022年1月19日
 * @version 1.0.0
 */
@Slf4j
public class CompressTypeHandler extends BaseTypeHandler<String> {

	@Setter
	private static MyDataSourceProperties prop;

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
			throws SQLException {
		// 压缩
		boolean compress = this.isCompress();
		if (StrUtil.isNotBlank(parameter)) {
			if (compress) {
				parameter = Base64.encode(ZipUtil.gzip(parameter, CharsetUtil.UTF_8));
			}
		}
		// 加密
		ps.setString(i, parameter);
	}

	@Override
	public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
		// 解密
		String columnValue = rs.getString(columnName);
		return this.decrypt(columnValue);
	}

	@Override
	public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		// 解密
		String columnValue = rs.getString(columnIndex);
		return this.decrypt(columnValue);
	}

	@Override
	public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String columnValue = cs.getString(columnIndex);
		return this.decrypt(columnValue);
	}

	private String decrypt(String columnValue) {
		boolean compress = this.isCompress();
		if (!compress || StrUtil.isBlank(columnValue)) {
			return columnValue;
		}
		// 解密
		try {
			if (Base64.isBase64(columnValue)) {
				return ZipUtil.unGzip(Base64.decode(columnValue), CharsetUtil.UTF_8);
			}
		} catch (Exception e) {
			// 解密失败
			log.info("【解压缩失败】: {}", columnValue);
		}
		return columnValue;
	}

	private boolean isCompress() {
		if (prop == null) {
			try {
				prop = SpringUtil.getBean(MyDataSourceProperties.class);
			} catch (Exception e) {
				// 忽略此异常
			}
		}
		if (prop != null) {
			return prop.isColumnCompress();
		}
		return false;
	}

}
