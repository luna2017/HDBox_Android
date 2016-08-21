package com.ibookpa.hdbox.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.Score;
import com.ibookpa.hdbox.android.model.ScoreModel;
import com.ibookpa.hdbox.android.ui.viewholder.ScoreItemViewHolder;
import com.ibookpa.hdbox.android.ui.viewholder.YearGPAItemViewHolder;
import com.ibookpa.hdbox.android.utils.CreditUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by tc on 6/25/16. 成绩表适配器
 */
public class ScoreRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SCHOOL_YEAR = 0;
    private static final int TYPE_SCORE_CONTENT = 1;

    private List<Score> mScoreList;

    private OnScoreItemClickListener mListener;

    public ScoreRecyclerAdapter() {
        mScoreList = new ArrayList<>();
    }

    public void refreshData(List<ScoreModel> list) {
        mScoreList.clear();
        mScoreList.addAll(distributeData(list));
        notifyDataSetChanged();
    }

    public void clear() {
        mScoreList.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SCHOOL_YEAR) {
            return new YearGPAItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score_year, parent, false));
        } else {
            return new ScoreItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_SCHOOL_YEAR) {
            bindYearGPAView((YearGPAItemViewHolder) holder, (CreditModel) mScoreList.get(position));
        } else {
            bindScoreView((ScoreItemViewHolder) holder, (ScoreModel) mScoreList.get(position));
        }
    }

    private void bindYearGPAView(YearGPAItemViewHolder holder, CreditModel model) {
        if (model == null) {
            return;
        }
        float gpa = model.getGpa();
        if (gpa >= 8.0) {
            holder.rlItem.setBackgroundColor(0xffffd700);
        } else if (gpa > 7.5) {
            holder.rlItem.setBackgroundColor(0xff81C784);
        } else if (gpa > 7.3) {
            holder.rlItem.setBackgroundColor(0xffAED581);
        } else if (gpa > 7.0) {
            holder.rlItem.setBackgroundColor(0xffFF8A65);
        } else {
            holder.rlItem.setBackgroundColor(0xffAED581);
        }

        holder.tvYear.setText(model.getSchoolYear());

        DecimalFormat gpaFormat = new DecimalFormat("0.0000");
        DecimalFormat creditFormat = new DecimalFormat("0.0");

        holder.tvPassCredit.setText(new StringBuilder("修读学分 ").append(creditFormat.format(model.getPassCredit())));
        holder.tvFailedCredit.setText(new StringBuilder("挂科学分 ").append(creditFormat.format(model.getFailedCredit())));
        holder.tvGpa.setText(new StringBuilder("绩点\n").append(gpaFormat.format(gpa)));
    }

    private void bindScoreView(ScoreItemViewHolder holder, ScoreModel data) {
        holder.rlScoreItem.setOnClickListener(new ItemClickListener(data));

        holder.tvCourseName.setText(data.getCname());
        holder.tvCourseType.setText(data.getType());
        holder.tvCredit.setText(new StringBuilder(String.valueOf(data.getCredit())).append("分"));

        if (data.getScore() < 60) {
            holder.tvScore.setTextColor(0xfff44336);
        } else {
            holder.tvScore.setTextColor(0xff4caf50);
        }
        holder.tvScore.setText(String.valueOf(data.getScore()));
    }

    @Override
    public int getItemCount() {
        return mScoreList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mScoreList.get(position) instanceof CreditModel) {
            return TYPE_SCHOOL_YEAR;
        } else {
            return TYPE_SCORE_CONTENT;
        }
    }

    /**
     * 分发数据,将数据按照学年分组
     */
    public List<Score> distributeData(List<ScoreModel> list) {
        List<Score> results = new ArrayList<>();

        Map<String, List<ScoreModel>> map = sortMapByKey(group(list));

        Set<String> set = map.keySet();

        for (String s : set) {
            CreditModel credit = CreditUtil.computeGPA(map.get(s));
            credit.setSchoolYear(s + "学年");

            results.add(credit);//这个位置用做判断当前是学分信息还是成绩信息
            results.addAll(map.get(s));
        }

        return results;
    }

    /**
     * 计算 GPA
     * 算法: GPA = 所有及格科目的(学分*成绩) 的总和 / (所有及格科目的学分总和*10)
     */
//    private GPAModel computeGPA(List<ScoreModel> list) {
//
//        float sumScoreCredit = 0.0F;//学分与成绩相乘的总和
//        float sumPassCredit = 0.0F;//及格的学分总和
//        float sumFailCredit = 0.0F;//挂科学分总和
//
//        for (int i = 0; i < list.size(); i++) {
//            ScoreModel score = list.get(i);
//            if (score.getScore() >= 60) {//只要及格的成绩
//                sumScoreCredit += score.getCredit() * score.getScore();
//                sumPassCredit += score.getCredit();
//            } else {
//                sumFailCredit += score.getCredit();
//            }
//        }
//        float gpa = sumScoreCredit / (sumPassCredit * 10);
//
//        return new GPAModel(sumPassCredit, sumFailCredit, gpa);
//    }


    /**
     * 将数据按照学年分组存放在 Map 中
     * 其中 Map 的 key 是学年,例如"2016-2015学年",value 是存放 schoolYear 字段为 key 值的 List
     */
    public Map<String, List<ScoreModel>> group(List<ScoreModel> list) {
        Map<String, List<ScoreModel>> map = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            String schoolYear = list.get(i).getSchoolYear();
            if (map.containsKey(schoolYear)) {
                map.get(schoolYear).add(list.get(i));
            } else {
                List<ScoreModel> mList = new ArrayList<>();
                mList.add(list.get(i));
                map.put(schoolYear, mList);
            }
        }
        return map;
    }

    /**
     * 将 Map 按照 key(学年)降序排列
     */
    public Map<String, List<ScoreModel>> sortMapByKey(Map<String, List<ScoreModel>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, List<ScoreModel>> sortMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return rhs.compareTo(lhs);
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }

    private class ItemClickListener implements View.OnClickListener {

        private ScoreModel score;

        public ItemClickListener(ScoreModel score) {
            this.score = score;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(score);
            }
        }
    }

    public interface OnScoreItemClickListener {
        void onItemClick(ScoreModel score);
    }

    public void addOnScoreItemClickListener(OnScoreItemClickListener listener) {
        mListener = listener;
    }

}
