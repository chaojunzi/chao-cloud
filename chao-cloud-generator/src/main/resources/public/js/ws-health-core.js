var $, layer, wsHealthCore;
layui.use([ 'layer', 'jquery', 'element' ], function() {
	layer = layui.layer;
	$ = layui.$;
	var element = layui.element;
	// 连接websocket
	wsHealthCore = function() {
		var url = document.location.protocol == "https:" ? "wss://" : "ws://" + window.location.host// 
				+ "/health/core/" + $("#userId").val();
		// 连接服务器
		ws = new ReconnectingWebSocket(url);
		// 刚刚打开连接
		ws.onopen = function(evt) {
			console.log("Connection open ...");
		};

		// 接收消息的情况
		ws.onmessage = function(evt) {
			var r = JSON.parse(evt.data);
			console.log(r);
			switch (r.type) {
			case 0:// 关闭-已经登录
				layer.msg(r.msg);
				ws = null;
				setTimeout(function() {
					// 关闭当前页
					window.location.href = "about:blank";
				}, 1000);
				break;
			case 1:// 开始连接
				break;
			case 2:// 提示
				break;
			case 3:// 健康检查
				var health = r.msg;
				// 赋值-折线图
				if (lineOption.xAxis.data.length > 10) {
					lineOption.xAxis.data.shift();
					lineOption.series[0].data.shift();
				}
				lineOption.xAxis.data.push(now());
				lineOption.series[0].data.push(health.useMemory.replace(/[a-zA-Z]/g, ""));
				lineChart.setOption(lineOption, true);
				// 仪表盘
				panelOption.series[0].data[0].value = health.useRate.replace("%", "");
				panelChart.setOption(panelOption, true);
				// 内存列表
				$("#memoryInfo").text(health.useMemory + ' / ' + health.totalMemory);
				$("#threadCount").text(health.threadCount);
				$("#totalMemory").text(health.totalMemory);
				$("#useMemory").text(health.useMemory);
				$("#freeMemory").text(health.freeMemory);
				// 进度条
				element.progress('useRate', health.useRate)
				break;
			}
		};
		// 关闭连接的情况
		ws.onclose = function(evt) {
			console.log("连接关闭");
		};

		// 连接失败的情况
		ws.onerror = function(event) {
			console.log("连接失败");
		};
	}
});
