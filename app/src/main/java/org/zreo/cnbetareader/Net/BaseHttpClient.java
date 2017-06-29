package org.zreo.cnbetareader.Net;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHeader;
import org.zreo.cnbetareader.AppConfig;

import java.util.Locale;

/**
 * Created by zqh on 2015/7/29  22:21.
 * Email:zqhkey@163.com
 * 所有数据连接方式
 */
public class BaseHttpClient {
    private static BaseHttpClient client;
    private AsyncHttpClient asyncHttpClient;
    private SyncHttpClient syncHttpClient;
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    public static BaseHttpClient getInsence( ) {
        if (client == null) {
            client = new BaseHttpClient();
        }
        return client;
    }

    /**
     * 初始化阶段进行属性设置
     */
    private BaseHttpClient( ) {

        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(5000);//响应时间
        asyncHttpClient.setConnectTimeout(3000);//连接时间
        asyncHttpClient.setCookieStore(new BasicCookieStore());
        asyncHttpClient.setMaxRetriesAndTimeout(3, 3000);
        asyncHttpClient.setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.45 Safari/537.36");
        syncHttpClient = new SyncHttpClient();
    }

    /**
     * 根据时间，类型，页数，获取相应的帖子
     *
     * @param Type            传入类型
     * @param Page            传入页数
     * @param responsehandler 响应接口返回
     */
    public void getNewsListByPage(String Type, String Page, ResponseHandlerInterface responsehandler) {
        RequestParams params = new RequestParams();
        params.put("page", Page);
        params.put("type", Type);
        params.put("_", System.currentTimeMillis() + "");
        Log.e(AppConfig.APP_NET_DEBUG, "Request_" + "params" + params.toString() + "_" + "URL" + AppConfig.NEWS_LIST_URL);
        asyncHttpClient.get(null, AppConfig.NEWS_LIST_URL, ClientHeader(), params, responsehandler);
    }

    public void getCommentBySnAndSid(String sn, String sid, ResponseHandlerInterface handlerInterface) {
        RequestParams params = new RequestParams();
        params.add("op", "1," + sid + "," + sn);
        asyncHttpClient.post(null, AppConfig.COMMENT_URL, ClientHeader(), params, CONTENT_TYPE, handlerInterface);
    }

    public void getNewsDetialBySid(int sid, ResponseHandlerInterface responseHandlerInterface) {
        String Url = String.format(Locale.CANADA, AppConfig.ARTICLE_URL, sid);
        asyncHttpClient.get(Url, responseHandlerInterface);
    }
    /**
     * 链接头部
     *
     * @return
     */
    private Header[] ClientHeader() {
        return new Header[]{
                new BasicHeader("Referer", "http://www.cnbeta.com/"),
                new BasicHeader("Origin", "http://www.cnbeta.com"),
                new BasicHeader("X-Requested-With", "XMLHttpRequest")
        };

    }

}
