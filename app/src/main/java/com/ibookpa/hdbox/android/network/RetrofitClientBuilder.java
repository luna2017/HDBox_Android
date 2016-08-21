package com.ibookpa.hdbox.android.network;

import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.persistence.Pref;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tc on 7/25/16. Retrofit 客户端构造器,根据不同的请求头和响应头要求,构造不同的客户端
 */
public class RetrofitClientBuilder {

    private static Retrofit.Builder mBuilder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());//Gson 适配器


    private static Retrofit buildRetrofit(OkHttpClient httpClient) {
        return mBuilder
                .client(httpClient)
                .baseUrl(Constant.BASE_MY_URL)
                .build();
    }

    private static Retrofit buildRetrofit(OkHttpClient httpClient, String url) {
        return mBuilder
                .client(httpClient)
                .baseUrl(url)
                .build();
    }

    /**
     * 创建访问阿里云服务器的 Client
     */
    public static Retrofit createDefaultRetrofitClient(){
        return buildRetrofit(HttpClientBuilder.defaultHttpClient(),Constant.BASE_URL);
    }

    /**
     * 创建访问应用门户的 Client,将返回的 Set-Cookie 保存在本地,作为后续访问的验证,构造一次 Session
     */
    public static Retrofit createXYWCookieRetrofitClient() {
        return buildRetrofit(HttpClientBuilder.saveCookieHttpClient(Constant.HLJU_INDEX_COOKIE));
    }

    /**
     * 创建获取验证码的 Client,在请求头中加入应用门户的 Cookie,返回图片 byte 流
     */
    public static Retrofit createCaptchaRetrofitClient() {
        return buildRetrofit(HttpClientBuilder.addCookieHttpClient(Pref.get().getCookie(Constant.HLJU_INDEX_COOKIE)));
    }

    /**
     * 创建登录校园网的 Client,在请求头中加入应用门户的 Cookie,返回登录结果
     */
    public static Retrofit createLoginRetrofitClient() {
        return buildRetrofit(HttpClientBuilder.addAndSaveCookieHttpClient(Pref.get().getCookie(Constant.HLJU_INDEX_COOKIE), Constant.HLJU_LOGIN_TOKEN));
    }

    /**
     * 创建访问登录成功后的主页的 Client,用户获取用户名
     */
    public static Retrofit createIndexRetrofitClient() {
        return buildRetrofit(HttpClientBuilder.addCookieHttpClient(Pref.get().getCookie(Constant.HLJU_LOGIN_TOKEN)));
    }

    /**
     * 创建访问师生服务的 Client
     *
     * @param secureCheckCookie 师生服务安全验证时返回的 Cookie
     */
    public static Retrofit createSSFWClient(String secureCheckCookie) {
        return buildRetrofit(HttpClientBuilder.addCookieHttpClient(secureCheckCookie), Constant.BASE_SSFW_URL);
    }
}
