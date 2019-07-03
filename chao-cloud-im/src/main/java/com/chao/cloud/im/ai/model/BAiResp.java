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
			private List<Action> action_list;

			@Data
			public static class Action {
				private String say;
			}
		}
	}

}
