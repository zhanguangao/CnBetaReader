package org.zreo.cnbetareader.Entitys;


public class CnComment {

    private String FName;
    private int imageView1;
    private String userName;
    private String testComment;
    private String support;
    private String against;
    private int supportNumber;
    private int againstNumber;
    private long tid;
    private int layout;

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public int getSupportNumber() {
        return supportNumber;
    }

    public int getAgainstNumber() {
        return againstNumber;
    }

    public void setSupportNumber(int supportNumber) {
        this.supportNumber = supportNumber;
    }

    public void setAgainstNumber(int againstNumber) {
        this.againstNumber = againstNumber;
    }

    private int commentMenu;

    public CnComment() {
    }

    public CnComment(String FName, String userName, int imageId, String testComment, String support,
                     String against,int commentMenu, long tid, int supportNumber,int againstNumber ) {
        this.userName = userName;
        this.imageView1 = imageId;
        this.testComment = testComment;
        this.support= support;
        this.against = against;
        this.commentMenu = commentMenu;
        this.FName = FName;
        this.tid = tid;
        this.supportNumber = supportNumber;
        this.againstNumber = againstNumber;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getFName() {
        return FName;

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setImageId(int imageId) {
        this.imageView1 = imageId;
    }

    public void setTestComment(String testComment) {
        this.testComment = testComment;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public void setAgainst(String against) {
        this.against = against;
    }

    public void setCommentMenu(int commentMenu) {
        this.commentMenu = commentMenu;
    }

    public String getUserName() {
        return userName;
    }

    public int getImageId() {
        return imageView1;
    }

    public String getTestComment() {
        return testComment;
    }

    public String getSupport() {
        return support;
    }


    public String getAgainst() {
        return against;
    }

    public int getCommentMenu() {
        return commentMenu;
    }
}

