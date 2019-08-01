package com.chao.cloud.common.extra.map;

import com.chao.cloud.common.extra.map.tencent.address.AddressResolveDTO;
import com.chao.cloud.common.extra.map.tencent.address.AddressVO;
import com.chao.cloud.common.extra.map.tencent.distance.DistanceResolveDTO;
import com.chao.cloud.common.extra.map.tencent.distance.DistanceVO;

/**
 * 地图服务
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public interface MapService {

	/**
	 * 地址转坐标
	 * @param vo {@link AddressVO}
	 * @return {@link AddressResolveDTO}
	 * @throws Exception 解析距离抛出的异常
	 */
	AddressResolveDTO addressToCoordinate(AddressVO vo) throws Exception;

	/**
	 * 距离计算一对多
	 * @param vo {@link DistanceVO}
	 * @return {@link DistanceResolveDTO}
	 * @throws Exception 计算距离抛出的异常
	 */
	DistanceResolveDTO distanceOneToMany(DistanceVO vo) throws Exception;

}
