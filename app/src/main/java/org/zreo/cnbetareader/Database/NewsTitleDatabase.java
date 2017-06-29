package org.zreo.cnbetareader.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.zreo.cnbetareader.Entitys.NewsEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by guang on 2015/8/1.
 */
public class NewsTitleDatabase {

    public static final String DB_NAME = "NewsData.db";  //数据库名
    public static final String TABLE = "NewsEntity";   //数据表
    public static final int VERSION = 1;   //数据库版本
    private static NewsTitleDatabase newsTitleDatabase;
    private SQLiteDatabase db;

    //将构造方法私有化
    private NewsTitleDatabase(Context context) {
        NewsTitleOpenHelper dbHelper = new NewsTitleOpenHelper(
                context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    //获取NewsTitleDatabase的实例
    public synchronized static NewsTitleDatabase getInstance(Context context) {
        if (newsTitleDatabase == null) {
            newsTitleDatabase = new NewsTitleDatabase(context);
        }
       return newsTitleDatabase;
    }

    //将NewsEntity实例存储到数据库
    public void saveNewsEntity(NewsEntity newsEntity) {
        if (newsEntity != null) {
            ContentValues values = new ContentValues();
            values.put("sid", newsEntity.getSid());
            values.put("catid", newsEntity.getCatid());
            values.put("topic", newsEntity.getTopic());
            values.put("aid", newsEntity.getAid());
            values.put("user_id", newsEntity.getUser_id());
            values.put("title", newsEntity.getTitle());
            values.put("hometext", newsEntity.getHometext());
            values.put("comments", newsEntity.getComments());
            values.put("counter", newsEntity.getCounter());
            values.put("inputtime", newsEntity.getInputtime());
            values.put("thumb", newsEntity.getThumb());

            values.put("SN", newsEntity.getSN());
            values.put("largeImage", newsEntity.getLargeImage());
            values.put("froms", newsEntity.getFrom());
            values.put("content", newsEntity.getContent());
            values.put("summary", newsEntity.getSummary());

            db.insert(TABLE, null, values);
        }
    }



    //从数据库读取新闻标题信息，返回List
    public List<NewsEntity> loadNewsEntity() {
        List<NewsEntity> list = new ArrayList<NewsEntity>();
        //Cursor cursor = db.query("NewsEntity", null, null, null, null, null, null);
        Cursor cursor = db.query(TABLE, null, null, null, null, null, "sid desc");   //查询结果用id排序，降序desc，升序asc
        if (cursor.moveToFirst()) {
            do {
                NewsEntity newsEntity = new NewsEntity();
                newsEntity.setSid(cursor.getInt(cursor.getColumnIndex("sid")));
                newsEntity.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                newsEntity.setHometext(cursor.getString(cursor.getColumnIndex("hometext")));
                newsEntity.setInputtime(cursor.getString(cursor.getColumnIndex("inputtime")));
                newsEntity.setThumb(cursor.getString(cursor.getColumnIndex("thumb")));
                newsEntity.setComments(cursor.getInt(cursor.getColumnIndex("comments")));
                newsEntity.setCounter(cursor.getInt(cursor.getColumnIndex("counter")));
                newsEntity.setSN(cursor.getString(cursor.getColumnIndex("SN")));
                newsEntity.setLargeImage(cursor.getString(cursor.getColumnIndex("largeImage")));
                newsEntity.setFrom(cursor.getString(cursor.getColumnIndex("froms")));
                newsEntity.setContent(cursor.getString(cursor.getColumnIndex("content")));
                newsEntity.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
                list.add(newsEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    //从数据库读取新闻标题信息，返回Map
    public Map<Integer,NewsEntity> loadMapNewsEntity() {

        Map<Integer,NewsEntity> map = new TreeMap<Integer,NewsEntity>(new Comparator<Integer>() {  //将获取到的新闻列表排序
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs.compareTo(lhs);   // 降序排序, 默认为升序
            }
        });
        int sid;  //新闻id
        //Cursor cursor = db.query("NewsEntity", null, null, null, null, null, null);
        Cursor cursor = db.query(TABLE, null, null, null, null, null, "sid desc");   //查询结果用id排序，降序desc，升序asc
        if (cursor.moveToFirst()) {
            do {
                NewsEntity newsEntity = new NewsEntity();
                sid = cursor.getInt(cursor.getColumnIndex("sid"));
                newsEntity.setSid(sid);
                newsEntity.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                newsEntity.setHometext(cursor.getString(cursor.getColumnIndex("hometext")));
                newsEntity.setInputtime(cursor.getString(cursor.getColumnIndex("inputtime")));
                newsEntity.setThumb(cursor.getString(cursor.getColumnIndex("thumb")));
                newsEntity.setComments(cursor.getInt(cursor.getColumnIndex("comments")));
                newsEntity.setCounter(cursor.getInt(cursor.getColumnIndex("counter")));
                newsEntity.setSN(cursor.getString(cursor.getColumnIndex("SN")));
                newsEntity.setLargeImage(cursor.getString(cursor.getColumnIndex("largeImage")));
                newsEntity.setFrom(cursor.getString(cursor.getColumnIndex("froms")));
                newsEntity.setContent(cursor.getString(cursor.getColumnIndex("content")));
                newsEntity.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
                map.put(sid, newsEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return map;
    }

    /**删除表*/
    public void deleteDateTable(){
        db.execSQL("DROP TABLE NewsEntity");
    }

    /**删除表中的所有数据*/
    public void deleteDate(){
        db.delete(TABLE, null, null);
    }
}
