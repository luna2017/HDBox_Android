package com.ibookpa.hdbox.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.model.UserInfoModel;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.network.SchoolApiService;
import com.ibookpa.hdbox.android.persistence.User;
import com.ibookpa.hdbox.android.ui.activity.LoginActivity;
import com.ibookpa.hdbox.android.ui.activity.SettingActivity;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.utils.DateUtil;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;

/**
 * Created by lin_sir on 2016/6/11. 个人界面
 */
public class PersonFragment extends Fragment implements View.OnClickListener {

    private static int REFRESH_TIMES = 0;// 用户刷新次数,just for fun :)

    private TextView tvTitle;
    private ImageView ivAvatar;

    private TextView tvToLogin;
    private LinearLayout llLoginUserInfo;
    private TextView tvName;
    private TextView tvUid;

    private ImageView ivSetting;

    private ImageView ivUserInfoRefresh;
    private CardView cardUserInfo;
    private TextView tvGrade;
    private TextView tvCollege;
    private TextView tvSpecialty;

    private TextView tvSlogan;

    private CardView cardCreditInfo;
    private ImageView ivCreditRefresh;
    private TextView tvPassCredit;
    private TextView tvFailedCredit;
    private TextView tvGpa;

    private HttpResultListener<UserInfoModel> mUserInfoListener;
    private HttpResultListener<CreditModel> mCreditListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        tvTitle = (TextView) view.findViewById(R.id.tv_person_title);
        ivAvatar = (ImageView) view.findViewById(R.id.iv_person_avatar);
        tvToLogin = (TextView) view.findViewById(R.id.tv_person_to_login);
        llLoginUserInfo = (LinearLayout) view.findViewById(R.id.ll_person_login);
        tvName = (TextView) view.findViewById(R.id.tv_person_realName);
        tvUid = (TextView) view.findViewById(R.id.tv_person_uid);

        ivSetting = (ImageView) view.findViewById(R.id.iv_person_setting);

        ivUserInfoRefresh = (ImageView) view.findViewById(R.id.iv_person_refresh);
        cardUserInfo = (CardView) view.findViewById(R.id.card_person_info);
        cardCreditInfo = (CardView) view.findViewById(R.id.card_person_credit);
        ivCreditRefresh = (ImageView) view.findViewById(R.id.iv_credit_refresh);

        tvSlogan = (TextView) view.findViewById(R.id.tv_slogan);
        tvCollege = (TextView) view.findViewById(R.id.tv_person_college);
        tvGrade = (TextView) view.findViewById(R.id.tv_person_grade);
        tvSpecialty = (TextView) view.findViewById(R.id.tv_person_specialty);
        tvPassCredit = (TextView) view.findViewById(R.id.tv_show_pass_credit);
        tvFailedCredit = (TextView) view.findViewById(R.id.tv_person_fail_credit);
        tvGpa = (TextView) view.findViewById(R.id.tv_person_gpa);

        ivAvatar.setOnClickListener(this);
        tvToLogin.setOnClickListener(this);
        ivSetting.setOnClickListener(this);
        ivUserInfoRefresh.setOnClickListener(this);
        ivCreditRefresh.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mUserInfoListener = new HttpResultListener<UserInfoModel>() {
            @Override
            public void onSuccess(UserInfoModel userInfoModel) {
                stopRefreshUserInfo();
                bindUserInfo(userInfoModel);
                REFRESH_TIMES = 0;
                showToast("刷新成功!");
            }

            @Override
            public void onError(Throwable e) {
                stopRefreshUserInfo();
                setUserInfoError();
                if (REFRESH_TIMES == 1) {
                    showToast("当前网络不稳定,请重试...");
                } else if (REFRESH_TIMES == 2) {
                    showToast("请再试一下~");
                } else if (REFRESH_TIMES == 3) {
                    showToast("校园网不太稳定,不要放弃,继续刷!");
                } else if (REFRESH_TIMES == 4) {
                    showToast("马上就能获取到数据了,加油~");
                } else if (REFRESH_TIMES == 5) {
                    showToast("我尽力了,这锅得让校园网背...");
                } else {
                    if (REFRESH_TIMES > 0) {
                        showToast("放弃吧,请过会儿再试:)");
                    }
                }
            }
        };

        mCreditListener = new HttpResultListener<CreditModel>() {
            @Override
            public void onSuccess(CreditModel creditModel) {
                stopRefreshCredit();
                bindCreditData(creditModel);
            }

            @Override
            public void onError(Throwable e) {
                stopRefreshCredit();
                setCreditError();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        tvTitle.setText(DateUtil.getCurrentHelloText());
        REFRESH_TIMES = 0;
        MobclickAgent.onPageStart("PersonFragment");

        if (User.currentUser().isLogin()) {//如果用户已经登录,则显示用户信息和学分信息,否则显示没有登录
            showLoginView();
            fetchUserInfoData();
            fetchCreditData();
        } else {
            showNotLoginView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PersonFragment");
    }

    /**
     * 显示已登录的界面视图
     */
    private void showLoginView() {
        tvSlogan.setVisibility(View.GONE);
        tvToLogin.setVisibility(View.GONE);

        llLoginUserInfo.setVisibility(View.VISIBLE);
        cardUserInfo.setVisibility(View.VISIBLE);
        cardCreditInfo.setVisibility(View.VISIBLE);
    }

    /**
     * 显示未登录的界面视图
     */
    private void showNotLoginView() {
        tvSlogan.setVisibility(View.VISIBLE);
        tvToLogin.setVisibility(View.VISIBLE);

        llLoginUserInfo.setVisibility(View.GONE);
        cardUserInfo.setVisibility(View.GONE);
        cardCreditInfo.setVisibility(View.GONE);
    }


    /**
     * 取用户信息数据,如果已经有了则直接绑定,否则先请求再绑定
     */
    private void fetchUserInfoData() {
        UserInfoModel userInfo = User.currentUser().getUserInfo();
        if (userInfo != null) {
            bindUserInfo(userInfo);
        } else {
            setUserInfoLoading();
            requestUserInfo();
        }
    }

    /**
     * 取学分信息数据,如果已经有了则直接绑定,否则先请求再绑定
     */
    private void fetchCreditData() {
        CreditModel credit = User.currentUser().getCreditInfo();
        if (credit != null) {
            bindCreditData(credit);
        } else {
            setCreditLoading();
            requestCreditInfo();
        }
    }

    /**
     * 请求用户信息
     */
    private void requestUserInfo() {
        startRefreshUserInfo();
        SchoolApiService.get().requestUserInfo(mUserInfoListener);
    }

    /**
     * 请求学分信息
     */
    private void requestCreditInfo() {
        startRefreshCredit();
        SchoolApiService.get().requestCredit(mCreditListener);
    }


    /**
     * 绑定用户信息
     */
    private void bindUserInfo(UserInfoModel user) {

        tvName.setText(user.getName());
        tvUid.setText(user.getUid());

        tvCollege.setText(user.getCollege());
        tvGrade.setText(user.getGrade());
        tvSpecialty.setText(user.getSpecialty());
    }

    /**
     * 绑定学分信息数据
     */
    private void bindCreditData(CreditModel credit) {

        DecimalFormat gpaFormat = new DecimalFormat("0.0000");
        DecimalFormat creditFormat = new DecimalFormat("0.0");

        tvPassCredit.setText(creditFormat.format(credit.getPassCredit()));
        tvPassCredit.setTextSize(20);
        tvFailedCredit.setText(creditFormat.format(credit.getFailedCredit()));
        tvFailedCredit.setTextSize(20);
        tvGpa.setText(gpaFormat.format(credit.getGpa()));
        tvGpa.setTextSize(20);
    }

    /**
     * 设置用户信息加载中
     */
    private void setUserInfoLoading() {
        tvCollege.setText("加载中...");
        tvSpecialty.setText("加载中...");
        tvGrade.setText("加载中...");
    }

    /**
     * 设置用户信息加载错误
     */
    private void setUserInfoError() {
        UserInfoModel userInfo = User.currentUser().getUserInfo();
        if (userInfo != null) {
            return;
        }
        tvCollege.setText("加载失败,请重试...");
        tvSpecialty.setText("加载失败,请重试...");
        tvGrade.setText("加载失败,请重试...");
    }


    /**
     * 设置学分信息加载中
     */
    private void setCreditLoading() {
        tvPassCredit.setText("加载中...");
        tvPassCredit.setTextSize(14);
        tvFailedCredit.setText("加载中...");
        tvFailedCredit.setTextSize(14);
        tvGpa.setText("加载中...");
        tvGpa.setTextSize(14);
    }

    /**
     * 设置学分信息加载错误
     */
    private void setCreditError() {
        CreditModel credit = User.currentUser().getCreditInfo();
        if (credit != null) {
            return;
        }
        tvPassCredit.setText("加载失败...");
        tvPassCredit.setTextSize(14);
        tvFailedCredit.setText("加载失败...");
        tvFailedCredit.setTextSize(14);
        tvGpa.setText("加载失败...");
        tvGpa.setTextSize(14);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_person_avatar:
                if (User.currentUser().isLogin()) {
                    Toast.makeText(getActivity(), "头像功能将在后续版本增加~", Toast.LENGTH_SHORT).show();


                } else {
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;

            case R.id.iv_person_setting:
                getActivity().startActivity(new Intent(getActivity(), SettingActivity.class));
                break;

            case R.id.tv_person_to_login:
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                break;

            case R.id.iv_person_refresh:
                REFRESH_TIMES++;
                requestUserInfo();
                break;
            case R.id.iv_credit_refresh:
                requestCreditInfo();
                break;
            default:
                break;
        }
    }

    private void startRefreshUserInfo() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_rotate_anim);
        ivUserInfoRefresh.startAnimation(animation);
    }

    private void stopRefreshUserInfo() {
        ivUserInfoRefresh.clearAnimation();
    }


    private void startRefreshCredit() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_rotate_anim);
        ivCreditRefresh.startAnimation(animation);
    }

    private void stopRefreshCredit() {
        ivCreditRefresh.clearAnimation();
    }


    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}