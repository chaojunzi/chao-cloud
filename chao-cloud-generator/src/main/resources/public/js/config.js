var $, layer;
layui.use([ 'layer', 'jquery' ], function() {
	layer = layui.layer;
	$ = layui.$;
});

/**
 * 不刷新当前页
 * 
 * @param url
 * @param data
 * @returns
 */
function ajaxPost(url, data) {
	ajaxPostLoad(url, data, false);
}

/**
 * 全局ajax-post提交(刷新当前页)
 */
function ajaxPostLoad(url, data, reload) {
	$.ajax({
		cache : true,
		type : "POST",
		url : url,
		data : data,
		// async : false, //同步
		layerIndex : -1,
		beforeSend : function() {
			this.layerIndex = parent.layer.load(0, {
				shade : [ 0.1, '#000' ]
			});
		},
		complete : function() {
			parent.layer.close(this.layerIndex);
		},
		success : function(data) {
			if (data.retCode == '0000') {
				parent.layer.msg("操作成功");
				if (reload != false) {// 刷新
					parent.reloadCurrTab(); // 当前页面刷新
					var index = parent.layer.getFrameIndex(window.name); // 获取窗口索引
					parent.layer.close(index);
				}
			} else {
				layer.alert(data.retMsg)
			}
		},
		error : function(request) {
			layer.msg("请检查参数");
		}
	});
}
