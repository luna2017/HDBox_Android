package com.ibookpa.hdbox.android.api;


import com.ibookpa.hdbox.android.model.CaptchaModel;
import com.ibookpa.hdbox.android.model.CheckUpdateModel;
import com.ibookpa.hdbox.android.model.LaunchImgModel;
import com.ibookpa.hdbox.android.model.ResponseModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by tc on 7/13/16. 校园网接口
 */
public interface SchoolApi {

    /**
     * 访问校园门户主页,用户获取 Cookie
     */
    @GET("login.portal")
    Observable<ResponseBody> hljuIndex();

    /**
     * 获取验证码
     */
    @GET("captchaGenerate.portal")
    Observable<ResponseBody> loginCaptcha();

    /**
     * 登录校园网
     *
     * @param uid     用户名
     * @param pwd     密码
     * @param captcha 验证码
     * @return 登录返回的结果
     */
    @FormUrlEncoded
    @POST("userPasswordValidate.portal")
    Observable<ResponseBody> loginHLJU(@Field("Login.Token1") String uid, @Field("Login.Token2") String pwd, @Field("captcha") String captcha);

    /**
     * 课表
     */
    @GET("pkgl/kcbxx/4/2016-2017-1.do")
    Observable<ResponseBody> courses();

    /**
     * 成绩
     */
    @GET("zhcx/cjxx.do")
    Observable<ResponseBody> scores();

    /**
     * 培养方案节点数据
     */
    @GET("zhcx/pyfa/node.do")
    Observable<ResponseBody> pyfaNode(@Query(value = "param", encoded = true) String param);

    /**
     * 获取真实姓名
     */
    @GET("index.portal")
    Observable<ResponseBody> realName();
//--------------------------------------------------------------------------------------------

    /**
     * 解析验证码,将验证码上传到服务器进行解析,返回解析后的字符串
     */
    @Multipart
    @POST("captcha")
    Observable<ResponseModel<List<CaptchaModel>>> uploadCaptcha(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("checkupdate")
    Observable<ResponseModel<List<CheckUpdateModel>>> checkUpdate(@Field("version") String version);


    @GET("launchimage")
    Observable<ResponseModel<List<LaunchImgModel>>> launchImage();
}
