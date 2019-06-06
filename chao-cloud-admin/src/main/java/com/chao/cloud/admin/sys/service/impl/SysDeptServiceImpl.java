package com.chao.cloud.admin.sys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysDept;
import com.chao.cloud.admin.sys.dal.mapper.SysDeptMapper;
import com.chao.cloud.admin.sys.domain.dto.SelectTreeDTO;
import com.chao.cloud.admin.sys.service.SysDeptService;

import cn.hutool.core.collection.CollUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

	@Override
	public List<SelectTreeDTO> selectTree() {
		List<SysDept> list = this.list();
		// 递归转化
		List<SysDept> rootList = list.stream().filter(l -> AdminConstant.TREE_DEFAULT_VALUE.equals(l.getParentId()))
				.collect(Collectors.toList());
		// 赋值
		List<SelectTreeDTO> result = CollUtil.newArrayList();
		this.recursionTree(listConverTreeDTO(list), listConverTreeDTO(rootList), result);
		return result;
	}

	private void recursionTree(List<SelectTreeDTO> data, List<SelectTreeDTO> rootList, List<SelectTreeDTO> result) {
		// 遍历叶子节点
		rootList.forEach(r -> {
			// 转化
			result.add(r);
			recursionTree(data, r);
		});
	}

	private void recursionTree(List<SelectTreeDTO> data, SelectTreeDTO root) {
		// 获取根菜单下所有菜单
		List<SelectTreeDTO> leafDepts = data.stream().filter(d -> root.getId().equals(d.getPid()))
				.collect(Collectors.toList());
		if (CollUtil.isNotEmpty(leafDepts)) {
			root.setChildren(leafDepts);
			// 再次循环
			leafDepts.forEach(r -> recursionTree(data, r));
		}

	}

	private List<SelectTreeDTO> listConverTreeDTO(List<SysDept> list) {
		return list.stream().map(r -> {
			SelectTreeDTO dto = new SelectTreeDTO();
			dto.setId(r.getDeptId());
			dto.setPid(r.getParentId());
			dto.setValue(r.getDeptId());
			dto.setName(r.getName());
			return dto;
		}).collect(Collectors.toList());
	}

}
