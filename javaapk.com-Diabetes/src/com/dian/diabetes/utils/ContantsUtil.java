package com.dian.diabetes.utils;

import com.dian.diabetes.db.dao.User;
import com.dian.diabetes.db.dao.UserInfo;

/**
 * 常量
 * 
 * @author longbh
 */
public class ContantsUtil {
	
	public final static String currentVesion = "v1.0.1";
	
    public final static String SYCN_URL = "";
    public final static String SYCN_DATA = "com.dian.diabetes.sycn_data";

    public final static String host = "";

    // 请求状态码
    public static final int GET_KEY_FAILED = 100024;
    public static final int limit = 20;

    // 用户id,作为sharedPreference的key
    public static String USER_ID = "user_id";
    // 判断首次使用，作为sharedPreference的key
    public final static String FIRST_USE = "first_use";
    // 登录成功后存放mid在sharedPreference,key
    public final static String USER_MID = "user_mid";
    // 登录成功后存放sessionId在sharedPreference,key
    public final static String USER_SESSIONID = "user_sessionid";
    
    //系统参数是否已经同步
    public static boolean isSycnSystem = false;

    // 数据状态码
    public final static short DELETE = 1;
    public final static short NO_SERVER = 0;
    public final static short SERVER = 2;

    // 血糖状态
    public final static int HIGH_DIABETES = 3;
    public final static int HIGH_PRE_DIABETES = 2;
    public final static int MID_DIABETES = 0;
    public final static int LOW_PRE_DIABETES = 1;

    // 四状态
    public final static int BRECKFAST = 0;
    public final static int LAUNCH = 1;
    public final static int DINNER = 2;
    public final static int SLEEP_PRE = 3;
    
    //控制页面刷新的常量
    public static boolean ENTRY_UPDATE = false;
    public static boolean EFFECT_UPDATE = false;
    public static boolean TOTAL_UPDATE = false;
    public static boolean TOTAL_AVG_UPDATE = false;
    public static boolean TOTAL_HIGH_UPDATE = false;
    public static boolean TOTAL_LOW_UPDATE = false;
    public static boolean TOTAL_COUNT_UPDATE = false;
    public static boolean UPDATE_AVTAR = false;
    //饮食统计
    public static boolean TOTAL_EAT_UPDATE = false;
    public static boolean NET_EAT_UPDATE = false;
    public static boolean EAT_MULTI_UPDATE = false;
    public static boolean EAT_STRUCT_UPDATE = false;
    public static boolean NUTRITION_UPDATE = false;
    public static boolean CALORE_STRUCT_UPDATE = false;
    
    //运动
    public static boolean SPORT_NET_UPDATE = false;
    public static boolean SPORT_ALL_UPDATE = false;
    public static boolean SPORT_STRUCT_UPDATE = false;
    public static boolean HEART_UPDATE = false;
    public static boolean MULTI_UPDATE = false;
    
    //首页
    public static boolean MAIN_UPDATE = false;
    public static boolean ADD_MEMBER = false;
    
    //指标
    public static boolean IDICATE_UPDATE = false;
    public static boolean REAL_UPDATE = false;
    
    public static boolean ENTRY_UPDATE_VIEW = false;
    public static boolean TOTAL_UPDATE_VIEW = false;
    
    //指标统计
    public static boolean INDICATE = false;
    public static boolean SELF_DETAIL = false;
    
    //日期常量
    public static long deltaWeek = 7 * 24 * 60 * 60 * 1000;
    public static long deltaDay = 24 * 60 * 60 * 1000;
    
    //蓝牙序列号
    public static final String bluthUserId = "372758946@qq.com";
    public static final String clientID = "0209f8aa6d6b4a86b3cb94c6ddd5ac42";
    public static final String clientSecret = "c4bd84d5388049b183d3f50cb70c1998";

    // 饭前 饭后
    public final static int EAT_PRE = 0;
    public final static int EAT_AFTER = 1;
    
    //上升 下降
    public final static int UP = 0;
    public final static int DOWN = 1;

    // 枚举分类group标识
    // 伴随状态
    public static final String DIABETES_PLUG_STATE = "dbtSigns";
    /** 用户主成员的mid*/
    public static String DEFAULT_TEMP_UID = "";
    public static User curUser;
    public static UserInfo curInfo;
    public static float height = 1.7f;

    // 闹钟类型标志
    public static final int SUGAR_ALARM = 1;
    public static final int MEDICINE_ALARM = 2;
    public static final int SPORT_ALARM = 3;
    public static final int EAT_ALARM = 4;
    
    //公用模板分类常量
    public static final int EAT_FOOD = -1;
    public static final int SPORT_TYPE = -2;
    public static final int MEDICINE = -3;
    
    // 服务端url
    //public static String HOST = "http://romanticle.xicp.net:12325";
    //public static String HOST = "http://58.215.177.180:9048";
    public static String HOST = "http://mobile.dazd.cn";
    public final static String PRED_UPDATE = HOST + "/api/pred/updating";		//拉取系统配置
    public final static String SETTING_UPDATE = HOST + "/api/cust/settings";
    public final static String LOAD_TARGET = HOST + "/api/cust/settings/setDbtTarget";	//提交血糖目标
    public final static String LOAD_TIME = HOST + "/api/cust/settings/setDbtMontimes";	//提交提醒时间
    public final static String MERGE_DATA = HOST + "/api/glucose/merge";	//合并数据
    public final static String  UPDATE_DBT= HOST + "/api/glucose/updating";
    public final static String NEWS_LIST = HOST + "/api/post/listPrePost";
    public final static String FAVORATE_LIST = HOST + "/api/user/getPostFavorites";
    public final static String NEWS_FAVORATE = HOST + "/api/user/addFavorite";
    public final static String REMOVE_FAVORATE = HOST + "/api/user/removeFavorite";
    public final static String DBT_SCHEME = HOST + "/api/cust/settings/setDbtScheme";	//自定义成员方案
    
    public final static String LOAD_EAT = HOST + "/api/dinner/updating";	//同步服务端饮食数据
    public final static String UPDATE_EAT = HOST + "/api/dinner/merge";		//上传数据到服务器
    public final static String LOAD_SPORT = HOST + "/api/sport/updating";	//同步运动数据
    public final static String UPDATE_SPORT = HOST + "/api/sport/merge";	//上传数据到服务器
    public final static String LOAD_MEDICINE = HOST + "/api/medic/updating";	//同步用药数据
    public final static String UPDATE_MEDICINE = HOST + "/api/medic/merge";	//上传用药数据到服务器
    public final static String LOAD_INDICATE = HOST + "/api/idc/updating";	//同步指标数据
    public final static String UPDATE_INDICATE = HOST + "/api/idc/merge";	//上次指标数据
    
    //饮食运动用药选择器
    public final static String EAT_TOOL = HOST + "/api/dinner/listFoodCategory";
    public final static String EAT_TOOL_DATA = HOST + "/api/dinner/listCatFood";
    public final static String EAT_TOOL_SEARCH = HOST + "/api/dinner/getFoodByName";
    public final static String EAT_TOOL_ITEM = HOST + "/api/dinner/getFoodHeat";
    
    public final static String SPORT_TOOL = HOST + "/api/sport/listSportCategory";
    public final static String SPORT_TOOL_DATA = HOST + "/api/sport/listCatSport";
    public final static String SPORT_TOOL_SEARCH = HOST + "/api/sport/getSportByName";
    public final static String SPORT_TOOL_ITEM = HOST + "/api/sport/getSportHeat";
    
    public final static String MEDICINE_TOOL = HOST + "/api/medic/listMdcCategory";
    public final static String MEDICINE_TOOL_DATA = HOST + "/api/medic/listCatMdc";
    public final static String MEDICINE_TOOL_SEARCH = HOST + "/api/medic/getMdcByName";
    
    //指标
    public final static String LIB_TRAN = HOST + "/api/lib/tran";
    public final static String GET_CATA = HOST + "/api/lib/getCata";
    public final static String PATHOLOGY = HOST + "/api/lib/getPathology";	//12.3.	取病理数据
    public final static String GENERA_DES= HOST + "/api/lib/getGeneraDes";
    public final static String GENERA_PRO = HOST + "/api/lib/getGeneraPro";
    public final static String GET_ALL = HOST + "/api/lib/getAll";
    public final static String PERIOD_PRO = HOST + "/api/lib/getPeriodPro";
    public final static String LIST_USER_INDICATE = HOST + "/api/lib/getLisRelation";
    
    //综合报告
    public final static String GET_ANSWER = HOST + "/api/exam/pull";	//拉取题目
    public final static String PUSH_ANSWER = HOST + "/api/exam/push";
    public final static String SUBMIT_ANSWER = HOST + "/api/exam/finish";		//提交
    
    public final static String GET_RESULT = HOST + "/api/report/show";
    
    //运动饮食计划
    public final static String EAT_PLAN = HOST + "/api/dinner/getFoodPlan";
    public final static String SPORT_PLAN = HOST + "/api/sport/getSportPlan";
    public final static String CALORE_SURPORT = HOST + "/api/user/member/getOneDayHeat";
    public final static String NUTRITION = HOST + "/api/dinner/distribut";
    
    //user
    public final static String URL_MEMBER_LIST = HOST + "/api/user/member/list";
    public final static String UPDATE_CHECK_URL = HOST + "/api/app/getLastestVersion";
}
