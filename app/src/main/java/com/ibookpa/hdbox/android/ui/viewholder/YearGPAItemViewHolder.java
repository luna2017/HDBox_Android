package com.ibookpa.hdbox.android.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;

/**
 * Created by tc on 7/16/16.学年 GPA 布局单项布局
 */
public class YearGPAItemViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlItem;
    public TextView tvYear;
    public TextView tvGpa;
    public TextView tvPassCredit;
    public TextView tvFailedCredit;

    public YearGPAItemViewHolder(View itemView) {
        super(itemView);
        rlItem = (RelativeLayout) itemView.findViewById(R.id.rl_item_score_year_item);
        tvYear = (TextView) itemView.findViewById(R.id.tv_item_score_year_year);
        tvGpa = (TextView) itemView.findViewById(R.id.tv_item_score_year_gpa);
        tvPassCredit = (TextView) itemView.findViewById(R.id.tv_item_score_year_pass_credit);
        tvFailedCredit = (TextView) itemView.findViewById(R.id.tv_item_score_year_failed_credit);
    }
}
