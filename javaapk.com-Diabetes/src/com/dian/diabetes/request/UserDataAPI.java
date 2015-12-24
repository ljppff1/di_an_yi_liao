package com.dian.diabetes.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.dian.diabetes.tool.Preference;
import com.dian.diabetes.utils.ContantsUtil;
import com.dian.diabetes.utils.HttpServiceUtil;
import com.dian.diabetes.utils.HttpServiceUtil.CallBack;

/**
 * 成员资料的API包装类
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月22日
 * 
 */
public class UserDataAPI {

    /**
     * http请求：成员列表
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void getlist(Map<String, Object> map, Context context,CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_MEMBER_LIST,
                HttpContants.REQUEST_MOTHOD, map , callBack);
    }

    /**
     * http请求：获取一个成员
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void getOne(Map<String, Object> map,Context context,CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_MEMBER_GETONE,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }

    /**
     * http请求：创建一个成员
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void createMember(Map<String, Object> map, Context context, CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_MEMBER_CREATE,
                HttpContants.REQUEST_MOTHOD, map , callBack);
    }

    /**
     * http请求：更新成员
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void updateMember(Map<String, Object> map, Context context, CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_MEMBER_UPDATE,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }

    /**
     * http请求：删除一个成员
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void delMember(Map<String, Object> map, Context context, CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_MEMBER_DEL,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }

    /**
     * http请求：上传成员头像
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void uploadAvatar(Map<String, Object> map, String filePath ,Context context, CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.subFile(ContantsUtil.HOST + HttpContants.URL_UPLOAD_AVATAR,
                filePath, map, callBack);
    }

    /**
     * http请求：上传二维码名片
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void uploadMatrix(Map<String, Object> map,String filePath , Context context, CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.subFile(ContantsUtil.HOST + HttpContants.URL_UPLOAD_MATRIX,
                filePath, map,callBack);
    }

    /**
     * http请求：设置主成员
     * 
     * @param map
     * @param sessionId
     * @param callBack
     */
    public static void setMaster(Map<String, Object> map, Context context, CallBack callBack) {
        String sessionId = Preference.instance(context).getString(ContantsUtil.USER_SESSIONID);
        map = HttpUtil.putHeader(map, sessionId);
        HttpServiceUtil.request(ContantsUtil.HOST + HttpContants.URL_SET_MASTER,
                HttpContants.REQUEST_MOTHOD, map, callBack);
    }
}
