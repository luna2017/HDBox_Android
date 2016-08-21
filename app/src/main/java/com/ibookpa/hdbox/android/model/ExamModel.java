package com.ibookpa.hdbox.android.model;

import io.realm.RealmObject;

/**
 * Created by lin_sir on 2016/6/18.考试信息模型
 */
public class ExamModel extends RealmObject {

    private String uid;          //学生 id
    private String cid;          //课程 id
    private String cname;        //课程名
    private String property;     //课程性质
    private String teacher;      //教师
    private float credit;        //课程学分
    private String date;         //考试日期
    private String time;         //考试时间
    private String classroom;    //考试地点
    private String type;         //考试形式:闭卷,开卷
    private String method;       //考试方式:笔试,机试
    private String status;       //考试状态:未开始,安排中,进行中,已结束

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
