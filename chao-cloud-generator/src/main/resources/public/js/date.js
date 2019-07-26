//时间格式转换
function now(fmt) {
	if (fmt == null || fmt == "") {
		fmt="HH:mm:ss";
	}
	return formatDate(new Date().getTime(),fmt);
}
function formatDate(value, fmt) {
	if (value == null || value == "") {
		return null;
	}
	var date = new Date(value);

	if(!fmt) {
		fmt = 'yyyy-MM-dd HH:mm:ss';
	}
	
	var o = {
		"M+": date.getMonth() + 1, // 月份
		"d+": date.getDate(), // 日
		"H+": date.getHours(), // 小时
		"m+": date.getMinutes(), // 分
		"s+": date.getSeconds(), // 秒
		"q+": Math.floor((date.getMonth() + 3) / 3), // 季度
		"S": date.getMilliseconds() // 毫秒
	};
	if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
	for (var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}