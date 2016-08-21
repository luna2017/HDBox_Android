package com.ibookpa.hdbox.android.network;

import android.content.Context;
import android.util.Log;

import com.ibookpa.hdbox.android.api.SchoolApi;
import com.ibookpa.hdbox.android.app.BaseApplication;
import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.model.CaptchaModel;
import com.ibookpa.hdbox.android.model.CheckUpdateModel;
import com.ibookpa.hdbox.android.model.CourseModel;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.LaunchImgModel;
import com.ibookpa.hdbox.android.model.ResponseModel;
import com.ibookpa.hdbox.android.model.ScoreModel;
import com.ibookpa.hdbox.android.model.UserInfoModel;
import com.ibookpa.hdbox.android.persistence.Pref;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.persistence.db.RealmCache;
import com.ibookpa.hdbox.android.utils.CreditUtil;
import com.ibookpa.hdbox.android.utils.DateUtil;
import com.ibookpa.hdbox.android.utils.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tc on 7/13/16. 校园网接口服务
 */
public class SchoolApiService {

    private Context mContext;

    private static SchoolApiService mInstance;

    private SchoolApiService() {
        this.mContext = BaseApplication.get().getAppContext();
    }

    public static SchoolApiService get() {
        if (mInstance == null) {
            synchronized (SchoolApiService.class) {
                if (mInstance == null) {
                    mInstance = new SchoolApiService();
                }
            }
        }
        return mInstance;
    }

    /**
     * 访问阿里云服务器的接口
     */
    private SchoolApi defaultService() {
        return RetrofitClientBuilder.createDefaultRetrofitClient().create(SchoolApi.class);
    }

    /**
     * 获取应用门户主页 Cookie 的接口
     */
    private SchoolApi xywCookieService() {
        return RetrofitClientBuilder.createXYWCookieRetrofitClient().create(SchoolApi.class);
    }

    /**
     * 获取验证码的接口
     */
    private SchoolApi captchaService() {
        return RetrofitClientBuilder.createCaptchaRetrofitClient().create(SchoolApi.class);
    }

    /**
     * 登录应用门户的接口
     */
    private SchoolApi loginService() {
        return RetrofitClientBuilder.createLoginRetrofitClient().create(SchoolApi.class);
    }

    /**
     * 访问登录成功后的主页
     */
    private SchoolApi indexService() {
        return RetrofitClientBuilder.createIndexRetrofitClient().create(SchoolApi.class);
    }

    /**
     * 访问师生服务页面
     *
     * @param secureCheckCookie 安全验证返回的 Cookie
     */
    private SchoolApi ssfwService(String secureCheckCookie) {
        return RetrofitClientBuilder.createSSFWClient(secureCheckCookie).create(SchoolApi.class);
    }

    /**
     * 师生服务安全验证
     * 返回师生服务安全验证的 Cookie,这个 Cookie 需要用登录时返回的 Cookie 去请求
     */
    private Observable<String> ssfwSecureCheck() {

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                if (User.currentUser().isLogin()) {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Cookie", User.currentUser().getSchoolLoginToken());

                    // 请求 cookie,如果在 retrofit 中设置重定向 follow 为 false ,会报 302 错误,所以自己写一个网络请求操作
                    String cookie = HttpUtil.getURLCookie(Constant.CHECK_SSFW_URL, headers, false);

                    if (cookie != null) {
                        subscriber.onNext(cookie.split(";")[0]);
                    } else {
                        subscriber.onError(new NetworkException(108));
                    }
                } else {
                    subscriber.onError(new NetworkException(109));
                }
            }
        });
    }

    /**
     * 获取培养方案 ID
     */
    private Observable<String> getPyfaId(final String ssfwCookie) {

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!User.currentUser().isLogin()) {
                    subscriber.onError(new NetworkException(108));
                } else {
                    Map<String, String> headers = new HashMap<>();

                    headers.put("Cookie", User.currentUser().getSchoolLoginToken() + ";" + ssfwCookie);

                    String locHeader = HttpUtil.getURLResponseHeader(Constant.SSFW_PYFA_ID, headers, false, "Location");
                    if (locHeader != null) {
                        subscriber.onNext(locHeader.split("=")[1]);
                    } else {
                        subscriber.onError(new NetworkException(109));
                    }
                }
            }
        });
    }

    /**
     * 获取真实姓名,需要传入 UserInfo 模型,通过网络请求得到数据后,将数据放到模型中,再返回模型
     */
    private Observable<UserInfoModel> getRealName(final UserInfoModel userInfo) {

        return Observable.create(new Observable.OnSubscribe<UserInfoModel>() {
            @Override
            public void call(Subscriber<? super UserInfoModel> subscriber) {
                if (userInfo != null) {
                    subscriber.onNext(userInfo);
                } else {
                    subscriber.onError(new NetworkException(109));
                }
            }
        }).flatMap(new Func1<UserInfoModel, Observable<ResponseBody>>() {//获取真实姓名
            @Override
            public Observable<ResponseBody> call(UserInfoModel userInfoModel) {
                return indexService().realName();
            }
        }).map(new Func1<ResponseBody, UserInfoModel>() {//解析姓名并返回
            @Override
            public UserInfoModel call(ResponseBody body) {
                String realName = HtmlParser.parseRealName(body);

                if (realName == null) {
                    return null;
                }
                userInfo.setName(realName);
                return userInfo;
            }
        });
    }
//----------------------------------------------------------------------------------------------------------

    /**
     * 请求验证码
     */
    public void requestCaptcha(HttpResultListener<ResponseBody> listener) {
        xywCookieService().hljuIndex()//访问应用门户首页,拿到 Cookie
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {//带着应用门户的 Cookie 获取验证码图片
                    @Override
                    public Observable<ResponseBody> call(ResponseBody body) {
                        return captchaService().loginCaptcha();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 登录校园网
     */
    public void loginHLJU(HttpResultListener<Boolean> listener, final String uid, final String pwd, String captcha) {
        loginHLJUObservable(uid, pwd, captcha)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    private Observable<Boolean> loginHLJUObservable(final String uid, final String pwd, String captcha) {
        return loginService().loginHLJU(uid, pwd, captcha)
                .map(new Func1<ResponseBody, Boolean>() {//对登录结果处理,判断是否登录成功
                    @Override
                    public Boolean call(ResponseBody body) {
                        return HtmlParser.parseLoginResult(body) && User.currentUser().saveLoginInfo(uid, pwd);
                    }
                });
    }

    /**
     * 请求课表
     */
    public void requestCourses(final HttpResultListener<Boolean> listener) {

        ssfwSecureCheck()
                .flatMap(new Func1<String, Observable<ResponseBody>>() {//通过安全验证,请求课表网页数据
                    @Override
                    public Observable<ResponseBody> call(String s) {
                        return ssfwService(s).courses();
                    }
                })
                .map(new Func1<ResponseBody, List<CourseModel>>() {// 得到课表网页数据,解析,返回 List
                    @Override
                    public List<CourseModel> call(ResponseBody body) {
                        return HtmlParser.parseCourse(body);
                    }
                })
                .map(new Func1<List<CourseModel>, Boolean>() {
                    @Override
                    public Boolean call(final List<CourseModel> courseModels) {
                        if (courseModels.size() == 0) {
                            return false;
                        }
                        Realm mRealm = RealmCache.getInstance().getRealm(mContext);
                        mRealm.executeTransaction(new Realm.Transaction() {//同步保存,先清除已有的数据,再保存
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<CourseModel> results = realm.where(CourseModel.class).findAll();
                                results.deleteAllFromRealm();
                                realm.copyToRealm(courseModels);
                            }
                        });
                        mRealm.close();
                        return true;
                    }
                })
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 请求成绩
     */
    public void requestScores(HttpResultListener<Boolean> listener) {
        ssfwSecureCheck()
                .flatMap(new Func1<String, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(String s) {
                        return ssfwService(s).scores();
                    }
                })

                .map(new Func1<ResponseBody, List<ScoreModel>>() {
                    @Override
                    public List<ScoreModel> call(ResponseBody body) {
                        return HtmlParser.parseScore(body);
                    }
                })
                .map(new Func1<List<ScoreModel>, Boolean>() {
                    @Override
                    public Boolean call(final List<ScoreModel> scoreModels) {
                        if (scoreModels.size() == 0) {
                            return false;
                        }
                        Realm mRealm = RealmCache.getInstance().getRealm(mContext);
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<ScoreModel> results = realm.where(ScoreModel.class).findAll();
                                results.deleteAllFromRealm();
                                realm.copyToRealm(scoreModels);
                            }
                        });
                        mRealm.close();
                        return true;
                    }
                })
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 请求用户信息
     */
    public void requestUserInfo(HttpResultListener<UserInfoModel> listener) {
        ssfwSecureCheck()
                .flatMap(new Func1<String, Observable<String>>() {// 获取培养方案 ID
                    @Override
                    public Observable<String> call(String s) {
                        return getPyfaId(s);
                    }
                })
                .flatMap(new Func1<String, Observable<ResponseBody>>() {// 获取培养方案
                    @Override
                    public Observable<ResponseBody> call(String s) {
                        // 本来需要将这个字符串作为 get 方式的参数发送,但是由于编码问题导致失败
//                        String paramStr = "{\"pyfadm\":" + s + ",\"nodeType\":\"-99\",\"nodeValue\":\"root\",\"sfggkz\":\"0\"}";
//                        try {
//                            paramStr = URLEncoder.encode(paramStr, "utf-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                        // 于是从抓包中得到下面这个字符串,是已经经过编码的参数,对于不同用于只需要传入不同的 id,其他不变
                        String paramStr = "%7B%22pyfadm%22%3A+%22" + s + "%22%2C+%22sfggkz%22%3A+%220%22%2C+%22nodeValue%22%3A+%22root%22%2C+%22nodeType%22%3A+%22-99%22%7D";
                        return ssfwService(User.currentUser().getSchoolLoginToken()).pyfaNode(paramStr);
                    }
                })
                .map(new Func1<ResponseBody, UserInfoModel>() {// 解析培养方案 json,没有真实姓名
                    @Override
                    public UserInfoModel call(ResponseBody body) {
                        return HtmlParser.parseUserInfo(body);
                    }
                })
                .flatMap(new Func1<UserInfoModel, Observable<UserInfoModel>>() {//获取真实姓名并解析放到模型中
                    @Override
                    public Observable<UserInfoModel> call(UserInfoModel userInfoModel) {
                        return getRealName(userInfoModel);
                    }
                })
                .map(new Func1<UserInfoModel, UserInfoModel>() {//保存用户信息
                    @Override
                    public UserInfoModel call(UserInfoModel userInfoModel) {
                        User.currentUser().saveUserInfo(userInfoModel);
                        return userInfoModel;
                    }
                })
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }


    /**
     * 请求学分绩点,在成绩的基础上计算
     */
    public void requestCredit(HttpResultListener<CreditModel> listener) {
        ssfwSecureCheck()// 安全验证
                .flatMap(new Func1<String, Observable<ResponseBody>>() {//请求成绩表
                    @Override
                    public Observable<ResponseBody> call(String s) {
                        return ssfwService(s).scores();
                    }
                })
                .map(new Func1<ResponseBody, List<ScoreModel>>() {//解析成绩表
                    @Override
                    public List<ScoreModel> call(ResponseBody body) {
                        return HtmlParser.parseScore(body);
                    }
                })
                .map(new Func1<List<ScoreModel>, CreditModel>() {//计算学分信息,并保存
                    @Override
                    public CreditModel call(final List<ScoreModel> scoreModels) {
                        if (scoreModels.size() == 0) {
                            return null;
                        }
                        CreditModel credit = CreditUtil.computeGPA(scoreModels);
                        User.currentUser().saveCreditInfo(credit);

                        Realm mRealm = RealmCache.getInstance().getRealm(mContext);
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<ScoreModel> results = realm.where(ScoreModel.class).findAll();
                                results.deleteAllFromRealm();
                                realm.copyToRealm(scoreModels);
                            }
                        });
                        mRealm.close();
                        return credit;
                    }
                })
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }


    /**
     * 上传验证码图片,返回解析结果
     */
    public void uploadCaptcha(HttpResultListener<String> listener, byte[] bytes) {
        uploadCaptchaObservable(bytes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    private Observable<String> uploadCaptchaObservable(byte[] bytes) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);

        MultipartBody.Part body = MultipartBody.Part.createFormData("captcha", "captcha.jpeg", requestFile);

        return defaultService().uploadCaptcha(body)
                .map(new HttpResultFunc<List<CaptchaModel>>())
                .map(new Func1<List<CaptchaModel>, String>() {
                    @Override
                    public String call(List<CaptchaModel> captchaModels) {
                        if (captchaModels != null && captchaModels.size() > 0) {
                            return captchaModels.get(0).getResult();
                        }
                        return null;
                    }
                });
    }

    /**
     * 更新登录验证,由于验证码解析并不保证 100% 成功,因此会有一个循环执行过程
     * <p/>
     * 请求验证码 --> 解析验证码 --> 登录校园网 --> 更新 Token
     */
    public void updateLoginToken(HttpResultListener<Boolean> listener) {
        //如果用户没有登录,或者上次检查 Token 时间还没过期,则不检查 Token
        if (!User.currentUser().isLogin() || !DateUtil.isVerifyExpired(Pref.get().getLastCheckTokenTime())) {
            Log.i("TAG,", "--tc-->User not login or token not verify");
            return;
        }

        xywCookieService().hljuIndex()//访问应用门户首页,拿到 Cookie
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {//请求验证码
                    @Override
                    public Observable<ResponseBody> call(ResponseBody body) {
                        return captchaService().loginCaptcha();
                    }
                })
                .flatMap(new Func1<ResponseBody, Observable<String>>() {//解析验证码
                    @Override
                    public Observable<String> call(ResponseBody body) {
                        try {
                            return uploadCaptchaObservable(body.bytes());
                        } catch (IOException e) {
                            return null;
                        }
                    }
                })
                .flatMap(new Func1<String, Observable<Boolean>>() {//登录校园网
                    @Override
                    public Observable<Boolean> call(String s) {
                        return loginHLJUObservable(User.currentUser().getUid(), User.currentUser().getPwd(), s);
                    }
                })
                .map(new Func1<Boolean, Boolean>() {//如果成功,更新上次检查 token 时间戳
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean && Pref.get().saveLastCheckTokenTime(System.currentTimeMillis());
                    }
                })
                .retry(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }


    /**
     * 检查更新
     */
    public void checkUpdate(HttpResultListener<CheckUpdateModel> listener, String version) {
        if (!DateUtil.isVerifyExpired(Pref.get().getLastCheckUpdateTime())) {
            Log.i("TAG", "--tc-->check update is in time");
            return;
        }

        defaultService().checkUpdate(version)
                .map(new HttpResultFunc<List<CheckUpdateModel>>())
                .map(new Func1<List<CheckUpdateModel>, CheckUpdateModel>() {
                    @Override
                    public CheckUpdateModel call(List<CheckUpdateModel> checkUpdateModels) {
                        if (checkUpdateModels != null && checkUpdateModels.size() > 0) {
                            return checkUpdateModels.get(0);
                        } else {
                            return null;
                        }
                    }
                })
                .map(new Func1<CheckUpdateModel, CheckUpdateModel>() {//如果获取成功,更新上次检查更新时间戳
                    @Override
                    public CheckUpdateModel call(CheckUpdateModel checkUpdateModel) {
                        if (checkUpdateModel != null) {
                            Pref.get().saveLastCheckUpdateTime(System.currentTimeMillis());
                            return checkUpdateModel;
                        } else {
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 获取启动页图片链接
     */
    public void launchImage(HttpResultListener<Boolean> listener) {
        defaultService().launchImage()
                .map(new HttpResultFunc<List<LaunchImgModel>>())
                .map(new Func1<List<LaunchImgModel>, Boolean>() {
                    @Override
                    public Boolean call(List<LaunchImgModel> list) {
                        return list != null && list.size() > 0 && Pref.get().save(Constant.LAUNCH_IMG_URL, list.get(0).getImgUrl());
                    }
                })
                .timeout(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }


//-------------------------------------------------------------------------------------------

    /**
     * 对返回结果做统一处理,只有当结果码为 100 时,才返回正常,否则返回错误
     */
    private static class HttpResultFunc<T> implements Func1<ResponseModel<T>, T> {

        @Override
        public T call(ResponseModel<T> tResponseModel) {
            if (tResponseModel.getCode() == NetworkException.REQUEST_OK) {
                return tResponseModel.getResult();
            } else {
                throw new NetworkException(tResponseModel.getCode());
            }
        }
    }


    private static class HttpResultSubscriber<T> extends Subscriber<T> {

        private HttpResultListener<T> mListener;

        public HttpResultSubscriber(HttpResultListener<T> listener) {
            mListener = listener;
        }


        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (mListener != null) {
                mListener.onError(e);
            }
        }

        @Override
        public void onNext(T t) {
            if (mListener != null) {
                mListener.onSuccess(t);
            }
        }
    }
}
