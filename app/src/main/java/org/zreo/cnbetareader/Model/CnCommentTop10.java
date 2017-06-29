package org.zreo.cnbetareader.Model;

/**
 * Created by Administrator on 2015/7/28.
 */
public class CnCommentTop10 {
    private String newsTitle;
    private String hot;
    private String ranking;
    public CnCommentTop10(){

    }
    public CnCommentTop10(String newsTitle, String hot, String ranking){
        this.hot = hot;
        this.newsTitle =newsTitle;
        this.ranking = ranking;}
    public void setHot(String hot){this.hot = hot;}
    public void setNewsTitle(String newsTitle) {this.newsTitle = newsTitle;}
    public void setRanking(String ranking){this.ranking = ranking;}
    public String getNewsTitle(){return  newsTitle;}
    public String getHot(){return hot;}
    public String getRanking() { return ranking;}
}
