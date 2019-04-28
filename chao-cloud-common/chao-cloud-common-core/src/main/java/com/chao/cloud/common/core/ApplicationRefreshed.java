package com.chao.cloud.common.core;

import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.chao.cloud.common.base.BaseLogger;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 此类用于容器启动完成后立即加载的类
 * 
 * @author 薛超
 *
 */
@Slf4j
public class ApplicationRefreshed implements ApplicationListener<ContextRefreshedEvent>, BaseLogger {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null) {// root
                                                                // application
                                                                // context
                                                                // 没有parent，他就是老大.
                                                                // 需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
                                                                // 验证实例化的bean
                                                                // 开始加载
                                                                // ok
                                                                // 1.加载类型转换器
        }
        String displayName = event.getApplicationContext().getDisplayName();
        if (displayName.contains("AnnotationConfigServletWebServerApplicationContext")) {
            // check();
            try {
                // initEditableValidation();
                this.check();
                // 注销 重置接口的实现类（执行一次后占用内存）
                reset();
            } catch (Exception e) {
                log.error("[错误:{}]", ExceptionUtil.getMessage(e));
            }
        } else {
            log.warn("容器[{}]启动", displayName);
        }
    }

    /**
     * 校验实例化的bean
     */
    private void check() {
        List<SpringBeanValidation> list = ApplicationOperation.getInterfaceImplClass(SpringBeanValidation.class);
        for (SpringBeanValidation springBeanValidation : list) {
            springBeanValidation.check();
        }
    }

    /**
     * 执行重置接口所实现的类
     */
    private void reset() {
        try {
            List<IApplicationRestart> interfaceImplClass = ApplicationOperation
                    .getInterfaceImplClass(IApplicationRestart.class);
            // 执行所有的restart方法
            for (IApplicationRestart restart : interfaceImplClass) {
                restart.restart();
            }
            // 销毁
            ApplicationOperation.destroyBeans(IApplicationRestart.class);
        } catch (Exception e) {
            log.error("[错误:{}]", ExceptionUtil.getMessage(e));
        }
    }

}