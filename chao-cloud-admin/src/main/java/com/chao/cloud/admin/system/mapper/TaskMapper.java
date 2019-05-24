package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.chao.cloud.admin.system.domain.dto.TaskDTO;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年3月14日
 * @version 1.0.0
 */
@Mapper
public interface TaskMapper {

    TaskDTO get(Long id);

    List<TaskDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(TaskDTO task);

    int update(TaskDTO task);

    int remove(Long id);

    int batchRemove(Long[] ids);
}
