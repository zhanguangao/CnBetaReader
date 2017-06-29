package org.zreo.cnbetareader.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import org.zreo.cnbetareader.Activitys.NewsActivity;
import org.zreo.cnbetareader.Adapters.HotComment_Adapter;
import org.zreo.cnbetareader.AppConfig;
import org.zreo.cnbetareader.Database.HotCommentDatabase;
import org.zreo.cnbetareader.Entitys.HotCommentItemEntity;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.Entitys.ResponseEntity;
import org.zreo.cnbetareader.Model.Net.HttpDateModel;
import org.zreo.cnbetareader.Net.BaseHttpClient;
import org.zreo.cnbetareader.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

/**
 * Created by guo on 2015/7/29.
 */
public class Comment_hot_Fragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
    Toast toast;
    TextView toastTextView;

    View view;  //当前布局
    private ListView hot_listView;
    private List<HotCommentItemEntity> cnComment_hotList; //= new ArrayList<HotCommentItemEntity>();
    // ListView item项，以降序排序的新闻列表
    Map<Integer, HotCommentItemEntity> map = new TreeMap<Integer, HotCommentItemEntity>(new Comparator<Integer>() {  //将获取到的新闻列表排序
        @Override
        public int compare(Integer lhs, Integer rhs) {
            return rhs.compareTo(lhs);   // 降序排序, 默认为升序
        }
    });
    HotComment_Adapter mAdapter;

    private int visibleLastIndex = 0;    //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private View loadMoreView;         //加载更多布局
    private TextView loadMoreText;    //加载提示文本


    SwipeRefreshLayout swipeLayout;  //下拉刷新控件

    private HotCommentDatabase hotCommentDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_c_hot_listview, container, false);

        hotCommentDatabase = HotCommentDatabase.getInstance(getActivity());  //初始化数据库实例

        customToast();

        initListItem();

        return view;
    }

    /**初始化列表*/
    public void initListItem(){
        map = hotCommentDatabase.loadMapHotComment();  //从数据库读取新闻列表 map跟listItems同步
        cnComment_hotList = new ArrayList<>(map.values());
        if(cnComment_hotList.size() > 0){
            initView();  //初始化布局
        }else{
            BaseHttpClient.getInsence().getNewsListByPage("jhcomment", "1", initResponse);
        }
    }


    private ResponseHandlerInterface initResponse = new HttpDateModel<List<HotCommentItemEntity>>
                            (new TypeToken<ResponseEntity<List<HotCommentItemEntity>>>() {}) {
        @Override
        protected void onSuccess(List<HotCommentItemEntity> result) {
            /**
             * 补充实体类中的信息
             */
            for (HotCommentItemEntity item:result){
                Matcher hotMatcher = AppConfig.HOT_COMMENT_PATTERN.matcher(item.getDescription());
                if (hotMatcher.find()) {
                    item.setFrom(hotMatcher.group(1));
                    item.setDescription(hotMatcher.group(2));
                    item.setSid(Integer.parseInt(hotMatcher.group(3)));
                    item.setNewstitle(hotMatcher.group(4));
                }
            }

            for(int i = 0; i < result.size(); i++){
                map.put(result.get(i).getSid(), result.get(i));
            }
            cnComment_hotList = new ArrayList<HotCommentItemEntity>(map.values());
            if(cnComment_hotList.size() > 0){
                initView();  //初始化布局
            }

            //开启子线程将数据保存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0 ; i < cnComment_hotList.size(); i++){
                        hotCommentDatabase.saveHotComment(cnComment_hotList.get(i));  //将数据保存到数据库
                    }
                }
            }).start();

        }

        @Override
        protected void onError() {
            toastTextView.setText("刷新错误");
            toast.show();
        }

        @Override
        protected void onFailure() {
            toastTextView.setText("刷新失败，请检查网络");
            toast.show();
        }
    };

    /**
     * 初始化布局
     */
    private void initView() {
        /**显示hot的ListView*/
        hot_listView = (ListView) view.findViewById(R.id.hot_listView);
        /**为ListView创建自定义适配器*/
        mAdapter = new HotComment_Adapter(getActivity(), R.layout.fragment_c_hot_textview, cnComment_hotList);
        hot_listView.setVerticalScrollBarEnabled(false);//隐藏ListView滑动进度条
        loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_more);
        hot_listView.addFooterView(loadMoreView);   //设置列表底部视图
        hot_listView.setOnScrollListener(this);     //添加滑动监听
        hot_listView.setAdapter(mAdapter);  //为ListView绑定Adapter

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_hot);
        swipeLayout.setOnRefreshListener(this);
        //设置刷新时动画的颜色，可以设置4个
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        hot_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NewsEntity entity = new NewsEntity();
                entity.setSid(cnComment_hotList.get(position).getSid());
                entity.setTitle(cnComment_hotList.get(position).getNewstitle());

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

        if(cnComment_hotList.size() > 0) {
            if((System.currentTimeMillis() - exitTime) < 2000) {   //当5秒内再次刷新时执行
                toastTextView.setText("刚刚刷新过，等下再试吧");
                toast.show();
            }else {
                BaseHttpClient.getInsence().getNewsListByPage("jhcomment", "1", refreshResponse);
            }
        }
        exitTime = System.currentTimeMillis();

        swipeLayout.setRefreshing(false);   //加载完数据后，隐藏刷新进度条
    }

    private int lastNumber = 0;  //更新数据前的新闻数
    private int currentNumber = 0;  //当前新闻数
    private int addNumber = 0; //每次刷新或加载增加的数据

    private ResponseHandlerInterface refreshResponse = new HttpDateModel<List<HotCommentItemEntity>>
            (new TypeToken<ResponseEntity<List<HotCommentItemEntity>>>() {}) {
        @Override
        protected void onSuccess(List<HotCommentItemEntity> result) {
            /**
             * 补充实体类中的信息
             */
            for (HotCommentItemEntity item:result){
                Matcher hotMatcher = AppConfig.HOT_COMMENT_PATTERN.matcher(item.getDescription());
                if (hotMatcher.find()) {
                    item.setFrom(hotMatcher.group(1));
                    item.setDescription(hotMatcher.group(2));
                    item.setSid(Integer.parseInt(hotMatcher.group(3)));
                    item.setNewstitle(hotMatcher.group(4));
                }
            }

            lastNumber = map.size();   //更新数据前的新闻数
            //Toast.makeText(getActivity(), lastNumber+" " , Toast.LENGTH_SHORT).show();

            for(int i = 0; i < result.size(); i++){
                if(!map.containsKey(result.get(i).getSid()))  //如果Map中没有该新闻的id，则添加到Map中
                map.put(result.get(i).getSid(), result.get(i));  //将返回的数据添加到Map中
            }

            if(map.size() > lastNumber)    //当列表有更新
            {
                cnComment_hotList.clear();
                cnComment_hotList.addAll(new ArrayList<HotCommentItemEntity>(map.values()));
                mAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter

                currentNumber = cnComment_hotList.size();  //当前新闻数
                addNumber = currentNumber - lastNumber;  //每次刷新增加的数据
                toastTextView.setText("新增 " + addNumber + " 条评论");

                //开启子线程将数据保存到数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<Integer, HotCommentItemEntity> tempMap =  hotCommentDatabase.loadMapHotComment();  //从数据库读取之前保存的数据
                        for (int i = 0 ; i < cnComment_hotList.size(); i++){
                            if(!tempMap.containsKey(cnComment_hotList.get(i).getSid())) {
                                hotCommentDatabase.saveHotComment(cnComment_hotList.get(i));  //将数据保存到数据库
                            }
                        }
                    }
                }).start();

            } else {
                toastTextView.setText("没有更多评论了");
            }

            toast.show();

        }

        @Override
        protected void onError() {
            toastTextView.setText("刷新错误");
            toast.show();
        }

        @Override
        protected void onFailure() {
            toastTextView.setText("刷新失败，请检查网络");
            toast.show();
        }
    };

    private boolean isAutoload = true;  //是否加载
    private int page; //传入页数，第一页为最新的，依次类推
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = cnComment_hotList.size() - 1;    //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
        if(isAutoload){
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                loadMoreText.setText("加载中...");
                page++;
                BaseHttpClient.getInsence().getNewsListByPage("jhcomment", String.valueOf(page), autoLoadResponse);
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }

    private ResponseHandlerInterface autoLoadResponse = new HttpDateModel<List<HotCommentItemEntity>>
            (new TypeToken<ResponseEntity<List<HotCommentItemEntity>>>() {}) {
        @Override
        protected void onSuccess(List<HotCommentItemEntity> result) {
            /**
             * 补充实体类中的信息
             */
            for (HotCommentItemEntity item:result){
                Matcher hotMatcher = AppConfig.HOT_COMMENT_PATTERN.matcher(item.getDescription());
                if (hotMatcher.find()) {
                    item.setFrom(hotMatcher.group(1));
                    item.setDescription(hotMatcher.group(2));
                    item.setSid(Integer.parseInt(hotMatcher.group(3)));
                    item.setNewstitle(hotMatcher.group(4));
                }
            }

            lastNumber = map.size();   //更新数据前的新闻数

            for(int i = 0; i < result.size(); i++){
                if(!map.containsKey(result.get(i).getSid()))  //如果Map中没有该新闻的id，则添加到Map中
                    map.put(result.get(i).getSid(), result.get(i));  //将返回的数据添加到Map中
            }

            if(map.size() > lastNumber)    //当列表有更新
            {
                cnComment_hotList.clear();
                cnComment_hotList.addAll(new ArrayList<HotCommentItemEntity>(map.values()));
                mAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter

                currentNumber = cnComment_hotList.size();  //当前新闻数
                addNumber = currentNumber - lastNumber;  //每次刷新增加的数据
                toastTextView.setText("新增 " + addNumber + " 条评论");

                //开启子线程将数据保存到数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<Integer, HotCommentItemEntity> tempMap =  hotCommentDatabase.loadMapHotComment();  //从数据库读取之前保存的数据
                        for (int i = 0 ; i < cnComment_hotList.size(); i++){
                            if(!tempMap.containsKey(cnComment_hotList.get(i).getSid())) {
                                hotCommentDatabase.saveHotComment(cnComment_hotList.get(i));  //将数据保存到数据库
                            }
                        }
                    }
                }).start();

            } else {
                toastTextView.setText("没有更多评论了");
                loadMoreText.setText("没有更多评论了");
                isAutoload = false;
            }
            toast.show();


        }

        @Override
        protected void onError() {
            toastTextView.setText("刷新错误");
            toast.show();
        }

        @Override
        protected void onFailure() {
            toastTextView.setText("刷新失败，请检查网络");
            toast.show();
        }
    };



    /**
     * 自定义Toast，用于数据更新的提示
     */
    private void customToast() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);  //获取屏幕分辨率
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(dm.widthPixels, dm.heightPixels / 15);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefPage = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        page = prefPage.getInt("hot_page", 1);   //读取页码, 没有值时为1
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putInt("hot_page", page);     //将页码保存
        editor.commit();
    }


    /*public void initComment_hotList() {
        String title = "有些人会说没有什么是这城里人不会的。就是比你会，信不信由你！";
        String newstitle = "城里人真会玩";
        String description ="nJohn";
        //String first = "有";
        for (int i = 1; i < 20; i++) {

            HotCommentItemEntity hotCommentItemEntitys = new HotCommentItemEntity();
            hotCommentItemEntitys.setNewstitle("评论于  " + newstitle);
            hotCommentItemEntitys.setTitle(title);
            hotCommentItemEntitys.setDescription(description + " ");
           // cnComment_hots.setFirstWord(firstWord);
            cnComment_hotList.add(hotCommentItemEntitys);
        }
    }*/
}


