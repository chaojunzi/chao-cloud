package com.chao.cloud.admin.system.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chao.cloud.admin.system.domain.dto.TreeDTO;

public class BuildTree {

    public static <T> TreeDTO<T> build(List<TreeDTO<T>> nodes) {

        if (nodes == null) {
            return null;
        }
        List<TreeDTO<T>> topNodes = new ArrayList<TreeDTO<T>>();

        for (TreeDTO<T> children : nodes) {

            String pid = children.getParentId();
            if (pid == null || "0".equals(pid)) {
                topNodes.add(children);

                continue;
            }

            for (TreeDTO<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);
                    continue;
                }
            }

        }

        TreeDTO<T> root = new TreeDTO<T>();
        if (topNodes.size() == 1) {
            root = topNodes.get(0);
        } else {
            root.setId("-1");
            root.setParentId("");
            root.setHasParent(false);
            root.setChildren(true);
            root.setChecked(true);
            root.setChildren(topNodes);
            root.setText("顶级节点");
            Map<String, Object> state = new HashMap<>(16);
            state.put("opened", true);
            root.setState(state);
        }

        return root;
    }

    public static <T> List<TreeDTO<T>> buildList(List<TreeDTO<T>> nodes, String idParam) {
        if (nodes == null) {
            return null;
        }
        List<TreeDTO<T>> topNodes = new ArrayList<TreeDTO<T>>();

        for (TreeDTO<T> children : nodes) {

            String pid = children.getParentId();
            if (pid == null || idParam.equals(pid)) {
                topNodes.add(children);

                continue;
            }

            for (TreeDTO<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);

                    continue;
                }
            }

        }
        return topNodes;
    }

}