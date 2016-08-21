package com.ibookpa.hdbox.android.model;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by lin_sir on 2016/6/18.成绩模型
 */
public class ScoreModel extends RealmObject implements Score {

    private String uid;             //学生 id
    private String schoolYear;      //学年
    private String term;            //学期
    private String cid;             //课程 id
    private String cname;           //课程名
    private String type;            //课程类别:专业必修，专业任选，专业指导，通识选修，通识必修
    private String property;        //课程性质:必修课，选修课
    private float credit;           //学分
    private float score;            //成绩
    private String studyMethod;     //修读方式:初修，重修


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getStudyMethod() {
        return studyMethod;
    }

    public void setStudyMethod(String studyMethod) {
        this.studyMethod = studyMethod;
    }
}
