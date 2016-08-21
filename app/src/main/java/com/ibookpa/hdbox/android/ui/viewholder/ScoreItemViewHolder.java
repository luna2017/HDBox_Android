package com.ibookpa.hdbox.android.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;

/**
 * Created by tc on 7/16/16.成绩单项布局
 */
public  class ScoreItemViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlScoreItem;
    public TextView tvCourseName;
    public TextView tvCourseType;
    public TextView tvCredit;
    public TextView tvScore;

    public ScoreItemViewHolder(View itemView) {
        super(itemView);
        rlScoreItem = (RelativeLayout) itemView.findViewById(R.id.rl_score_item);
        tvCourseName = (TextView) itemView.findViewById(R.id.tv_score_item_name);
        tvCourseType = (TextView) itemView.findViewById(R.id.tv_score_item_type);
        tvCredit = (TextView) itemView.findViewById(R.id.tv_score_item_credit);
        tvScore = (TextView) itemView.findViewById(R.id.tv_score_item_score);
    }

}
