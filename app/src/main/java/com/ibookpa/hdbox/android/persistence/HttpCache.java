package com.ibookpa.hdbox.android.persistence;


import com.ibookpa.hdbox.android.app.BaseApplication;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;

/**
 * Created by tc on 6/21/16. 网络缓存
 */
public class HttpCache {

    private Cache mCache;
    private static HttpCache mInstance;

    public HttpCache() {
        File cacheFile = new File(BaseApplication.get().getAppContext().getCacheDir(), "hdbox_http_cache");
        mCache = new Cache(cacheFile, 1024 * 1024 * 20); // 20Mb
    }

    public static HttpCache getInstance() {
        if (mInstance == null) {
            mInstance = new HttpCache();
        }
        return mInstance;
    }

    public Cache getCache() {
        return mCache;
    }

    public long getCacheSize() {
        long size = 0;
        try {
            size = mCache.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public boolean hasCache() {
        return getCacheSize() > 0;
    }

    public boolean clear() {
        try {
            mCache.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
