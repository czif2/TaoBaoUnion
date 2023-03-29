package com.example.taobaounion.base;

import com.example.taobaounion.view.IHomeCallback;

public interface IBasePresenter<T> {

    //通知ui注册接口
    void registerViewCallback(T callback);

    //取消ui通知接口
    void unregisterViewCallback(T callback);

}
