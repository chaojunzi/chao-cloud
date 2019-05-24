package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.chao.cloud.admin.system.domain.dto.LogDTO;

/**
 * 系统日志
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 15:45:42
 */
@Mapper
public interface LogMapper {

    LogDTO get(Long id);

    List<LogDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(LogDTO log);

    int update(LogDTO log);

    int remove(Long id);

    int batchRemove(Long[] ids);
}
