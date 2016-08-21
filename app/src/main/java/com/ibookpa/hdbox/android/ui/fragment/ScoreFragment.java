package com.ibookpa.hdbox.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.dialog.CustomDialog;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.network.SchoolApiService;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.persistence.db.RealmCache;
import com.ibookpa.hdbox.android.model.ScoreModel;
import com.ibookpa.hdbox.android.ui.adapter.ScoreRecyclerAdapter;
import com.umeng.analytics.MobclickAgent;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by lin_sir on 2016/6/18.成绩界面
 */
public class ScoreFragment extends BaseFragment implements ScoreRecyclerAdapter.OnScoreItemClickListener, RealmChangeListener<RealmResults<ScoreModel>> {

    private LinearLayout llLoading;
    private LinearLayout llPleaseLogin;

    private RecyclerView mRecyclerView;

    private ScoreRecyclerAdapter mAdapter;

    private HttpResultListener<Boolean> mListener;

    private Realm mRealm;
    private RealmResults<ScoreModel> mResults;

    @Override
    int setViewId() {
        return R.layout.fragment_score;
    }

    @Override
    public void initViews(View view) {

        llPleaseLogin = (LinearLayout) view.findViewById(R.id.ll_please_login);
        llLoading = (LinearLayout) view.findViewById(R.id.ll_loading);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_score);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ScoreRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addOnScoreItemClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListener = new HttpResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                Log.i("TAG", "--tc-->ScoreFragment error:" + e.toString());
                stopRefresh();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mRealm = RealmCache.getInstance().getRealm(getActivity());
        mResults = mRealm.where(ScoreModel.class).findAllAsync();
        mResults.addChangeListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!User.currentUser().isLogin()) {//如果用户没有登录,则显示请登录,否则显示数据
            showPleaseLogin();
        } else {
            showDataView();
        }
        MobclickAgent.onPageStart("ScoreFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ScoreFragment");
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
        mRealm = null;
    }

    @Override
    public void refreshData() {
        if (User.currentUser().isLogin()) {
            startRefresh();
            SchoolApiService.get().requestScores(mListener);
        } else {
            showPleaseLogin();
        }
    }

    @Override
    public void onItemClick(ScoreModel score) {
        CustomDialog.showScoreItemDialog(getActivity(), score);
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
    public void onChange(RealmResults<ScoreModel> element) {
        if (element.size() > 0 && element.isValid()) {
            showDataView();
            mAdapter.refreshData(element);
            stopRefresh();
        } else {
            mAdapter.clear();
            refreshData();

        }
    }
}