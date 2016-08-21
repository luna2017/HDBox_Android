package com.ibookpa.hdbox.android.api;

import com.ibookpa.hdbox.android.model.CheckUpdateModel;
import com.ibookpa.hdbox.android.model.CourseModel;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.ExamModel;
import com.ibookpa.hdbox.android.model.LoginModel;
import com.ibookpa.hdbox.android.model.NewsModel;
import com.ibookpa.hdbox.android.model.ResponseModel;
import com.ibookpa.hdbox.android.model.ScoreModel;
import com.ibookpa.hdbox.android.model.UserInfoModel;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by tc on 6/28/16. api 0.3 版
 */
public interface Api {

    @FormUrlEncoded
    @POST("login")
    Observable<ResponseModel<List<LoginModel>>> login(@Field("uid") String uid, @Field("pwd") String pwd);

    @FormUrlEncoded
    @POST("userinfo")
    Observable<ResponseModel<List<UserInfoModel>>> userinfo(@Field("uid") String uid, @Field("token") String token);

    @FormUrlEncoded
    @POST("credit")
    Observable<ResponseModel<List<CreditModel>>> credit(@Field("uid") String uid, @Field("token") String token);


    @FormUrlEncoded
    @POST("courses")
    Observable<ResponseModel<List<CourseModel>>> courses(@Field("uid") String uid, @Field("token") String token);


    @FormUrlEncoded
    @POST("scores")
    Observable<ResponseModel<List<ScoreModel>>> scores(@Field("uid") String uid, @Field("token") String token);


    @FormUrlEncoded
    @POST("exams")
    Observable<ResponseModel<List<ExamModel>>> exams(@Field("uid") String uid, @Field("token") String token);


    @Headers("Cache-Control: public, max-age=30")
    @GET("newslist")
    Observable<ResponseModel<List<NewsModel>>> newslist(@Query("page") int page);


    @GET("newsdetail")
    Observable<ResponseModel<List<NewsModel>>> newsdetail(@Query("nid") int nid);

    @FormUrlEncoded
    @POST("checkupdate")
    Observable<ResponseModel<List<CheckUpdateModel>>> checkUpdate(@Field("device") String device, @Field("versionCode") int versionCode);


}
