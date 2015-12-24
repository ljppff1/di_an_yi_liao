package com.dian.diabetes.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;

/**
 * http请求的工具类
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月23日
 * 
 */
public class HttpUtil {

    /**
     * 加header，服务器确认用户是否登录的凭证
     * 
     * @param map
     * @param sessionId
     * @return
     */
    public static Map<String, Object> putHeader(Map map, String sessionId) {
        if (map == null) {
            map = new HashMap<String, Object>();
        }
        map.put("dean_usession", sessionId);
        return map;
    }

}
