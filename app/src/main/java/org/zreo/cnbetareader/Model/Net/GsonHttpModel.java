package org.zreo.cnbetareader.Model.Net;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.zreo.cnbetareader.AppConfig;
import org.zreo.cnbetareader.Utils.NetTools;

import java.lang.reflect.Type;

/**
 * Created by zqh on 2015/7/30  09:13.
 * Email:zqhkey@163.com
 * 这里只做判断，不进行处理
 */
public abstract class GsonHttpModel<T> extends TextHttpResponseHandler {

    private Type type;

    public GsonHttpModel(TypeToken<T> typeToken) {
        type=typeToken.getType();
    }

    @Override
    public final void onSuccess(int statusCode, Header[] headers, String responseString) {
        Log.e(AppConfig.APP_NET_DEBUG,"Result_"+ "statusCode=" + statusCode + "_" + "response" + responseString);
        if (statusCode == 200) {
            try {

                T model = NetTools.getGson().fromJson(responseString, type);

                if (model == null) {
                    onFailure(statusCode, responseString, new RuntimeException("response is empty"));
                }

                onSuccess(statusCode, model);
            } catch (Exception e) {
                onError(statusCode, responseString, e);
            }
        } else {
            onFailure(statusCode, responseString, new Exception("statusCode not match"));
        }

    }

    @Override
    public final void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.e(AppConfig.APP_NET_DEBUG,"Result-Failure_"+ "statusCode=" + statusCode + "_" + "response" + responseString);
        onFailure(statusCode, responseString, throwable);
    }

    protected abstract void onFailure(int sttusCode,String responseString ,Throwable throwable);
    protected abstract void onError(int sttusCode,String responseString ,Throwable throwable);

    protected abstract void onSuccess(int sattusCode,T model);
}
