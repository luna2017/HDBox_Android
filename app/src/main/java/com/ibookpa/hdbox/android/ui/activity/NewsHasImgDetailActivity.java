package com.ibookpa.hdbox.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.model.NewsModel;
import com.ibookpa.hdbox.android.network.ApiService;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.utils.ImageUtil;

import java.util.List;

/**
 * Created by tc on 6/23/16.新闻详情页
 */
public class NewsHasImgDetailActivity extends AppCompatActivity {

    private ProgressBar mProgress;
    private CollapsingToolbarLayout mCollapsingLayout;
    private ImageView ivBackground;
    private Toolbar mToolbar;

    private WebView mWebView;

    private HttpResultListener<List<NewsModel>> mNewsResultListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_has_img_detail);
        initViews();

        int nid = getIntent().getIntExtra("nid", 0);

        String imgUrl = getIntent().getStringExtra("imgUrl");

        ImageUtil.requestImg(NewsHasImgDetailActivity.this, imgUrl, ivBackground);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);


        mNewsResultListener = new HttpResultListener<List<NewsModel>>() {
            @Override
            public void onSuccess(List<NewsModel> list) {
                Log.i("TAG", "--tc-->NewsDetail success:" + list.size());
                hideProgress();
                bindData(list.get(0));
            }

            @Override
            public void onError(Throwable e) {
                Log.i("TAG", "--tc-->NewsDetail failure");
            }
        };
        loadData(nid);
    }

    private void initViews() {
        mProgress = (ProgressBar) findViewById(R.id.progress_news_detail);
        mCollapsingLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_news_detail);
        ivBackground = (ImageView) findViewById(R.id.iv_news_detail_background);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_news_detail);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mWebView = (WebView) findViewById(R.id.web_news_detail);

        mCollapsingLayout.setContentScrimColor(getResources().getColor(R.color.red_500));
        mCollapsingLayout.setStatusBarScrimColor(getResources().getColor(R.color.red_700));

    }

    private void bindData(NewsModel news) {
        mCollapsingLayout.setTitle(news.getTitle());
        String content = news.getContent();
        mWebView.loadData(content, "text/html;charset=UTF-8", null);
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
}
