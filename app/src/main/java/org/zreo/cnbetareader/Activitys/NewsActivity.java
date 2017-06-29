package org.zreo.cnbetareader.Activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import org.zreo.cnbetareader.Database.CollectionDatabase;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.Model.Net.BaseWebHttpModel;
import org.zreo.cnbetareader.Model.Net.NewsWebDetailModel;
import org.zreo.cnbetareader.R;

import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


public class NewsActivity extends AppCompatActivity implements OnGestureListener{
    private Toolbar mToolbar;
    ImageButton imageButton;//悬浮按钮
    WebView webView;//网页视图
    WebSettings webSettings;//设置字体大小
    ButtonOnclick buttonOnclick;//字体大小选择对话框的监听
    GestureDetector gestureDetector;//手势识别器
    private String[] textsize1 = new String[]{"大", "中", "小"};//字号
    private NewsWebDetailModel newsWebDetailModel;//数据来源
    private NewsEntity mentity;//获取方法的包装类
    private boolean isExist;//判断是否收藏
    CollectionDatabase collectionDatabase;


    /*
     *一个页面里放入了一个webview组件，并将其组件铺满屏幕，全屏幕除了下面的导航栏其余 都是这个webview，
     * 后来我想在webview中触发滑动手势的onfling方法，在webview还没加载完网页内容之前正常，可是 webview加载完网页之后，就无法触发方法了，
     *一般我们用于接收GestureDetector对象的方法是OnTouchevent();,而在View组件占用了屏幕空间之后，这个方法就无 效了，只有换成 dispatchTouchEvent方法才有效！
     */
//    public boolean dispatchTouchEvent(MotionEvent ev) {    //注意这里不能用ONTOUCHEVENT方法，不然无效的
//        gestureDetector.onTouchEvent(ev);
//        webView.onTouchEvent(ev);//这几行代码也要执行，将webview载入MotionEvent对象一下，况且用载入把，不知道用什么表述合适
//        return super.dispatchTouchEvent(ev);
//    }
//以上代码去掉注释可实现右滑返回功能

    private class ButtonOnclick implements DialogInterface.OnClickListener {//创建字体选择单选对话框的监听类
        private int index;

        public ButtonOnclick(int index) {
            this.index = index;
        }
        public void setDefault(ButtonOnclick buttonOnclick){
            buttonOnclick.index=1;
        }


        public void onClick(DialogInterface dialogInterface, int whichButton) {
            //whichButton表示单击的按钮索引，所有列表项的索引都是大于等于0的，然而按钮的索引都是<0的！
            if (whichButton >= 0) {
                index = whichButton; //单击的是列表项
                // dialog.cancel();
            } else {
                if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                    if (index == 0) {
                        webSettings.setTextSize(WebSettings.TextSize.LARGER);
                    } else if (index == 1) {
                        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
                    } else {
                        webSettings.setTextSize(WebSettings.TextSize.SMALLER);
                    }
                } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {

                }

            }
        }

    }

    public void init(){


        ShareSDK.initSDK(this);
        //gestureDetector=new GestureDetector(this);
        buttonOnclick=new ButtonOnclick(1);
        webView = (WebView) findViewById(R.id.wv);
        webSettings = webView.getSettings();//用来设置webview所显示的网页的字体大小
        imageButton = (ImageButton) findViewById(R.id.imageBtn);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);   //ToolBar布局
        String title=mentity.getTitle();
        mToolbar.setTitle(title);   // 标题的文字需在setSupportActionBar之前，不然会无效
        mToolbar.setTitleTextColor(Color.WHITE);  //设置ToolBar字体颜色为白色
        mToolbar.setBackgroundColor(getResources().getColor(R.color.mainColor));
        setSupportActionBar(mToolbar);  //将ToolBar设置为ActionBAr
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //在ToolBar左边，即当前标题前添加图标
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SharedPreferences pref = getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);
        setThemeColor(pref.getInt("theme", 0));    //设置文件里主题的值

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*只有当加载完成页面后才能进行点击跳转，不然无法获取数据*/
                Intent intent5 = new Intent(NewsActivity.this, CommentActivity.class);
                Bundle NewsItem=new Bundle();
                NewsItem.putSerializable("NewsItem",mentity);
                intent5.putExtras(NewsItem);
                startActivity(intent5);
            }
        });
//        webView.loadUrl("http://m.cnbeta.com");
//        webView.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlloading(WebView webView, String url) {
//                webView.loadUrl(url);
//                return false;
//            }
//
//            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//                return super.shouldOverrideKeyEvent(view, event);
//            }
//
//        });
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键时的操作
//
//
//                         webView.goBack();   //后退
//
//                        //webview.goForward();//前进
//                        return true;    //已处理
//                    }
//                }
//                return false;
//            }
//        });
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mentity = (NewsEntity)getIntent().getExtras().getSerializable("NewsItem");//获取封装了数据的NewEntity对象
        init();//对组件进行初始化
        newsWebDetailModel = new NewsWebDetailModel(new BaseWebHttpModel(this));
        newsWebDetailModel.setActivity(this);
        newsWebDetailModel.InitView(webView, mentity, this, imageButton);
        newsWebDetailModel.LoadData(true);


    }

    Menu menu1;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        menu1 = menu;
        collectionDatabase = CollectionDatabase.getInstance(this);  //初始化数据库实例
        Map<Integer, NewsEntity> tempMap =  collectionDatabase.loadMapCollection();  //从数据库读取之前保存的数据
        isExist = tempMap.containsKey(mentity.getSid());  //判断数据库是否已收藏选中的资讯
        if(!isExist){
            menu1.findItem(R.id.qxsc).setVisible(false);
            menu1.findItem(R.id.sc).setVisible(true);
            //如果收藏数据库中不存在这个键值id的话
            // alert.setTitle("收藏选中的资讯");
        }else{
            menu1.findItem(R.id.qxsc).setVisible(true);
            menu1.findItem(R.id.sc).setVisible(false);
            // alert.setTitle("删除收藏");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int sid=mentity.getSid();
        switch (item.getItemId()) {
            case R.id.refresh:
                Toast.makeText(getApplicationContext(), "刷新中", Toast.LENGTH_SHORT).show();
                webView.loadUrl("http://m.cnbeta.com/view_"+sid+".htm");
                return true;
          case R.id.qxsc:
                   collectionDatabase.deleteCollection(mentity);
                   Toast.makeText(NewsActivity.this, "已删除收藏", Toast.LENGTH_SHORT).show();
              menu1.findItem(R.id.qxsc).setVisible(false);
              menu1.findItem(R.id.sc).setVisible(true);
                //Toast.makeText(getApplicationContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sc:
                    collectionDatabase.saveCollection(mentity);  //如果数据库中不存在这个键值id的话，则添加到数据库
                    Toast.makeText(NewsActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                menu1.findItem(R.id.qxsc).setVisible(true);
                menu1.findItem(R.id.sc).setVisible(false);
                return true;
            case R.id.font:
                buttonOnclick.setDefault(buttonOnclick);
                new AlertDialog.Builder(this).setTitle("请选择字体的大小")
                        .setSingleChoiceItems(textsize1, 1,buttonOnclick).
                                setPositiveButton("确定",buttonOnclick).setNegativeButton("取消",buttonOnclick).create().show();
                return true;
            case R.id.browser:
                //Toast.makeText(getApplicationContext(), "打开浏览器", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://m.cnbeta.com/view_"+sid+".htm");
                intent.setData(content_url);
                startActivity(intent);
                return true;
            case R.id.action_share:
                showShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    //实现右滑返回
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() < -100) {
            Intent intent=new Intent(NewsActivity.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);//设置动画
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return newsWebDetailModel.onKeyDown(keyCode, event)|| super.onKeyDown(keyCode, event);
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }


}
