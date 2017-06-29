package org.zreo.cnbetareader.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.zreo.cnbetareader.Entitys.HotCommentItemEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2015/8/11.
 */
public class HotCommentDatabase {

    public static final String DB_NAME = "NewsData.db";  //数据库名
    public static final String TABLE = "HotCommentEntity";   //数据表
    public static final int VERSION = 1;   //数据库版本
    private static HotCommentDatabase hotCommentDatabase;
    private SQLiteDatabase db;

    //将构造方法私有化
    private HotCommentDatabase(Context context) {
        NewsTitleOpenHelper dbHelper = new NewsTitleOpenHelper(
                context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    //获取CollectionDatabase的实例
    public synchronized static HotCommentDatabase getInstance(Context context) {
        if (hotCommentDatabase == null) {
            hotCommentDatabase = new HotCommentDatabase(context);
        }
        return hotCommentDatabase;
    }

    //将NewsEntity实例存储到收藏数据库
    public void saveHotComment(HotCommentItemEntity hotCommentItemEntity) {
        if (hotCommentItemEntity != null) {
            ContentValues values = new ContentValues();
            values.put("sid", hotCommentItemEntity.getSid());
            values.put("description", hotCommentItemEntity.getDescription());
            values.put("froms", hotCommentItemEntity.getFrom());
            values.put("newsTitle", hotCommentItemEntity.getNewstitle());
            values.put("title", hotCommentItemEntity.getTitle());
            db.insert(TABLE, null, values);
        }
    }


    //从数据库读取收藏信息，返回List
    public List<HotCommentItemEntity> loadHotComment() {
        List<HotCommentItemEntity> list = new ArrayList<HotCommentItemEntity>();
        Cursor cursor = db.query(TABLE, null, null, null, null, null, "id desc");   //查询结果用id排序，降序desc，升序asc
        if (cursor.moveToFirst()) {
            do {
                HotCommentItemEntity hotCommentItemEntity = new HotCommentItemEntity();
                hotCommentItemEntity.setSid(cursor.getInt(cursor.getColumnIndex("sid")));
                hotCommentItemEntity.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                hotCommentItemEntity.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                hotCommentItemEntity.setNewstitle(cursor.getString(cursor.getColumnIndex("newstitle")));
                hotCommentItemEntity.setFrom(cursor.getString(cursor.getColumnIndex("froms")));
                list.add(hotCommentItemEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    //从数据库读取收藏信息，返回Map
    public Map<Integer, HotCommentItemEntity> loadMapHotComment() {

        Map<Integer, HotCommentItemEntity> map = new TreeMap<Integer, HotCommentItemEntity>(new Comparator<Integer>() {  //将获取到的新闻列表排序
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs.compareTo(lhs);   // 降序排序, 默认为升序
            }
        });
        int sid;  //新闻id
        Cursor cursor = db.query(TABLE, null, null, null, null, null, "id desc");   //查询结果用id排序，降序desc，升序asc
        if (cursor.moveToFirst()) {
            do {
                HotCommentItemEntity hotCommentItemEntity = new HotCommentItemEntity();
                sid = cursor.getInt(cursor.getColumnIndex("sid"));
                hotCommentItemEntity.setSid(sid);
                hotCommentItemEntity.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                hotCommentItemEntity.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                hotCommentItemEntity.setNewstitle(cursor.getString(cursor.getColumnIndex("newstitle")));
                hotCommentItemEntity.setFrom(cursor.getString(cursor.getColumnIndex("froms")));
                map.put(sid, hotCommentItemEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return map;
    }
}