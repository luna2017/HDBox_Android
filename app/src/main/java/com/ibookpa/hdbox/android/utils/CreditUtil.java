package com.ibookpa.hdbox.android.utils;

import com.ibookpa.hdbox.android.model.CreditModel;
import com.ibookpa.hdbox.android.model.ScoreModel;

import java.util.List;

/**
 * Created by tc on 7/16/16. 学分工具类
 */
public class CreditUtil {

    public static CreditModel computeGPA(List<ScoreModel> list) {

        float sumScoreCredit = 0.0F;//学分与成绩相乘的总和
        float sumPassCredit = 0.0F;//及格的学分总和
        float sumFailCredit = 0.0F;//挂科学分总和

        for (int i = 0; i < list.size(); i++) {
            ScoreModel score = list.get(i);
            if (score.getScore() >= 60) {//只要及格的成绩
                sumScoreCredit += score.getCredit() * score.getScore();
                sumPassCredit += score.getCredit();
            } else {
                sumFailCredit += score.getCredit();
            }
        }
        float gpa = sumScoreCredit / (sumPassCredit * 10);

        return new CreditModel(sumPassCredit, sumFailCredit, gpa);
    }
}
