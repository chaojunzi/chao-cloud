package com.chao.cloud.common.extra.mybatis.common;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 自定义sql对象
 * 
 * @author 薛超
 * @since 2021年4月12日
 * @version 1.0.0
 */
@Data(staticConstructor = "of")
@Accessors(chain = true)
public class ApplySql {

	/**
	 * 格式化sql<br>
	 * WHERE 之后的条件
	 */
	private ApplySqlSegment normalSegment;
	/**
	 * 分组字段
	 */
	private List<String> groupByList;
	/**
	 * 分组sql
	 */
	private ApplySqlSegment havingSegment;
	/**
	 * 排序字段
	 */
	private List<String> orderByList;

	/**
	 * sql和条件
	 */
	@Data(staticConstructor = "of")
	@Accessors(chain = true)
	public static class ApplySqlSegment {
		/**
		 * sql片段
		 */
		private String sqlSegment;
		/**
		 * 占位符中的参数值
		 */
		private List<ParamValue> valueList;
	}

	/**
	 * 参数值
	 */
	@Data(staticConstructor = "of")
	@Accessors(chain = true)
	public static class ParamValue {
		private String type;// 参数类型
		private Object value;// 参数值
	}

}
