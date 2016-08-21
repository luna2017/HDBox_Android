package com.ibookpa.hdbox.android.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;

/**
 * Created by tc on 7/12/16.列表最后一项的布局
 */

public class FooterItemViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar mProgress;
    public TextView tvNoMore;

    public FooterItemViewHolder(View itemView) {
        super(itemView);
        mProgress = (ProgressBar) itemView.findViewById(R.id.pb_footer_load_more);
        tvNoMore = (TextView) itemView.findViewById(R.id.tv_footer_no_more);
    }


    /**
     * 显示加载更多
     */
    public void showLoadMore() {
        mProgress.setVisibility(View.VISIBLE);
        tvNoMore.setVisibility(View.GONE);
    }

    /**
     * 显示已加载全部
     */
    public void showLoadMoreFinish() {
        tvNoMore.setVisibility(View.VISIBLE);
        tvNoMore.setText("已加载全部");
        mProgress.setVisibility(View.GONE);
    }

    /**
     * 显示网络错误
     */
    public void showLoadError() {
        tvNoMore.setVisibility(View.VISIBLE);
        tvNoMore.setText("网络错误,请重试...");
        mProgress.setVisibility(View.GONE);
    }
}
