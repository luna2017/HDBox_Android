package com.ibookpa.hdbox.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.model.ExamModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tc on 6/25/16.考试安排表的适配器
 */
public class ExamRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ExamModel> mList;

    public ExamRecyclerAdapter() {
        mList = new ArrayList<>();
    }


    public void refresh(List<ExamModel> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExamItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView((ExamItemViewHolder) holder, mList.get(position));
    }

    private void bindView(ExamItemViewHolder holder, ExamModel data) {
        holder.tvCourseName.setText(data.getCname());
        String status = data.getStatus();
        if (status.equals("未开始")) {
            holder.tvStatus.setBackgroundColor(0xffff9800);
        } else if (status.equals("已结束")) {
            holder.tvStatus.setBackgroundColor(0xff4caf50);
        } else {
            status = "即将开始";
            holder.tvStatus.setBackgroundColor(0xfff44336);
        }

        holder.tvStatus.setText(status);
        holder.tvDate.setText(data.getDate());
        holder.tvTime.setText(data.getTime());
        holder.tvLoc.setText(data.getClassroom());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ExamItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCourseName;
        private TextView tvStatus;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvLoc;

        public ExamItemViewHolder(View itemView) {
            super(itemView);

            tvCourseName = (TextView) itemView.findViewById(R.id.tv_exam_item_course_name);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_exam_item_status);
            tvDate = (TextView) itemView.findViewById(R.id.tv_exam_item_date);
            tvTime = (TextView) itemView.findViewById(R.id.tv_exam_item_time);
            tvLoc = (TextView) itemView.findViewById(R.id.tv_exam_item_loc);
        }
    }


}
