package org.zreo.cnbetareader.Fragments;



import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.zreo.cnbetareader.Activitys.NewsActivity;
import org.zreo.cnbetareader.Adapters.CollectNews_Adapter;
import org.zreo.cnbetareader.Database.CollectionDatabase;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.R;

import java.util.List;

/**
 * 收藏
 */
public class CollectNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    private ListView collectnew_listview;
    private List<NewsEntity> CollectNewsList; //= new ArrayList<NewsEntity>();
    CollectNews_Adapter cnsAdapter;

    private View loadMoreView;     //加载更多布局
    private TextView loadMoreText;    //加载提示文本
    SwipeRefreshLayout swipeLayout;  //下拉刷新控件
    TextView hintText;

    private CollectionDatabase collectionDatabase;   //数据库

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_collectnews_listview, container, false);
        collectionDatabase = CollectionDatabase.getInstance(getActivity());  //初始化数据库实例
        initCollectNewsList();  //初始化收藏列表

        return view;
    }

    /**
     *初始化收藏列表
     */
   private void  initCollectNewsList(){

       CollectNewsList = collectionDatabase.loadCollection();//从数据库读取收藏列表
       initView();
       if (CollectNewsList.size() == 0) {      //如果数据库没数据
           hintText.setVisibility(View.VISIBLE);  //显示没有收藏新闻的提示
           collectnew_listview.setVisibility(View.GONE);  //隐藏ListView
       }

   }
    /**
     *初始化布局
     */
    private void initView(){
        collectnew_listview = (ListView) view.findViewById(R.id.collectnews_listview);
        cnsAdapter = new CollectNews_Adapter(getActivity(), R.layout.collect_news, CollectNewsList);
        loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more, null);
        loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_more);
        loadMoreText.setBackgroundColor(getResources().getColor(R.color.gray));
        loadMoreText.setText("-- The End --");
        loadMoreText.setTextColor(Color.DKGRAY);
        collectnew_listview.addFooterView(loadMoreView);   //设置列表底部视图
        collectnew_listview.setAdapter(cnsAdapter);   //为ListView绑定Adapter
        hintText = (TextView) view.findViewById(R.id.hint_text);   //当没有收藏时提示的文本
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.collectnews_list);
        swipeLayout.setOnRefreshListener(this);
        //设置刷新时动画的颜色，可以设置4个
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        collectnew_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsEntity entity = CollectNewsList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("NewsItem", entity);

                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        /**长按弹出取消收藏对话框*/
        collectnew_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final NewsEntity entity = CollectNewsList.get(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.cancel_collection);     //取消收藏
                alert.setCancelable(true);  //为真时可以通过返回键取消
                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        collectionDatabase.deleteCollection(entity);

                        CollectNewsList.clear();
                        CollectNewsList.addAll(collectionDatabase.loadCollection());
                        cnsAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), R.string.cancelled_collection, Toast.LENGTH_SHORT).show();  //已取消收藏

                        if(CollectNewsList.size() == 0) {    //当收藏列表为空时
                            hintText.setVisibility(View.VISIBLE);  //显示没有收藏新闻的提示
                            collectnew_listview.setVisibility(View.GONE);  //隐藏ListView
                        }
                    }
                });
                alert.setNegativeButton("取消", null);
                alert.show();   //显示对话框
                return true;
            }
        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        List<NewsEntity> temp = collectionDatabase.loadCollection();
        if(temp.size() != CollectNewsList.size()){  //如果收藏列表改变
            CollectNewsList.clear();
            CollectNewsList.addAll(temp);

            hintText.setVisibility(View.GONE);   //隐藏没有收藏新闻的提示
            collectnew_listview.setVisibility(View.VISIBLE);    //显示ListView

        }else if(temp.size() == 0) {    //当收藏列表为空时
            hintText.setVisibility(View.VISIBLE);  //显示没有收藏新闻的提示
            collectnew_listview.setVisibility(View.GONE);  //隐藏ListView
        }
        cnsAdapter.notifyDataSetChanged();
    }

    @Override
       public void onRefresh() {

        CollectNewsList.clear();
        CollectNewsList.addAll(collectionDatabase.loadCollection());
        if (CollectNewsList.size() > 0) {      //如果数据库没数据
            hintText.setVisibility(View.GONE);
            collectnew_listview.setVisibility(View.VISIBLE);
        } else {
            hintText.setVisibility(View.VISIBLE);
            collectnew_listview.setVisibility(View.GONE);
        }
        cnsAdapter.notifyDataSetChanged();

        swipeLayout.setRefreshing(false);   //加载完数据后，隐藏刷新进度条
    }


}



