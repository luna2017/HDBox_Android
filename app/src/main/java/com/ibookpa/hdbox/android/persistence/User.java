package com.ibookpa.hdbox.android.persistence;

import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.LoginModel;
import com.ibookpa.hdbox.android.model.UserInfoModel;

/**
 * Created by tc on 7/1/16. 用户管理类
 */
public class User {


//--------存在内存泄漏的写法----------------------------------
//    private User(Context context) {
//        this.context = context;
//    }
//
//    public static User getCurrentUser(Context context) {
//        if (mInstance == null) {
//            mInstance = new User(context);
//        }
//        return mInstance;
//    }
// ---------------------------------------------------------

    private static User mInstance;

    private User() {
    }

    public static User currentUser() {
        if (mInstance == null) {
            synchronized (User.class) {
                if (mInstance == null) {
                    mInstance = new User();
                }
            }
        }
        return mInstance;
    }

    public boolean isLogin() {
        return Pref.get().isLogin();
    }

    public String getUid() {
        return Pref.get().getUserInfoItem("uid");
    }

    public String getPwd() {
        return Pref.get().getUserInfoItem("pwd");
    }

    public boolean saveLoginInfo(String uid, String pwd) {
        return Pref.get().saveLoginInfo(uid, pwd);
    }

    /**
     * 获取登录校园网成功后的 Token
     */
    public String getSchoolLoginToken() {
        return Pref.get().getCookie(Constant.HLJU_LOGIN_TOKEN);
    }

    /**
     * 保存登录校园网成功后的 Token
     */
    public boolean saveSchoolLoginToken(String token) {
        return Pref.get().saveCookie(Constant.HLJU_LOGIN_TOKEN, token);
    }


    public UserInfoModel getUserInfo() {
        return Pref.get().getUserInfo();
    }

    public boolean saveUserInfo(UserInfoModel userInfo) {
        return Pref.get().saveUserInfo(userInfo);
    }


    public CreditModel getCreditInfo() {
        return Pref.get().getCreditInfo();
    }

    public boolean saveCreditInfo(CreditModel credit) {
        return Pref.get().saveCreditInfo(credit);
    }


    /**
     * 退出登录时,清空 SharedPreference
     */
    public void logout() {
        Pref.get().clearAll();
    }


    @Deprecated
    public LoginModel getLoginInfo() {
        return Shared.getLoginInfo();
    }

}
