package com.ibookpa.hdbox.android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.model.NewsModel;
import com.ibookpa.hdbox.android.network.ApiService;
import com.ibookpa.hdbox.android.network.HttpResultListener;
import com.ibookpa.hdbox.android.ui.activity.NewsHasImgDetailActivity;
import com.ibookpa.hdbox.android.ui.activity.NewsNoImgDetailActivity;
import com.ibookpa.hdbox.android.ui.viewholder.FooterItemViewHolder;
import com.ibookpa.hdbox.android.ui.viewholder.NewsHasImgItemViewHolder;
import com.ibookpa.hdbox.android.ui.viewholder.NewsNoImgItemViewHolder;
import com.ibookpa.hdbox.android.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin_sir on 2016/6/18. 新闻页列表适配器
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public static final int FOOTER_TYPE = 0;//最后一个的类型
    public static final int HAS_IMG_TYPE = 1;//有图片的类型
    public static final int NO_IMG_TYPE = 2;//没有图片的类型

    private static int PAGE = 1;//请求分页,默认从 1 开始,每页 10 条数据

    private static boolean isRefresh;//用于标记当前操作是刷新还是加载更多

    private List<NewsModel> mList;
    private FooterItemViewHolder mFooter;

    private HttpResultListener<List<NewsModel>> onResultListener;

    private OnNewsRefreshFinishListener mListener;

    public NewsRecyclerAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
        onResultListener = new HttpResultListener<List<NewsModel>>() {
            @Override
            public void onSuccess(List<NewsModel> list) {
                bindData(list);
                loadMoreFinish();
                refreshFinish();
            }

            @Override
            public void onError(Throwable e) {
                if (isEmpty()) {
                    showLoadError();
                } else {
                    loadMoreFinish();
                }
                refreshFinish();
            }
        };
    }

    public boolean isEmpty() {
        return mList == null || mList.isEmpty();
    }

    /**
     * 绑定数据
     */
    private void bindData(List<NewsModel> list) {
        if (isRefresh) {//如果是刷新,则先清理掉之前的数据
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 暴露给外部调用的刷新操作
     */
    public void refresh() {
        PAGE = 1;
        isRefresh = true;
        requestData(PAGE);
    }

    /**
     * 暴露给外部的加载更多操作
     */
    public void loadMore() {
        isRefresh = false;
        showLoadMore();
        requestData(++PAGE);
    }

    /**
     * 刷新完成,回调 onFinish
     */
    private void refreshFinish() {
        if (mListener != null) {
            mListener.onFinish();
        }
    }

    /**
     * 连网请求数据
     */
    private void requestData(int page) {
        ApiService.getInstance().newsList(onResultListener, page);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER_TYPE) {
            return new FooterItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false));
        } else if (viewType == HAS_IMG_TYPE) {
            return new NewsHasImgItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_has_img, parent, false));
        } else {
            return new NewsNoImgItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_no_img, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if (type == FOOTER_TYPE) {
            bindFooterView((FooterItemViewHolder) holder);
        } else if (type == HAS_IMG_TYPE) {
            bindHasImgView((NewsHasImgItemViewHolder) holder, mList.get(position));
        } else {
            bindNoImgView((NewsNoImgItemViewHolder) holder, mList.get(position));
        }
    }


    private void bindFooterView(FooterItemViewHolder viewHolder) {
        mFooter = viewHolder;
    }


    private void bindHasImgView(NewsHasImgItemViewHolder viewHolder, NewsModel news) {
        String imgUrl = news.getImgUrl();

        if (imgUrl != null) {
            ImageUtil.requestImg(context, imgUrl, viewHolder.ivImg);
        }
        viewHolder.rlItem.setOnClickListener(new OnRecyclerItemClickListener(context, news));

        viewHolder.tvType.setText(new StringBuilder("来自 ").append(news.getType()));

        viewHolder.tvTitle.setText(news.getTitle());

        viewHolder.tvDigest.setText(news.getDigest());
    }

    private void bindNoImgView(NewsNoImgItemViewHolder viewHolder, NewsModel news) {

        viewHolder.rlItem.setOnClickListener(new OnRecyclerItemClickListener(context, news));
        viewHolder.tvType.setText(new StringBuilder("来自 ").append(news.getType()));
        viewHolder.tvTitle.setText(news.getTitle());
        viewHolder.tvDigest.setText(news.getDigest());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            return FOOTER_TYPE;
        } else {
            NewsModel news = mList.get(position);
            if (!TextUtils.isEmpty(news.getImgUrl())) {
                return HAS_IMG_TYPE;
            } else {
                return NO_IMG_TYPE;
            }
        }
    }


    /**
     * 加载更多完成
     */
    public void loadMoreFinish() {
        if (getItemCount() > 0 && mFooter != null) {
            mFooter.showLoadMoreFinish();
        }
    }

    /**
     * 显示加载更多
     */
    public void showLoadMore() {
        if (mFooter != null) {
            mFooter.showLoadMore();
        }
    }

    /**
     * 显示网络错误
     */
    public void showLoadError() {
        if (mFooter != null) {
            mFooter.showLoadError();
        }
    }

    /**
     * Item 的点击事件
     */
    private class OnRecyclerItemClickListener implements View.OnClickListener {

        private Context mContext;
        private NewsModel news;

        public OnRecyclerItemClickListener(Context context, NewsModel news) {
            mContext = context;
            this.news = news;
        }

        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(news.getImgUrl())) {//没有图片的跳转
                Intent intent = new Intent(mContext, NewsNoImgDetailActivity.class);
                intent.putExtra("nid", news.getNid());
                mContext.startActivity(intent);
            } else {//有图片的跳转
                Intent intent = new Intent(mContext, NewsHasImgDetailActivity.class);

                intent.putExtra("nid", news.getNid());
                intent.putExtra("imgUrl", news.getImgUrl());

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, v.findViewById(R.id.iv_item_news_img), mContext.getString(R.string.transition_news_img));

                ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
            }
        }
    }


    public void setOnNewsRefreshFinishListener(OnNewsRefreshFinishListener listener) {
        mListener = listener;
    }

    /**
     * 这个接口用于刷新操作完成后,使回调者能够停止刷新控件
     */
    public interface OnNewsRefreshFinishListener {
        void onFinish();
    }
}
