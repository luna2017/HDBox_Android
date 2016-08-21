package com.ibookpa.hdbox.android.model;

/**
 * Created by lin_sir on 2016/6/20. 学分信息模型
 */
public class CreditModel implements Score {

    private String uid;         //学号
    private String schoolYear;  //学年
    private float passCredit;   //已修学分
    private float failedCredit; //挂科学分
    private float gpa;          //学分绩点


    public CreditModel(float passCredit, float failedCredit, float gpa) {
        this.passCredit = passCredit;
        this.failedCredit = failedCredit;
        this.gpa = gpa;
    }

    public CreditModel(String uid, float passCredit, float failedCredit, float gpa) {
        this.uid = uid;
        this.passCredit = passCredit;
        this.failedCredit = failedCredit;
        this.gpa = gpa;
    }

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

    public float getPassCredit() {
        return passCredit;
    }

    public void setPassCredit(float passCredit) {
        this.passCredit = passCredit;
    }

    public float getFailedCredit() {
        return failedCredit;
    }

    public void setFailedCredit(float failedCredit) {
        this.failedCredit = failedCredit;
    }

    public float getGpa() {
        return gpa;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }
}
