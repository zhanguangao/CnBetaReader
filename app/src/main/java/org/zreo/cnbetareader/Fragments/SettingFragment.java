package org.zreo.cnbetareader.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;

import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.zreo.cnbetareader.R;
import org.zreo.cnbetareader.Utils.FileKit;

import java.io.File;


public class SettingFragment extends PreferenceFragment {

    /**
     * 定义更改主题配色的接口
     */
    public interface SetColorListener{
        void setColor(int index);
    }
    String path = "/data/data/org.zreo.cnbetareader/cache";
    File file=new File("/data/data/org.zreo.cnbetareader/cache");
  //  private static  String cache ="/data/data/org.zreo.cnbetareader/cache";
    private int themeid;
    private Toolbar mToolbar;
    private Handler clearHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pre_setting);
        Preference preference = findPreference(getString(R.string.clean_cache_key));
        preference.setSummary(getFileSize());
        Preference theme = findPreference("theme");
        theme.setOnPreferenceClickListener(onPreferenceClickListener);
        preference.setOnPreferenceClickListener(onPreferenceClickListener);
        View view = getActivity().getLayoutInflater().inflate(R.layout.toolbar, null); //获取布局
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);   //ToolBar布局
        mToolbar.setTitleTextColor(Color.RED);  //设置ToolBar字体颜色为白色
    }

   Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("theme")) {
                final String[] items = new String[]{"蓝色", "棕色", "橙色", "紫色", "绿色"};
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("主题配色");
                alert.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE).edit();
                        editor.putInt("theme", which);     //将页码保存
                        editor.commit();
                        SetColorListener listener = (SetColorListener) getActivity();
                        listener.setColor(which);
                    }
                });
                alert.show();   //显示对话框
            }

            else{
                clearCache();

            }
        return false;}
   };

     public void clearCache(){

        /**通知已清除缓存*/
        clearHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x101) {
                    Toast.makeText(getActivity(), "缓存已清除", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageLoader.getInstance().clearMemoryCache();  // 清除新闻标题图片本地缓存内存缓存
                ImageLoader.getInstance().clearDiskCache();  // 清除新闻标题图片本地缓存
               // getActivity().deleteDatabase("NewsEntity");  //删除数据库
                FileKit.deleteFiles(file);//删除cache文件夹
                Message message = clearHandler.obtainMessage();
                message.what = 0x101;
                clearHandler.sendMessage(message);   //告诉主线程执行任务
            }
        }).start();

    }


      private String getFileSize() {
        long size = 0;
        size += FileKit.getFolderSize(path);
        return Formatter.formatFileSize(getActivity(), size);
    }

}





