package org.zreo.cnbetareader.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.R;
import org.zreo.cnbetareader.Utils.MyImageLoader;
import org.zreo.cnbetareader.Utils.NetTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by guang on 2015/7/24. 实现视图与数据的绑定
 */

public class NewsTitleAdapter extends BaseAdapter{

    private int resourceId;
    private List<NewsEntity> listItem;
    private Context mContext;

    private MyImageLoader myImageLoader;
    private ImageLoader imageLoader;  //图片加载器对象
    private DisplayImageOptions options;  ////显示图片的配置
    SharedPreferences pref;

    /**构造函数*/
   public NewsTitleAdapter(Context context, int textViewResourcedId, List<NewsEntity> objects) {
        super();
        resourceId = textViewResourcedId;
        listItem = objects;
        mContext = context;
        myImageLoader = new MyImageLoader(mContext);
        imageLoader = myImageLoader.getImageLoader();
        options = myImageLoader.getDisplayImageOptions();
        //读取设置文件的值
        pref = mContext.getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);

    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.newsTitle = (TextView) view.findViewById(R.id.news_title);
            viewHolder.newsContent = (TextView) view.findViewById(R.id.news_summary);
            viewHolder.publishTime = (TextView) view.findViewById(R.id.news_publish_time);
            viewHolder.titleImage = (ImageView) view.findViewById(R.id.news_image);
            viewHolder.commentNumber = (TextView) view.findViewById(R.id.news_comment_number);
            viewHolder.readerNumber = (TextView) view.findViewById(R.id.news_reader_number);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();  //重新获取viewHolder
        }
        //格式化输出资讯标题
        viewHolder.newsTitle.setText(listItem.get(position).getTitle().replace("</span>", "")
                                            .replace("<span style=\"color:#c00000;\">", ""));
        //格式化输出资讯简介
        /*String homeText = listItem.get(position).getHometext().replace("<p>", "").replace("<strong>", "")
                                                            .replace("</p>", "").replace("</strong>", "")*/
        String homeText = listItem.get(position).getHometext().replace("<strong>", "").replace("</strong>", "");
        viewHolder.newsContent.setText(Html.fromHtml(homeText));

        if(NetTools.isWifiConnected()){   //判断是否在Wifi环境下，如果是就加载图片
            imageLoader.displayImage(listItem.get(position).getThumb(), viewHolder.titleImage, options);
        }else {     // 在移动网络下
            if(pref.getBoolean("informationList", true)){   // 读取是否加载列表图片的值，如果为真，就加载图片
                imageLoader.displayImage(listItem.get(position).getThumb(), viewHolder.titleImage, options);
            }else {    //如果设置不加载图片就显示默认图片
                viewHolder.titleImage.setImageResource(R.mipmap.news_title_default_image);
            }
        }

        viewHolder.publishTime.setText(listItem.get(position).getInputtime());
        viewHolder.commentNumber.setText(String.valueOf(listItem.get(position).getComments()));
        viewHolder.readerNumber.setText(String.valueOf(listItem.get(position).getCounter()));
        return view;
    }
    class ViewHolder {
        private TextView newsTitle;     //标题
        private TextView newsContent;   //简介
        private TextView publishTime;   //发表时间
        private ImageView titleImage;    //显示图片
        private TextView commentNumber; //评论数
        private TextView readerNumber;  //阅读数
    }

}

