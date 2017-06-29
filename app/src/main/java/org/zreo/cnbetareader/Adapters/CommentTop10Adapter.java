package org.zreo.cnbetareader.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.R;

import java.util.List;

/**
 * Created by Administrator on 2015/7/28.
 */
public class CommentTop10Adapter extends BaseAdapter {

    Context context;
    private int resourceId;
    private List<NewsEntity> CommentTop10Item;


    public CommentTop10Adapter(Context mContext, int textViewResourceId, List<NewsEntity>objects) {
        super();
        context=mContext;
        resourceId = textViewResourceId;
        CommentTop10Item=objects;

    }

    @Override
    public int getCount() {
        return CommentTop10Item.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent ){
        View view;
        final ViewHolder holder;
        if (convertView==null){
            LayoutInflater inflater =LayoutInflater.from(context);
            view = inflater.inflate(resourceId,null);
            holder = new ViewHolder();
            holder.newsTitle =(TextView) view.findViewById(R.id.news_title1);
            holder.hot =(TextView)view.findViewById(R.id.hot);
            holder.ranking =(TextView)view.findViewById(R.id.ranking);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(ViewHolder)view.getTag();
        }
        holder.newsTitle.setText(CommentTop10Item.get(position).getTitle().replace("</span>", "")
                .replace("<span style=\"color:#c00000;\">", ""));
        holder.hot.setText(String.valueOf("热度:"+ CommentTop10Item.get(position).getCounter()));
        holder.ranking.setText(String.valueOf(position + 1));
        return view;
    }

   public class ViewHolder{

       public TextView newsTitle;
       public TextView ranking ;
       public TextView hot;

    }
}

