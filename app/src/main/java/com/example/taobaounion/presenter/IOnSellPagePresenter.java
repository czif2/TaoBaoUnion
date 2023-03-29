package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.IOnSellCallback;

public interface IOnSellPagePresenter extends IBasePresenter<IOnSellCallback> {
    void getOnSellContent();

    void reload();

    void loaderMore();
}
