package com.ibookpa.hdbox.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibookpa.hdbox.android.ui.fragment.BaseFragment;
import com.ibookpa.hdbox.android.ui.fragment.CourseFragment;
import com.ibookpa.hdbox.android.ui.fragment.ScoreFragment;
import com.ibookpa.hdbox.android.ui.listener.OnDataRefreshListener;
import com.ibookpa.hdbox.android.ui.listener.OnFragmentRefreshListener;


/**
 * Created by tc on 6/24/16.校内页面 tab 的适配器
 */
public class SchoolTabFragmentAdapter extends FragmentPagerAdapter implements OnDataRefreshListener {

    private BaseFragment[] mFragments = {new ScoreFragment(),new CourseFragment()};
    private String[] titles = {"成绩","课表"};

    private OnFragmentRefreshListener mListener;

    public SchoolTabFragmentAdapter(FragmentManager fm) {
        super(fm);
        for (BaseFragment fragment : mFragments) {
            fragment.setOnDataRefreshListener(this);
        }
    }

    /**
     * 刷新指定的 Fragment
     */
    public void refreshFragment(int position) {
        mFragments[position].refreshData();
    }

//    /**
//     * 设置课表界面当前周
//     *
//     * @param week 当前周,0表示全部课程,从 1 开始,最大 18 周
//     */
//    public void setCourseCurrentWeek(int week) {
//        ((CourseFragment) mFragments[]).setCurrentWeek(week);
//    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }


    @Override
    public void onStartRefresh() {
        if (mListener != null) {
            mListener.onStartRefresh();
        }
    }

    @Override
    public void onStopRefresh() {
        if (mListener != null) {
            mListener.onStopRefresh();
        }
    }

    public void setOnFragmentRefreshListener(OnFragmentRefreshListener listener) {
        mListener = listener;
    }
}
