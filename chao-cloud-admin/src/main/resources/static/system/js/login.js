window.onload = function() {
	if (window.parent.window != window) {
		window.top.location = "/login";
	}
}

layui.use([ 'form', 'layer', 'jquery' ], function() {
	var form = layui.form, layer = parent.layer === undefined ? layui.layer : top.layer
	$ = layui.jquery;

	$(".loginBody .seraph").click(function() {
		layer.msg("这只是做个样式，至于功能，你见过哪个后台能这样登录的？还是老老实实的找管理员去注册吧", {
			time : 5000
		});
	})

	$("#verify").click(function() {
		$(this).attr("src", "/getVerify?" + Math.random());
	})

	// 登录按钮
	form.on("submit(login)", function(data) {
		$(this).text("登录中...").attr("disabled", "disabled").addClass("layui-disabled");
		$.ajax({
			type : "POST",
			url : "/login",
			data : $('form').serialize(),
			success : function(r) {
				if (r.code == 0) {
					var index = layer.load(1, {
						shade : [ 0.1, '#fff' ]
					// 0.1透明度的白色背景
					});
					window.location.href = '/index';
				} else {
					layer.msg(r.msg);
					$("#bt-login").text("登录").attr("disabled", false).removeClass("layui-disabled");
				}
			},
		});
		return false;
	})

	// 表单输入效果
	$(".loginBody .input-item").click(function(e) {
		e.stopPropagation();
		$(this).addClass("layui-input-focus").find(".layui-input").focus();
	})
	$(".loginBody .layui-form-item .layui-input").focus(function() {
		$(this).parent().addClass("layui-input-focus");
	})
	$(".loginBody .layui-form-item .layui-input").blur(function() {
		$(this).parent().removeClass("layui-input-focus");
		if ($(this).val() != '') {
			$(this).parent().addClass("layui-input-active");
		} else {
			$(this).parent().removeClass("layui-input-active");
		}
	})
})
