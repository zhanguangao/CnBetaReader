package org.zreo.cnbetareader.Entitys;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zqh on 2015/8/8  15:58.
 * Email:zqhkey@163.com
 */
public class  CommentListEntity {
    private ArrayList<CommentItemEntity> hotlist = new ArrayList<CommentItemEntity>();
    private ArrayList<CommentItemEntity> cmntlist = new ArrayList<CommentItemEntity>();
    private HashMap<String, CommentItemEntity> cmntstore = new HashMap<String, CommentItemEntity>();
    private int comment_num;
    private String token;
    private int page;
    private int open;

    public ArrayList<CommentItemEntity> getHotlist() {
        return hotlist;
    }

    public void setHotlist(ArrayList<CommentItemEntity> hotlist) {
        this.hotlist = hotlist;
    }

    public ArrayList<CommentItemEntity> getCmntlist() {
        return cmntlist;
    }

    public void setCmntlist(ArrayList<CommentItemEntity> cmntlist) {
        this.cmntlist = cmntlist;
    }

    public HashMap<String, CommentItemEntity> getCmntstore() {
        return cmntstore;
    }

    public void setCmntstore(HashMap<String, CommentItemEntity> cmntstore) {
        this.cmntstore = cmntstore;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }
}
