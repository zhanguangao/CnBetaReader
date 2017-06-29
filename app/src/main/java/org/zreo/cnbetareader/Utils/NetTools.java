package org.zreo.cnbetareader.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import org.zreo.cnbetareader.MyApplication;

/**
 * Created by zqh on 2015/7/30  09:20.
 * Email:zqhkey@163.com
 */
public class NetTools {
    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * 检测是否Wifi连接
     *
     * @return
     */
    public static boolean isWifiConnected() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.getMyApplicationInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isNetWorkConnected() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.getMyApplicationInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        } else {
            return false;
        }
    }
}
