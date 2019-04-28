package com.chao.cloud.common.base;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chao.cloud.common.constants.ResultCodeEnum;
import com.chao.cloud.common.entity.Response;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

public interface BaseWirter extends BaseHttpServlet {

    default void wirteJsonObject(String retCode, String description, Object body) {
        HttpServletResponse response = getResponse();
        HttpServletRequest request = getRequest();
        // 输出对象
         response.setContentType("application/json; charset=utf-8");
        try (OutputStream writer = response.getOutputStream()) {
            Response<Object> resp = new Response<Object>();
            resp.setBody(body);
            resp.setRespFailed(retCode, description);
            String jsonStr = JSONUtil.toJsonStr(resp);
            byte[] bytes = StrUtil.utf8Bytes(jsonStr);
            writer.write(bytes);
        } catch (IOException e) {
            logger().info("[输出流异常: {}]", e);
        } finally {
            request.setAttribute("exit", "!");
        }
    }

    default void wirteJsonObject(String retCode, String description) {
        wirteJsonObject(retCode, description, null);
    }

    default void wirteError(String description) {
        wirteError(description, null);
    }

    default void wirteError(String description, Object body) {
        wirteJsonObject(ResultCodeEnum.CODE_500.code(), description, body);
    }

    default void exit() {
        getRequest().setAttribute("exit", "!");
    }
}
