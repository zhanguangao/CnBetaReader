package org.zreo.cnbetareader.Model.Net;

import android.app.Activity;
import android.graphics.Bitmap;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.zreo.cnbetareader.AppConfig;
import org.zreo.cnbetareader.Entitys.NewsEntity;
import org.zreo.cnbetareader.MyApplication;
import org.zreo.cnbetareader.Net.BaseHttpClient;

import java.util.regex.Matcher;

/**
 * Created by zqh on 2015/7/31  21:37.
 * Email:zqhkey@163.com
 * 基础的WebHttp连接抽象模型
 */
public  class BaseWebHttpModel extends BaseDateModel<String> {
    public BaseWebHttpModel(Activity activity) {
        setActivity(activity);

    }

    private TextHttpResponseHandler httpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mCallBack != null) {
                mCallBack.LoadFialure();
            }
        }

        @Override
        public void onStart() {
            if (mCallBack != null) {
                mCallBack.LoadStart();
            }
        }

        @Override
        public void onFinish() {
            if (mCallBack != null) {
                mCallBack.LoadFinish();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            if (mCallBack != null) {
                mCallBack.LoadSuccess(responseString);
            }
        }
    };

    public void loadWebData(int sid) {
        BaseHttpClient.getInsence().getNewsDetialBySid(sid, httpResponseHandler);
    }
    public static boolean handleResponceString(NewsEntity item,String resp,boolean shouldCache){
        return handleResponceString(item, resp,shouldCache,false);
    }

    public static boolean handleResponceString(NewsEntity item,String resp,boolean shouldCache,boolean cacheImage){
        Document doc = Jsoup.parse(resp);
        Elements newsHeadlines = doc.select(".body");
        item.setFrom(newsHeadlines.select(".where").html());
        item.setInputtime(newsHeadlines.select(".date").html());
        Elements introduce = newsHeadlines.select(".introduction");
        introduce.select("div").remove();
        item.setHometext(introduce.html());
        Elements content = newsHeadlines.select(".content");
        if(cacheImage){
            Elements images = content.select("img");
            for(Element image:images){
                Bitmap img = ImageLoader.getInstance().loadImageSync(image.attr("src"), MyApplication.getDefaultDisplayOption());
                if(img!=null) {
                    img.recycle();
                }
            }
        }
        item.setContent(content.html());
        Matcher snMatcher = AppConfig.SN_PATTERN.matcher(resp);
        if (snMatcher.find())
            item.setSN(snMatcher.group(1));
        if(item.getContent()!=null&&item.getContent().length()>0){
            if(shouldCache) {
//                FileCacheKit.getInstance().put(item.getSid() + "", Toolkit.getGson().toJson(item));
            }
            return true;
        }else{
            return false;
        }
    }

}
