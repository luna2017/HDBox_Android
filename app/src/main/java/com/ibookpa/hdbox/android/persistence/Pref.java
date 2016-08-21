package com.ibookpa.hdbox.android.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.ibookpa.hdbox.android.app.BaseApplication;
import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.UserInfoModel;

/**
 * Created by tc on 7/25/16. SharedPreference 持久化存储类
 */
public class Pref {

    private static Pref mInstance = new Pref();
    private static SharedPreferences mDefaultPref = BaseApplication.get().getAppContext().getSharedPreferences(Constant.PREF_DEFAULT_CACHE, Context.MODE_PRIVATE);
    private static SharedPreferences mCookiePref = BaseApplication.get().getAppContext().getSharedPreferences(Constant.PREF_COOKIE_CACHE, Context.MODE_PRIVATE);
    private static SharedPreferences mUserPref = BaseApplication.get().getAppContext().getSharedPreferences(Constant.PREF_USER_CACHE, Context.MODE_PRIVATE);

    private Pref() {
    }

    public static Pref get() {
        return mInstance;
    }

    /**
     * 判断当前用户是否登录,通过判断是否存在当前用户的 uid 字段来判断是否登录
     */
    public boolean isLogin() {
        return mUserPref.getString("uid", null) != null;
    }

    public String getCookie(String cookieKey) {
        if (mCookiePref == null) {
            return null;
        }
        return mCookiePref.getString(cookieKey, null);
    }

    public boolean saveCookie(String cookieKey, String cookieVal) {
        SharedPreferences.Editor mEditor = mCookiePref.edit();
        mEditor.putString(cookieKey, cookieVal);
        return mEditor.commit();
    }

    /**
     * TODO 目前只能获取到用户信息中的 String 类型数据,以后需要加上对其他数据类型的支持
     */
    public String getUserInfoItem(String key) {
        return mUserPref.getString(key, null);
    }

    public boolean saveUserItem(String key, Object value) {
        SharedPreferences.Editor mEditor = mUserPref.edit();
        if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            mEditor.putInt(key, (Integer) value);
        } else {
            return false;
        }
        return mEditor.commit();
    }

    /**
     * 保存登录信息
     *
     * @param uid 用户名
     * @param pwd 密码
     */
    public boolean saveLoginInfo(String uid, String pwd) {
        SharedPreferences.Editor mEditor = mUserPref.edit();
        mEditor.putString("uid", uid);
        mEditor.putString("pwd", pwd);
        return mEditor.commit();
    }

    /**
     * 获取用户信息
     */
    public UserInfoModel getUserInfo() {
        if (mUserPref == null) {
            return null;
        }

        String uid = mUserPref.getString("uid", null);
        String name = mUserPref.getString("name", null);
        String grade = mUserPref.getString("grade", null);
        String college = mUserPref.getString("college", null);
        String specialty = mUserPref.getString("specialty", null);

        if (uid == null || name == null || grade == null || college == null || specialty == null) {
            return null;
        }

        return new UserInfoModel(uid, name, grade, college, specialty);
    }

    /**
     * 保存用户信息
     */
    public boolean saveUserInfo(UserInfoModel userInfo) {
        SharedPreferences.Editor mEditor = mUserPref.edit();
        mEditor.putString("name", userInfo.getName());
        mEditor.putString("grade", userInfo.getGrade());
        mEditor.putString("college", userInfo.getCollege());
        mEditor.putString("specialty", userInfo.getSpecialty());
        return mEditor.commit();
    }


    /**
     * 获取学分信息
     */
    public CreditModel getCreditInfo() {
        if (mUserPref == null) {
            return null;
        }

        String uid = mUserPref.getString("uid", null);
        float passCredit = mUserPref.getFloat("passCredit", 0);
        float failedCredit = mUserPref.getFloat("failedCredit", 0);
        float gpa = mUserPref.getFloat("gpa", 0);

        if (uid == null) {
            return null;
        }
        return new CreditModel(uid, passCredit, failedCredit, gpa);
    }

    /**
     * 保存学分信息
     */
    public boolean saveCreditInfo(CreditModel credit) {
        SharedPreferences.Editor mEditor = mUserPref.edit();

        mEditor.putFloat("passCredit", credit.getPassCredit());
        mEditor.putFloat("failedCredit", credit.getFailedCredit());
        mEditor.putFloat("gpa", credit.getGpa());
        return mEditor.commit();
    }


    /**
     * 获取上次检查更新的时间戳
     */
    public long getLastCheckUpdateTime() {

        return mDefaultPref.getLong("LAST_CHECK_UPDATE_TIME_IN_MILL", 0);
    }

    /**
     * 保存上次检查更新的时间戳
     */
    public boolean saveLastCheckUpdateTime(long time) {
        SharedPreferences.Editor mEditor = mDefaultPref.edit();
        mEditor.putLong("LAST_CHECK_UPDATE_TIME_IN_MILL", time);
        return mEditor.commit();
    }


    /**
     * 获取上次检查 Token 的时间戳
     */
    public long getLastCheckTokenTime() {
        return mDefaultPref.getLong("LAST_CHECK_TOKEN_TIME_IN_MILL", 0);
    }

    /**
     * 保存上次检查 Token 的时间戳
     */
    public boolean saveLastCheckTokenTime(long time) {
        SharedPreferences.Editor mEditor = mDefaultPref.edit();
        mEditor.putLong("LAST_CHECK_TOKEN_TIME_IN_MILL", time);
        return mEditor.commit();
    }


    /**
     * 清除所有数据
     */
    public boolean clearAll() {
        return mCookiePref.edit().clear().commit() && mUserPref.edit().clear().commit() && mDefaultPref.edit().clear().commit();
    }


    public String getString(String key) {
        return mDefaultPref.getString(key, null);
    }

    public Integer getInt(String key) {
        return mDefaultPref.getInt(key, 0);
    }

    public Long getLong(String key) {
        return mDefaultPref.getLong(key, 0);
    }

    public Float getFloat(String key) {
        return mDefaultPref.getFloat(key, 0F);
    }

    public Boolean getBoolean(String key) {
        return mDefaultPref.getBoolean(key, false);
    }

    public boolean save(String key, Object value) {
        SharedPreferences.Editor mEditor = mDefaultPref.edit();

        if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            mEditor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else {
            return false;
        }
        return mEditor.commit();
    }
}
