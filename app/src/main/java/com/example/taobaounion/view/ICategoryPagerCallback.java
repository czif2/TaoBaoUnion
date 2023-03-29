package com.example.taobaounion.view;

import com.example.taobaounion.model.domain.HomePagerContent;

import java.util.List;

public interface ICategoryPagerCallback {
    //数据加载回来
    void onContentLoaded(List<HomePagerContent.DataBean> contents);

    int getCategoryId();

    void onLoading();

    void onError();

    void onEmpty();

    void onLoadMoreError();

    void onLoadMoreEmpty();

    void onLoadMoreLoaded(List<HomePagerContent.DataBean > contents);

    void onLooperListLoaded(List<HomePagerContent.DataBean > contents);
}
