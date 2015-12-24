package com.dian.diabetes.request;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.dian.diabetes.db.AlarmBo;
import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;

/**
 * 登录注册API的请求包装类
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月22日
 * 
 */
public class LoginRegisterAPI {

    /**
     * http请求：发送手机验证码
     * 
     * @param map
     * @param callBack
     */
    public static void genCode(Map<String, Object> map, CallBack callBack) {
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_GEN_CODE,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }

    /**
     * http请求：注册
     * 
     * @param map
     * @param callBack
     */
    public static void register(Map<String, Object> map, CallBack callBack) {
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_REGISTER,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }

    /**
     * http请求：登录
     * @param map
     * @param callBack
     */
    public static void login(Map<String, Object> map, CallBack callBack) {
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_LOGIN,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }
    
    /**
     * http请求：注销
     * @param map
     * @param callBack
     */
    public static void logout(Map<String, Object> map, Context context, CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map,sessionId);
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_LOGOUT,
                HttpContants.REQUEST_MOTHOD, HttpUtil.putHeader(map,sessionId), callBack);
        new AlarmBo(context).disableAlert();
    }
    
    /**
     * http请求：修改密码-验证码
     * @param map
     * @param callBack
     */
    public static void pswGenCode(Map<String, Object> map , CallBack callBack) {
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_PSW_GENCODE,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }
    
    /**
     * http请求：修改密码-更改密码
     * @param map
     * @param callBack
     */
    public static void change(Map<String, Object> map, CallBack callBack) {
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_CHANGE_PSW,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }
    
}
