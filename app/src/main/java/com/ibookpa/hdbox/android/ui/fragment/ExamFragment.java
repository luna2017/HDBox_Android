package com.ibookpa.hdbox.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.model.ExamModel;
import com.ibookpa.hdbox.android.network.ApiService;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.persistence.db.RealmCache;
import com.ibookpa.hdbox.android.ui.adapter.ExamRecyclerAdapter;
import com.umeng.analytics.MobclickAgent;


import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by lin_sir on 2016/6/16. 考试安排界面
 */
public class ExamFragment extends BaseFragment implements RealmChangeListener<RealmResults<ExamModel>> {

    private LinearLayout llPleaseLogin;
    private LinearLayout llLoading;

    private RecyclerView mRecyclerView;
    private ExamRecyclerAdapter mAdapter;

    private HttpResultListener<Boolean> mExamListener;

    private Realm mRealm;
    private RealmResults<ExamModel> mResults;

    @Override
    int setViewId() {
        return R.layout.fragment_exam;
    }

    @Override
    public void initViews(View view) {
        llPleaseLogin = (LinearLayout) view.findViewById(R.id.ll_please_login);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_exams);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new ExamRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mExamListener = new HttpResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.i("TAG", "--tc-->Exam success");
                stopRefresh();

            }

            @Override
            public void onError(Throwable e) {
                Log.i("TAG", "--tc-->Exam error:" + e.toString());
                stopRefresh();
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();

        mRealm = RealmCache.getInstance().getRealm(getActivity());
        mResults = mRealm.where(ExamModel.class).findAllAsync();
        mResults.addChangeListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!User.currentUser().isLogin()) {//如果当前用户没有登录,则显示请登录,否则显示数据
            Log.i("TAG", "--tc-->Exam Fragment not login");
            showPleaseLogin();
        } else {
            Log.i("TAG", "--tc-->Exam Fragment is login");
            showDataView();
        }

        MobclickAgent.onPageStart("ExamFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ExamFragment");
    }

    @Override
    public void onStop() {
        super.onStop();
        mResults.removeChangeListener(this);
        mResults = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        mRealm = null;
    }

    @Override
    public void refreshData() {
        if (User.currentUser().isLogin()) {
            startRefresh();
            ApiService.getInstance().exams(mExamListener);
        } else {
            showPleaseLogin();
        }
    }


    /**
     * 显示请先登录的布局
     */
    @Override
    protected void showPleaseLogin() {
        llPleaseLogin.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    /**
     * 显示加载中的布局
     */
    @Override
    protected void showLoading() {
        llLoading.setVisibility(View.VISIBLE);
        llPleaseLogin.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    /**
     * 显示课表数据的布局
     */
    @Override
    protected void showDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.GONE);
        llPleaseLogin.setVisibility(View.GONE);
    }

    @Override
    public void onChange(RealmResults<ExamModel> element) {
        Log.i("TAG", "--tc-->SchoolFragment ExamFragment onChange:" + element.size());
        if (element.size() > 0 && element.isValid()) {
            showDataView();
            mAdapter.refresh(element);
            stopRefresh();
        } else {
            mAdapter.clear();
            refreshData();
        }
    }
}