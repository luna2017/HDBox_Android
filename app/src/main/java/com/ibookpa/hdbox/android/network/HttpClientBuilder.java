package com.ibookpa.hdbox.android.network;

import android.util.Log;

import com.ibookpa.hdbox.android.persistence.Pref;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tc on 7/25/16.根据不同需求构造不同的 HttpClient
 */
public class HttpClientBuilder {


    /**
     * 创建默认的 Http
     */
    public static OkHttpClient defaultHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    /**
     * 请求头中加入 Cookie 的 Http
     *
     * @param cookieVal cookie 值
     */
    public static OkHttpClient addCookieHttpClient(final String cookieVal) {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

        httpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // 将 Cookie 放入请求头中
                Request.Builder requestBuilder = request.newBuilder().addHeader("Cookie", cookieVal != null ? cookieVal : "");

                return chain.proceed(requestBuilder.build());
            }
        });
        return httpBuilder.build();
    }

    /**
     * 保存相应头中的 Cookie 的 Http
     *
     * @param cookieKey Cookie 本地保存的键
     */
    public static OkHttpClient saveCookieHttpClient(final String cookieKey) {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

        httpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Response response = chain.proceed(chain.request());

                String cookie = response.header("Set-Cookie");//获取响应头中的 Cookie
                if (cookie != null) {
                    Pref.get().saveCookie(cookieKey, cookie.split(";")[0]);
                } else {
                    Log.i("TAG", "--tc-->Response Set-Cookie is null");
                }
                return response;
            }
        });
        return httpBuilder.build();
    }

    /**
     * 在请求头中加入 Cookie,并且保存响应头中的 Cookie 的Http
     *
     * @param addCookieVal  请求头中 Cookie 的值
     * @param saveCookieKey Cookie 本地保存的键
     */
    public static OkHttpClient addAndSaveCookieHttpClient(final String addCookieVal, final String saveCookieKey) {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

        httpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                //请求头添加 Cookie
                Request.Builder requestBuilder = request.newBuilder().addHeader("Cookie", addCookieVal != null ? addCookieVal : "");

                Response response = chain.proceed(requestBuilder.build());
                String cookie = response.header("Set-Cookie");
                if (cookie != null) {//保存响应头中的 Cookie
                    Pref.get().saveCookie(saveCookieKey, cookie.split(";")[0]);
                }
                return response;
            }
        });
        return httpBuilder.build();
    }
}
