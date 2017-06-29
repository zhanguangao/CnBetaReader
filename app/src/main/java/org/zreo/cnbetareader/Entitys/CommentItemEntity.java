package org.zreo.cnbetareader.Entitys;

/**
 * Created by zqh on 2015/8/8  15:59.
 * Email:zqhkey@163.com
 */
public class CommentItemEntity {
    private int score;
    private String tid;
    private String pid;
    private int sid;
    private int reason;
    private String icon;
    private String date;
    private String name;
    private String comment;
    private String host_name;
    private String refContent;
    private String FName;
    private String support;
    private String against;
    private int layout;
    private int commentMenu;
    private int imageView1;

    public void setImageView1(int imageView1) {
        this.imageView1 = imageView1;
    }

    public int getImageView1() {
        return imageView1;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getFName() {
        return FName;
    }

    public void setCommentMenu(int commentMenu) {
        this.commentMenu = commentMenu;
    }

    public int getCommentMenu() {
        return commentMenu;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public void setAgainst(String against) {
        this.against = against;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public String getSupport() {
        return support;
    }

    public String getAgainst() {
        return against;
    }

    public int getLayout() {
        return layout;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getRefContent() {
        return refContent;
    }

    public void setRefContent(String refContent) {
        this.refContent = refContent;
    }

    public void copy(CommentItemEntity item) {
        this.score = item.getScore();
        this.reason = item.getReason();
        this.icon = item.getIcon();
        this.date = item.getDate();
        this.name = item.getName();
        this.comment = item.getComment();
        this.host_name = item.getHost_name();
    }

}
