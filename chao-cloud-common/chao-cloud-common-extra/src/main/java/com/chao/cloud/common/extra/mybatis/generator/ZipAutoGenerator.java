package com.chao.cloud.common.extra.mybatis.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.zip.ZipOutputStream;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.chao.cloud.common.extra.mybatis.generator.engine.ZipVelocityTemplateEngine;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * zip  
 * @功能：生成代码直接传至浏览器
 * @author： 薛超
 * @时间：2019年5月22日
 * @version 2.0
 */
@Slf4j
public class ZipAutoGenerator extends AutoGenerator {

    /**
     * 输出到流-zip
     * @param out
     * @throws IOException 
     */
    public void execute(OutputStream out) throws Exception {
        log.info("==========================准备生成文件...==========================");
        // 初始化配置
        if (null == config) {
            config = new ConfigBuilder(super.getPackageInfo(), super.getDataSource(), super.getStrategy(),
                    super.getTemplate(), super.getGlobalConfig());
            if (null != injectionConfig) {
                injectionConfig.setConfig(config);
            }
        }
        if (null == super.getTemplateEngine()) {
            // 为了兼容之前逻辑，采用 Velocity 引擎 【 默认 】
            super.setTemplateEngine(new ZipVelocityTemplateEngine());
        }
        // 模板引擎初始化执行文件输出
        ZipVelocityTemplateEngine engine = (ZipVelocityTemplateEngine) super.getTemplateEngine()
                .init(this.pretreatmentConfigBuilder(config));
        // 使用zip输出流
        try (ZipOutputStream zip = new ZipOutputStream(out);) {
            // 输出
            engine.batchOutput(zip);
        }
        log.info("==========================文件生成完成！！！==========================");
    }
}