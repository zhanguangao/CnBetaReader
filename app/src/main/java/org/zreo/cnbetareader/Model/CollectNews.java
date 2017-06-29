package org.zreo.cnbetareader.Model;

/**
 * Created by Admin on 2015/8/1.
 */
public class CollectNews {

   private String newsfirstWord;
    private String newscontent;
    private String newstypes;
    public CollectNews(){}
    public CollectNews(String newsfirstWord,String newscontent,String newstypes){
        this.newsfirstWord=newsfirstWord;
        this.newscontent=newscontent;
        this.newstypes=newstypes;
    }

    public String getNewsfirstWord() {
        return newsfirstWord;
    }

    public void setNewsfirstWord(String newsfirstWord) {
        this.newsfirstWord = newsfirstWord;
    }

    public String getNewscontent() {
        return newscontent;
    }

    public void setNewscontent(String newscontent) {
        this.newscontent = newscontent;
    }

    public String getNewstypes() {
        return newstypes;
    }

    public void setNewstypes(String newstypes) {
        this.newstypes = newstypes;
    }
}
