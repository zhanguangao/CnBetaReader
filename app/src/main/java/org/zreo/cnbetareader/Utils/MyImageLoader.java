package org.zreo.cnbetareader.Utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.zreo.cnbetareader.R;

/**
 * Created by guang on 2015/8/4.
 */
public class MyImageLoader {

    private ImageLoader imageLoader;  //图片加载器对象
    private DisplayImageOptions options;  ////显示图片的配置
    private Context mContext;

    /**构造函数初始化*/
    public MyImageLoader(Context context){
        mContext = context;
        initImageLoader();
    }

    public ImageLoader getImageLoader(){
        return imageLoader;
    }

    public DisplayImageOptions getDisplayImageOptions(){
        return options;
    }

    /**初始化图片加载器*/
    public void  initImageLoader(){

        imageLoader = ImageLoader.getInstance();  //获取图片加载器对象
        //File cacheDir = StorageUtils.getOwnCacheDirectory(mContext, "imageloader/Cache");   //自定义缓存路径
        //File cacheDir = StorageUtils.getCacheDirectory(mContext);   //缓存文件夹路径

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions 内存缓存文件的最大长宽
                .threadPoolSize(5) // default  线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
                .memoryCacheSizePercentage(13) // default
                //.diskCache(new UnlimitedDiskCache(cacheDir)) // default 可以自定义缓存路径
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
                .diskCacheFileCount(100)  // 可以缓存的文件数量
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())  // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
                .build(); //开始构建

        imageLoader.init(config);   //Initialize ImageLoader with configuration.

        //显示图片的配置
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.news_title_default_image) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.news_title_default_image) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.news_title_default_image) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中  //开启缓存后默认会缓存到外置SD卡如下地址(/sdcard/Android/data/[package_name]/cache).
                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                        //.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                        //.displayer(new SimpleBitmapDisplayer()) // default  还可以设置圆角图片new RoundedBitmapDisplayer(20)
                .build();

        //ImageSize mImageSize = new ImageSize(100, 100);
        //imageLoader.displayImage(imageUrl, viewHolder.imageView, options);

        /*public void onClearMemoryClick(View view) {
            Toast.makeText(this, 清除内存缓存成功, Toast.LENGTH_SHORT).show();
            ImageLoader.getInstance().clearMemoryCache();  // 清除内存缓存
        }

        public void onClearDiskClick(View view) {
            Toast.makeText(this, 清除本地缓存成功, Toast.LENGTH_SHORT).show();
            ImageLoader.getInstance().clearDiskCache();  // 清除本地缓存
        }*/
    }
}
