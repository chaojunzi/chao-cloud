package com.chao.cloud.common.extra.voice.baidu;

import java.util.HashMap;

import org.json.JSONObject;

import com.baidu.aip.http.AipRequest;
import com.baidu.aip.http.EBodyFormat;
import com.baidu.aip.http.Headers;
import com.baidu.aip.http.HttpContentType;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.util.AipClientConst;
import com.baidu.aip.util.Base64Util;
import com.baidu.aip.util.SignUtil;
import com.baidu.aip.util.Util;

/**
 * 急速版
 * @功能：
 * @author： 薛超
 * @时间：2019年4月1日
 * @version 1.0.0
 */
public class TopspeedAipSpeech extends AipSpeech {

    public TopspeedAipSpeech(String appId, String apiKey, String secretKey) {
        super(appId, apiKey, secretKey);
    }

    private static String SPEECH_ASR_URL = "https://vop.baidu.com/pro_api";

    public JSONObject topspeed(byte[] data, String format, int rate, HashMap<String, Object> options) {
        AipRequest request = new AipRequest();

        preOperation(request); // get access token
        if (this.isBceKey.get()) {
            return Util.getGeneralError(AipClientConst.OPENAPI_NO_ACCESS_ERROR_CODE,
                    AipClientConst.OPENAPI_NO_ACCESS_ERROR_MSG);
        }
        // state.setState(EAuthState.STATE_TRUE_AIP_USER);
        String base64Content = Base64Util.encode(data);
        request.addBody("speech", base64Content);
        request.addBody("format", format);
        request.addBody("rate", rate);
        request.addBody("channel", 1);
        String cuid = SignUtil.md5(accessToken, "UTF-8");
        request.addBody("cuid", cuid);
        request.addBody("token", accessToken);
        request.addBody("len", data.length);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(SPEECH_ASR_URL);
        request.setBodyFormat(EBodyFormat.RAW_JSON);
        request.addHeader(Headers.CONTENT_TYPE, HttpContentType.JSON_DATA);
        return requestServer(request);
    }

}
