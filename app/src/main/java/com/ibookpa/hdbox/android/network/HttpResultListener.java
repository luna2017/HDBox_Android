package com.ibookpa.hdbox.android.network;

/**
 * Created by tc on 7/1/16.网络请求回调
 */
public interface HttpResultListener<T> {

    void onSuccess(T t);

    void onError(Throwable e);
}
