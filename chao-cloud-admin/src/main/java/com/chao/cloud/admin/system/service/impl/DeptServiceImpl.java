package com.chao.cloud.admin.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.DeptDTO;
import com.chao.cloud.admin.system.domain.dto.TreeDTO;
import com.chao.cloud.admin.system.mapper.DeptMapper;
import com.chao.cloud.admin.system.service.DeptService;
import com.chao.cloud.admin.system.utils.BuildTree;


@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper sysDeptMapper;
 
    @Override
    public DeptDTO get(Long deptId) {
        return sysDeptMapper.get(deptId);
    }

    @Override
    public List<DeptDTO> list(Map<String, Object> map) {
        return sysDeptMapper.list(map);
    }

    @Override
    public int count(Map<String, Object> map) {
        return sysDeptMapper.count(map);
    }

    @Override
    public int save(DeptDTO sysDept) {
        return sysDeptMapper.save(sysDept);
    }

    @Override
    public int update(DeptDTO sysDept) {
        return sysDeptMapper.update(sysDept);
    }

    @Override
    public int remove(Long deptId) {
        return sysDeptMapper.remove(deptId);
    }

    @Override
    public int batchRemove(Long[] deptIds) {
        return sysDeptMapper.batchRemove(deptIds);
    }

    @Override
    public TreeDTO<DeptDTO> getTree() {
        List<TreeDTO<DeptDTO>> trees = new ArrayList<TreeDTO<DeptDTO>>();
        List<DeptDTO> sysDepts = sysDeptMapper.list(new HashMap<String, Object>(16));
        for (DeptDTO sysDept : sysDepts) {
            TreeDTO<DeptDTO> tree = new TreeDTO<DeptDTO>();
            tree.setId(sysDept.getDeptId().toString());
            tree.setParentId(sysDept.getParentId().toString());
            tree.setText(sysDept.getName());
            Map<String, Object> state = new HashMap<>(16);
            state.put("opened", true);
            tree.setState(state);
            trees.add(tree);
        }
        // 默认顶级菜单为０，根据数据库实际情况调整
        TreeDTO<DeptDTO> t = BuildTree.build(trees);
        return t;
    }

    @Override
    public boolean checkDeptHasUser(Long deptId) {
        // TODO Auto-generated method stub
        //查询部门以及此部门的下级部门
        int result = sysDeptMapper.getDeptUserNumber(deptId);
        return result == 0 ? true : false;
    }

    /*@Override
    public List<Long> listChildrenIds(Long parentId) {
        List<DeptDTO> deptDOS = list(null);
        return treeMenuList(deptDOS, parentId);
    }

    List<Long> treeMenuList(List<DeptDTO> menuList, long pid) {
        List<Long> childIds = new ArrayList<>();
        for (DeptDTO mu : menuList) {
            //遍历出父id等于参数的id，add进子节点集合
            if (mu.getParentId() == pid) {
                //递归遍历下一级
                treeMenuList(menuList, mu.getDeptId());
                childIds.add(mu.getDeptId());
            }
        }
        return childIds;
    }*/

}
