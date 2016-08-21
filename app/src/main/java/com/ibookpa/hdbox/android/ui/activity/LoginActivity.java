package com.ibookpa.hdbox.android.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.network.SchoolApiService;
import com.ibookpa.hdbox.android.utils.ImageUtil;
import com.ibookpa.hdbox.android.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import okhttp3.ResponseBody;

/**
 * Created by lin_sir on 2016/6/11.登录界面
 */
public class LoginActivity extends AppCompatActivity {
    private EditText etUid;
    private EditText etPwd;
    private EditText etCaptcha;

    private ImageView ivCaptcha;

    private HttpResultListener<ResponseBody> mCaptchaListener;//获取验证码的回调
    private HttpResultListener<Boolean> mSchoolLoginListener;//获取登录结果的回调

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        mProgress = new ProgressDialog(LoginActivity.this);

        mCaptchaListener = new HttpResultListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                try {
                    ImageUtil.requestImgFromBytes(LoginActivity.this, response.bytes(), ivCaptcha);
                } catch (Exception e) {
                    Log.i("TAG","--tc-->up captcha error:"+e.toString());
                    ToastUtil.showToast(LoginActivity.this, "验证码获取失败,请点击图片重试");
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showToast(LoginActivity.this, "验证码获取失败,点击图片重试");
            }
        };

        mSchoolLoginListener = new HttpResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    hideProgress();
                    finish();
                } else {
                    hideProgress();
                    ToastUtil.showToast(LoginActivity.this, "登录失败,请重试");
                }
            }

            @Override
            public void onError(Throwable e) {
                hideProgress();
                ToastUtil.showToast(LoginActivity.this, "登录失败,请重试...");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        SchoolApiService.get().requestCaptcha(mCaptchaListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress != null) {
            mProgress = null;
        }
    }

    private void initViews() {
        etUid = (EditText) findViewById(R.id.et_login_uid);
        etPwd = (EditText) findViewById(R.id.et_login_pwd);
        etCaptcha = (EditText) findViewById(R.id.et_login_captcha);
        ivCaptcha = (ImageView) findViewById(R.id.iv_login_captcha);
    }

    public void onBack(View view) {
        //TODO 点击返回时,应确保网络操作停止
        finish();
    }

    public void onRequestCaptcha(View view) {
        SchoolApiService.get().requestCaptcha(mCaptchaListener);
    }

    public void onLogin(View view) {
        String uid = etUid.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        String captcha = etCaptcha.getText().toString().trim();

//        ToastUtil.showToast(LoginActivity.this,"根据相关规定,黑大盒子暂时停止访问校园网...");
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(captcha)) {
            ToastUtil.showToast(LoginActivity.this, "输入不能为空");
        } else {
            login(uid, pwd, captcha);
        }
    }

    private void login(String uid, String pwd, String captcha) {
        showProgress();
        SchoolApiService.get().loginHLJU(mSchoolLoginListener, uid, pwd, captcha);
    }

    private void setProgressMessage(String message) {
        if (mProgress != null) {
            mProgress.setMessage(message);
        }
    }

    private void showProgress() {
        if (mProgress != null) {
            setProgressMessage("登录中...");
            mProgress.show();
        }
    }

    private void hideProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    private void uploadCaptcha(byte[] bytes){
        SchoolApiService.get().uploadCaptcha(new HttpResultListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i("TAG","--tc-->upload result:"+s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i("TAG","--tc-->upload error:"+e.toString());
            }
        },bytes);
    }
}












