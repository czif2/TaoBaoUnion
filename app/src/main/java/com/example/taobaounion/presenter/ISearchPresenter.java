package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.ISearchViewCallback;

public interface ISearchPresenter extends IBasePresenter<ISearchViewCallback> {

    void getHistory();

    void deleteHistory();

    void doSearch(String keyword);

    void reSearch();

    void loaderMore();

    void getRecommendWords();
}
