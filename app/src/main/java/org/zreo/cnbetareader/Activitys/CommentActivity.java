package org.zreo.cnbetareader.Activitys;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;

import org.zreo.cnbetareader.Adapters.CommentAdapter;
import org.zreo.cnbetareader.Database.CommentDatabase;
import org.zreo.cnbetareader.Entitys.CommentItemEntity;
import org.zreo.cnbetareader.Entitys.CommentListEntity;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.Entitys.NewsListEntity;
import org.zreo.cnbetareader.Entitys.ResponseEntity;
import org.zreo.cnbetareader.Model.Net.HttpDateModel;
import org.zreo.cnbetareader.Net.BaseHttpClient;
import org.zreo.cnbetareader.R;
import org.zreo.cnbetareader.Views.XListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CommentActivity extends AppCompatActivity implements XListView.IXListViewListener, View.OnClickListener {

    private  int I = 10;
    private CommentDatabase commentDatabase;  //数据库
    private List<CommentItemEntity> cnCommentList = new ArrayList <CommentItemEntity>();
    CommentItemEntity commentItemEntity;
    CommentAdapter myAdapter;
    private TextView loadMore;
    private NewsEntity newsEntity;
    private Toolbar mToolbar;
    SharedPreferences pref;
    private XListView myListView;
    private ImageView mQuickReturnView; // 下拉快速显示item功能
    Map<String,CommentItemEntity> map = new TreeMap<String,CommentItemEntity>(new Comparator<String>() {  //将获取到的评论列表排序
        @Override
        public int compare(String lhs, String rhs) {
            return rhs.compareTo(lhs);   // 降序排序, 默认为升序
        }
    });

    public CommentActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_listview);
        Bundle bundle= new Bundle();
        bundle=getIntent().getExtras();
        newsEntity= (NewsEntity) bundle.getSerializable("NewsItem");
        commentDatabase = CommentDatabase.getInstance(CommentActivity.this);  //初始化数据库实例
        //initView();
    //    BaseHttpClient.getInsence().getCommentBySnAndSid(newsEntity.getSN(), newsEntity.getSid() + "", responseHandlerInterface);
        initListItem();
        //读取设置文件的值
        pref = getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);
        setThemeColor(pref.getInt("theme", 0));    //设置文件里主题的值
    }

    public void setStatusColor(int color){
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(color); //状态栏颜色
        }
    }
    /**更改主题颜色*/
    @SuppressLint("NewApi")
    public void setThemeColor(int index){
        switch (index){
            case 0:  //蓝色（默认）
                mToolbar.setBackgroundColor(getResources().getColor(R.color.mainColor));  //ActionBar颜色
                setStatusColor(getResources().getColor(R.color.mainColor));
                break;
            case 1:  //棕色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.brown));
                setStatusColor(getResources().getColor(R.color.brown));
                break;
            case 2:  //橙色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.orange));
                setStatusColor(getResources().getColor(R.color.orange));
                break;
            case 3:  //紫色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                setStatusColor(getResources().getColor(R.color.purple));
                break;
            case 4:  //绿色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.green));
                setStatusColor(getResources().getColor(R.color.green));
                break;
            default:  //默认
                mToolbar.setBackgroundColor(getResources().getColor(R.color.mainColor));
                setStatusColor(getResources().getColor(R.color.mainColor));
                break;
        }
    }
    /** 初始化评论列表*/
    public void initListItem(){
       // cnCommentList = commentDatabase.loadCommentItemEntity();   //从数据库读取评论列表
            BaseHttpClient.getInsence().getCommentBySnAndSid(newsEntity.getSN(), newsEntity.getSid() + "", responseHandlerInterface);
            initView();   //初始化布局
        new Thread(new Runnable() {
            @Override
            public void run() {
                map = commentDatabase.loadMapCommentItemEntity();  //从数据库读取新闻列表 map跟listItems同步
            }
        }).start();
    }
    /**
     * 初始化布局
     */
    private void initView() {
        myListView = (XListView) findViewById(R.id.xListView);
        myListView.setPullRefreshEnable(false);
        myListView.setPullLoadEnable(true);
        myAdapter = new CommentAdapter(this, R.layout.comment_textview, cnCommentList);
        myListView.setAdapter(myAdapter);
        myListView.setXListViewListener(this);
        // 下拉快速显示item功能
        loadMore = (TextView)findViewById(R.id.xlistview_footer_hint_textview);
        mQuickReturnView = (ImageView) findViewById(R.id.forum_listview_linearfooter);
        loadMore = (TextView)findViewById(R.id.xlistview_footer_hint_textview);
        mQuickReturnView.setOnClickListener(this); // 下拉快速显示item功能
        myListView.setQuickReturnView(mQuickReturnView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);   //ToolBar布局
        mToolbar.setTitle("评论");   // 标题的文字需在setSupportActionBar之前，不然会无效
        mToolbar.setTitleTextColor(Color.WHITE);  //设置ToolBar字体颜色为白色
        setSupportActionBar(mToolbar);  //将ToolBar设置为ActionBAr
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //在ToolBar左边，即当前标题前添加图标
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onRefresh() {
        BaseHttpClient.getInsence().getCommentBySnAndSid(newsEntity.getSN(), newsEntity.getSid() + "", responseHandlerInterface);
           }

       private ResponseHandlerInterface responseHandlerInterface = new HttpDateModel<CommentListEntity>(new TypeToken<ResponseEntity<CommentListEntity>>(){
          }) {

              @Override
               protected void onSuccess(CommentListEntity result) {
               final ArrayList<CommentItemEntity> cmntlist = result.getCmntlist();
                   HashMap<String, CommentItemEntity> cmntstore = result.getCmntstore();
                  for (CommentItemEntity item : cmntlist) {
                      StringBuilder sb = new StringBuilder();
                      item.copy(cmntstore.get(item.getTid()));
                      CommentItemEntity parent = cmntstore.get(item.getPid());
                      while (parent != null) {
                          sb.append("//@");
                          sb.append(parent.getName());
                          sb.append(": [");
                          sb.append(parent.getHost_name());
                          sb.append("] ");
                          sb.append(parent.getComment());
                          parent = cmntstore.get(parent.getPid());
                      }
                      item.setRefContent(sb.toString());
                  }
                  ArrayList<CommentItemEntity> hotcmntlist = result.getHotlist();
                  for (CommentItemEntity item : hotcmntlist) {
                      StringBuilder sb = new StringBuilder();
                      item.copy(cmntstore.get(item.getTid()));
                      CommentItemEntity parent = cmntstore.get(item.getPid());
                      while (parent != null) {
                          sb.append("//@");
                          sb.append(parent.getName());
                          sb.append(": [");
                          sb.append(parent.getHost_name());
                          sb.append("] ");
                          sb.append(parent.getComment());
                          parent = cmntstore.get(parent.getPid());
                      }
                      item.setRefContent(sb.toString());
                  }

                  for (int i = 0 ; i < cmntlist.size(); i++){
                      if (!map.containsKey(cmntlist.get(i).getTid()))
                      map.put(cmntlist.get(i).getTid(), cmntlist.get(i));
                  }
 //                cnCommentList = new ArrayList<CommentItemEntity>(map.values()); //将Map值转化为List
//                  initView();  //初始化布局
                  //if (cmntlist.size() < 10) {
                      for (int i = 0; i < cmntlist.size(); i++) {
                          String sText = "支持:";
                          String aText = "反对:";
                          CommentItemEntity cnComments = new CommentItemEntity();
                          cnComments.setImageView1(R.id.imageView1);
                          cnComments.setName(cmntlist.get(i).getName());
                          char[] fName = cmntlist.get(i).getName().toCharArray();
                          cnComments.setFName(String.valueOf(fName[0]));
                          cnComments.setScore(cmntlist.get(i).getScore());
                          cnComments.setReason(cmntlist.get(i).getReason());

                          cnComments.setComment(cmntlist.get(i).getComment());
                          cnComments.setCommentMenu(R.drawable.more_grey);
                          cnComments.setRefContent(cmntlist.get(i).getRefContent());
                          cnComments.setSupport(sText);
                          cnComments.setAgainst(aText);
                          cnCommentList.add(cnComments);
                    }
//                  } else {
//                      for (int i = 0; i < 10; i++) {
//                          String sText = "支持:";
//                          String aText = "反对:";
//                          CommentItemEntity cnComments = new CommentItemEntity();
//                          cnComments.setImageView1(R.id.imageView1);
//                          cnComments.setFName("匿");
//                          cnComments.setScore(cmntlist.get(i).getScore());
//                          cnComments.setReason(cmntlist.get(i).getReason());
//                          cnComments.setName(cmntlist.get(i).getName());
//                          cnComments.setComment(cmntlist.get(i).getComment());
//                          cnComments.setCommentMenu(R.drawable.more_grey);
//                          cnComments.setRefContent(cmntlist.get(i).getRefContent());
//                          cnComments.setSupport(sText);
//                          cnComments.setAgainst(aText);
//                          cnCommentList.add(cnComments);
//                      }
//                  }
                  myAdapter.notifyDataSetChanged();

                  //开启子线程将数据保存到数据库
                  new Thread(new Runnable() {
                      @Override
                      public void run() {
                          for (int i = 0 ; i < cmntlist.size(); i++){
                                commentDatabase.saveCommentItemEntity(cmntlist.get(i));
                              }
                      }
                  }).start();
                 }
                       @Override
               protected void onError() {
                           Toast.makeText(CommentActivity.this,"error", Toast.LENGTH_LONG).show();

                           }

                       @Override
               protected void onFailure() {
                           Toast.makeText(CommentActivity.this,"failure", Toast.LENGTH_LONG).show();
                           }
                    };
    private void loadMoreData() {
//        Map<String, CommentItemEntity> tempMap = commentDatabase.loadMapCommentItemEntity();  //从数据库读取之前保存的数据
//        List<CommentItemEntity> loadList;
//        loadList = new ArrayList<CommentItemEntity>(map.values()); //将Map值转化为List
////        if (10<loadList.size()) {
//            for (int i= 10; i < loadList.size(); i++) {
//               // if (!tempMap.get("tid").getTid().equals(loadList.get(i).getTid())) {
//                    String sText = "支持:";
//                    String aText = "反对:";
//                    CommentItemEntity cnComments = new CommentItemEntity();
//                    cnComments.setImageView1(R.id.imageView1);
//                    cnComments.setFName("匿");
//                    cnComments.setScore(loadList.get(i).getScore());
//                    cnComments.setReason(loadList.get(i).getReason());
//                    cnComments.setName(loadList.get(i).getName());
//                    cnComments.setComment(loadList.get(i).getComment());
//                    cnComments.setCommentMenu(R.drawable.more_grey);
//                    cnComments.setRefContent(loadList.get(i).getRefContent());
//                    cnComments.setSupport(sText);
//                    cnComments.setAgainst(aText);
//                    cnCommentList.add(cnComments);
//                //}
//            }
//            myAdapter.notifyDataSetChanged();
//        } else{
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore.setText("没有更多的评论了");
            }
        });
    }
    @Override
    public void onLoadMore() {
         loadMoreData();
        // initCommentList();
        myListView.stopRefresh();
        myListView.stopLoadMore();
    }
    /**
     * 悬浮按钮点击响应
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CommentActivity.this, PostCommentActivity.class);
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String contentData = data.getStringExtra("content");
                    String userName = "匿名用户";
                    //String textComment = "100块都不给我";
                    String[] FName = {"匿"};
                    String sText = "支持:";
                    String aText = "反对:";
                    int supportNum = 0;
                    int againstNum = 0;
                   // ArrayList<CommentItemEntity> resultList = new ArrayList<CommentItemEntity>();
                    CommentItemEntity cnComments = new CommentItemEntity();
                    cnComments.setFName(FName[0]);
                    cnComments.setScore(supportNum);
                    cnComments.setReason(againstNum);
                    cnComments.setName(userName);
                    cnComments.setComment(contentData);
                    cnComments.setCommentMenu(R.drawable.more_grey);
                    cnComments.setSupport(sText);
                    cnComments.setAgainst(aText);
                    cnCommentList.add(cnComments);
                   // myAdapter.AddData(resultList);
                    myAdapter.notifyDataSetChanged();
                    myListView.setSelection(myAdapter.getCount());// 将myListView定位到刚刚评论的一行
                }
                break;
            default:
                break;
        }
    }
}
