package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.Histories;
import com.example.taobaounion.model.domain.SearchRecommend;
import com.example.taobaounion.model.domain.SearchResult;
import com.example.taobaounion.presenter.ISearchPresenter;
import com.example.taobaounion.utils.JsonCacheUtils;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.view.ISearchViewCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPresenter {

    private final Api mApi;
    private ISearchViewCallback mSearchViewCallback=null;
    private int mCurrentPage=1;
    private String mCurrentKeyWord=null;
    private final JsonCacheUtils mJsonCacheUtils;

    public SearchPresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtils = JsonCacheUtils.getInstance();
    }

    @Override
    public void registerViewCallback(ISearchViewCallback callback) {
        this.mSearchViewCallback=callback;
    }

    @Override
    public void unregisterViewCallback(ISearchViewCallback callback) {
        this.mSearchViewCallback=null;
    }

    @Override
    public void getHistory() {
        Histories histories=mJsonCacheUtils.getValue("key_histories",Histories.class);
        if (mSearchViewCallback!=null){
            mSearchViewCallback.onHistoryLoad(histories);
        }
    }

    @Override
    public void deleteHistory() {
        mJsonCacheUtils.deleteCache("key_histories");
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onHistoryDelete();
        }

    }

    private void saveHistory(String history){

        Histories histories = mJsonCacheUtils.getValue("key_histories", Histories.class);
        //如果已经存在，就干掉，再添加
        List<String> historiesList=null;
        if (histories!=null&&histories.getHistories()!=null){
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        //处理没有数据的情况
        if (historiesList==null){
            historiesList=new ArrayList<>();
        }
        if (histories==null){
            histories=new Histories();

        }
        histories.setHistories(historiesList);

        //对个数进行限制
        if (historiesList.size()>10){
            historiesList=historiesList.subList(0,10);
        }

        //添加记录
        historiesList.add(history);

        mJsonCacheUtils.saveCache("key_histories",histories);

    }

    @Override
    public void doSearch(String keyword) {
        if (mCurrentKeyWord == null || !mCurrentKeyWord.equals(keyword)) {
            this.mCurrentKeyWord=keyword;
            this.saveHistory(keyword);
        }

        if (mSearchViewCallback != null) {
            mSearchViewCallback.onLoading();
        }
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.code()==HttpURLConnection.HTTP_OK){
                    handleSearchResult(response.body());
                    LogUtils.d(this,"搜索结果---->"+response.body().toString());
                }else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void handleSearchResult(SearchResult body) {
        if (mSearchViewCallback != null) {
            if (isResultEmpty(body)){
                mSearchViewCallback.onEmpty();
            }else {
                mSearchViewCallback.onSearchSuccess(body);
            }
        }
    }

    private boolean isResultEmpty(SearchResult result){
        try{
            return result==null||result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size()==0;
        }catch (Exception e){

            e.printStackTrace();
            return false;
        }
    }

    private void onError() {
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onNetWorkError();
        }
    }

    @Override
    public void reSearch() {
        if (mCurrentKeyWord==null){
            if (mSearchViewCallback != null) {
                mSearchViewCallback.onEmpty();
            }
        }else {
            this.doSearch(mCurrentKeyWord);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        if (mCurrentKeyWord==null) {
            if (mSearchViewCallback != null) {
                mSearchViewCallback.onEmpty();
            }
        }else {
            doSearchMore();
        }
    }

    private void doSearchMore() {
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, mCurrentKeyWord);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.code()==HttpURLConnection.HTTP_OK){
                    handleMoreSearchResult(response.body());
                }else {
                    onLoaderMoreError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onLoaderMoreError();
            }
        });
    }

    private void handleMoreSearchResult(SearchResult body) {
        if (mSearchViewCallback != null) {
            if (isResultEmpty(body)){
                mSearchViewCallback.onMoreLoadedEmpty();
            }else {
                mSearchViewCallback.onMoreLoaded(body);
            }
        }
    }

    private void onLoaderMoreError() {
        mCurrentPage--;
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onMoreLoadedError();
        }
    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                if (response.code()== HttpURLConnection.HTTP_OK){
                    if (mSearchViewCallback != null) {
                        mSearchViewCallback.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
