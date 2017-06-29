package org.zreo.cnbetareader.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.zreo.cnbetareader.Model.CnInformation_Theme;
import org.zreo.cnbetareader.R;

import java.util.List;

/**
 * Created by Admin on 2015/8/14.
 */
public class Fragment1_Adapter extends BaseAdapter{

    Context mContext;
    private int resourceId;
    private List<CnInformation_Theme> CollectNewsItem;
    public Fragment1_Adapter(Context mContext,int resourceId,List<CnInformation_Theme> list){
        super();
        this.mContext=mContext;
        this.resourceId=resourceId;
        CollectNewsItem=list;
    }
    @Override
       public int getCount() {
        return CollectNewsItem.size();}

    @Override
    public Object getItem(int position) {
        return null;}
    @Override
    public long getItemId(int position) {
        return 0;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v;
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(mContext);
            v=inflater.inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.firstWord=(TextView)v.findViewById(R.id.firstword_text1);
            viewHolder.title=(TextView)v.findViewById(R.id.itv_context1);
            v.setTag(viewHolder);
        }
        else {
            v = convertView;
            viewHolder=(ViewHolder)v.getTag();
        }
        String firstword=CollectNewsItem.get(position).getFirstWord();
        String title=CollectNewsItem.get(position).getContent();
        viewHolder.firstWord.setText(firstword);
        viewHolder.title.setText(title);
        GradientDrawable grad = (GradientDrawable) viewHolder.firstWord.getBackground();
        setImageColor(grad, position);
        return v;
    }
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
        public  TextView firstWord;
        public TextView title;
    }
}

