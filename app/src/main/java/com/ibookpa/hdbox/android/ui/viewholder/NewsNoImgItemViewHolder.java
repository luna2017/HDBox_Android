package com.ibookpa.hdbox.android.ui.viewholder;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;

/**
 * Created by tc on 7/12/16.新闻不带图片的 item 布局
 */
public class NewsNoImgItemViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlItem;
    public TextView tvType;
    public TextView tvTitle;
    public TextView tvDigest;


    public NewsNoImgItemViewHolder(View itemView) {
        super(itemView);
        rlItem = (RelativeLayout) itemView.findViewById(R.id.rl_item_news_no_img);
        // 向下兼容的设置阴影
        ViewCompat.setElevation(rlItem, 4);
        tvType = (TextView) itemView.findViewById(R.id.tv_item_news_type);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_item_news_title);
        tvDigest = (TextView) itemView.findViewById(R.id.tv_item_news_digest);
    }
}
