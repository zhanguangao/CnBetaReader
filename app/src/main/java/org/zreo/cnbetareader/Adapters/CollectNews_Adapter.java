package org.zreo.cnbetareader.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.R;
import java.util.List;

/**
 * Created by Admin on 2015/8/1.
 */
public class CollectNews_Adapter extends BaseAdapter {
    Context mContext;
    private int resourceId;
    private List<NewsEntity> CollectNewsItem;

    public CollectNews_Adapter(Context context, int newsResourceId, List<NewsEntity>objects){
        super();
        mContext = context;
        resourceId = newsResourceId;
        CollectNewsItem = objects;
    }
    @Override
    public int getCount() {
        return CollectNewsItem.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if (convertView == null){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(resourceId, null);
            holder = new ViewHolder();
            holder.firstWord = (TextView) view.findViewById(R.id.first_word);
            holder.collectNewsTitle = (TextView) view.findViewById(R.id.collect_news_title);
            view.setTag(holder);

        }else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        //格式化输出资讯标题
        String title = CollectNewsItem.get(position).getTitle().replace("</span>", "")
                                        .replace("<span style=\"color:#c00000;\">", "");
        holder.collectNewsTitle.setText(title);
        formatFirstWord(title, holder.firstWord);


        GradientDrawable grad = (GradientDrawable) holder.firstWord.getBackground();
        setImageColor(grad, position);  //myGrad.setColor(Color.RED);

        return view;
    }

    /**格式化显示的第一字安符*/
    public void formatFirstWord(String title, TextView tv){

        if(title.charAt(0) == '《' | title.charAt(0) == '“'){
            tv.setText(String.valueOf(title.charAt(1)));
        }else if(title.charAt(2) == ']'){   //[*]
            tv.setText(String.valueOf(title.charAt(3)));
        }else if(title.charAt(3) == ']'){  //[**]
            tv.setText(String.valueOf(title.charAt(4)));
        }else if(title.charAt(4) == ']'){   //[***]
            tv.setText(String.valueOf(title.charAt(5)));
        }else if(title.charAt(5) == ']'){   //[****]
            tv.setText(String.valueOf(title.charAt(6)));
        }else if(title.charAt(6) == ']'){   //[*****]
            tv.setText(String.valueOf(title.charAt(7)));
        }else {
            tv.setText(String.valueOf(title.charAt(0)));
        }

    }

    /**设置圆形图标背景的图片*/
    public void setImageColor(GradientDrawable grad, int index){

        int blue = Resources.getSystem().getColor(android.R.color.holo_blue_light);
        int gray = Resources.getSystem().getColor(android.R.color.darker_gray);
        int greenDark = Resources.getSystem().getColor(android.R.color.holo_green_dark);
        int greenLight = Resources.getSystem().getColor(android.R.color.holo_green_light);
        int purple = Resources.getSystem().getColor(android.R.color.holo_purple);
        int orange = Resources.getSystem().getColor(android.R.color.holo_orange_light);
        int mainColor = mContext.getResources().getColor(R.color.mainColor);
        int [] colorList = {blue, gray, purple, greenDark, orange, greenLight, mainColor};  //7种颜色
        grad.setColor(colorList[Math.abs(CollectNewsItem.size() - index) % 7]);

    }

    public class ViewHolder{
        public TextView firstWord;
        public TextView collectNewsTitle ;
    }
}

