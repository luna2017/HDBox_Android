package com.ibookpa.hdbox.android.ui.fragment;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.dialog.CustomDialog;
import com.ibookpa.hdbox.android.model.CourseModel;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.network.SchoolApiService;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.persistence.db.RealmCache;
import com.ibookpa.hdbox.android.ui.view.CoursesView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by lin_sir on 2016/6/16.课程表界面
 * <p/>
 * Updated by classTC on 2016/7/01. 数据刷新回来后,课表数据没有更新,因此在刷新后,再点击单个课程,会崩溃
 * 解决办法是重写课表,在 onChange 中更新课表中的数据
 */
public class CourseFragment extends BaseFragment implements CoursesView.OnCourseItemClickListener, RealmChangeListener<RealmResults<CourseModel>> {
    private CoursesView coursesView;

    private LinearLayout llLoading;
    private LinearLayout llPleaseLogin;

    private HttpResultListener<List<CourseModel>> mCoursesListener;
    private HttpResultListener<Boolean> mListener;

    private Realm mRealm;
    private RealmResults<CourseModel> mResults;

    @Override
    int setViewId() {
        return R.layout.fragment_course;
    }

    @Override
    void initViews(View view) {
        coursesView = (CoursesView) view.findViewById(R.id.courses_view);
        coursesView.setOnCourseItemClickListener(this);

        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);
        llPleaseLogin = (LinearLayout) view.findViewById(R.id.ll_please_login);
    }

    @Override
    public void onStart() {
        super.onStart();

        mRealm = RealmCache.getInstance().getRealm(getActivity());

        mResults = mRealm.where(CourseModel.class).findAllAsync();//首先查询缓存
        mResults.addChangeListener(this);

        mCoursesListener = new HttpResultListener<List<CourseModel>>() {
            @Override
            public void onSuccess(List<CourseModel> list) {
                Log.i("TAG","--tc-->Course http ok");
                stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                Log.i("TAG","--tc-->Course http error:"+e.toString());
                stopRefresh();
            }
        };

//        mListener = new HttpResultListener<List<CourseModel>>() {
//            @Override
//            public void onSuccess(List<CourseModel> courseModels) {
//                stopRefresh();
//                Log.i("TAG", "--tc-->Parse Course success + "+courseModels.size());
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("TAG", "--tc-->Parse Course error:"+e.toString());
//            }
//        };

        mListener = new HttpResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                stopRefresh();
                Log.i("TAG", "--tc-->Parse Course success:" + aBoolean);
            }

            @Override
            public void onError(Throwable e) {
                stopRefresh();
                Log.i("TAG", "--tc-->Parse Course error:" + e.toString());
            }
        };
    }

    /**
     * 回到界面时,如果用户已经退出了,则显示请登录.否则显示数据
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!User.currentUser().isLogin()) {//如果返回到界面时,用户没有登录,则显示请登录,否则显示数据
            showPleaseLogin();
        } else {
            showDataView();
        }
        MobclickAgent.onPageStart("CoursesFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CoursesFragment");
    }

    @Override
    public void onStop() {
        super.onStop();
        mResults.removeChangeListener(this);
        mResults = null;
        mRealm.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 刷新数据,由外层 Fragment 调用,作用是将信号发送给外部的刷新按钮
     */
    @Override
    public void refreshData() {
        if (User.currentUser().isLogin()) {
            startRefresh();
            SchoolApiService.get().requestCourses(mListener);
//            ApiService.get().courses(mCoursesListener);
        } else {
            showPleaseLogin();
        }
    }

    /**
     * 显示请先登录的布局
     */
    @Override
    protected void showPleaseLogin() {
        super.showPleaseLogin();
        llPleaseLogin.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.GONE);
        coursesView.setVisibility(View.GONE);
    }

    /**
     * 显示加载中的布局
     */
    @Override
    protected void showLoading() {
        llLoading.setVisibility(View.VISIBLE);
        llPleaseLogin.setVisibility(View.GONE);
        coursesView.setVisibility(View.GONE);
    }

    /**
     * 显示课表数据的布局
     */
    @Override
    protected void showDataView() {
        coursesView.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.GONE);
        llPleaseLogin.setVisibility(View.GONE);
    }

    /**
     * 数据发生改变时,判断有没有数据,如果有,则加载到布局中,否则请求数据
     */
    @Override
    public void onChange(RealmResults<CourseModel> element) {
        Log.i("TAG", "--tc-->CourseFragment onChange:" + element.size());
        if (element.size() > 0 && element.isValid()) {//如果有缓存,则加载缓存并显示,否则刷新数据
            Log.i("TAG", "--tc-->CourseFragment valid");
            coursesView.loadCourses(element);
            showDataView();
            stopRefresh();
        } else {
            coursesView.clear();
            showLoading();
            refreshData();
        }
    }

    public void setCurrentWeek(int currentWeek) {
        if (coursesView != null) {
            coursesView.setCurrentWeek(currentWeek);
        }
    }

    /**
     * 点击显示课程详情
     */
    @Override
    public void onCourseItemClick(CourseModel item) {
        CustomDialog.showCourseItemDialog(getContext(), item);
    }

}
