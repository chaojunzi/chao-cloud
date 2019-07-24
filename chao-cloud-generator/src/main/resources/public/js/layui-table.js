/**
 * 自定义分页参数
 */
var chaoLayuiTable = {
	// 请求
	request : {
		pageName : 'current', // 页码的参数名称，默认：page
		limitName : 'size', // 每页数据量的参数名，默认：limit
	},
	// 响应
	response : {
		statusName : 'code', // 规定数据状态的字段名称，默认：code
		statusCode : 0000, // 规定成功的状态码，默认：0
		msgName : 'msg',// 规定状态信息的字段名称，默认：msg
		countName : 'count', // 规定数据总数的字段名称，默认：count
		dataName : 'data', // 规定数据列表的字段名称，默认：data
	},
	// 数据解析
	parseData : function(res) { // res 即为原始返回的数据
		if (res.retCode != '0000') {
			return {
				"code" : res.retCode, // 解析接口状态
				"msg" : res.retMsg, // 解析提示文本
			}
		}
		return {
			"code" : res.retCode, // 解析接口状态
			"msg" : res.retMsg, // 解析提示文本
			"count" : res.body.total, // 解析数据长度
			"data" : res.body.records
		// 解析数据列表
		};
	}
}