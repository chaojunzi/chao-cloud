package com.chao.cloud.common.extra.license;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于获取客户Linux服务器的基本信息
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Slf4j
public class LinuxServerInfo implements ServerInfo {

	@Override
	public String getCPUSerial() {
		// 序列号
		try {
			String[] cmds = { //
					"/bin/bash", //
					"-c", //
					"dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1"//
			};
			// 使用dmidecode命令获取CPU序列号
			String result = RuntimeUtil.execForStr(cmds);
			return StrUtil.trim(result);
		} catch (Exception e) {
			log.error("cpu 序列号获取失败", e);
		}
		return StrUtil.EMPTY;
	}

	@Override
	public String getMainBoardSerial() {
		// 序列号
		try {
			String[] cmds = { //
					"/bin/bash", //
					"-c", //
					"dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1"//
			};
			// 使用dmidecode命令获取主板序列号
			String result = RuntimeUtil.execForStr(cmds);
			return StrUtil.trim(result);
		} catch (Exception e) {
			log.error("主板序列号获取失败", e);
		}
		return StrUtil.EMPTY;
	}
}
