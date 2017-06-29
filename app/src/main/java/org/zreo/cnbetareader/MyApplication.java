package org.zreo.cnbetareader;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.zreo.cnbetareader.Utils.FileCacheKit;

import java.io.File;

/**
 * Created by Administrator on 2015/7/28.
 */
public class MyApplication extends Application {
    private static MyApplication mApplicationInstance;
    private static MyApplication instance;
   // private static LoadImage instance;
    public static DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    public void onCreate() {
        FileCacheKit.getInstance(this);
        super.onCreate();
        mApplicationInstance=this;
        initImageLoader(getApplicationContext());

    }
    public void initImageLoader(Context context) {

        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        ImageLoader.getInstance().init(configuration);

    }
    public static DisplayImageOptions getDefaultDisplayOption() {
        return options;
    }
    public File getInternalCacheDir() {
        return super.getCacheDir();
    }
    public static MyApplication getInstance() {
        return instance;
    }
    public static Application getMyApplicationInstance() {
        return mApplicationInstance;
    }

    public File getCacheDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return getExternalCacheDir();
        } else {
            return super.getCacheDir();
        }
    }
}
