package com.ibookpa.hdbox.android.model;

/**
 * Created by lin_sir on 2016/6/22. 新闻模型
 */
public class NewsModel {

    private int nid;            //新闻 id
    private String title;       //新闻标题
    private String imgUrl;      //新闻图片
    private String digest;      //新闻摘要
    private String type;        //新闻类型
    private String pubTime;     //发布时间
    private String publisher;   //发布者
    private String content;     //新闻内容


    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
