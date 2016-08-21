package com.ibookpa.hdbox.android.network;

import android.content.Context;

import com.ibookpa.hdbox.android.api.Api;
import com.ibookpa.hdbox.android.app.BaseApplication;
import com.ibookpa.hdbox.android.model.CheckUpdateModel;
import com.ibookpa.hdbox.android.model.CourseModel;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.ExamModel;
import com.ibookpa.hdbox.android.model.LoginModel;
import com.ibookpa.hdbox.android.model.NewsModel;
import com.ibookpa.hdbox.android.model.ResponseModel;
import com.ibookpa.hdbox.android.model.ScoreModel;
import com.ibookpa.hdbox.android.model.UserInfoModel;
import com.ibookpa.hdbox.android.persistence.Shared;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.persistence.db.RealmCache;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tc on 7/1/16. Api 0.3 版本服务
 */
public class ApiService {

    private Api mApi;
    private Context mContext;
    private static ApiService mInstance;

//-------- 存在内存泄漏的写法,如果传入 Activity 的 Context,会导致 Activity 无法被回收-------------------
//    private ApiService(Context mContext) {
//        this.mContext = mContext;
//        mApi = RetrofitClient.getClient(mContext).create(Api.class);
//    }
//
//    public static ApiService get(Context mContext) {
//        if (mInstance == null) {
//            mInstance = new ApiService(mContext);
//        }
//        return mInstance;
//    }
//------------------------------------------------------------------------------------------------

    private ApiService() {
        this.mContext = BaseApplication.get().getAppContext();
        mApi = RetrofitClient.getClient(mContext).create(Api.class);
    }

    public static ApiService getInstance() {
        if (mInstance == null) {
            mInstance = new ApiService();
        }
        return mInstance;
    }

    /**
     * 登录
     */
    public void login(HttpResultListener<Boolean> listener, final String uid, final String pwd) {
        mApi.login(uid, pwd)
                .map(new HttpResultFunc<List<LoginModel>>())
                .map(new Func1<List<LoginModel>, Boolean>() {//保存登录信息
                    @Override
                    public Boolean call(List<LoginModel> loginModels) {
                        if (loginModels.size() == 0) {
                            return false;
                        }
                        MobclickAgent.onProfileSignIn(uid);
                        Shared.saveLoginInfo(uid, pwd, loginModels.get(0).getToken());
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 获取用户信息
     */
    public void userInfo(HttpResultListener<Boolean> listener) {

        getToken()//获取 token
                .flatMap(new Func1<LoginModel, Observable<ResponseModel<List<UserInfoModel>>>>() {

                    @Override
                    public Observable<ResponseModel<List<UserInfoModel>>> call(LoginModel loginModel) {
                        return mApi.userinfo(loginModel.getUid(), loginModel.getToken());
                    }
                })
                .map(new HttpResultFunc<List<UserInfoModel>>())
                .map(new Func1<List<UserInfoModel>, Boolean>() {//获取成功,保存用户信息
                    @Override
                    public Boolean call(List<UserInfoModel> userInfoModels) {
                        if (userInfoModels.size() == 0) {
                            return false;
                        }
                        Shared.saveUserInfo(userInfoModels.get(0));
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 获取学分信息
     */
    public void credit(HttpResultListener<Boolean> listener) {
        getToken()
                .flatMap(new Func1<LoginModel, Observable<ResponseModel<List<CreditModel>>>>() {
                    @Override
                    public Observable<ResponseModel<List<CreditModel>>> call(LoginModel loginModel) {
                        return mApi.credit(loginModel.getUid(), loginModel.getToken());
                    }
                })
                .map(new HttpResultFunc<List<CreditModel>>())
                .map(new Func1<List<CreditModel>, Boolean>() {
                    @Override
                    public Boolean call(List<CreditModel> creditModels) {
                        if (creditModels.size() == 0) {
                            return false;
                        }

                        Shared.saveCreditInfo(creditModels.get(0));
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 获取课程表
     */
    public void courses(HttpResultListener<List<CourseModel>> listener) {

        getToken()
                .flatMap(new Func1<LoginModel, Observable<ResponseModel<List<CourseModel>>>>() {
                    @Override
                    public Observable<ResponseModel<List<CourseModel>>> call(LoginModel loginModel) {
                        return mApi.courses(loginModel.getUid(), loginModel.getToken());
                    }
                })
                .map(new HttpResultFunc<List<CourseModel>>())
                .map(new Func1<List<CourseModel>, List<CourseModel>>() {//获取成功,保存
                    @Override
                    public List<CourseModel> call(final List<CourseModel> list) {
                        Realm mRealm = RealmCache.getInstance().getRealm(mContext);
                        mRealm.executeTransaction(new Realm.Transaction() {//同步保存,先清除已有的数据,再保存
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<CourseModel> results = realm.where(CourseModel.class).findAll();
                                results.deleteAllFromRealm();
                                realm.copyToRealm(list);
                            }
                        });
                        mRealm.close();
                        return list;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }


    /**
     * 获取成绩表
     */
    public void scores(HttpResultListener<Boolean> listener) {
        getToken()
                .flatMap(new Func1<LoginModel, Observable<ResponseModel<List<ScoreModel>>>>() {
                    @Override
                    public Observable<ResponseModel<List<ScoreModel>>> call(LoginModel loginModel) {
                        return mApi.scores(loginModel.getUid(), loginModel.getToken());
                    }
                })
                .map(new HttpResultFunc<List<ScoreModel>>())
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }


    /**
     * 获取考试信息表
     */
    public void exams(HttpResultListener<Boolean> listener) {
        getToken()
                .flatMap(new Func1<LoginModel, Observable<ResponseModel<List<ExamModel>>>>() {
                    @Override
                    public Observable<ResponseModel<List<ExamModel>>> call(LoginModel loginModel) {
                        return mApi.exams(loginModel.getUid(), loginModel.getToken());
                    }
                })
                .map(new HttpResultFunc<List<ExamModel>>())
                .map(new Func1<List<ExamModel>, Boolean>() {
                    @Override
                    public Boolean call(final List<ExamModel> list) {
                        if (list.size() == 0) {
                            return false;
                        }
                        Realm mRealm = RealmCache.getInstance().getRealm(mContext);
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<ExamModel> results = realm.where(ExamModel.class).findAll();
                                results.deleteAllFromRealm();
                                realm.copyToRealm(list);
                            }
                        });
                        mRealm.close();
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 获取新闻列表
     */
    public void newsList(HttpResultListener<List<NewsModel>> listener, int page) {
        mApi.newslist(page)
                .delay(2, TimeUnit.SECONDS)//延迟 2 秒,使下拉刷新不至于刚下拉就收回
                .map(new HttpResultFunc<List<NewsModel>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    /**
     * 获取新闻详情
     */
    public void newsDetail(HttpResultListener<List<NewsModel>> listener, int nid) {
        mApi.newsdetail(nid)
                .map(new HttpResultFunc<List<NewsModel>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

    public void checkUpdate(HttpResultListener<List<CheckUpdateModel>> listener, int versionCode) {
        mApi.checkUpdate("android", versionCode)
                .map(new HttpResultFunc<List<CheckUpdateModel>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpResultSubscriber<>(listener));
    }

//------------------------------------------------------------------------------------

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

    /**
     * 获取 uid 和 token
     */
    private Observable<LoginModel> getToken() {
        return Observable.create(new Observable.OnSubscribe<LoginModel>() {
            @Override
            public void call(Subscriber<? super LoginModel> subscriber) {
                LoginModel login = User.currentUser().getLoginInfo();
                if (login != null) {
                    subscriber.onNext(login);
                } else {
                    subscriber.onError(new NetworkException(108));
                }
            }
        });
    }

}
