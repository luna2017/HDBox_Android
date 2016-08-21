package com.ibookpa.hdbox.android.utils;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by tc on 7/14/16. Http 网络操作
 */
public class HttpUtil {

    /**
     * HTTP GET 方式获取响应头中的 Cookie,因为在 Retrofit 中设置 followRedirect 为 false 时,会报 302 错误,因此只能自己实现这个网络操作
     *
     * @param url      请求的链接
     * @param headers  请求头
     * @param redirect 是否重定向
     */
    public static String getURLCookie(String url, Map<String, String> headers, boolean redirect) {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
            conn.setInstanceFollowRedirects(redirect);
            conn.setConnectTimeout(5000);

            // 添加请求
            for (Map.Entry<String, String> header : headers.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                if (key == null && value == null) {
                    continue;
                }
                conn.setRequestProperty(header.getKey(), header.getValue());
            }

            //获取响应头中的 Set-Cookie
            String cookie = conn.getHeaderField("Set-Cookie");
            conn.disconnect();

            return cookie;
        } catch (Exception e) {
            Log.i("TAG", "--tc-->HttpUtil getURLCookie error:" + e.toString());
            return null;
        }
    }

    public static String getURLResponseHeader(String url, Map<String, String> requestHeaders, boolean redirect, String needHeader) {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
            conn.setInstanceFollowRedirects(redirect);
            conn.setConnectTimeout(5000);

            // 添加请求
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                if (key == null && value == null) {
                    continue;
                }
                conn.setRequestProperty(header.getKey(), header.getValue());
            }

//            Map<String,List<String>> headers= conn.getHeaderFields();
//
//            for (Map.Entry<String,List<String>> header:headers.entrySet()){
//                String key = header.getKey();
//                List<String> value = header.getValue();
//
//                for (String s: value){
//                    Log.i("TAG","--tc-->UserInfo "+url+" s value in "+key+" is:"+s);
//                }
//            }

            //获取响应头中的 Set-Cookie
            String header = conn.getHeaderField(needHeader);
            conn.disconnect();

            return header;
        } catch (Exception e) {
            Log.i("TAG", "--tc-->HttpUtil getURLCookie error:" + e.toString());
            return null;
        }
    }


}
