package com.ibookpa.hdbox.android.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.manager.PermissionManager;
import com.ibookpa.hdbox.android.manager.UpdateManager;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.network.SchoolApiService;
import com.ibookpa.hdbox.android.persistence.Pref;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.ui.adapter.TabPagerAdapter;
import com.ibookpa.hdbox.android.ui.view.ScrollableViewPager;
import com.ibookpa.hdbox.android.utils.DateUtil;
import com.ibookpa.hdbox.android.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by tc on 6/23/16. 主 Tab 界面
 */
public class TabActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ScrollableViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        initViews();
        initViewPager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new CheckThread().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        tabLayout = (TabLayout) findViewById(R.id.tab_main);
        viewPager = (ScrollableViewPager) findViewById(R.id.pager_main);
    }

    private void initViewPager() {
        TabPagerAdapter mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager.setScrollable(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mPagerAdapter);

        mPagerAdapter.setupTabLayout(tabLayout, viewPager);
    }

    /**
     * 后台检查的线程,检查 token 和 检查更新
     */
    private class CheckThread extends Thread {
        @Override
        public void run() {
            super.run();
            SystemClock.sleep(3000);

            //如果没有读写文件的权限,先申请权限,有权限就直接检查更新
            if (PermissionManager.checkPermission(TabActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                checkUpdate(TabActivity.this);
            }

            checkToken();
        }
    }

    /**
     * 检查 token 是否过期,如果过期了就重新获取,默认过期时限为 10 分钟
     */
    private void checkToken() {
        // 如果当前用户没有登录,或者验证过期时间还没到,不执行检查操作
//        if (!User.currentUser().isLogin() || !DateUtil.isVerifyExpired(Pref.get().getLastCheckUpdateTime())) {
//            return;
//        }
//
//        SchoolApiService.get().updateLoginToken(new HttpResultListener<Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//                if (aBoolean) {
//                    Pref.get().saveLastCheckUpdateTime(System.currentTimeMillis());
//                } else {
//                    //TODO 延后 3 分钟再次请求
//                }
//                Log.i("TAG", "--tc-->Auto Login update success:" + aBoolean);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("TAG", "--tc-->Auto Login update error:" + e.toString());
//            }
//        });

        SchoolApiService.get().updateLoginToken(new HttpResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.i("TAG", "--tc--> Auto login update success:" + aBoolean);
            }

            @Override
            public void onError(Throwable e) {
                Log.i("TAG", "--tc--> Auto login update error:" + e.toString());
            }
        });
    }

    /**
     * 检查更新
     */
    private void checkUpdate(Context context) {
        UpdateManager.getInstance().checkUpdate(context);
    }

    /**
     * 权限请求回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 申请权限成功,执行操作,否则提示用户
        if (requestCode == Constant.PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkUpdate(TabActivity.this);
        } else {
            ToastUtil.showToast(TabActivity.this, "权限申请失败,无法检查更新");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
