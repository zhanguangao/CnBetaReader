package org.zreo.cnbetareader.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by guang on 2015/8/1.
 */
public class NewsTitleOpenHelper extends SQLiteOpenHelper {

    //新闻标题数据库表建表语句
    public static final String CREATE_NEWS_TITLE = "create table NewsEntity ("
            + "id integer primary key autoincrement, "
            + "sid integer, "   /*帖子id*/
            + "catid integer, "
            + "topic integer, "
            + "aid text, "
            + "user_id text, "  /*用户ID*/
            + "SN text, "
            + "largeImage text, "
            + "froms text, "
            + "content text, "
            + "summary text, "
            + "title text, "    /*标题*/
            + "hometext text, "  /*简介*/
            + "comments text, "  /*评论数*/
            + "counter text, "   /*查看数*/
            + "inputtime text, "  /*发布时间*/
            + "thumb text )";   /*图片地址*/

    //收藏新闻据库表建表语句
    public static final String CREATE_COLLECTION = "create table Collection ("
            + "id integer primary key autoincrement, "
            + "sid integer, "   /*帖子id*/
            + "catid integer, "
            + "topic integer, "
            + "aid text, "
            + "user_id text, "  /*用户ID*/

            + "SN text, "
            + "largeImage text, "
            + "froms text, "
            + "content text, "
            + "summary text, "

            + "title text, "    /*标题*/
            + "hometext text, "  /*简介*/
            + "comments text, "  /*评论数*/
            + "counter text, "   /*查看数*/
            + "inputtime text, "  /*发布时间*/
            + "thumb text )";   /*图片地址*/

    //创建新闻评论表
    public static final String CREATE_COMMENT_TABLE = "create table CommentItemEntity ("
            + "id integer primary key autoincrement, "
            + "sid integer, "   /*帖子id*/
            + "pid integer, "
            + "fName text, "
            + "tid integer, "
            + "icon text, "
            + "date text,"
            + "host_name text, "
            + "name text, "    /*用户名*/
            + "score integer, "  /*支持数*/
            + "reason integer, "  /*反对数*/
            + "comment text, "   /*评论*/
            + "refContent text )";

    //创建热门评论表
    public static final String CREATE_HOTCOMMENT = "create table HotCommentEntity ("
            + "id integer primary key autoincrement, "
            + "sid integer, "   /*帖子id*/
            + "title text, "    /*评论内容*/
            + "description text, "  /*用户ID*/
            + "froms text, "  /*用户地址*/
            + "newstitle text )";  /*新闻标题*/

    //Top10数据库表建表语句
    public static final String CREATE_TOPCOMMENT = "create table TopComment ("
            + "id integer primary key autoincrement, "
            + "sid integer, "   /*帖子id*/
            + "title text, "    /*标题*/
            + "counter text )";   /*查看数*/

    public NewsTitleOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS_TITLE);
        db.execSQL(CREATE_COLLECTION);
        db.execSQL(CREATE_COMMENT_TABLE);
        db.execSQL(CREATE_HOTCOMMENT);
        db.execSQL(CREATE_TOPCOMMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
