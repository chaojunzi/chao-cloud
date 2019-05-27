package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chao.cloud.admin.system.domain.dto.LogDTO;

/**
 * 
 * @功能：系统日志
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Mapper
public interface LogMapper extends BaseMapper<LogDTO>{

    LogDTO get(Long id);

    List<LogDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(LogDTO log);

    int update(LogDTO log);

    int remove(Long id);

    int batchRemove(Long[] ids);
}
