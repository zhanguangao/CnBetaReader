package org.zreo.cnbetareader.Entitys;

import java.io.Serializable;

/**
 * Created by zqh on 2015/7/30  10:25.
 * Email:zqhkey@163.com
 */
public class NewsEntity implements Serializable {


    private int sid;/*帖子id*/
    private int catid;/**/
    private int topic;/**/
    private String aid;/**/
    private String user_id;/*用户ID*/
    private String title;/*标题*/
    private String hometext;/*简介*/
    private int comments;/*评论数*/
    private int counter;/*查看数*/
    private String inputtime;/*发布时间*/
    private String thumb;/*图片地址*/
    private String SN;
    private String largeImage;
    private String from;
    private String content;
    private String summary;
    @Override
    public String toString() {
        return "NewsEntity{" +
                "sid=" + sid +
                ", catid=" + catid +
                ", topic=" + topic +
                ", aid='" + aid + '\'' +
                ", user_id='" + user_id + '\'' +
                ", title='" + title + '\'' +
                ", hometext='" + hometext + '\'' +
                ", comments=" + comments +
                ", counter=" + counter +
                ", inputtime='" + inputtime + '\'' +
                ", thumb='" + thumb + '\'' +
                ", SN='" + SN + '\'' +
                ", largeImage='" + largeImage + '\'' +
                ", from='" + from + '\'' +
                ", content='" + content + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHometext() {
        return hometext;
    }

    public void setHometext(String hometext) {
        this.hometext = hometext;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
