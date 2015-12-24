package com.dian.diabetes.request;


/**
 * 接口请求的常量
 * 
 * @author Chanjc@ifenguo.com
 * @createDate 2014年7月22日
 * 
 */
public class HttpContants {

    // 请求方式统一为post
    public final static String REQUEST_MOTHOD = "post";
    // 请求服务器地址

    /**用户名或密码格式错误*/
    public final static int WRONG_FORMAT = 100;
    /**修改成功*/
    public final static int MODIFY_SUCCESS = 101;
    /**正在获取验证码*/
    public final static int GETTING_CODE = 102;
    /**登录成功*/
    public final static int LONGIN_SUCCESS = 103;
    /**正在获取验证码*/
    public final static int MODIFY_PSW_SUCCESS = 104;
    /**输入手机号的格式有误*/
    public final static int PHONE_FORMAT_ERROR = 105;
    /** 获取验证码成功*/
    public final static int CODE_SUCCESS = 106;
    /** 注册成功*/
    public final static int REGISTER_SUCCESS = 107;
    /** 注销成功*/
    public final static int LOGOUT_SUCCESS = 108;
    /** 上传头像失败*/
    public final static int UPLOAD_AVATAR_FAILED = 109;
    /** 更新用户列表成功*/
    public final static int UPDATE_ULIST_SUCCESS = 110;
    /** 设置主成员成功*/
    public final static int SET_MASTER_SUCCESS = 111;
    /** 添加成员成功*/
    public final static int ADD_USER_SUCCESS = 112;
    /** 删除成员成功*/
    public final static int DEL_USER_SUCCESS = 113;
    /** 上传头像成功*/
    public final static int UPLOAD_AVATAR_SUCCESS = 114;
    /** 上传二维码成功*/
    public final static int UPLOAD_MAXCARD_SUCCESS = 115;
    /** 上传二维码失败*/
    public final static int UPLOAD_MAXCARD_FAILED = 116;
    
    /**
     * 状态码
     */
    /**调用成功*/
    public final static int REQUEST_SUCCESS = 2000000;
    /**服务端内部错误*/
    public final static int SERVER_ERROR = 5000000;
    /**通用的失败状态码*/
    public final static int REQUEST_ERROR = 5000001;
    /**参数有误。比如参数不能为空，或长度超出限制等*/
    public final static int PARAM_ERROR = 5000020;
    /**找不到用户*/
    public final static int USER_NOTFOUND = 5000100;
    /**手机号已经被使用*/
    public final static int PHONE_ERROR = 5000101;
    /**用户名密码错误*/
    public final static int NAMEPSW_ERROR = 5000102;
    /**会话信息失效*/
    public final static int SESSION_ERROR = 5000103;
    /**用户未登录*/
    public final static int USER_UNLOGIN = 5000104;
    /**手机校验码无效*/
    public final static int CODE_ERROR = 5000105;
    /**当前成员为主成员，不允许删除*/
    public final static int MAINUSER_DEL_ERROR = 5000150;
    /**访问出错*/
    public final static int WRONG_CONNECTION = 0;
    
    
    /**
     * 登录注册API
     */
    // 发送手机验证码
    public final static String URL_GEN_CODE = "/api/auth/genCode";
    // 注册
    public final static String URL_REGISTER = "/api/auth/register";
    // 登录
    public final static String URL_LOGIN = "/api/auth/login";
    // 注销
    public final static String URL_LOGOUT = "/api/auth/logout";
    // 修改密码-验证码
    public final static String URL_PSW_GENCODE = "/api/auth/pwd/genCode";
    // 修改密码-更改密码
    public final static String URL_CHANGE_PSW = "/api/auth/pwd/change";

    /**
     * 成员资料API
     */
    // 成员列表
    public final static String URL_MEMBER_LIST = "/api/user/member/list";
    // 获取成员
    public final static String URL_MEMBER_GETONE = "/api/user/member/get";
    // 创建成员
    public final static String URL_MEMBER_CREATE = "/api/user/member/create";
    // 更新成员
    public final static String URL_MEMBER_UPDATE = "/api/user/member/update";
    // 删除成员
    public final static String URL_MEMBER_DEL = "/api/user/member/delete";
    // 上传成员头像
    public final static String URL_UPLOAD_AVATAR = "/api/user/member/uploadAvatar";
    // 上传二维码名片
    public final static String URL_UPLOAD_MATRIX = "/api/user/member/uploadMatrix";
    // 设置主成员
    public final static String URL_SET_MASTER = "/api/user/member/setMaster";

    
}
