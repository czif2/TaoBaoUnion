package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.model.domain.TicketParams;
import com.example.taobaounion.model.domain.TicketResult;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ITicketCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {

    private ITicketCallback mViewCallback=null;
    private String mCover=null;
    private TicketResult mTicketResult=null;

    enum LoadState{
        LOADING,SUCCESS,ERROR,NONE
    }

    private LoadState currentState=LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {


        this.onTicketLoading();

        this.mCover=cover;


        String targetUrl = UrlUtils.getTicketUrl(url);

        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(targetUrl,title);
        LogUtils.d(this,"sssssss->"+targetUrl);
        LogUtils.d(this,"sssssss->"+title);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {



            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(this,"tic_code---->"+code);
                if (code== HttpURLConnection.HTTP_OK){
                    mTicketResult = response.body();
                    LogUtils.d(this,"tic_body--->"+ mTicketResult.toString());

                    //通知ui更新

                    onLoadSuccess();
                }else {
                    onLoadedError();

                }
            }



            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {

                onLoadedError();
            }
        });
    }

    private void onLoadSuccess() {
        if (mViewCallback != null) {
            mViewCallback.onTicketLoaded(mCover, mTicketResult);
        }else {
            currentState=LoadState.SUCCESS;
        }

    }

    private void onLoadedError() {
        if (mViewCallback != null) {
            mViewCallback.onNetWorkError();
        }else {
            currentState=LoadState.ERROR;
        }
    }


    @Override
    public void registerViewCallback(ITicketCallback callback) {
        if (currentState!=LoadState.NONE){
            //状态已经改变
            if (currentState==LoadState.SUCCESS){
                onLoadSuccess();
            }else if(currentState==LoadState.ERROR){
                onLoadedError();
            }else if(currentState==LoadState.LOADING){
                onTicketLoading();
            }
        }
        this.mViewCallback=callback;
    }

    private void onTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }else {
            currentState=LoadState.LOADING;
        }
    }

    @Override
    public void unregisterViewCallback(ITicketCallback callback) {
        this.mViewCallback=null;
    }


}
