var $, layer, connSingleChat, layim, ws;
layui.use([ 'layer', 'jquery' ], function() {
	layer = layui.layer;
	$ = layui.$;
	
	$("#off_line").click(function(){
		// 退出
		layer.msg('您确定要下线吗？', {
			   time: 0 // 不自动关闭
			  ,btn: ['确定', '取消']
			  ,yes: function(index){
			    layer.close(index);
			    ws.close();
			    window.top.location ="/logout";
			  }
			});
	});
	
	// 连接websocket
	connSingleChat = function() {
		var url = document.location.protocol == "https:" ? "wss://" : "ws://" + window.location.host// 
				+ "/im/chat/" + $("#userId").val();
		// 连接

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
				setTimeout(function() {
					// 跳转登录页
					window.top.location = "/logout";
				}, 3000);
				break;
			case 1:// 开始连接
				break;
			case 2:// 提示
				break;
			case 3:// 上线
				layim.setFriendStatus(r.msg, 'online');
				break;
			case 4:// 下线
				layim.setFriendStatus(r.msg, 'offline');
				break;
			case 5:// 聊天->追加消息
				layim.setChatStatus('');// 清除对方正在输入
				layim.getMessage(r.msg);
				break;
			case 6:// 离线消息
				$.each(r.msg,function(i,item){
					layim.getMessage(item);
　				　});
				break;
			case 7:// 对方正在输入
				layim.setChatStatus(r.msg);
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
