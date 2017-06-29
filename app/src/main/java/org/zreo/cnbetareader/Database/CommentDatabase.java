package org.zreo.cnbetareader.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.zreo.cnbetareader.Entitys.CommentItemEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hcy on 2015/8/9.
 */
public class CommentDatabase {
    public static final String DB_NAME = "NewsData.db";  //数据库名
    public static final String TABLE_NAME = "CommentItemEntity";  //表名
    public static final int VERSION = 1;
    private static CommentDatabase commentDatabase;
    private SQLiteDatabase db;

    //将构造方法私有化
    private CommentDatabase(Context context) {
        NewsTitleOpenHelper commentOpenHelper = new NewsTitleOpenHelper(
                context, DB_NAME, null, VERSION);
        db = commentOpenHelper.getWritableDatabase();
    }

    //获取CommentDatabase的实例
    public synchronized static CommentDatabase getInstance(Context context) {
        if (commentDatabase == null) {
            commentDatabase = new CommentDatabase(context);
        }
        return commentDatabase;
    }


    //将CommentItemEntity实例存储到数据库
    public void saveCommentItemEntity(CommentItemEntity commentItemEntity) {
        if (commentItemEntity != null) {
            ContentValues values = new ContentValues();
            values.put("fName",commentItemEntity.getFName());
            values.put("sid", commentItemEntity.getSid());
            values.put("pid", commentItemEntity.getSid());
            values.put("tid", commentItemEntity.getTid());
            values.put("icon", commentItemEntity.getIcon());
            values.put("date", commentItemEntity.getDate());
            values.put("host_name", commentItemEntity.getHost_name());
            values.put("name", commentItemEntity.getName());
            values.put("score", commentItemEntity.getScore());
            values.put("reason", commentItemEntity.getReason());
            values.put("comment", commentItemEntity.getComment());
            values.put("refContent", commentItemEntity.getRefContent());
            db.insert(TABLE_NAME, null, values);
        }
    }



    //从数据库读取评论，返回List
    public List<CommentItemEntity> loadCommentItemEntity() {
        List<CommentItemEntity> list = new ArrayList<CommentItemEntity>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "tid desc");
        if (cursor.moveToFirst()) {
            do {
                CommentItemEntity commentItemEntity = new CommentItemEntity();
                commentItemEntity.setFName(cursor.getString(cursor.getColumnIndex("fName")));
                commentItemEntity.setTid(cursor.getString(cursor.getColumnIndex("tid")));
                commentItemEntity.setSid(cursor.getInt(cursor.getColumnIndex("sid")));
                commentItemEntity.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
                commentItemEntity.setHost_name(cursor.getString(cursor.getColumnIndex("host_name")));
                commentItemEntity.setName(cursor.getString(cursor.getColumnIndex("name")));
                commentItemEntity.setScore(cursor.getInt(cursor.getColumnIndex("score")));
                commentItemEntity.setReason(cursor.getInt(cursor.getColumnIndex("reason")));
                commentItemEntity.setComment(cursor.getString(cursor.getColumnIndex("comment")));
                commentItemEntity.setRefContent(cursor.getString(cursor.getColumnIndex("refContent")));
                list.add(commentItemEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    //从数据库读取评论，返回Map
    public Map<String,CommentItemEntity> loadMapCommentItemEntity() {
        
        Map<String,CommentItemEntity> map = new TreeMap<String,CommentItemEntity>(new Comparator<String>() {  //将获取到的评论列表排序
            @Override
            public int compare(String lhs, String rhs) {
                return rhs.compareTo(lhs);   // 降序排序, 默认为升序
            }
        });
        String tid;  //新闻id
        String icon;
        String host_name;
        String fName;
        String name;
        int score;
        int reason;
        String comment;
        String refContent;
        //Cursor cursor = db.query("NewsEntity", null, null, null, null, null, null);
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "tid desc");   //查询结果用id排序，降序desc，升序asc
        if (cursor.moveToFirst()) {
            do {
                CommentItemEntity commentItemEntity = new CommentItemEntity();
                tid = cursor.getString(cursor.getColumnIndex("tid"));
                commentItemEntity.setTid(tid);
                fName = cursor.getString(cursor.getColumnIndex("fName"));
                commentItemEntity.setFName(fName);
                icon = cursor.getString(cursor.getColumnIndex("icon"));
                commentItemEntity.setIcon(icon);
                host_name = cursor.getString(cursor.getColumnIndex("host_name"));
                commentItemEntity.setHost_name(host_name);
                name = cursor.getString(cursor.getColumnIndex("name"));
                commentItemEntity.setName(name);
                score = cursor.getInt(cursor.getColumnIndex("score"));
                commentItemEntity.setScore(score);
                reason = cursor.getInt(cursor.getColumnIndex("reason"));
                commentItemEntity.setReason(reason);
                comment = cursor.getString(cursor.getColumnIndex("comment"));
                commentItemEntity.setComment(comment);
                refContent = cursor.getString(cursor.getColumnIndex("refContent"));
                commentItemEntity.setRefContent(refContent);
                map.put(tid, commentItemEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return map;
    }
}
