package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chao.cloud.admin.system.domain.dto.MenuDTO;

/**
 * 菜单管理
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 09:45:09
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuDTO> {

	MenuDTO get(Long menuId);

	List<MenuDTO> list(Map<String, Object> map);

	int count(Map<String, Object> map);

	int save(MenuDTO menu);

	int update(MenuDTO menu);

	int remove(Long menuId);

	int batchRemove(Long[] menuIds);

	List<MenuDTO> listMenuByUserId(@Param(value = "id") Long id);

	List<MenuDTO> allMenu();

	List<String> listUserPerms(@Param(value = "id") Long id);
}
