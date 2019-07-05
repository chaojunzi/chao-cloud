package com.chao.cloud.im.ai.model;

import java.util.List;

import lombok.Data;

@Data
public class BAiResp {

	private Integer error_code;
	private String error_msg;
	private Result result;

	@Data
	public static class Result {

		private String log_id;
		private String interaction_id;
		private String version;
		private List<Response> response_list;

		@Data
		public static class Response {
			private String msg;
			private String origin; // 技能编号
			private Schema schema;// 信任度
			private List<Action> action_list;

			@Data
			public static class Schema {
				private Double intent_confidence;// 一级准确度

			}

			@Data
			public static class Action {
				private String say;
				private String type;// 返回类型
				private Double confidence;// 二级准确度

			}
		}
	}

}
