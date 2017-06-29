package org.zreo.cnbetareader.Entitys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqh on 2015/7/30  10:23.
 * Email:zqhkey@163.com
 */
public class NewsListEntity {
    private List<NewsEntity> list=new ArrayList<NewsEntity>();
    private String type;
    private  String page;

    @Override
    public String toString() {
        return "NewsListEntity{" +
                "list=" + list +
                ", type='" + type + '\'' +
                ", page='" + page + '\'' +
                '}';
    }

    public List<NewsEntity> getList() {
        return list;
    }

    public void setList(List<NewsEntity> list) {
        this.list = list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
