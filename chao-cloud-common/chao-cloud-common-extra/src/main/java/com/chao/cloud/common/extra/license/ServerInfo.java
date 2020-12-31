package com.chao.cloud.common.extra.license;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import cn.hutool.core.exceptions.ExceptionUtil;

/**
 * 用于获取客户服务器的基本信息，如：IP、Mac地址、CPU序列号、主板序列号等
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
public interface ServerInfo {

	/**
	 * 获取CPU序列号
	 * 
	 * @return CPU序列号
	 */
	String getCPUSerial();

	/**
	 * 获取主板序列号
	 * 
	 * @return 主板序列号
	 */
	String getMainBoardSerial();

	/**
	 * 获取InetAddress
	 * 
	 * @return {@link InetAddress}
	 */
	static Set<InetAddress> getIpAddress() {
		Set<InetAddress> ipList = new HashSet<>();
		try {
			// 遍历所有的网络接口
			for (Enumeration<?> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces
					.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) networkInterfaces.nextElement();
				// 在所有的接口下再遍历IP
				for (Enumeration<?> inetAddresses = iface.getInetAddresses(); inetAddresses.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddresses.nextElement();
					// 排除LoopbackAddress、SiteLocalAddress、LinkLocalAddress、MulticastAddress类型的IP地址
					if (!inetAddr.isLoopbackAddress() /* && !inetAddr.isSiteLocalAddress() */
							&& !inetAddr.isLinkLocalAddress() && !inetAddr.isMulticastAddress()) {
						ipList.add(inetAddr);
					}
				}
			}
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
		return ipList;
	}

}
