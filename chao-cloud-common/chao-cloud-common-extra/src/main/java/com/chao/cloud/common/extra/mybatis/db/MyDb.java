package com.chao.cloud.common.extra.mybatis.db;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import com.chao.cloud.common.extra.mybatis.model.ColsModel;
import com.chao.cloud.common.extra.mybatis.model.DBEntity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据库相关操作
 * 
 * @author 薛超
 * @since 2021年12月24日
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data(staticConstructor = "of")
@Slf4j
public class MyDb extends Db {

	private static final long serialVersionUID = 1L;

	public MyDb(DataSource ds) {
		super(ds);
	}

	public static MyDb use(DataSource ds) {
		return ds == null ? null : new MyDb(ds);
	}

	/**
	 * 查询或执行
	 *
	 * @param sql    查询语句
	 * @param params 参数
	 * @return 结果对象
	 */
	public DBEntity queryOrExecute(String sql, Object... params) {
		Connection conn = null;
		TimeInterval time = new TimeInterval();
		time.start();
		try {
			conn = this.getConnection();
			List<Entity> records = null;
			log.info("[SQL] ==> {}", sql);
			try {
				records = SqlExecutor.query(conn, sql, new EntityListHandler(this.caseInsensitive), params);
			} catch (Exception e) {
				if (!StrUtil.equalsAnyIgnoreCase(e.getMessage(), "Query does not return results")) {
					throw e;
				}
				// 需执行非查询语句
			}
			boolean r = records != null;
			DBEntity result = new DBEntity(r, sql);
			if (r) {
				// 设置列信息
				Entity e = CollUtil.getFirst(records);
				if (e != null) {
					// 按照自然排序
					Set<String> colums = e.getFieldNames();
					List<ColsModel> colsList = CollUtil.map(colums, c -> new ColsModel().setTitle(c).setField(c), true);
					result.setColsList(colsList);
				}
				// 设置返回值
				result.setRecords(records);
				result.setTotal(CollUtil.size(records));
			} else {
				int total = this.execute(sql, params);
				result.setTotal(total);
			}
			return result.setTime(time.intervalPretty());
		} catch (Exception e) {
			log.info("【SQL执行失败】[{}]: {}", sql, e.getMessage(), e);
			return new DBEntity(false, sql).setMsg(e.getMessage()).setTime(time.intervalPretty());
		} finally {
			this.closeConnection(conn);
		}
	}

}
