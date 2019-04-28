package com.chao.cloud.common.extra.map;

import com.chao.cloud.common.extra.map.tencent.address.AddressResolveDTO;
import com.chao.cloud.common.extra.map.tencent.address.AddressVO;
import com.chao.cloud.common.extra.map.tencent.distance.DistanceResolveDTO;
import com.chao.cloud.common.extra.map.tencent.distance.DistanceVO;

/**
 * 地图服务
 * @功能：
 * @author： 薛超
 * @时间：2019年3月5日
 * @version 1.0.0
 */
public interface MapService {  

    /**
     * 地址转坐标
     * @return
     */
    AddressResolveDTO addressToCoordinate(AddressVO vo) throws Exception;

    /**
     * 距离计算一对多
     * @return
     */
    DistanceResolveDTO distanceOneToMany(DistanceVO vo) throws Exception;

}
