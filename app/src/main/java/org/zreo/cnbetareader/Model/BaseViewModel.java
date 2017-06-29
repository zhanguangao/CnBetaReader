package org.zreo.cnbetareader.Model;


import android.app.Activity;
import android.view.View;

import org.zreo.cnbetareader.Model.Net.BaseDateModel;

/**
 * Created by zqh on 2015/8/1  11:17.
 * Email:zqhkey@163.com
 * 基础View接口，
 */
public interface BaseViewModel <E,DataModel extends BaseDateModel<E>>{
    void onResume();
    void onPause();
    void onDestroy();
    void assumeView(View view);
    void LoadData(boolean startup);
    Activity getActivity();
    void setDataModel(DataModel model);
    void setActivity(Activity activity);
}
