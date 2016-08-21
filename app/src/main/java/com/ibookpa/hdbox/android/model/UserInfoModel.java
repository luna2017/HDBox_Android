package com.ibookpa.hdbox.android.model;

/**
 * Created by lin_sir on 2016/6/13.用户模型
 */
public class UserInfoModel {

    private String uid;         //学号
    private String name;        //真实姓名
    private String grade;       //年级
    private String college;     //学院
    private String specialty;   //专业

    public UserInfoModel(){}

    public UserInfoModel(String uid, String name, String grade, String college, String specialty) {
        this.uid = uid;
        this.name = name;
        this.grade = grade;
        this.college = college;
        this.specialty = specialty;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
