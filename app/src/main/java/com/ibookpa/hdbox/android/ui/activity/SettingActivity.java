package com.ibookpa.hdbox.android.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.persistence.db.RealmCache;
import com.umeng.analytics.MobclickAgent;

import io.realm.Realm;

/**
 * Created by lin_sir on 2016/6/18. 设置界面
 */
public class SettingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //设置状态栏文字为深色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }

    }

    public void onBack(View view) {
        finish();
    }

    public void onFeedback(View view) {

        Toast.makeText(SettingActivity.this, "该功能尚未开启", Toast.LENGTH_SHORT).show();
    }

    public void onCheck(View view) {

    }

    public void onAbout(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void onLogout(View view) {
        if (User.currentUser().isLogin()) {

            User.currentUser().logout();
            MobclickAgent.onProfileSignOff();
            final Realm mRealm = RealmCache.getInstance().getRealm(SettingActivity.this);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });
            finish();
        } else {
            //TODO 未登录时的一些操作
        }
    }

}
