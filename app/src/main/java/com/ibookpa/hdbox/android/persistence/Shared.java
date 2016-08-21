package com.ibookpa.hdbox.android.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.ibookpa.hdbox.android.app.BaseApplication;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.LoginModel;
import com.ibookpa.hdbox.android.model.UserInfoModel;

/**
 * Created by tc on 6/23/16. SharedPreference 工具类
 * 注意这里的所有写操作都是异步方法,不保证立即执行,因此建议不要在执行写操作后立即执行读操作
 */
public class Shared {

    private static SharedPreferences mDefaultShared;
    private static SharedPreferences mCookieShared;
    private static SharedPreferences mLoginShared;
    private static SharedPreferences mUserInfoShared;
    private static SharedPreferences mCreditShared;

    private SharedPreferences.Editor mEditor;

    /**
     * 默认的 Shared,用于存储一些配置信息
     */
    public static SharedPreferences getDefaultShared() {
        if (mDefaultShared == null) {
            mDefaultShared = BaseApplication.get().getAppContext().getSharedPreferences("SHARED_CACHE_DATA", Context.MODE_PRIVATE);
        }
        return mDefaultShared;
    }

    /**
     * 存储 Cookie 的一些配置信息
     */
    public static SharedPreferences getCookieShared() {
        if (mCookieShared == null) {
            mCookieShared = BaseApplication.get().getAppContext().getSharedPreferences("COOKIE_CACHE_DATA", Context.MODE_PRIVATE);
        }
        return mCookieShared;
    }


    /**
     * 存储登录信息的 Shared
     */
    private static SharedPreferences getLoginShared() {
        if (mLoginShared == null) {
            mLoginShared = BaseApplication.get().getAppContext().getSharedPreferences("LOGIN_CACHE_DATA", Context.MODE_PRIVATE);
        }
        return mLoginShared;
    }

    /**
     * 存储用户信息的 Shared
     */
    private static SharedPreferences getUserInfoShared() {
        if (mUserInfoShared == null) {
            mUserInfoShared = BaseApplication.get().getAppContext().getSharedPreferences("USER_CACHE_DATA", Context.MODE_PRIVATE);
        }
        return mUserInfoShared;
    }

    /**
     * 存储学分信息的 Shared
     */
    private static SharedPreferences getCreditShared() {
        if (mCreditShared == null) {
            mCreditShared = BaseApplication.get().getAppContext().getSharedPreferences("CREDIT_CACHE_DATA", Context.MODE_PRIVATE);
        }
        return mCreditShared;
    }


    public static void saveCookie(String key, String value) {
        SharedPreferences.Editor mEditor = getCookieShared().edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public static String getCookie(String key) {
        SharedPreferences cookieShared = getCookieShared();
        return cookieShared.getString(key, null);
    }


    /**
     * 保存用户登录信息
     *
     * @param uid   学号
     * @param pwd   密码
     * @param token 令牌
     *              TODO 考虑要不要做密码的加密存储
     */
    public static void saveLoginInfo(String uid, String pwd, String token) {

        SharedPreferences.Editor mEditor = getLoginShared().edit();

        mEditor.putString("uid", uid);
        mEditor.putString("pwd", pwd);
        mEditor.putString("token", token);
        mEditor.apply();
    }

    public static void saveLoginInfo(String uid, String pwd) {

        SharedPreferences.Editor mEditor = getLoginShared().edit();

        mEditor.putString("uid", uid);
        mEditor.putString("pwd", pwd);
        mEditor.apply();
    }

    public static void updateLoginToken(String token) {
        SharedPreferences.Editor mEditor = getLoginShared().edit();

        mEditor.putString("token", token);
        mEditor.apply();
    }

    public static void updateLoginInfoItem(String key, String val) {
        SharedPreferences.Editor mEditor = getLoginShared().edit();

        mEditor.putString(key, val);
        mEditor.apply();
    }

    /**
     * 获取用户登录信息
     *
     * @return 如果用户没有登录或者因为其他原因导致没有用户登录数据, 返回 null
     */
    public static LoginModel getLoginInfo() {
        SharedPreferences loginShared = getLoginShared();

        String uid = loginShared.getString("uid", null);
        String pwd = loginShared.getString("pwd", null);
        String token = loginShared.getString("token", null);
        if (uid == null || pwd == null || token == null) {
            return null;
        }
        return new LoginModel(uid, pwd, token);
    }

    /**
     * 清除用户登录信息
     */
    public static void clearLoginInfo() {
        SharedPreferences.Editor mEditor = getLoginShared().edit();
        mEditor.clear();
        mEditor.apply();
    }

    /**
     * 保存用户信息
     */
    public static void saveUserInfo(UserInfoModel user) {
        SharedPreferences.Editor mEditor = getUserInfoShared().edit();

        mEditor.putString("uid", user.getUid());
        mEditor.putString("name", user.getName());
        mEditor.putString("grade", user.getGrade());
        mEditor.putString("college", user.getCollege());
        mEditor.putString("specialty", user.getSpecialty());
        mEditor.apply();
    }

    /**
     * 获取用户信息
     *
     * @return null if not login,else user info entity
     */
    public static UserInfoModel getUserInfo() {

        SharedPreferences userInfoShared = getUserInfoShared();

        String uid = userInfoShared.getString("uid", null);
        String name = userInfoShared.getString("name", null);
        String grade = userInfoShared.getString("grade", null);
        String college = userInfoShared.getString("college", null);
        String specialty = userInfoShared.getString("specialty", null);

        if (uid == null || name == null || grade == null || college == null || specialty == null) {
            return null;
        }

        return new UserInfoModel(uid, name, grade, college, specialty);
    }


    /**
     * 清理用户信息
     */
    public static void clearUserInfo() {
        SharedPreferences.Editor mEditor = getUserInfoShared().edit();
        mEditor.clear();
        mEditor.apply();
    }


    /**
     * 保存学分信息
     */
    public static void saveCreditInfo(CreditModel credit) {
        SharedPreferences.Editor mEditor = getCreditShared().edit();
        mEditor.putString("uid", credit.getUid());
        mEditor.putFloat("passCredit", credit.getPassCredit());
        mEditor.putFloat("failedCredit", credit.getFailedCredit());
        mEditor.putFloat("gpa", credit.getGpa());
        mEditor.apply();
    }

    /**
     * 获取学分信息
     */
    public static CreditModel getCreditInfo() {
        SharedPreferences creditShared = getCreditShared();

        String uid = creditShared.getString("uid", null);
        float passCredit = creditShared.getFloat("passCredit", 0);
        float failedCredit = creditShared.getFloat("failedCredit", 0);
        float gpa = creditShared.getFloat("gpa", 0);

        if (uid == null) {
            return null;
        }
        return new CreditModel(uid, passCredit, failedCredit, gpa);
    }

    /**
     * 清除学分信息
     */
    public static void clearCreditInfo() {
        SharedPreferences.Editor mEditor = getCreditShared().edit();
        mEditor.clear();
        mEditor.apply();
    }


    /**
     * 保存上次请求的时间戳
     */
    public static void saveLastRequestTime(long time) {
        SharedPreferences.Editor mEditor = getDefaultShared().edit();
        mEditor.putLong("lastRequestTimeInMill", time);
        mEditor.apply();
    }

    /**
     * 获取上次请求的时间戳
     */
    public static long getLastRequestTime() {
        SharedPreferences defaultShared = getDefaultShared();
        return defaultShared.getLong("lastRequestTimeInMill", 0);
    }

    /**
     * 保存上次检查更新的时间戳
     */
    public static void saveLastCheckUpdateTime(long time) {
        SharedPreferences.Editor mEditor = getDefaultShared().edit();
        mEditor.putLong("lastCheckUpdateTimeInMill", time);
        mEditor.apply();
    }

    /**
     * 获取上次检查更新的时间戳
     */
    public static long getLastCheckUpdateTime() {
        SharedPreferences defaultShared = getDefaultShared();
        return defaultShared.getLong("lastCheckUpdateTimeInMill", 0);
    }


    //-----------------------------------------------------------------------------

    public void saveString(String key, String value) {
        mEditor = mDefaultShared.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public void saveInt(String key, int value) {
        mEditor = mDefaultShared.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    public void saveBoolean(String key, Boolean value) {
        mEditor = mDefaultShared.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    public void saveFloat(String key, float value) {
        mEditor = mDefaultShared.edit();
        mEditor.putFloat(key, value);
        mEditor.apply();
    }

    public String getString(String key) {
        return mDefaultShared.getString(key, "");
    }

    public int getInt(String key) {
        return mDefaultShared.getInt(key, 0);
    }

    public int getInt(String key, int def) {
        return mDefaultShared.getInt(key, def);
    }

    public Boolean getBoolean(String key) {
        return mDefaultShared.getBoolean(key, false);
    }

    public float getFloat(String key) {
        return mDefaultShared.getFloat(key, 0);
    }

}
