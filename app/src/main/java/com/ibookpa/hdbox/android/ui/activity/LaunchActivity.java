package com.ibookpa.hdbox.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.app.Constant;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.network.SchoolApiService;
import com.ibookpa.hdbox.android.persistence.Pref;

/**
 * Created by tc on 8/2/16.启动页
 */
public class LaunchActivity extends AppCompatActivity {


    private ImageView ivLaunch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);
        ivLaunch = (ImageView) findViewById(R.id.iv_launch);

    }

    @Override
    protected void onStart() {
        super.onStart();

//        SchoolApiService.get().launchImage(new HttpResultListener<Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//                if (aBoolean) {
//                    Glide.with(LaunchActivity.this)
//                            .load(Pref.get().getString(Constant.LAUNCH_IMG_URL))
////                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .override(512, 1024)
//                            .into(ivLaunch);
//
//                    //设置LOGO的动画效果
//                    Animation logoAnim = AnimationUtils.loadAnimation(LaunchActivity.this, R.anim.launch_in_anim);
//                    logoAnim.setAnimationListener(new AnimListener());
//                    ivLaunch.startAnimation(logoAnim);
//                } else {
//                    Log.i("TAG", "--tc-->launch error");
//                    intoMain();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("TAG", "--tc-->launch error is:" + e.toString());
//                intoMain();
//            }
//        });

        intoMain();
    }

    /**
     * 动画监听事件,动画结束跳转到主页
     */
    private class AnimListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            intoMain();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private void intoMain() {
        startActivity(new Intent(LaunchActivity.this, TabActivity.class));
        finish();
    }

}
