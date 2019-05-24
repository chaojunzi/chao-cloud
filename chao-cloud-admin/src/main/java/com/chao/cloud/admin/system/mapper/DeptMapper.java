package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chao.cloud.admin.system.domain.dto.DeptDTO;

/** 
 * 部门管理
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 15:35:39
 */
@Mapper
public interface DeptMapper extends BaseMapper<DeptDTO>{

    DeptDTO get(Long deptId);

    List<DeptDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(DeptDTO dept);

    int update(DeptDTO dept);

    int remove(Long deptId);

    int batchRemove(Long[] deptIds);

    Long[] listParentDept();

    int getDeptUserNumber(Long deptId);
}
