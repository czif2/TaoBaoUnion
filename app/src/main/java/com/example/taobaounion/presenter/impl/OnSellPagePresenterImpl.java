package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.IOnSellCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {
    public static final int DEFAULT_PAGE=1;
    private int mCurrentPage=DEFAULT_PAGE;
    private IOnSellCallback mOnSellCallback=null;
    private final Api mApi;

    public OnSellPagePresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void registerViewCallback(IOnSellCallback callback) {
        this.mOnSellCallback=callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellCallback callback) {
        this.mOnSellCallback=null;
    }

    @Override
    public void getOnSellContent() {
        if (mOnSellCallback != null) {
            mOnSellCallback.onLoading();
        }
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                int code = response.code();
                if (code== HttpURLConnection.HTTP_OK){
                    OnSellContent result = response.body();
                    LogUtils.d(this,"特惠接口数据---->"+result.toString());
                    onSuccess(result);
                }else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                LogUtils.e(this,"特惠出错："+t.toString());
                onError();
            }
        });
    }

    private void onSuccess(OnSellContent result) {
        if (mOnSellCallback != null) {
            try {
                if (isEmpty(result)){
                    onEmpty();
                }else {
                    mOnSellCallback.onContentLoadedSuccess(result);
                }
            }catch (Exception e){
                e.printStackTrace();
                onError();
            }
        }
    }

    private boolean isEmpty(OnSellContent content){
        int size = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        return size==0;
    }

    private void onEmpty(){
        if (mOnSellCallback != null) {
            mOnSellCallback.onEmpty();
        }
    }

    private void onError() {
        if (mOnSellCallback != null) {
            mOnSellCallback.onNetWorkError();
        }
    }

    @Override
    public void reload() {
        this.getOnSellContent();
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                int code = response.code();
                if (code== HttpURLConnection.HTTP_OK){
                    OnSellContent result = response.body();
                    onLoaderMore(result);
                }else {
                    onLoaderMoreError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onLoaderMoreError();
            }
        });
    }

    private void onLoaderMoreError() {
        mCurrentPage--;
        mOnSellCallback.onMoreLoadError();
    }

    private void onLoaderMore(OnSellContent result) {
        if (mOnSellCallback != null) {
            if (isEmpty(result)) {
                mCurrentPage--;
                mOnSellCallback.onMoreLoadError();

            }else {
                mOnSellCallback.onMoreLoad(result);
            }
        }
    }
}
