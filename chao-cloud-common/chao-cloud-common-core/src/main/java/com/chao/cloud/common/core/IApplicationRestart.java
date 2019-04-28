package com.chao.cloud.common.core;

import com.chao.cloud.common.entity.Response;

/**
 * 顶级接口 --->约定：为了统一修改请使用xml配置该文件
 * 
 * @author 项目启动需要产生的行为
 *
 */
public interface IApplicationRestart {

    String SUCCESS = "SUCCESS";
    
    String ERROR = "ERROR";

    Response<?> restart();

}
