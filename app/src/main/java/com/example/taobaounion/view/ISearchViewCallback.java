package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;

import java.util.List;

public interface ISearchViewCallback extends IBaseCallback {
    void onHistoryLoad(Histories history);

    void onHistoryDelete();

    void onSearchSuccess(SearchResult result);

    void onMoreLoaded(SearchResult result);
    void onMoreLoadedError();
    void onMoreLoadedEmpty();

    void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords);
}
