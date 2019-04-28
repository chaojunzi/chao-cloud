/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.chao.cloud.common.extra.mybatis.intercept;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;

import com.chao.cloud.common.base.BaseLogger;
import com.chao.cloud.common.extra.mybatis.locker.util.PluginUtil;

/**
 * <p>
 * 打印sql日志
 * <p>
 * 
 * @since JDK1.8
 *
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class Slf4jLogInterceptor implements Interceptor, BaseLogger {

    private static StringBuilder parameter = new StringBuilder();

    @Override
    public Object intercept(Invocation invocation) throws Exception {
        parameter.delete(0, parameter.length());
        String interceptMethod = invocation.getMethod().getName();
        if (!"prepare".equals(interceptMethod)) {
            return invocation.proceed();
        }
        StatementHandler handler = (StatementHandler) PluginUtil.processTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        SqlCommandType sqlCmdType = ms.getSqlCommandType();
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String sqlId = ms.getId();
        Class<?> mapper = getMapper(ms);
        Logger logger = logger(mapper);
        Configuration configuration = ms.getConfiguration();
        String sql = getSql(configuration, boundSql);
        Object proceed = invocation.proceed();
        // sql--->参数：
        logger.info(
                "\n【SQL--- Mapper ===>】 [{}]"//
                        + "\n【SQL--- Type ===>】[{}]"//
                        + "\n【SQL--- Preparing ===>】 [{}]" //
                        + "\n【SQL--- Parameter ===>】 [{}]", //
                sqlId, sqlCmdType, sql, parameter);
        return proceed;
    }

    private Class<?> getMapper(MappedStatement ms) {
        String namespace = getMapperNamespace(ms);
        Collection<Class<?>> mappers = ms.getConfiguration().getMapperRegistry().getMappers();
        for (Class<?> clazz : mappers) {
            if (clazz.getName().equals(namespace)) {
                return clazz;
            }
        }
        return null;
    }

    private String getMapperNamespace(MappedStatement ms) {
        String id = ms.getId();
        int pos = id.lastIndexOf(".");
        return id.substring(0, pos);
    }

    /**
     * sql执行的具体位置与sql的拼接
     */
    public static String getSql(Configuration configuration, BoundSql boundSql) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder();
        str.append(sql);
        return str.toString();
    }

    /**
     * 拼接sql中的所有参数
     */
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }

    /**
     * 解析sql
     */
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                String value = getParameterValue(parameterObject);
                // sql = sql.replaceFirst("\\?",
                // getParameterValue(parameterObject));
                parameter.append("(");
                parameter.append(value);
                parameter.append(")");
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        String value = getParameterValue(obj);
                        // sql = sql.replaceFirst("\\?", value);
                        parameter.append("(");
                        parameter.append(value);
                        parameter.append(")");
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        String value = getParameterValue(obj);
                        // sql = sql.replaceFirst("\\?", value);
                        parameter.append("(");
                        parameter.append(value);
                        parameter.append(")");
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ParameterHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    public void setProperties(Properties properties0) {
    }

}