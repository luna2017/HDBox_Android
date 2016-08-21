package com.ibookpa.hdbox.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.ui.adapter.NewsRecyclerAdapter;
import com.ibookpa.hdbox.android.ui.view.PullToRefreshView;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by lin_sir on 2016/6/13. 新闻页
 */
public class NewsFragment extends Fragment implements PullToRefreshView.OnRefreshListener, NewsRecyclerAdapter.OnNewsRefreshFinishListener {

    private PullToRefreshView mRefresh;
    private LinearLayoutManager mLayoutManager;
    private NewsRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NewsFragment");
        if (mAdapter.isEmpty()) {
            mRefresh.setRefreshing(true);
            mAdapter.refresh();
        } else {
            mRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NewsFragment");
    }

    private void initViews(View view) {
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar_news);
        ViewCompat.setElevation(mToolbar, 4);
        AppCompatActivity mActivity = (AppCompatActivity) getActivity();
        mActivity.setSupportActionBar(mToolbar);

        mRefresh = (PullToRefreshView) view.findViewById(R.id.refresh_news);
        mRefresh.setOnRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_news);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NewsRecyclerAdapter(getActivity());
        mAdapter.setOnNewsRefreshFinishListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new OnRecyclerScrollListener());
    }


    /**
     * 滑动到底部,自动加载更多
     */
    public class OnRecyclerScrollListener extends RecyclerView.OnScrollListener {

        int lastVisibleItem = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && mAdapter.getItemCount() > 1 && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                mAdapter.loadMore();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }
    }

    @Override
    public void onRefresh() {
        mRefresh.setRefreshing(true);
        mAdapter.refresh();
    }

    @Override
    public void onFinish() {
        mRefresh.setRefreshing(false);
    }
}
