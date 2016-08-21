package com.ibookpa.hdbox.android.persistence.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by tc on 6/29/16. 使用 Realm 的持久化存储工具
 */
public class RealmCache {

    private static RealmCache mInstance;
    private static final String DB_NAME = "REALM_CACHE.realm";

    private RealmCache() {

    }

    public static RealmCache getInstance() {
        synchronized (RealmCache.class) {
            if (mInstance == null) {
                mInstance = new RealmCache();
            }
        }
        return mInstance;
    }

    public Realm getRealm(Context context) {
        return Realm.getInstance(new RealmConfiguration.Builder(context).name(DB_NAME).build());
    }

}
