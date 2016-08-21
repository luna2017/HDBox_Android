package com.ibookpa.hdbox.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.ui.adapter.SchoolTabFragmentAdapter;
import com.ibookpa.hdbox.android.ui.listener.OnFragmentRefreshListener;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by lin_sir on 2016/6/11. 校内界面
 * <p>
 * TODO 在界面切换的时候,要停止当前界面的数据请求,并且停止刷新动画
 */
public class SchoolFragment extends Fragment implements View.OnClickListener, OnFragmentRefreshListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SchoolTabFragmentAdapter mAdapter;

    private ImageView ivRefresh;

    private int currentPageIndex;

//    private Spinner mSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragments();

//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mAdapter.setCourseCurrentWeek(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    private void initViews(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tab_school);
        viewPager = (ViewPager) view.findViewById(R.id.pager_school);
        ivRefresh = (ImageView) view.findViewById(R.id.iv_school_refresh);
        ivRefresh.setOnClickListener(this);
//        mSpinner = (Spinner) view.findViewById(R.id.spinner_school);
    }

    private void initFragments() {
        mAdapter = new SchoolTabFragmentAdapter(getChildFragmentManager());
        mAdapter.setOnFragmentRefreshListener(this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mAdapter);

        viewPager.addOnPageChangeListener(new OnViewPagerChangeListener());
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (User.currentUser().isLogin() && currentPageIndex == 1) {
//            mSpinner.setVisibility(View.VISIBLE);
//        } else {
//            mSpinner.setVisibility(View.GONE);
//        }

        MobclickAgent.onPageStart("SchoolFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SchoolFragment");
    }

    public class OnViewPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            stopRefreshAnim();//在切换界面的时候不显示刷新动画
        }

        @Override
        public void onPageSelected(int position) {
            currentPageIndex = position;
//            if (position != 2) {
//                mSpinner.setVisibility(View.GONE);
//            } else {
//
//                mSpinner.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_school_refresh:
                refreshFragment(currentPageIndex);
                break;
        }
    }

    /**
     * 开始刷新动画
     */
    private void startRefreshAnim() {
        if (!User.currentUser().isLogin()) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_rotate_anim);
        ivRefresh.startAnimation(animation);
    }

    /**
     * 停止刷新动画
     */
    private void stopRefreshAnim() {
        if (ivRefresh != null) {
            ivRefresh.clearAnimation();
        }
    }

    /**
     * 根据当前所在的界面刷新数据
     */
    private void refreshFragment(int position) {
        mAdapter.refreshFragment(position);
    }

    @Override
    public void onStartRefresh() {
        startRefreshAnim();
    }

    @Override
    public void onStopRefresh() {
        stopRefreshAnim();
    }
}

