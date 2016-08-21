package com.ibookpa.hdbox.android.model;

/**
 * Created by tc on 6/28/16. 登录模型
 */
public class LoginModel {

    private String uid;
    private String pwd;
    private String token;

    public LoginModel(String uid, String pwd, String token) {
        this.uid = uid;
        this.pwd = pwd;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
