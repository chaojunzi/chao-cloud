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
package com.chao.cloud.common.support.mybatis.locker.interceptor;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.chao.cloud.common.support.mybatis.annotation.VersionLocker;
import com.chao.cloud.common.support.mybatis.locker.cache.Cache;
import com.chao.cloud.common.support.mybatis.locker.cache.Cache.MethodSignature;
import com.chao.cloud.common.support.mybatis.locker.cache.LocalVersionLockerCache;
import com.chao.cloud.common.support.mybatis.locker.cache.VersionLockerCache;
import com.chao.cloud.common.support.mybatis.locker.util.PluginUtil;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

/**
 * <p>
 * MyBatis乐观锁插件<br>
 * <p>
 * MyBatis Optimistic Locker Plugin<br>
 * 
 * @author 342252328@qq.com
 * @date 2016-05-27
 * @version 1.0
 * @since JDK1.7
 *
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
@Slf4j
public class OptimisticLockerInterceptor implements Interceptor {

    private static VersionLocker trueLocker;
    private static VersionLocker falseLocker;

    static {
        try {
            trueLocker = OptimisticLockerInterceptor.class.getDeclaredMethod("versionValueTrue")
                    .getAnnotation(VersionLocker.class);
            falseLocker = OptimisticLockerInterceptor.class.getDeclaredMethod("versionValueFalse")
                    .getAnnotation(VersionLocker.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("The plugin init faild.", e);
        }
    }

    private Properties props = null;
    private VersionLockerCache versionLockerCache = new LocalVersionLockerCache();

    @VersionLocker(true)
    private void versionValueTrue() {
    }

    @VersionLocker(false)
    private void versionValueFalse() {
    }

    @Override
    public Object intercept(Invocation invocation) throws Exception {
        String versionColumn;
        String versionField;
        if (null == props || props.isEmpty()) {
            versionColumn = "version";
            versionField = "version";
        } else {
            versionColumn = props.getProperty("versionColumn", "version");
            versionField = props.getProperty("versionField", "version");
        }
        String interceptMethod = invocation.getMethod().getName();
        if (!"prepare".equals(interceptMethod)) {
            return invocation.proceed();
        }
        Connection conn = (Connection) invocation.getArgs()[0];
        DatabaseMetaData data = conn.getMetaData();
        String url = data.getURL();
        log.info("\n【数据库信息：[jdbc-url={}]】", url);
        StatementHandler handler = (StatementHandler) PluginUtil.processTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(handler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        SqlCommandType sqlCmdType = ms.getSqlCommandType();
        if (sqlCmdType != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        VersionLocker vl = getVersionLocker(ms, boundSql);
        if (null != vl && !vl.value()) {
            return invocation.proceed();
        }
        Object originalVersion = metaObject.getValue("delegate.boundSql.parameterObject." + versionField);
        if (originalVersion == null || Long.parseLong(originalVersion.toString()) < 0) {
            throw new BindingException("value of version field[" + versionField + "]can not be empty");
        }
        String originalSql = boundSql.getSql();
        if (log.isDebugEnabled()) {
            log.debug("==> originalSql: " + originalSql);
        }

        originalSql = addVersionToSql(originalSql, versionColumn, originalVersion);
        metaObject.setValue("delegate.boundSql.sql", originalSql);

        metaObject.setValue("delegate.boundSql.parameterObject." + versionField,
                Optional.of(originalVersion).map(o -> Integer.parseInt(originalVersion.toString())).orElse(0) + 1);
        if (log.isDebugEnabled()) {
            log.debug("==> originalSql after add version: " + originalSql);
        }
        return invocation.proceed();
    }

    private String addVersionToSql(String originalSql, String versionColumnName, Object originalVersion) {
        try {
            Statement stmt = CCJSqlParserUtil.parse(originalSql);
            if (!(stmt instanceof Update)) {
                return originalSql;
            }
            Update update = (Update) stmt;
            if (!contains(update, versionColumnName)) {
                buildVersionExpression(update, versionColumnName);
            }
            Expression where = update.getWhere();
            if (where != null) {
                AndExpression and = new AndExpression(where, buildVersionEquals(versionColumnName, originalVersion));
                update.setWhere(and);
            } else {
                update.setWhere(buildVersionEquals(versionColumnName, originalVersion));
            }
            return stmt.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return originalSql;
        }
    }

    private boolean contains(Update update, String versionColumnName) {
        List<Column> columns = update.getColumns();
        for (Column column : columns) {
            if (column.getColumnName().equalsIgnoreCase(versionColumnName)) {
                return true;
            }
        }
        return false;
    }

    private void buildVersionExpression(Update update, String versionColumnName) {

        List<Column> columns = update.getColumns();
        Column versionColumn = new Column();
        versionColumn.setColumnName(versionColumnName);
        columns.add(versionColumn);

        List<Expression> expressions = update.getExpressions();
        Addition add = new Addition();
        add.setLeftExpression(versionColumn);
        add.setRightExpression(new LongValue(1));
        expressions.add(add);
    }

    private Expression buildVersionEquals(String versionColumnName, Object originalVersion) {
        EqualsTo equal = new EqualsTo();
        Column column = new Column();
        column.setColumnName(versionColumnName);
        equal.setLeftExpression(column);
        LongValue val = new LongValue(originalVersion.toString());
        equal.setRightExpression(val);
        return equal;
    }

    private VersionLocker getVersionLocker(MappedStatement ms, BoundSql boundSql) {

        Class<?>[] paramCls = null;
        Object paramObj = boundSql.getParameterObject();

        /****************** 下面处理参数只能按照下面3个的顺序 ***********************/
        /******************
         * Process param must order by below
         ***********************/
        // 1、处理@Param标记的参数
        // 1、Process @Param param
        if (paramObj instanceof MapperMethod.ParamMap<?>) {
            MapperMethod.ParamMap<?> mmp = (MapperMethod.ParamMap<?>) paramObj;
            if (null != mmp && !mmp.isEmpty()) {
                paramCls = new Class<?>[mmp.size() / 2];
                int mmpLen = mmp.size() / 2;
                for (int i = 0; i < mmpLen; i++) {
                    Object index = mmp.get("param" + (i + 1));
                    paramCls[i] = index.getClass();
                    if (List.class.isAssignableFrom(paramCls[i])) {
                        return falseLocker;
                    }
                }
            }

            // 2、处理Map类型参数
            // 2、Process Map param
        } else if (paramObj instanceof Map) {// 不支持批量
            @SuppressWarnings("rawtypes")
            Map map = (Map) paramObj;
            if (map.get("list") != null || map.get("array") != null) {
                return falseLocker;
            } else {
                paramCls = new Class<?>[] { Map.class };
            }
            // 3、处理POJO实体对象类型的参数
            // 3、Process POJO entity param
        } else if (paramObj != null) {
            paramCls = new Class<?>[] { paramObj.getClass() };
        }

        Cache.MethodSignature vm = new MethodSignature(ms.getId(), paramCls);
        VersionLocker versionLocker = versionLockerCache.getVersionLocker(vm);
        if (null != versionLocker) {
            return versionLocker;
        }

        Class<?> mapper = getMapper(ms);
        if (mapper != null) {
            Method m;
            try {
                m = mapper.getDeclaredMethod(getMapperShortId(ms), paramCls);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException("The Map type param error." + e, e);
            }
            versionLocker = m.getAnnotation(VersionLocker.class);
            if (null == versionLocker) {
                versionLocker = trueLocker;
            }
            if (!versionLockerCache.containMethodSignature(vm)) {
                versionLockerCache.cacheMethod(vm, versionLocker);
            }
            return versionLocker;
        } else {
            throw new RuntimeException("Config info error, maybe you have not config the Mapper interface");
        }
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

    private String getMapperShortId(MappedStatement ms) {
        String id = ms.getId();
        int pos = id.lastIndexOf(".");
        return id.substring(pos + 1);
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ParameterHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        if (null != properties && !properties.isEmpty())
            props = properties;
    }

}