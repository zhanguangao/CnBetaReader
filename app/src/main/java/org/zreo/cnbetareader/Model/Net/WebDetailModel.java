package org.zreo.cnbetareader.Model.Net;

import android.app.Activity;

import org.zreo.cnbetareader.Model.BaseViewModel;

/**
 * Created by zqh on 2015/8/1  10:43.
 * Email:zqhkey@163.com
 * 基础WebView抽象模型
 */
public abstract class WebDetailModel<T, WebData extends BaseDateModel<T>> implements LoadFeekBackInterface<T>,BaseViewModel<T, WebData> {
    protected Activity mActivity;
    protected WebData DataModel;
    public WebDetailModel(WebData model) {
        model.setCallBack(this);
        DataModel=model;
    }

    @Override
    public void setActivity(Activity activity) {
        mActivity=activity;
    }

    @Override
    public void setDataModel(WebData model) {
        model.setCallBack(this);
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }
}
