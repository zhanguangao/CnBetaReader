package org.zreo.cnbetareader.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;

import org.zreo.cnbetareader.Activitys.NewsActivity;
import org.zreo.cnbetareader.Adapters.CommentTop10Adapter;
import org.zreo.cnbetareader.Database.Top10CommentDatabase;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.Entitys.NewsListEntity;
import org.zreo.cnbetareader.Entitys.ResponseEntity;
import org.zreo.cnbetareader.Model.Net.HttpDateModel;
import org.zreo.cnbetareader.Net.BaseHttpClient;
import org.zreo.cnbetareader.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2015/7/29.
 */
public class Comment_Top10Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {
    View view;
    private ListView top10_listView;
    private CommentTop10Adapter mAdapter;
    private List<NewsEntity> cnCommentTop10List;
    Map<Integer, NewsEntity> map = new TreeMap<Integer, NewsEntity>(new Comparator<Integer>() {  //将获取到的新闻列表排序
        @Override
        public int compare(Integer lhs, Integer rhs) {
            return rhs.compareTo(lhs);   // 降序排序, 默认为升序
        }
    });

    Toast toast;                //数据更新提示的Toast
    TextView toastTextView;    //Toast显示的文本
    private Top10CommentDatabase top10CommentDatabase;  //数据库

    private View loadMoreView;         //加载更多布局
    private TextView loadMoreText;    //加载提示文本
    SwipeRefreshLayout swipeLayout;  //下拉刷新控件

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_c_top10listview, container, false);

        customToast();
        top10CommentDatabase = Top10CommentDatabase.getInstance(getActivity());
        //BaseHttpClient.getInsence().getNewsListByPage("dig", "1", initResponse);

        initListItem(); //初始化新闻列表及布局


        return view;
    }
    private boolean isLoad = true;   //打开软件时自动加载的新闻
    /** 初始化新闻列表*/
    public void initListItem(){
        map = top10CommentDatabase.loadMapTop10Comment();  //从数据库读取新闻列表 map跟listItems同步
        cnCommentTop10List = new ArrayList<NewsEntity>(map.values());
        if (cnCommentTop10List.size() > 0) {     //数据库有数据，直接显示数据库中的数据
            initView();   //初始化布局
        } else {
            //如果数据库没数据，再从网络加载最新的新闻，首次打开软件时执行
            BaseHttpClient.getInsence().getNewsListByPage("dig", "1", initResponse);
        }


    }

    private ResponseHandlerInterface initResponse = new HttpDateModel<NewsListEntity>
            (new TypeToken<ResponseEntity<NewsListEntity>>(){}) {
        @Override
        protected void onFailure() {
            toastTextView.setText("加载失败，请检查网络连接");
            toast.show();
        }

        @Override
        protected void onSuccess(NewsListEntity result) {
            List<NewsEntity> list = result.getList();   //网络请求返回的数据

            for (int i = 0 ; i < 10; i++){
                map.put(list.get(i).getCounter(), list.get(i));
            }
            cnCommentTop10List = new ArrayList<NewsEntity>(map.values());
            initView();  //初始化布局
            //开启子线程将数据保存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0 ; i < cnCommentTop10List.size(); i++){
                        top10CommentDatabase.saveTop10comment(cnCommentTop10List.get(i));  //将数据保存到数据库
                    }
                }
            }).start();

        }

        @Override
        protected void onError() {
            toastTextView.setText("加载错误，请刷新重试");
            toast.show();
        }
    };

    private void initView(){
        /**显示CnCommentTop10的ListView*/
        top10_listView = (ListView)view.findViewById(R.id.top10_listView);
        /**为ListView创建自定义适配器*/
        mAdapter = new CommentTop10Adapter(getActivity(),R.layout.fragment_c_top10textview,cnCommentTop10List);
        loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_more);
        loadMoreText.setText("--The End--");
        top10_listView .addFooterView(loadMoreView);   //设置列表底部视图
        top10_listView .setAdapter(mAdapter);         //为ListView绑定Adapter

        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_top);
        view.setEnabled(false);
        swipeLayout.setOnRefreshListener(this);
        //设置刷新时动画的颜色，可以设置4个
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        /**事件点击*/
        top10_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsEntity entity = cnCommentTop10List.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("NewsItem", entity);

                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }


    private long exitTime = 0;
    /**下拉刷新监听*/
    @Override
    public void onRefresh() {

        if((System.currentTimeMillis() - exitTime) < 5000) {   //当5秒内再次刷新时执行
            toastTextView.setText("刚刚刷新过，等下再试吧");
            toast.show();
        }else {
            BaseHttpClient.getInsence().getNewsListByPage("dig", "1", refreshResponse);
        }
        exitTime = System.currentTimeMillis();

        swipeLayout.setRefreshing(false);   //加载完数据后，隐藏刷新进度条
    }

    private ResponseHandlerInterface refreshResponse = new HttpDateModel<NewsListEntity>
            (new TypeToken<ResponseEntity<NewsListEntity>>(){}) {
        @Override
        protected void onFailure() {
            toastTextView.setText("刷新失败，请检查网络连接");
            toast.show();
        }

        @Override
        protected void onSuccess(NewsListEntity result) {
            List<NewsEntity> list = result.getList();   //网络请求返回的数据

            map.clear();
            for (int i = 0 ; i < 10; i++){
                map.put(list.get(i).getCounter(), list.get(i));
            }

            cnCommentTop10List.clear();
            cnCommentTop10List.addAll(new ArrayList<NewsEntity>(map.values()));
            mAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter

            top10CommentDatabase.deleteDate();

            //开启子线程将数据保存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0 ; i < cnCommentTop10List.size(); i++){
                        top10CommentDatabase.saveTop10comment(cnCommentTop10List.get(i));  //将数据保存到数据库
                    }
                }
            }).start();

        }

        @Override
        protected void onError() {
            toastTextView.setText("刷新错误，请重新刷新");
            toast.show();
        }
    };

    /**自定义Toast，用于数据更新的提示*/
    private void customToast() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);  //获取屏幕分辨率
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(dm.widthPixels, dm.heightPixels/15);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View toastView = inflater.inflate(R.layout.toast, null);  // 取得xml里定义的view
        toastTextView = (TextView) toastView.findViewById(R.id.toast_text); // 取得xml里定义的TextView
        toastTextView.setLayoutParams(params);  //设置Toast的宽度和高度
        toast = new Toast(getActivity());
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        //toast.show();
    }

}
