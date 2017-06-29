package org.zreo.cnbetareader.Entitys;

/**
 * Created by zqh on 2015/8/11  15:02.
 * Email:zqhkey@163.com
 */
public class HotCommentItemEntity {
    private String title;//评论内容
    private String description;//评论人名称
    private String from;//评论人地址
    private int sid;//评论id
    private String newstitle;//评论的新闻

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getNewstitle() {
        return newstitle;
    }

    public void setNewstitle(String newstitle) {
        this.newstitle = newstitle;
    }


}
