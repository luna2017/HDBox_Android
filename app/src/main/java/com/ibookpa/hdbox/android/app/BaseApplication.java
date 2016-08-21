package com.ibookpa.hdbox.android.app;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by lin_sir on 2016/6/13. 基础 Application
 */
public class BaseApplication extends Application {

    private static BaseApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        LeakCanary.install(this);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    public static BaseApplication get() {
        return mApp;
    }

    public Context getAppContext() {
        return getApplicationContext();
    }
}
