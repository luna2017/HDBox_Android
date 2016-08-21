package com.ibookpa.hdbox.android.ui.adapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.ui.fragment.NewsFragment;
import com.ibookpa.hdbox.android.ui.fragment.PersonFragment;
import com.ibookpa.hdbox.android.ui.fragment.SchoolFragment;

/**
 * Created by tc on 6/23/16. tab 的适配器,填充 Fragment 到 ViewPager 中,并设置 tab 选中于没选中的状态
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments = {new NewsFragment(), new SchoolFragment(), new PersonFragment()};

    private int[] imgNormalIds = {R.mipmap.ic_society_normal, R.mipmap.ic_school_normal, R.mipmap.ic_person_normal};
    private int[] imgSelectIds = {R.mipmap.ic_society_selected, R.mipmap.ic_school_selected, R.mipmap.ic_person_selected};


    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    public void setupTabLayout(final TabLayout tabLayout, final ViewPager viewPager) {

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
                tab.setIcon(imgSelectIds[tabLayout.getSelectedTabPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(imgNormalIds[tabLayout.getSelectedTabPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 添加 tab
        for (int i = 0; i < getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                if (i == 0) {
                    tab.setIcon(imgSelectIds[i]);
                } else {
                    tab.setIcon(imgNormalIds[i]);
                }
            }
        }
    }


    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }


    @Override
    public int getCount() {
        return fragments.length;
    }
}
