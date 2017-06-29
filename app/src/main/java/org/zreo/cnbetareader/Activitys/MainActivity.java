package org.zreo.cnbetareader.Activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.zreo.cnbetareader.Fragments.ArrayFragment;
import org.zreo.cnbetareader.Fragments.CollectNewsFragment;
import org.zreo.cnbetareader.Fragments.Comment_Top10Fragment;
import org.zreo.cnbetareader.Fragments.Comment_hot_Fragment;
import org.zreo.cnbetareader.Fragments.DrawerLayoutFragment;
import org.zreo.cnbetareader.Fragments.NewsTitleFragment;
import org.zreo.cnbetareader.Fragments.SettingFragment;
import org.zreo.cnbetareader.R;

/**
 * Created by guang on 2015/7/28.
 * 功能：实现右滑菜单和Toolbar与右滑菜单的关联, 以及管理不同页面的显示
 */
public class MainActivity extends AppCompatActivity implements DrawerLayoutFragment.TabSelectionListener,
                                                               SettingFragment.SetColorListener{

    private FragmentManager fragmentManager;    //用于对Fragment进行管理
    private NewsTitleFragment mNewsTitleFragment;   //全部资讯界面
    private Comment_hot_Fragment mCommentHotFragment;   //精彩评论界面
    private Comment_Top10Fragment mCommentTop10Fragment;    //本月Top10界面
    private CollectNewsFragment mCollectNewsFragment;   //收藏界面
    private ArrayFragment mArrayFragment;   //资讯主题界面
    private SettingFragment mSettingFragment;   //设置界面

    private DrawerLayout mDrawerLayout;   //右滑菜单布局
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    SharedPreferences pref;

    private LinearLayout cnBetaBackground;   //右滑菜单cnbeta文字背景

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        initView();  //初始化右滑菜单布局和Toolbar
        setTabSelection(1);  //显示默认标签页

        //读取设置文件的值
        pref = getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);
        setThemeColor(pref.getInt("theme", 0));    //设置文件里主题的值

    }

    /**
     *  初始化右滑菜单布局和Toolbar
     */
    private void initView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);  //右滑菜单布局
        mToolbar = (Toolbar) findViewById(R.id.toolbar);   //ToolBar布局
        setToolBarTitle(1);
        mToolbar.setTitleTextColor(Color.WHITE);  //设置ToolBar字体颜色为白色
        setSupportActionBar(mToolbar);  //将ToolBar设置为ActionBAr
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //在ToolBar左边，即当前标题前添加图标
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();   //该方法会自动和actionBar关联, 将开关的图片显示在了action上
        mDrawerLayout.setDrawerListener(mDrawerToggle);  //设置drawer的开关监听

        cnBetaBackground = (LinearLayout) findViewById(R.id.cnBeta_background);
    }

    /**设置当前Fragment界面标题*/
    private void setToolBarTitle(int index){
        if(index == 1){
            mToolbar.setTitle(R.string.all_information);  //全部资讯
        }else if(index == 2){
            mToolbar.setTitle(R.string.hot_comment);    //精彩评论
        }else if(index == 3){
            mToolbar.setTitle(R.string.month_top10);    //本月Top10
        }else if(index == 4){
            mToolbar.setTitle(R.string.favorites);      //收藏
        }else if(index == 5){
            mToolbar.setTitle(R.string.topic_theme);   //资讯主题
        }else if(index == 7){
            mToolbar.setTitle(R.string.setting);       //设置
        }
    }

    /**根据传入的index参数来设置选中的tab页*/
    private void setTabSelection(int index){
        FragmentTransaction transaction = fragmentManager.beginTransaction();  // 开启一个Fragment事务
        hideFragments(transaction);  // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        switch (index) {
            case 1:    //全部资讯界面
                if (mNewsTitleFragment == null) {  // 如果mNewsTitleFragment为空，则创建一个并添加到界面上
                    mNewsTitleFragment = new NewsTitleFragment();
                    transaction.add(R.id.fragment_content, mNewsTitleFragment);
                } else{
                    transaction.show(mNewsTitleFragment);  // 如果mNewsTitleFragment不为空，则直接将它显示出来
                }
                break;

            case 2:   //精彩评论界面
                if (mCommentHotFragment == null) {   // 如果mCommentHotFragment为空，则创建一个并添加到界面上
                    mCommentHotFragment = new Comment_hot_Fragment();
                    transaction.add(R.id.fragment_content, mCommentHotFragment);
                } else{
                    transaction.show(mCommentHotFragment);  // 如果mCommentHotFragment不为空，则直接将它显示出来
                }
                break;

            case 3:  //本月Top10界面
                if (mCommentTop10Fragment == null) {  // 如果mCommentTop10Fragment为空，则创建一个并添加到界面上
                    mCommentTop10Fragment = new Comment_Top10Fragment();
                    transaction.add(R.id.fragment_content, mCommentTop10Fragment);
                } else{
                    transaction.show(mCommentTop10Fragment);  // 如果mCommentTop10Fragment不为空，则直接将它显示出来
                }
                break;

            case 4:   //收藏界面
                if (mCollectNewsFragment == null) {  // 如果mCollectNewsFragment为空，则创建一个并添加到界面上
                    mCollectNewsFragment = new CollectNewsFragment();
                    transaction.add(R.id.fragment_content, mCollectNewsFragment);
                } else{
                    transaction.show(mCollectNewsFragment);   //如果mCollectNewsFragment不为空，则直接将它显示出来
                }
                break;
            case 5:   //资讯主题界面 mArrayFragment
                if (mArrayFragment == null) {  // 如果mCommentTop10Fragment为空，则创建一个并添加到界面上
                    mArrayFragment = new ArrayFragment();
                    transaction.add(R.id.fragment_content, mArrayFragment);
                } else{
                    transaction.show(mArrayFragment);  //如果mCommentTop10Fragment不为空，则直接将它显示出来
                }

                break;
            case 7:   //设置界面
                if (mSettingFragment == null) {  // 如果mCommentTop10Fragment为空，则创建一个并添加到界面上
                    mSettingFragment = new SettingFragment();
                    transaction.add(R.id.fragment_content, mSettingFragment);
                } else{
                    transaction.show(mSettingFragment);   // 如果mCommentTop10Fragment不为空，则直接将它显示出来
                }
                break;

            default:
                break;
        }
        transaction.commit();  //提交事务
    }

    /**将所有的Fragment都置为隐藏状态*/
    private void hideFragments(FragmentTransaction transaction){
        if (mNewsTitleFragment != null){
            transaction.hide(mNewsTitleFragment);
        }
        if (mCommentHotFragment != null){
            transaction.hide(mCommentHotFragment);
        }
        if (mCommentTop10Fragment != null){
            transaction.hide(mCommentTop10Fragment);
        }
        if (mCollectNewsFragment != null){
            transaction.hide(mCollectNewsFragment);
        }
        if (mArrayFragment != null){
            transaction.hide(mArrayFragment);
        }
        if (mSettingFragment != null){
            transaction.hide(mSettingFragment);
        }
    }

    /**实现接口，接收DrawerLayoutFragment返回的数据*/
    @Override
    public void selection(int index) {
        setTabSelection(index);
        setToolBarTitle(index);
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);  //点击后关闭右滑菜单
        }
    }

    public void setStatusColor(int color){
        if (Build.VERSION.SDK_INT >= 21) {   //判断系统是否是5.0以上
            getWindow().setStatusBarColor(color); //状态栏颜色
        }
    }

    /**更改主题颜色*/
    @SuppressLint("NewApi")
    public void setThemeColor(int index){
        switch (index){
            case 0:  //蓝色（默认）
                mToolbar.setBackgroundColor(getResources().getColor(R.color.mainColor));  //ActionBar颜色
                cnBetaBackground.setBackgroundColor(getResources().getColor(R.color.mainColor));  //右滑菜单背景
                setStatusColor(getResources().getColor(R.color.mainColor));  //状态栏颜色
                break;
            case 1:  //棕色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.brown));
                cnBetaBackground.setBackgroundColor(getResources().getColor(R.color.brown));
                setStatusColor(getResources().getColor(R.color.brown));
                break;
            case 2:  //橙色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.orange));
                cnBetaBackground.setBackgroundColor(getResources().getColor(R.color.orange));
                setStatusColor(getResources().getColor(R.color.orange));
                break;
            case 3:  //紫色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                cnBetaBackground.setBackgroundColor(getResources().getColor(R.color.purple));
                setStatusColor(getResources().getColor(R.color.purple));
                break;
            case 4:  //绿色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.green));
                cnBetaBackground.setBackgroundColor(getResources().getColor(R.color.green));
                setStatusColor(getResources().getColor(R.color.green));
                break;
            default:  //默认
                mToolbar.setBackgroundColor(getResources().getColor(R.color.mainColor));
                cnBetaBackground.setBackgroundColor(getResources().getColor(R.color.mainColor));
                setStatusColor(getResources().getColor(R.color.mainColor));
                break;
        }

    }

    /**设置右滑菜单状态*/
    public void setLayoutButton(int index){
        ImageView settingImage = (ImageView) findViewById(R.id.setting_image);
        TextView settingText = (TextView) findViewById(R.id.setting_text);
        int themeColor;  //主题颜色值
        switch (index){
            case 0:
                themeColor = R.color.mainColor;
                break;
            case 1:
                themeColor = R.color.brown;
                break;
            case 2:
                themeColor = R.color.orange;
                break;
            case 3:
                themeColor = R.color.purple;
                break;
            case 4:
                themeColor = R.color.green;
                break;
            default:
                themeColor = R.color.mainColor;
                break;
        }
        int color = getResources().getColor(themeColor);
        settingImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_setting, color));
        settingImage.setAlpha(1f);
        settingText.setTextColor(color);
    }

    /**提取图像Alpha位图,更改图片轮廓的颜色*/
    public Bitmap getAlphaBitmap(int resourceImage, int color) {
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),
                resourceImage);
        Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mAlphaBitmap);
        Paint mPaint = new Paint();
        mPaint.setColor(color);
        Bitmap alphaBitmap = mBitmap.extractAlpha();  //从原位图中提取只包含alpha的位图
        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);  //在画布上（mAlphaBitmap）绘制alpha位图
        return mAlphaBitmap;
    }

    /**通过设置界面选择更改颜色*/
    @Override
    public void setColor(int index) {
        setThemeColor(index);
        setLayoutButton(index);
    }

    private long exitTime = 0;
    /**实现再按一次后退键退出应用程序*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                mDrawerLayout.closeDrawer(Gravity.LEFT);  //点击返回键后关闭右滑菜单
            }else if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

