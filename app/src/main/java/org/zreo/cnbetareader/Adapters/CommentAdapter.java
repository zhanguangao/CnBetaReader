package org.zreo.cnbetareader.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import org.zreo.cnbetareader.Entitys.CnComment;
import org.zreo.cnbetareader.Entitys.CommentItemEntity;
import org.zreo.cnbetareader.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends BaseAdapter{
    Context _context;
    private int resourceId;
    private List<CommentItemEntity> commentItem;
    public static int I = 1;
    public static int TAG = 0;

    public  CommentAdapter(Context mContext,int textViewResourcedId, List<CommentItemEntity> objects) {
        _context=mContext;
        resourceId = textViewResourcedId;
        commentItem=objects;
    }
    public void addList(ArrayList<CommentItemEntity> list) {
        if (list != null) {
            commentItem.addAll(list);
        } else {
            commentItem = new ArrayList<CommentItemEntity>();
        }
    }
    public void AddData(ArrayList<CommentItemEntity> list) {
        this.addList(list);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return commentItem.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(_context);
            view = inflater.inflate(resourceId, null);
            holder = new ViewHolder();
            holder.FName = (TextView)view.findViewById(R.id.textName);
            holder.imageView=(ImageView)view.findViewById(R.id.imageView1);
            holder.textView=(TextView)view.findViewById(R.id.user_name);
            holder.textView1 =(TextView)view.findViewById(R.id.support);
            holder.viewBtn=(ImageButton)view.findViewById(R.id.button1);
            holder.textView2 =(TextView)view.findViewById(R.id.comment_text);
            holder.textView3 = (TextView)view.findViewById(R.id.against);
            holder.supportNumber = (TextView)view.findViewById(R.id.supportNumber);
            holder.againstNumber = (TextView)view.findViewById(R.id.againstNumber);
            holder.response_text = (TextView)view.findViewById(R.id.response_text);
            holder.layout =(LinearLayout)view.findViewById(R.id.layout);
                    View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popup = new PopupMenu(_context, v);
                    popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                   popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action1:
                                    if (TAG == 0) {
                                        holder.supportNumber.setText(String.valueOf(commentItem.get(position).getScore() + 1));
                                        Toast.makeText(_context, "你选择了" + item.getTitle(), Toast.LENGTH_SHORT).show();
                                       // TAG = 1;
                                    }else {
                                        Toast.makeText(_context, "你已经表过态了", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.action2:
                                    if (TAG == 0) {
                                        holder.againstNumber.setText(String.valueOf(commentItem.get(position).getReason() + 1));
                                        Toast.makeText(_context, "你选择了" + item.getTitle(), Toast.LENGTH_SHORT).show();
                                       // TAG = 1;
                                    }else {
                                        Toast.makeText(_context, "你已经表过态了" , Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.action3:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                                    LayoutInflater inflater = LayoutInflater.from(_context);
                                    final View view = inflater.inflate(R.layout.response_dialog, null);
                                    builder.setView(view);
                                    builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
                                        //将评论动态的加载到LinearLayout
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                           EditText etComment = (EditText)view.findViewById(R.id.etComment );
                                                String etContent = etComment.getText().toString();
                                                TextView text = new TextView(_context);
                                                text.setText("匿名用户                                 "+I++ +"\n"+etContent);
                                            text.setTextColor(Color.BLACK);
                                            // 加入分割线
                                            final TextView line = new TextView(_context);
                                            line.setHeight(1);
                                            line.setBackgroundColor(Color.WHITE);
                                            holder.layout.addView(text);
                                            holder.layout.addView(line);
                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    //Toast.makeText(_context, " 发送成功" , Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            };
            holder.viewBtn.setOnClickListener(listener);
            view.setTag(holder);
        }
        else{
            view = convertView;
            holder=(ViewHolder)view.getTag();
        }
        holder.FName.setText(commentItem.get(position).getFName());
        holder.textView2.setText(commentItem.get(position).getComment());
        holder.textView.setText(commentItem.get(position).getName());
        holder.textView1.setText(commentItem.get(position).getSupport());
        holder.viewBtn.setImageResource(commentItem.get(position).getCommentMenu());
        holder.textView3.setText(commentItem.get(position).getAgainst());
        holder.supportNumber.setText(String.valueOf(commentItem.get(position).getScore()));
        holder.againstNumber.setText(String.valueOf(commentItem.get(position).getReason()));
        holder.response_text.setText(commentItem.get(position).getRefContent());
       // holder.layout.setVisibility(View.GONE);
        return view;
    }
    public class ViewHolder{
        public TextView supportNumber;
        public TextView againstNumber;
        public TextView FName;
        public ImageView imageView;
        public TextView textView1;
        public TextView textView;
        public ImageButton viewBtn;
        public TextView textView2;
        public TextView textView3;
        public  LinearLayout layout;
        public TextView response_text;
    }

}
