package com.ibookpa.hdbox.android.ui.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;

/**
 * Created by tc on 6/29/16. 关于我们
 */
public class AboutActivity extends AppCompatActivity {

    private TextView tvVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tvVersion = (TextView) findViewById(R.id.tv_setting_version);

        String version = getVersionName(this);

        tvVersion.setText(new StringBuilder("当前版本:").append(version));
    }

    public void onBack(View view) {
        finish();
    }

    public static String getVersionName(Context context) {
        String name = "1.0";
        try {
            name = context.getPackageManager().getPackageInfo("com.ibookpa.hdbox.android", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

}
