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
 * Created by Administrator on 2015/8/14.
 */
public class Top10CommentDatabase {
        public static final String DB_NAME = "NewsData.db";  //数据库名
        public static final String TABLE = " TopComment";   //数据表
        public static final int VERSION = 1;   //数据库版本
        private static Top10CommentDatabase top10CommentDatabase;
        private SQLiteDatabase db;

        //将构造方法私有化
        private Top10CommentDatabase(Context context) {
            NewsTitleOpenHelper dbHelper = new NewsTitleOpenHelper(
                    context, DB_NAME, null, VERSION);
            db = dbHelper.getWritableDatabase();
        }

        //获取Top10CommentDatabase的实例
        public synchronized static Top10CommentDatabase getInstance(Context context) {
            if (top10CommentDatabase == null) {
                top10CommentDatabase = new Top10CommentDatabase(context);
            }
            return top10CommentDatabase;
        }

        //将NewsEntity实例存储到收藏数据库
        public void saveTop10comment(NewsEntity newsEntity) {
            if (newsEntity != null) {
                ContentValues values = new ContentValues();
                values.put("sid", newsEntity.getSid());
                values.put("title", newsEntity.getTitle());
                values.put("counter", newsEntity.getCounter());
                db.insert(TABLE, null, values);
            }
        }


        //从数据库读取收藏信息，返回List
        public List<NewsEntity> loadTop10comment() {
            List<NewsEntity> list = new ArrayList<NewsEntity>();
            Cursor cursor = db.query(TABLE, null, null, null, null, null, "id desc");   //查询结果用counter排序，降序desc，升序asc
            if (cursor.moveToFirst()) {
                do {
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setSid(cursor.getInt(cursor.getColumnIndex("sid")));
                    newsEntity.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    newsEntity.setCounter(cursor.getInt(cursor.getColumnIndex("counter")));
                    list.add(newsEntity);
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }
            return list;
        }

        //从数据库读取收藏信息，返回Map
        public Map<Integer, NewsEntity> loadMapTop10Comment() {

            Map<Integer, NewsEntity> map = new TreeMap<Integer, NewsEntity>(new Comparator<Integer>() {  //将获取到的新闻列表排序
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    return rhs.compareTo(lhs);   // 降序排序, 默认为升序
                }
            });
            int id;  //新闻查看数
            Cursor cursor = db.query(TABLE, null, null, null, null, null, "id desc");   //查询结果用counter排序，降序desc，升序asc
            if (cursor.moveToFirst()) {
                do {
                    NewsEntity newsEntity = new NewsEntity();
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                    newsEntity.setCounter(id);
                    newsEntity.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    newsEntity.setCounter(cursor.getInt(cursor.getColumnIndex("counter")));
                    map.put(id, newsEntity);
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }
            return map;
        }

    /**删除表中的所有数据*/
    public void deleteDate(){
        db.delete(TABLE, null, null);
    }
}


