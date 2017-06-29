package org.zreo.cnbetareader.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.zreo.cnbetareader.Activitys.MainActivity;
import org.zreo.cnbetareader.R;
import org.zreo.cnbetareader.Utils.MaterialRippleLayout;

/**
 * Created by guang on 2015/7/23. */

public class DrawerLayoutFragment extends Fragment implements View.OnClickListener{

    /**
     * 定义监控右滑菜单点击选项的接口
     */
    public interface TabSelectionListener{
        void selection(int index);
    }

    /**
     * 右滑菜单的按钮
     */
    private RelativeLayout mBtnInformation;
    private RelativeLayout mBtnComment;
    private RelativeLayout mBtnHot;
    private RelativeLayout mBtnFavorites;
    private RelativeLayout mBtnTopic;
    private RelativeLayout mBtnWeb;
    private RelativeLayout mBtnSetting;

    /**
     * 右滑菜单的按钮的图片
     */
    private ImageView informationImage;
    private ImageView commentImage;
    private ImageView hotImage;
    private ImageView favoritesImage;
    private ImageView topicImage;
    private ImageView webImage;
    private ImageView settingImage;

    /**
     * 右滑菜单的按钮的文字
     */
    private TextView informationText;
    private TextView commentText;
    private TextView hotText;
    private TextView favoritesText;
    private TextView topicText;
    private TextView webText;
    private TextView settingText;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_drawer_layout, container, false); //获取布局
        initViews(); //初始化布局
        return view;
    }

    /**
     * 初始化布局
     */
    public void initViews() {
        mBtnInformation = (RelativeLayout) view.findViewById(R.id.btn_information);
        mBtnComment = (RelativeLayout) view.findViewById(R.id.btn_comment);
        mBtnHot = (RelativeLayout) view.findViewById(R.id.btn_hot);
        mBtnFavorites = (RelativeLayout) view.findViewById(R.id.btn_favorites);
        mBtnTopic = (RelativeLayout) view.findViewById(R.id.btn_topic);
        mBtnWeb = (RelativeLayout) view.findViewById(R.id.btn_web);
        mBtnSetting = (RelativeLayout) view.findViewById(R.id.btn_setting);

        mBtnInformation.setOnClickListener(this);
        mBtnComment.setOnClickListener(this);
        mBtnHot.setOnClickListener(this);
        mBtnFavorites.setOnClickListener(this);
        mBtnTopic.setOnClickListener(this);
        mBtnWeb.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);

        materialRipple(mBtnInformation);
        materialRipple(mBtnComment);
        materialRipple(mBtnHot);
        materialRipple(mBtnFavorites);
        materialRipple(mBtnTopic);
        materialRipple(mBtnWeb);
        materialRipple(mBtnSetting);

        informationImage = (ImageView) view.findViewById(R.id.information_image);
        commentImage = (ImageView) view.findViewById(R.id.comment_image);
        hotImage = (ImageView) view.findViewById(R.id.hot_image);
        favoritesImage = (ImageView) view.findViewById(R.id.favorites_image);
        topicImage = (ImageView) view.findViewById(R.id.topic_image);
        webImage = (ImageView) view.findViewById(R.id.web_image);
        settingImage = (ImageView) view.findViewById(R.id.setting_image);

        informationText = (TextView) view.findViewById(R.id.information_text);
        commentText = (TextView) view.findViewById(R.id.comment_text);
        hotText = (TextView) view.findViewById(R.id.hot_text);
        favoritesText = (TextView) view.findViewById(R.id.favorites_text);
        topicText = (TextView) view.findViewById(R.id.topic_text);
        webText = (TextView) view.findViewById(R.id.web_text);
        settingText = (TextView) view.findViewById(R.id.setting_text);

        resetLayoutButton();
        setLayoutButton(1);
    }

    /**实现点击水波纹效果*/
    public void materialRipple(View view){
        MaterialRippleLayout.on(view)
                .rippleColor(Color.GRAY)
                .rippleAlpha(0.2f)
                        //.rippleHover(true)
                .create();
    }


    /**
     * 初始化右滑菜单
     **/
    public void resetLayoutButton(){
        informationImage.setImageResource(R.mipmap.drawer_information);
        commentImage.setImageResource(R.mipmap.drawer_comment);
        hotImage.setImageResource(R.mipmap.drawer_hot);
        favoritesImage.setImageResource(R.mipmap.drawer_favorites);
        topicImage.setImageResource(R.mipmap.drawer_topic);
        webImage.setImageResource(R.mipmap.drawer_web);
        settingImage.setImageResource(R.mipmap.drawer_setting);
        informationImage.setAlpha(0.5f);
        commentImage.setAlpha(0.5f);
        hotImage.setAlpha(0.5f);
        favoritesImage.setAlpha(0.5f);
        topicImage.setAlpha(0.5f);
        webImage.setAlpha(0.5f);
        settingImage.setAlpha(0.5f);
        informationText.setTextColor(Color.GRAY);
        commentText.setTextColor(Color.GRAY);
        hotText.setTextColor(Color.GRAY);
        favoritesText.setTextColor(Color.GRAY);
        topicText.setTextColor(Color.GRAY);
        webText.setTextColor(Color.GRAY);
        settingText.setTextColor(Color.GRAY);
    }

    /**
     * 设置右滑菜单状态
     **/
    public void setLayoutButton(int index){
        //读取设置文件的值
        SharedPreferences pref = getActivity().getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);
        int theme = pref.getInt("theme", 0);    //设置文件里主题的值
        int themeColor;  //主题颜色值
        switch (theme){
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
        int color = getActivity().getResources().getColor(themeColor);

        switch (index){
            case 1:
                informationImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_information, color));
                informationImage.setAlpha(1f);
                informationText.setTextColor(color);
                break;
            case 2:
                commentImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_comment, color));
                commentImage.setAlpha(1f);
                commentText.setTextColor(color);
                break;
            case 3:
                hotImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_hot, color));
                hotImage.setAlpha(1f);
                hotText.setTextColor(color);
                break;
            case 4:
                favoritesImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_favorites, color));
                favoritesImage.setAlpha(1f);
                favoritesText.setTextColor(color);
                break;
            case 5:
                topicImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_topic, color));
                topicImage.setAlpha(1f);
                topicText.setTextColor(color);
                break;
            case 6:
                webImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_web, color));
                webImage.setAlpha(1f);
                webText.setTextColor(color);
                break;
            case 7:
                settingImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_setting, color));
                settingImage.setAlpha(1f);
                settingText.setTextColor(color);
                break;
            default:
                informationImage.setImageBitmap(getAlphaBitmap(R.mipmap.drawer_information, color));
                informationImage.setAlpha(1f);
                informationText.setTextColor(color);
                break;
        }
    }

    /**提取图像Alpha位图,更改图片轮廓的颜色*/
    public Bitmap getAlphaBitmap(int resourceImage, int color) {
        //BitmapDrawable mBitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.enemy_infantry_ninja);
        //Bitmap mBitmap = mBitmapDrawable.getBitmap();
        //BitmapDrawable的getIntrinsicWidth（）方法，Bitmap的getWidth（）方法
        //注意这两个方法的区别
        //Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmapDrawable.getIntrinsicWidth(), mBitmapDrawable.getIntrinsicHeight(), Config.ARGB_8888);

        Bitmap mBitmap = BitmapFactory.decodeResource(getActivity().getResources(),
                resourceImage);
        Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(mAlphaBitmap);
        Paint mPaint = new Paint();

        mPaint.setColor(color);
        //从原位图中提取只包含alpha的位图
        Bitmap alphaBitmap = mBitmap.extractAlpha();
        //在画布上（mAlphaBitmap）绘制alpha位图
        mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);

        return mAlphaBitmap;
    }


    @Override
    public void onClick(View v) {
        TabSelectionListener listener = (TabSelectionListener) getActivity();
        resetLayoutButton();
        switch (v.getId()) {
            case R.id.btn_information:
                listener.selection(1);  //选择全部资讯
                setLayoutButton(1);
                break;
            case R.id.btn_comment:
                listener.selection(2);  //选择精彩评论界面
                setLayoutButton(2);
                break;
            case R.id.btn_hot:
                listener.selection(3);   //选择全本月Top10界面
                setLayoutButton(3);
                break;
            case R.id.btn_favorites:
                listener.selection(4);   //选择收藏界面
                setLayoutButton(4);
                break;
            case R.id.btn_topic:
                listener.selection(5);  //选择资讯主题界面
                setLayoutButton(5);
                break;
            case R.id.btn_web:
                mBtnWebAction();
                setLayoutButton(6);
                break;
            case R.id.btn_setting:    //选择设置界面
                listener.selection(7);
                setLayoutButton(7);
                break;
            default:
                break;
        }
    }

    /**“手机版网站”按钮的操作*/
    public void mBtnWebAction() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("是否调用浏览器打开 cnBeta.COM 手机版网站？");   //设置要显示的内容
        alert.setCancelable(true);  //为真时可以通过返回键取消
        alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://m.cnbeta.com"));
                startActivity(intent);
            }
        });
        alert.setNegativeButton("取消", null);
        alert.show();   //显示对话框
    }


}

