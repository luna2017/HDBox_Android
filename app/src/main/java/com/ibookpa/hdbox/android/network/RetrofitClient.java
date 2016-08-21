package com.ibookpa.hdbox.android.network;

import android.content.Context;
import android.util.Log;

import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.persistence.HttpCache;
import com.ibookpa.hdbox.android.utils.NetworkUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tc on 6/21/16. 网络操作客户端
 */
public class RetrofitClient {

    public static Retrofit getClient(Context context) {
        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(httpClient(context))
                .addConverterFactory(GsonConverterFactory.create())//Gson 适配器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())// RxJava 适配器
                .build();
    }

    /**
     * 校园网网络请求客户端,主要拦截登录响应头
     */
    private static OkHttpClient httpClient(Context context) {
        return new OkHttpClient.Builder()
                .connectTimeout(Constant.NETWORK_TIME_OUT_IN_SECOND, TimeUnit.SECONDS)
                .addNetworkInterceptor(rewriteCacheControlInterceptor)
                .addInterceptor(rewriteCacheControlInterceptor)
                .addInterceptor(new LoggingInterceptor())
                .cache(HttpCache.getInstance().getCache())
                .build();
    }

    /**
     * 拦截请求头,添加必要的请求头信息
     */
    private static Interceptor requestHeaderInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();

//            Headers hs = request.headers();
//
//            String hsc = hs.getURLCookie("Cache-Control");
//            Log.i("TAG", "--tc-->url request cache control is:" + hsc);
//
//            //如果没有网,并且之前没有设置缓存控制,则请求缓存
//            if (!NetworkUtil.hasNetwork() && request.headers().getURLCookie("Cache-Control") == null) {
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build();
//            }
            return chain.proceed(request);
        }
    };

    /**
     * 拦截 LeanCloud 的响应头,设置缓存间隔时间
     * 在 wifi 环境下,最快 3 秒请求一次，3 秒内的请求都是使用缓存
     * 在移动网络环境下,最快 9 秒请求一次,9 秒内都使用缓存
     * 在没有网络的情况下一直使用缓存,直到有网络
     */
    private static Interceptor rewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.hasNetwork()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Log.i("TAG", "--tc-->Retrofit No Net");
            }

            Response originalResponse = chain.proceed(request);

            if (NetworkUtil.hasNetwork()) {
                //有网的时候读接口上的@Headers里的配置，也可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };


    private static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.i("TAG", "--network-->url: sending request:" + request.url() + "\n-->connection:" + chain.connection() + "\n-->request header:" + request.headers());
            Log.i("TAG", "--network-->url: request body:" + request.toString());


            Response response = chain.proceed(request);

            long t2 = System.nanoTime();

            Log.i("TAG", "--network-->url:received response:" + response.request().url() + "\n--tc-->time:" + (t2 - t1) / 1e6d + "\n-->response header:" + response.headers());

            return response;
        }
    }


}
