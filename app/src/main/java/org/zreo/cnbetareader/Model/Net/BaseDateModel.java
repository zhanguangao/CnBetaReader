package org.zreo.cnbetareader.Model.Net;

import android.app.Activity;

/**
 * Created by zqh on 2015/7/31  22:43.
 * Email:zqhkey@163.com
 * 基础数据抽象模型
 */
public abstract  class BaseDateModel<T> {
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity=activity;
    }
    protected LoadFeekBackInterface<T> mCallBack;

    public void setCallBack(LoadFeekBackInterface<T> callBack) {
        mCallBack=callBack;
    }

}
