package com.ibookpa.hdbox.android.app;


/**
 * Created by tc on 6/21/16. 全局常量定义
 */
public class Constant {

    /**
     * 当前应用包名
     */
    public static final String PKG_NAME = "com.ibookpa.hdbox.android";

    /**
     * 权限申请时的请求码
     */
    public static final int PERMISSION_REQUEST_CODE = 1010;

    /**
     * 网络连接超时时间,单位:秒
     */
    public static final int NETWORK_TIME_OUT_IN_SECOND = 8;

    /**
     * 检查更新和检查 Token 过期的时间间隔
     */
    public static final int CHECK_TIME_INTERVAL = 10;

    /**
     * 应用门户主页 Cookie 本地保存时的键
     */
    public static final String HLJU_INDEX_COOKIE = "HLJU_XYW_COOKIE";

    /**
     * 应用门户登录成功后返回的令牌本地保存时的键
     */
    public static final String HLJU_LOGIN_TOKEN = "HLJU_LOGIN_TOKEN";

    /**
     * 默认的 SharedPreference 文件名
     */
    public static final String PREF_DEFAULT_CACHE = "PREF_DEFAULT_CACHE";

    /**
     * Cookie 缓存的 SharedPreference 文件名
     */
    public static final String PREF_COOKIE_CACHE = "PREF_COOKIE_CACHE";

    /**
     * 当前用户的配置信息 SharedPreference 文件名
     */
    public static final String PREF_USER_CACHE = "PREF_USER_CACHE";

    /**
     * 上次检查的毫秒时间,可以用做检查更新或者 Token 更新的标志
     */
    public static final String LAST_CHECK_TIME_MILLIS = "LAST_CHECK_TIME_MILLIS";

    /**
     * 启动页图片链接
     */
    public static final String LAUNCH_IMG_URL = "LAUNCH_IMG_URL";

    //--------------------------------------------------------------------------------------------------
    /**
     * 服务器根地址,当前版本 V6
     */
    public static final String BASE_URL = "http://115.28.180.129/api/6/";

    public static final String BASE_MY_URL = "http://my.hlju.edu.cn/";

    public static final String BASE_SSFW_URL = "http://ssfw1.hlju.edu.cn/ssfw/";

    public static final String SSFW_PYFA_ID = "http://ssfw1.hlju.edu.cn/ssfw/zhcx/pyfa/xsfaTree.do";


    //---------------------------------------------------------------------------------------------------
    /**
     * 应用门户主页,获取验证码,登录
     */
    public static final String HLJU_INDEX_URL = "http://my.hlju.edu.cn/login.portal";
    public static final String LOGIN_VCODE_URL = "http://my.hlju.edu.cn/captchaGenerate.portal";
    public static final String PASSWORD_VALIDATE_URL = "http://my.hlju.edu.cn/userPasswordValidate.portal";


    public static final String CHECK_SSFW_URL = "http://ssfw.hlju.edu.cn/ssfw/j_spring_ids_security_check";

    public static final String SCORE_URL = "http://ssfw.hlju.edu.cn/ssfw/zhcx/cjxx.do";

}
