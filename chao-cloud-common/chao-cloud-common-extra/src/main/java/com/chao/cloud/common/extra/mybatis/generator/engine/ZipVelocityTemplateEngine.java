package com.chao.cloud.common.extra.mybatis.generator.engine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.mybatis.annotation.EnableMybatisGenerator;
import com.chao.cloud.common.extra.mybatis.constant.KeyWordConstant;
import com.chao.cloud.common.extra.mybatis.generator.parse.TableCommentParse;
import com.chao.cloud.common.extra.mybatis.generator.template.HtmlTemplateConfig;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 将模板输出至浏览器
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public class ZipVelocityTemplateEngine extends VelocityTemplateEngine {

	public void writer(VelocityContext context, String templatePath, ZipOutputStream zip, String zipNodeFile)
			throws Exception {
		if (StrUtil.isBlank(templatePath)) {
			return;
		}
		VelocityEngine velocityEngine = (VelocityEngine) ReflectUtil.getFieldValue(this, "velocityEngine");
		// 渲染模板
		StringWriter sw = new StringWriter();
		Template tpl = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
		tpl.merge(context, sw);
		try {
			// 获取全局配置信息
			String outputDir = getConfigBuilder().getGlobalConfig().getOutputDir();
			// 替换->修复路径
			zipNodeFile = StrUtil.removePrefix(FileUtil.normalize(zipNodeFile.replace(outputDir, "")), "/");
			// 添加到zip
			zip.putNextEntry(new ZipEntry(zipNodeFile));
			IOUtils.write(sw.toString(), zip, "UTF-8");
			IOUtils.closeQuietly(sw);
			zip.closeEntry();
		} catch (IOException e) {
			throw new BusinessException("渲染模板失败，模块：" + zipNodeFile);
		}
		logger.info("模板: {};", zipNodeFile);
	}

	/**
	 * 输出到文件流
	 * @param zip zip输出流
	 * @return {@link AbstractTemplateEngine}
	 * @throws Exception 生成文件模板时抛出的异常
	 */
	public AbstractTemplateEngine batchOutput(ZipOutputStream zip) throws Exception {
		try {
			List<TableInfo> tableInfoList = getConfigBuilder().getTableInfoList();
			for (TableInfo tableInfo : tableInfoList) {
				Map<String, Object> objectMap = super.getObjectMap(tableInfo);
				Map<String, String> pathInfo = getConfigBuilder().getPathInfo();
				TemplateConfig template = getConfigBuilder().getTemplate();
				// 自定义内容
				InjectionConfig injectionConfig = getConfigBuilder().getInjectionConfig();
				if (null != injectionConfig) {
					injectionConfig.initMap();
					objectMap.put("cfg", injectionConfig.getMap());
					List<FileOutConfig> focList = injectionConfig.getFileOutConfigList();
					if (CollectionUtils.isNotEmpty(focList)) {
						for (FileOutConfig foc : focList) {
							if (isCreate(FileType.OTHER, foc.outputFile(tableInfo))) {
								writer(objectMap, foc.getTemplatePath(), foc.outputFile(tableInfo));
							}
						}
					}
				}
				// appendTableInfo
				this.appendTableInfo(tableInfo, objectMap);
				VelocityContext context = new VelocityContext(objectMap);
				// MpEntity.java
				String entityName = tableInfo.getEntityName();
				if (null != entityName && null != pathInfo.get(ConstVal.ENTITY_PATH)) {
					String entityFile = String.format(
							(pathInfo.get(ConstVal.ENTITY_PATH) + File.separator + "%s" + suffixJavaOrKt()),
							entityName);
					if (isCreate(FileType.ENTITY, entityFile)) {
						this.writer(context,
								templateFilePath(template.getEntity(getConfigBuilder().getGlobalConfig().isKotlin())),
								zip, entityFile);
					}
				}
				// MpMapper.java
				if (null != tableInfo.getMapperName() && null != pathInfo.get(ConstVal.MAPPER_PATH)) {
					String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator
							+ tableInfo.getMapperName() + suffixJavaOrKt()), entityName);
					if (isCreate(FileType.MAPPER, mapperFile)) {
						this.writer(context, templateFilePath(template.getMapper()), zip, mapperFile);
					}
				}
				// MpMapper.xml
				if (null != tableInfo.getXmlName() && null != pathInfo.get(ConstVal.XML_PATH)) {
					String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator
							+ tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
					if (isCreate(FileType.XML, xmlFile)) {
						this.writer(context, templateFilePath(template.getXml()), zip, xmlFile);
					}
				}
				// IMpService.java
				if (null != tableInfo.getServiceName() && null != pathInfo.get(ConstVal.SERVICE_PATH)) {
					String serviceFile = String.format((pathInfo.get(ConstVal.SERVICE_PATH) + File.separator
							+ tableInfo.getServiceName() + suffixJavaOrKt()), entityName);
					if (isCreate(FileType.SERVICE, serviceFile)) {
						this.writer(context, templateFilePath(template.getService()), zip, serviceFile);
					}
				}
				// MpServiceImpl.java
				if (null != tableInfo.getServiceImplName() && null != pathInfo.get(ConstVal.SERVICE_IMPL_PATH)) {
					String implFile = String.format((pathInfo.get(ConstVal.SERVICE_IMPL_PATH) + File.separator
							+ tableInfo.getServiceImplName() + suffixJavaOrKt()), entityName);
					if (isCreate(FileType.SERVICE_IMPL, implFile)) {
						this.writer(context, templateFilePath(template.getServiceImpl()), zip, implFile);
					}
				}
				// MpController.java
				if (null != tableInfo.getControllerName() && null != pathInfo.get(ConstVal.CONTROLLER_PATH)) {
					String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator
							+ tableInfo.getControllerName() + suffixJavaOrKt()), entityName);
					if (isCreate(FileType.CONTROLLER, controllerFile)) {
						this.writer(context, templateFilePath(template.getController()), zip, controllerFile);
					}
				}
				// html->模板
				if (template instanceof HtmlTemplateConfig) {
					this.genHtml(tableInfo, (HtmlTemplateConfig) template, context, zip);
				}
			}
		} catch (Exception e) {
			logger.error("无法创建文件，请检查配置信息！", e);
			throw e;
		}
		return this;
	}

	/**
	 * 添加
	 * @param tableInfo 表信息
	 * @param objectMap 模板对象
	 */
	private void appendTableInfo(TableInfo tableInfo, Map<String, Object> objectMap) {
		// 请求路径前缀
		objectMap.put("controllerMappingPrefix", tableInfo.getName().replaceFirst("_", "/"));
		// shiroPermissionsPrefix
		objectMap.put("shiroPermissionsPrefix", tableInfo.getName().replaceFirst("_", ":"));
		// 是否存在date 数据类型
		boolean hasDate = tableInfo.getFields().stream()
				.anyMatch(f -> DbColumnType.DATE.getType().equals(f.getPropertyType()));
		if (hasDate) {
			tableInfo.getImportPackages().add("org.springframework.format.annotation.DateTimeFormat");
			tableInfo.getImportPackages().add("cn.hutool.core.date.DatePattern");
		}
		// 版本
		objectMap.put("version", EnableMybatisGenerator.VERSION);
		// 主键名称类型
		TableField pk = tableInfo.getFields().stream().filter(f -> f.isKeyFlag()).findFirst().orElse(null);
		objectMap.put("pk", pk);
		// 弹窗大小
		long i = tableInfo.getFields().stream().filter(f -> !(f.isKeyFlag()
				|| f.getName().equals(getConfigBuilder().getStrategyConfig().getVersionFieldName()))).count();
		long rate = 30 + (i - 1) * 8;// 比率
		objectMap.put("openHeight", rate > 100 ? 100 : rate);
		// 模糊查询 tableInfo
		String comment = tableInfo.getComment();
		Map<String, String> map = this.commentToMap(comment);
		TableCommentParse parse = BeanUtil.mapToBean(map, TableCommentParse.class, true);
		objectMap.put("likeFields", parse.parseLike(tableInfo.getFields()));
		// 标题
		objectMap.put("menuTitle", parse.getTitle());
		// 关键字-保留字处理
		Map<String, String> kws = tableInfo.getFields().stream()
				.collect(Collectors.toMap(TableField::getName, t -> KeyWordConstant.contains(t.getName()) ? "`" : ""));
		objectMap.put("kws", kws);
		if (kws.values().contains("`")) {
			tableInfo.getImportPackages().add("com.baomidou.mybatisplus.annotation.TableField");
		}
	}

	private Map<String, String> commentToMap(String comment) {
		Map<String, String> map = MapUtil.newHashMap();
		// 读取注释为单行对象
		if (StrUtil.isNotBlank(comment)) {
			Stream.of(comment.replace("\r", "").split("\n")).filter(c -> c.contains("=")).forEach(c -> {
				List<String> comList = StrUtil.split(c, "=", 2, true, true);// 该list size固定为2
				map.put(comList.get(0), comList.get(1));
			});
		}
		return map;
	}

	/**
	 * 生成html
	 * @throws Exception 
	 */
	private void genHtml(TableInfo tableInfo, HtmlTemplateConfig template, VelocityContext context, ZipOutputStream zip)
			throws Exception {
		String tableName = tableInfo.getName();
		// html list.html
		String outListFile = StrUtil.format(template.getHtmlListPath(), tableName.replaceFirst("_", "/"));
		this.writer(context, templateFilePath(template.getList()), zip, outListFile);
		// html add.html
		String outAddFile = StrUtil.format(template.getHtmlAddPath(), tableName.replaceFirst("_", "/"));
		this.writer(context, templateFilePath(template.getAdd()), zip, outAddFile);
		// html edit.html
		String outEditFile = StrUtil.format(template.getHtmlEditPath(), tableName.replaceFirst("_", "/"));
		this.writer(context, templateFilePath(template.getEdit()), zip, outEditFile);

	}

}
