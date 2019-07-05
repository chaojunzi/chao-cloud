package com.chao.cloud.im.ai.model;

import lombok.Data;

/**

{
"ret":0,
"msg":"ok",
"data":{
"session":"1",
"answer":"呃。。你应该不认得我。。。。。用最末的话说曾经点头之交吧"
}
}

*/
@Data
public class TAiResp {
	private Integer ret;
	private String msg;
	private ResultData data;

	@Data
	public static class ResultData {
		private String session;
		private String answer;
		private String text;

	}
}
