package com.ibookpa.hdbox.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.model.NewsModel;
import com.ibookpa.hdbox.android.network.ApiService;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.utils.StatusBarUtil;

import java.util.List;

/**
 * Created by tc on 6/23/16.新闻详情页
 */
public class NewsNoImgDetailActivity extends AppCompatActivity {


    private ProgressBar mProgress;
    private TextView tvTitle;
    private WebView mWebView;

    private HttpResultListener<List<NewsModel>> mNewsResultListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_no_img_detail);
        initViews();
        StatusBarUtil.setStatusBarColor(this,R.color.red_700);
        int nid = getIntent().getIntExtra("nid", 0);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        mNewsResultListener = new HttpResultListener<List<NewsModel>>() {
            @Override
            public void onSuccess(List<NewsModel> list) {
                hideProgress();
                bindData(list.get(0));
            }

            @Override
            public void onError(Throwable e) {

            }
        };

//        onNextListener = new SubscriberOnNextListener<List<NewsModel>>() {
//            @Override
//            public void onNext(List<NewsModel> list) {
//                Log.i("TAG", "--tc--> request news detail success: " + list.size());
//                hideProgress();
//                bindData(list.getURLCookie(0));
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//        };

        loadData(nid);
    }


    private void initViews() {
        mProgress = (ProgressBar) findViewById(R.id.progress_news_no_img_detail);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        mWebView = (WebView) findViewById(R.id.web_news_no_img);
    }

    private void bindData(NewsModel news) {
        tvTitle.setText(news.getTitle());
        mWebView.loadData(news.getContent(), "text/html;charset=UTF-8", null);
    }

    private void loadData(int nid) {
        showProgress();
        ApiService.getInstance().newsDetail(mNewsResultListener, nid);
    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
    }

    public void onBack(View view) {
        finish();
    }
}
