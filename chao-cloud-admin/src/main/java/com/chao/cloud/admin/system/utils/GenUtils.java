package com.chao.cloud.admin.system.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.chao.cloud.admin.system.constant.AdminConstant;
import com.chao.cloud.admin.system.domain.dto.ColumnDTO;
import com.chao.cloud.admin.system.domain.dto.TableDTO;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.date.DateUtil;

/**
 * 代码生成器   工具类
 */
public class GenUtils {

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        // 实体类
        templates.add("templates/system/generator/vm/entity.java.vm");
        // 持久层
        templates.add("templates/system/generator/vm/Mapper.java.vm");
        templates.add("templates/system/generator/vm/Mapper.xml.vm");
        // 业务层
        templates.add("templates/system/generator/vm/Service.java.vm");
        templates.add("templates/system/generator/vm/ServiceImpl.java.vm");
        // 控制层
        templates.add("templates/system/generator/vm/Controller.java.vm");
        // 视图层
        templates.add("templates/system/generator/vm/list.html.vm");
        templates.add("templates/system/generator/vm/list.js.vm");

        templates.add("templates/system/generator/vm/add.html.vm");
        templates.add("templates/system/generator/vm/add.js.vm");

        templates.add("templates/system/generator/vm/edit.html.vm");
        templates.add("templates/system/generator/vm/edit.js.vm");
        // templates.add("templates/system/generator/vm/menu.sql.vm");
        return templates;
    }

    /**
     * 生成代码
     */

    public static void generatorCode(Map<String, String> table, List<Map<String, String>> columns,
            ZipOutputStream zip) {
        // 配置信息
        Configuration config = getConfig();
        // 表信息
        TableDTO tableDO = new TableDTO();
        tableDO.setTableName(table.get("tableName"));
        tableDO.setComments(table.get("tableComment"));
        // 表名转换成Java类名
        String className = tableToJava(tableDO.getTableName(), config.getString("tablePrefix"),
                config.getString("autoRemovePre"));
        tableDO.setClassName(className);
        tableDO.setClassname(StringUtils.uncapitalize(className));

        // 列信息
        List<ColumnDTO> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnDTO columnDO = new ColumnDTO();
            columnDO.setColumnName(column.get("columnName"));
            columnDO.setDataType(column.get("dataType"));
            columnDO.setComments(column.get("columnComment"));
            columnDO.setExtra(column.get("extra"));

            // 列名转换成Java属性名
            String attrName = columnToJava(columnDO.getColumnName());
            columnDO.setAttrName(attrName);
            columnDO.setAttrname(StringUtils.uncapitalize(attrName));

            // 列的数据类型，转换成Java类型
            String attrType = config.getString(columnDO.getDataType(), "unknowType");
            columnDO.setAttrType(attrType);

            // 是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableDO.getPk() == null) {
                tableDO.setPk(columnDO);
            }

            columsList.add(columnDO);
        }
        tableDO.setColumns(columsList);

        // 没主键，则第一个字段为主键
        if (tableDO.getPk() == null) {
            tableDO.setPk(tableDO.getColumns().get(0));
        }

        // 设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        // 封装模板数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", tableDO.getTableName());
        map.put("comments", tableDO.getComments());
        map.put("pk", tableDO.getPk());
        map.put("className", tableDO.getClassName());
        map.put("classname", tableDO.getClassname());
        map.put("pathName", config.getString("pathName"));
        map.put("columns", tableDO.getColumns());
        map.put("package", config.getString("package"));
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtil.now());
        // 判断乐观锁
        Set<String> cols = columsList.stream().map(ColumnDTO::getColumnName).collect(Collectors.toSet());
        if (!cols.contains("version")) {
            map.put("unLock", true);
        }
        // 判断BigDecimal
        Set<String> attrType = columsList.stream().map(ColumnDTO::getAttrType).collect(Collectors.toSet());
        if (attrType.contains("BigDecimal")) {
            map.put("hasBigDecimal", true);
        }
        if (attrType.contains("Date")) {
            map.put("hasDate", true);
        }
        VelocityContext context = new VelocityContext(map);

        // 获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(//
                        getFileName(template, tableDO.getClassname(), tableDO.getClassName(), //
                                config.getString("package").substring(config.getString("package").lastIndexOf(".") + 1), //
                                config.getString("pathName")//
                        )));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new BusinessException("渲染模板失败，表名：" + tableDO.getTableName());
            }
        }
    }

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[] { '_' }).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix, String autoRemovePre) {
        if (AdminConstant.AUTO_REOMVE_PRE.equals(autoRemovePre)) {
            tableName = tableName.substring(tableName.indexOf("_") + 1);
        }
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replace(tablePrefix, "");
        }

        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static PropertiesConfiguration getConfig() {
        try {
            return new PropertiesConfiguration("config/generator.properties");
        } catch (ConfigurationException e) {
            throw new BusinessException("获取配置文件失败：文件未找到[config/generator.properties]");
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String classname, String className, String packageName,
            String pathName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        // String modulesname=config.getString("packageName");
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }

        if (template.contains("entity.java.vm")) {
            return packagePath + "dal\\genentity" + File.separator + className + "Entity.java";
        }

        if (template.contains("Mapper.java.vm")) {
            return packagePath + "dal\\genmapper" + File.separator + className + "Mapper.java";
        }
        if (template.contains("Mapper.xml.vm")) {
            return packagePath + "dal\\genmapper" + File.separator + className + "Mapper.xml";
        }

        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("list.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator + pathName
                    + File.separator + classname + File.separator + classname + ".html";
        }
        if (template.contains("add.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator + pathName
                    + File.separator + classname + File.separator + "add.html";
        }
        if (template.contains("edit.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator + pathName
                    + File.separator + classname + File.separator + "edit.html";
        }

        if (template.contains("list.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js"
                    + File.separator + "appjs" + File.separator + pathName + File.separator + classname + File.separator
                    + classname + ".js";
        }

        if (template.contains("add.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js"
                    + File.separator + "appjs" + File.separator + pathName + File.separator + classname + File.separator
                    + "add.js";
        }
        if (template.contains("edit.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js"
                    + File.separator + "appjs" + File.separator + pathName + File.separator + classname + File.separator
                    + "edit.js";
        }

        return null;
    }
}
