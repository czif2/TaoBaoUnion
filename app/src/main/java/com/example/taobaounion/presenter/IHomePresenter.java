package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.IHomeCallback;

public interface IHomePresenter extends IBasePresenter<IHomeCallback> {
    void getCategories();//获取商品分类
}
