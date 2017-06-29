package org.zreo.cnbetareader.Model;

import android.widget.ImageView;

import java.util.List;

/**
 * Created by Admin on 2015/7/30.
 */
public class CnInformation_Theme {
    private String firstWord;
    private String content;
    private String themetype;
    private List<CnInformation_Theme> CnInformation_Themelist;
    public CnInformation_Theme(){}
    public CnInformation_Theme(String firstWord, String content, String themetype)
    {
        this.firstWord=firstWord;
        this.content=content;
        this.themetype=themetype;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFirstWord(String firstWord) {
        this.firstWord = firstWord;
    }

    public String getContent() {
        return content;
    }

    public String getFirstWord() {
        return firstWord;
    }

    public String getThemetype() {
        return themetype;
    }

    public void setThemetype(String themetype) {
        this.themetype = themetype;
    }

}
