package com.chao.cloud.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.chao.cloud.common.config.web.EnableWeb;
import com.chao.cloud.common.extra.mybatis.annotation.EnableMybatisPlugins;
import com.chao.cloud.common.extra.token.annotation.EnableFormToken;

/**
 * admin-后台管理系统
 * @功能：
 * @author： 薛超
 * @时间：2019年3月13日
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching // 缓存
@EnableTransactionManagement // 事务
@EnableWeb // web
@EnableFormToken // 防止表单重复提交
@EnableMybatisPlugins // 数据库插件-乐观锁
public class ChaoAdminApplication {
    /**
     * 进度：https://gitee.com/whvse/treetable-lay/tree/master
     * 菜单列表
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ChaoAdminApplication.class, args);
    }

}
