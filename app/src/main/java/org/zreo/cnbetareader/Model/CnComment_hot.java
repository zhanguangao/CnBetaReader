package org.zreo.cnbetareader.Model;

/**
 * Created by Administrator on 2015/7/29.
 */
public class CnComment_hot {
    private String firstWord;
    private String content;
    private String comment;
    public CnComment_hot(){
    }
    public CnComment_hot(String firstWord,String comment,String content){
        this.comment = comment;
        this.content = content;
        this.firstWord = firstWord;
    }
    public void setFirstWord(String firstWord){this.firstWord = firstWord;}
    public void setComment(String comment) {this.comment = comment;}
    public void setContent(String content){this.content = content;}
    public String getComment() {return comment;}
    public String getContent() {return content;}
    public String getFirstWord() {return firstWord;}
}
