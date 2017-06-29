package org.zreo.cnbetareader.Model.Net;

/**
 * Created by zqh on 2015/7/31  22:45.
 * Email:zqhkey@163.com
 */
public interface LoadFeekBackInterface<T> {

    void LoadStart();
    void LoadSuccess(T object);
    void LoadFialure();

    void LoadFinish();
}
