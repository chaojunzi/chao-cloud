package com.chao.cloud.common.extra.license;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于获取客户Windows服务器的基本信息
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Slf4j
public class WindowsServerInfo implements ServerInfo {

	@Override
	public String getCPUSerial() {
		// cpu序列号
		try {
			String result = RuntimeUtil.execForStr("wmic cpu get processorid").replace("ProcessorId", StrUtil.EMPTY);
			return StrUtil.trim(result);
		} catch (Exception e) {
			log.error("cpu 序列号获取失败", e);
		}
		return StrUtil.EMPTY;
	}

	@Override
	public String getMainBoardSerial() {
		try {
			// 使用WMIC获取主板序列号
			String result = RuntimeUtil.execForStr("wmic baseboard get serialnumber").replace("SerialNumber",
					StrUtil.EMPTY);
			return StrUtil.trim(result);
		} catch (Exception e) {
			log.error("主板序列号获取失败", e);
		}
		return StrUtil.EMPTY;
	}

}
