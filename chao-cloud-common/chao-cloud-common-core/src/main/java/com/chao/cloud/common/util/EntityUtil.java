package com.chao.cloud.common.util;

import java.util.List;
import java.util.stream.Collectors;

import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * 实体转换
 * @功能：
 * @author： 薛超
 * @时间：2019年4月24日
 * @version 1.0.0
 */
public class EntityUtil {

    /**
     * 转换list
     * 
     * @param source
     *            要转换的list
     * @param clazz
     *            需要转换的 class
     */
    public static <T, S> List<T> listConver(List<S> source, Class<T> clazz) {
        if (ObjectUtil.isNull(clazz)) {
            throw new BusinessException("class is null");
        }
        if (!CollUtil.isEmpty(source)) {
            return source.stream().map(s -> BeanUtil.toBean(s, clazz)).collect(Collectors.toList());
        }
        return null;
    }
}