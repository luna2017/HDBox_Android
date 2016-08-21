package com.ibookpa.hdbox.android.model;

/**
 * Created by tc on 7/2/16. 检查更新结果模型
 */
public class CheckUpdateModel {

    private int hasNew;         //是否有新版,1 有新版本,0 没有新版本
    private int versionCode;    //新版本号
    private String versionName; //新版本名
    private String description; //新版描述
    private String url;         //新版下载链接

    public int getHasNew() {
        return hasNew;
    }

    public void setHasNew(int hasNew) {
        this.hasNew = hasNew;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
