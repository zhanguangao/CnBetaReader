package org.zreo.cnbetareader.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.zreo.cnbetareader.Activitys.NewsActivity;
import org.zreo.cnbetareader.Adapters.NewsTitleAdapter;
import org.zreo.cnbetareader.Database.CollectionDatabase;
import org.zreo.cnbetareader.Database.NewsTitleDatabase;
import org.zreo.cnbetareader.Database.NewsTitleOpenHelper;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.Entitys.NewsListEntity;
import org.zreo.cnbetareader.Entitys.ResponseEntity;
import org.zreo.cnbetareader.Model.Net.HttpDateModel;
import org.zreo.cnbetareader.Net.BaseHttpClient;
import org.zreo.cnbetareader.R;
import org.zreo.cnbetareader.Utils.MyImageLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by guang on 2015/7/23.
 * 显示新闻列表
 */

public class NewsTitleFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener{

    View view;  //当前布局
    private ListView lv;
    NewsTitleAdapter mAdapter;
    SwipeRefreshLayout swipeLayout;  //下拉刷新控件

    /**listItems跟map同步*/
    private List<NewsEntity> listItems; // = new ArrayList<NewsEntity>();  // ListView item项，以添加顺序排序
    // ListView item项，以降序排序的新闻列表
    Map<Integer, NewsEntity> map = new TreeMap<Integer, NewsEntity>(new Comparator<Integer>() {  //将获取到的新闻列表排序
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    return rhs.compareTo(lhs);   // 降序排序, 默认为升序
                }
            });

    Toast toast;  //数据更新提示的Toast
    TextView toastTextView; //Toast显示的文本

    private View loadMoreView;     //加载更多布局
    private TextView loadMoreText;    //加载提示文本
    private LinearLayout loadMoreLayout;  //点击加载更多布局

    private NewsTitleDatabase newsTitleDatabase;  //数据库
    SharedPreferences pref;

    private MyImageLoader myImageLoader; //自定义图片加载器
    private ImageLoader imageLoader;  //图片加载器对象
    private DisplayImageOptions options;  //显示图片的配置

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_news_title, container, false);
        setHasOptionsMenu(true);    //在fragment中使用menu菜单

        newsTitleDatabase = NewsTitleDatabase.getInstance(getActivity());  //初始化数据库实例
        ////读取设置文件的值
        pref = getActivity().getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);

        customToast();  //初始化自定义Toast，用于数据更新的提示

        initSwipeRefreshLayout();  //初始化下拉刷新控

        initListItem(); //初始化新闻列表及布局
        return  view;
    }

    private boolean isLoad = true;   //打开软件时判断是否自动加载资讯
    /** 初始化新闻列表*/
    public void initListItem(){
        map = newsTitleDatabase.loadMapNewsEntity();  //从数据库读取新闻列表 map跟listItems同步
        listItems = new ArrayList<NewsEntity>(map.values());
        if (listItems.size() > 0) {     //数据库有数据，直接显示数据库中的数据
            initView();   //初始化布局
        } else {    //如果数据库没数据，再从网络加载最新的新闻，首次打开软件时执行
            BaseHttpClient.getInsence().getNewsListByPage("all", "1", initResponse);
        }

        if(pref.getBoolean("loadLatestInformation", true)){    //读取设置里的是否打开软件加载最新资讯
            isLoad = false;
            BaseHttpClient.getInsence().getNewsListByPage("all", "1", refreshResponse);
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
            final List<NewsEntity> list = result.getList();   //网络请求返回的数据

            for (int i = 0 ; i < list.size(); i++){
                map.put(list.get(i).getSid(), list.get(i));
            }

            //listItems.addAll(new ArrayList<NewsEntity>(map.values()));
            listItems = new ArrayList<NewsEntity>(map.values()); //将Map值转化为List
            initView();  //初始化布局

            toastTextView.setText("自动为您加载了 " + listItems.size() + " 条资讯");
            toast.show();

            //开启子线程将数据保存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0 ; i < list.size(); i++){
                        newsTitleDatabase.saveNewsEntity(list.get(i));  //将数据保存到数据库
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

    /** 初始化下拉刷新控件 */
    public void initSwipeRefreshLayout(){
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        //设置刷新时动画的颜色，可以设置4个
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /** 初始化布局 */
    private void initView() {
        /**显示新闻标题的ListView*/
        lv = (ListView) view.findViewById(R.id.news_title_list_view);
        /**为ListView创建自定义适配器*/
        mAdapter = new NewsTitleAdapter(getActivity(), R.layout.news_title_item, listItems);
        //lv.setVerticalScrollBarEnabled(false);    //隐藏ListView滑动进度条
        loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more, null);  //加载更多布局
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_more);   //加载更多文本
        loadMoreLayout = (LinearLayout) loadMoreView.findViewById(R.id.load_more_LinearLayout);  //点击加载更多布局
        lv.addFooterView(loadMoreView);   //设置列表底部视图
        lv.setOnScrollListener(this);     //添加滑动监听
        lv.setAdapter(mAdapter);  //为ListView绑定Adapter

        /**ListView点击事件*/
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsEntity entity = listItems.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("NewsItem", entity);

                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        /**ListView长按弹出收藏对话框*/
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final NewsEntity entity = listItems.get(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                final CollectionDatabase collectionDatabase = CollectionDatabase.getInstance(getActivity());  //初始化数据库实例

                Map<Integer, NewsEntity> tempMap =  collectionDatabase.loadMapCollection();  //从数据库读取之前保存的数据
                final boolean isExist = tempMap.containsKey(entity.getSid());  //判断数据库是否已收藏选中的资讯

                if(!isExist){  //如果收藏数据库中不存在这个键值id的话
                    alert.setTitle(R.string.collect);   //收藏选中的资讯
                }else{
                    alert.setTitle(R.string.cancel_collection);  //取消收藏
                }

                alert.setCancelable(true);  //为真时可以通过返回键取消

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!isExist){
                            collectionDatabase.saveCollection(entity);  //如果数据库中不存在这个键值id的话，则添加到数据库
                            Toast.makeText(getActivity(), R.string.collect_succeed, Toast.LENGTH_SHORT).show();   //收藏成功
                        }else {
                            collectionDatabase.deleteCollection(entity);
                            Toast.makeText(getActivity(), R.string.cancelled_collection, Toast.LENGTH_SHORT).show();   //已取消收藏
                        }
                    }
                });

                alert.setNegativeButton("取消", null);
                alert.show();   //显示对话框
                return true;
            }
        });
    }

    private long exitTime = 0;
    /**下拉刷新监听*/
    @Override
    public void onRefresh() {

        if(listItems.size() > 0) {
            if((System.currentTimeMillis() - exitTime) < 5000) {   //当5秒内再次刷新时执行
                toastTextView.setText("刚刚刷新过，等下再试吧");
                toast.show();
            }else {
                BaseHttpClient.getInsence().getNewsListByPage("all", "1", refreshResponse);
            }
        }else {
            //清除缓存后当前没有数据，下拉刷新获取最新的网络数据
            BaseHttpClient.getInsence().getNewsListByPage("all", "1", initResponse);
        }

        exitTime = System.currentTimeMillis();

        swipeLayout.setRefreshing(false);   //加载完数据后，隐藏刷新进度条
    }

    private int lastNumber = 0;   //更新数据前的资讯数
    private int currentNumber = 0;  //当前资讯数
    private int addNumber = 0;   //每次刷新或加载增加的数据

    /**刷新时更新数据*/
    private ResponseHandlerInterface refreshResponse = new HttpDateModel<NewsListEntity>
                                 (new TypeToken<ResponseEntity<NewsListEntity>>(){}) {
        @Override
        protected void onFailure() {
            toastTextView.setText("刷新失败，请检查网络连接");
            toast.show();
        }

        @Override
        protected void onSuccess(NewsListEntity result) {
            final List<NewsEntity> list = result.getList();  //网络请求返回的数据
            lastNumber = listItems.size();   //更新数据前的资讯数

            for (int i = 0 ; i < list.size(); i++){
                if(!map.containsKey(list.get(i).getSid())) {  //如果Map中没有该资讯的id，则添加到Map中
                    map.put(list.get(i).getSid(), list.get(i));  //将返回的数据添加到Map中
                }
            }

            if(map.size() > lastNumber)    //当资讯列表有更新
            {
                listItems.clear();
                listItems.addAll(new ArrayList<NewsEntity>(map.values()));
                mAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter

                currentNumber = listItems.size();  //当前资讯数
                addNumber = currentNumber - lastNumber;  //刷新增加的资讯数
                toastTextView.setText("新增 " + addNumber + " 条资讯");
                toast.show();

                //开启子线程将数据保存到数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<Integer, NewsEntity> tempMap =  newsTitleDatabase.loadMapNewsEntity();  //从数据库读取之前保存的数据
                        for (int i = 0 ; i < list.size(); i++){
                            if(!tempMap.containsKey(list.get(i).getSid())){
                                newsTitleDatabase.saveNewsEntity(list.get(i));  //如果数据库中不存在这个键值id的话，则添加到数据库
                            }
                        }
                    }
                }).start();

            } else {
                toastTextView.setText("没有更多内容了");
                if(isLoad){    //当软件自动加载的资讯是最新的时，不需要提示
                    toast.show();
                }
                isLoad = true;
            }


        }

        @Override
        protected void onError() {
            toastTextView.setText("刷新错误，请重新刷新");
            toast.show();
        }
    };

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }

    private int page; //传入页数，第一页为最新的资讯，依次类推
    /**滑到底部自动加载*/
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = listItems.size() - 1;    //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
        //当滑到底部时自动加载
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
            if(pref.getBoolean("autoLoadMore", true)){   //如果设置自动加载
                loadMoreText.setText(R.string.loading);   //加载中...
                page++;
                BaseHttpClient.getInsence().getNewsListByPage("all", String.valueOf(page), autoLoadResponse);
            }else{        //读取是否加载自动加载的值，如果为真，则要点击才有加载更多
                loadMoreText.setText(R.string.click_load_more);    //点击加载更多
                loadMoreLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        page++;
                        BaseHttpClient.getInsence().getNewsListByPage("all", String.valueOf(page), autoLoadResponse);
                    }
                });
            }

        }
    }

    /**自动加载更新数据*/
    private ResponseHandlerInterface autoLoadResponse = new HttpDateModel<NewsListEntity>
            (new TypeToken<ResponseEntity<NewsListEntity>>(){}) {
        @Override
        protected void onFailure() {
            toastTextView.setText("加载失败，请检查网络连接");
            toast.show();
        }

        @Override
        protected void onSuccess(NewsListEntity result) {
            loadMoreText.setText(R.string.load_more);   //加载更多
            final List<NewsEntity> list = result.getList();  //网络请求返回的数据
            lastNumber = listItems.size();   //更新数据前的资讯数

            for (int i = 0 ; i < list.size(); i++){
                if(!map.containsKey(list.get(i).getSid())) {  //如果Map中没有该资讯的id，则添加到Map中
                    map.put(list.get(i).getSid(), list.get(i));  //将返回的数据添加到Map中
                }
            }

            if(map.size() > lastNumber) {    //当资讯列表有更新
                listItems.clear();
                listItems.addAll(new ArrayList<NewsEntity>(map.values()));
                mAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter

                currentNumber = listItems.size();  //当前新闻数
                addNumber = currentNumber - lastNumber;  //每次刷新增加的数据
                toastTextView.setText("新增 " + addNumber + " 条资讯");
                toast.show();

                //开启子线程将数据保存到数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<Integer, NewsEntity> tempMap =  newsTitleDatabase.loadMapNewsEntity();  //从数据库读取之前保存的数据
                        for (int i = 0 ; i < list.size(); i++){
                            if(!tempMap.containsKey(list.get(i).getSid())){
                                newsTitleDatabase.saveNewsEntity(list.get(i));  //如果数据库中不存在这个键值id的话，则添加到数据库
                            }
                        }
                    }
                }).start();

            }else{   //加载当前页返回的数据与显示的数据相同时，加载下一页的新闻
                page++;
                BaseHttpClient.getInsence().getNewsListByPage("all", String.valueOf(page), autoLoadResponse);
            }

        }

        @Override
        protected void onError() {
            toastTextView.setText("加载错误，请重新加载");
            toast.show();
        }
    };


    /**自定义Toast，用于资讯更新的提示*/
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.offline_download:  //离线下载
                offlineDownload();
                break;
            case R.id.clear_cache:  //开启新线程清除缓存
                clearCache();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private int offlinePage = 0;     //离线下载页数
    private int offlineTimes = 0;    //离线下载次数
    private int offlineFailure = 0;  //离线下载网络请求失败次数
    private int offlineError = 0;    //离线下载网络请求错误次数
    private int offlineLoadingImage = 0;  //离线下载的新闻标题图片数
    private int downloadPage = 2;    //每次离线下载的页数

    /**离线下载*/
    public void offlineDownload(){

        myImageLoader = new MyImageLoader(getActivity());
        imageLoader = myImageLoader.getImageLoader();
        options = myImageLoader.getDisplayImageOptions();

        progressDialog();  //离线下载进度框
        offlineFailure = 0;
        offlineError = 0;
        offlineLoadingImage = 0;
        offlineTimes++;
        lastNumber = listItems.size();   //离线下载前的新闻数
        for(int i = 0; i < downloadPage; i++){   //离线下载2页的新闻内容，大概有80条新闻
            page++;
            BaseHttpClient.getInsence().getNewsListByPage("all", String.valueOf(page), offlineDownloadResponse);
        }

    }

    /**离线下载保存数据*/
    private ResponseHandlerInterface offlineDownloadResponse = new HttpDateModel<NewsListEntity>
            (new TypeToken<ResponseEntity<NewsListEntity>>(){}) {
        @Override
        protected void onFailure() {
            offlineFailure++;  //网络请求失败次数加1
            offlinePage++;     //离线下载页数加1
            if(offlineFailure == downloadPage){
                page = page - downloadPage;   //离线下载失败后，将页码定位到失败前的页数
                offlineProgressDialog.hide();
                toastTextView.setText("离线下载失败，请检查网络连接");
                toast.show();
            }
        }

        @Override
        protected void onSuccess(NewsListEntity result) {
            final List<NewsEntity> list = result.getList();  //网络请求返回的数据

            for (int i = 0 ; i < list.size(); i++){
                if(!map.containsKey(list.get(i).getSid())) {  //如果Map中没有该新闻的id，则添加到Map中
                    map.put(list.get(i).getSid(), list.get(i));  //将返回的数据添加到Map中

                    imageLoader.loadImage(list.get(i).getThumb(), options, new SimpleImageLoadingListener() {  //缓存图片

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);

                            offlineLoadingImage++;  //下载图片数加1
                            if(offlineLoadingImage == addNumber - 2){   //图片缓存完成，缓存有时会出错
                                offlineLoadingImage = addNumber;
                                offlineProgressDialog.setTitle("离线下载完成");
                                toastTextView.setText("离线下载了 " + addNumber + " 条资讯");
                                toast.show();

                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        offlineProgressDialog.hide();  //下载完成1秒后隐藏进度框
                                    }
                                }, 1000);

                            }
                            offlineProgressDialog.setProgress(offlineLoadingImage);  //设置下载对话框进度条

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            offlineLoadingImage++;  //下载图片数加1
                            offlineProgressDialog.setProgress(offlineLoadingImage);  //设置下载对话框进度条
                        }

                    });

                }
            }

            offlinePage++;  //离线下载页数加1
            if(offlinePage == offlineTimes * downloadPage){   //当下载完数据后

                listItems.clear();
                listItems.addAll(new ArrayList<NewsEntity>(map.values()));
                mAdapter.notifyDataSetChanged();  //数据集变化后,通知adapter

                currentNumber = listItems.size();  //当前资讯数
                addNumber = currentNumber - lastNumber;  //离线下载增加的资讯数
                offlineProgressDialog.setMax(addNumber);  //设置下载进度框的资讯数量
            }

            //开启子线程将数据保存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<Integer, NewsEntity> tempMap =  newsTitleDatabase.loadMapNewsEntity();  //从数据库读取之前保存的数据
                    for (int i = 0 ; i < list.size(); i++){
                        if(!tempMap.containsKey(list.get(i).getSid())){
                            newsTitleDatabase.saveNewsEntity(list.get(i));  //如果数据库中不存在这个键值id的话，则添加到数据库
                        }
                    }
                }
            }).start();

        }

        @Override
        protected void onError() {
            offlinePage++;  //离线下载页数加1
            offlineError++; //网络请求错误次数加1
            if(offlineError == downloadPage) {
                page = page - downloadPage;   //离线下载错误后，将页码定位到错误前的页数
                toastTextView.setText("离线下载错误, 请重试");
                toast.show();
            }
        }
    };

    ProgressDialog offlineProgressDialog;
    /**离线下载进度条*/
    public void progressDialog(){
        offlineProgressDialog = new ProgressDialog(getActivity());   //实例化
        offlineProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  //设置进度条风格，风格为长形，有刻度的
        offlineProgressDialog.setTitle("离线下载中 . . .");  //设置ProgressDialog 标题
        offlineProgressDialog.setMax(80);
        offlineProgressDialog.show();   //让ProgressDialog显示
    }

    private  Handler clearHandler;
    public void clearCache(){

        /**通知已清除缓存*/
        clearHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x101) {
                    lv.removeFooterView(loadMoreView);  //移除底部自动加载布局
                    mAdapter.notifyDataSetChanged(); //数据集变化后，通知adapter
                    Toast.makeText(getActivity(), "缓存已清除", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                page = 0;  //清除缓存将加载的页码清0
                ImageLoader.getInstance().clearMemoryCache();  // 清除新闻标题图片本地缓存内存缓存
                ImageLoader.getInstance().clearDiskCache();  // 清除新闻标题图片本地缓存
                newsTitleDatabase.deleteDate();  //删除表中的所有数据
                listItems.clear();  //清空新闻列表
                map.clear();

                Message message = clearHandler.obtainMessage();
                message.what = 0x101;
                clearHandler.sendMessage(message);   //告诉主线程执行任务
            }
        }).start();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefPage = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        page = prefPage.getInt("page", 1);   //读取页码, 没有值时为1
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putInt("page", page);     //将页码保存
        editor.commit();
    }
}
