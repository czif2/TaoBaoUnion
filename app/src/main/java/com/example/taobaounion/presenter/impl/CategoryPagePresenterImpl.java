package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagePresenterImpl implements ICategoryPagerPresenter {

    private Map<Integer,Integer>pageInfo=new HashMap<>();
    private Integer mCurrentPage;

    private List<ICategoryPagerCallback> callbacks=new ArrayList<>();
    @Override
    public void registerViewCallback(ICategoryPagerCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(ICategoryPagerCallback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId()==categoryId){
                callback.onLoading();
            }

        }

        //根据id加载内容

        Integer targetPage= pageInfo.get(categoryId);
        if (targetPage==null){
            targetPage=1;
            pageInfo.put(categoryId,1);
        }
        Call<HomePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
                LogUtils.d(this,"code--->"+code);
                if (code== HttpURLConnection.HTTP_OK){
                    HomePagerContent homePagerContent=response.body();
//                    LogUtils.d(this,"pageContent-->"+homePagerContent);

                    //更新UI
                    handlerHomePageContentResult(homePagerContent,categoryId);
                }else {
                    handlerNetWorkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.e(this,"请求错误"+t);
                handlerNetWorkError(categoryId);
            }
        });
    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        String homePageUrl = UrlUtils.createHomePageUrl(categoryId, targetPage);
//        LogUtils.d(this,homePageUrl);
        Call<HomePagerContent> task = api.getHomePageContent(homePageUrl);
        return task;
    }

    private void handlerNetWorkError(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId()==categoryId) {
                callback.onError();
            }
        }
    }

    private void handlerHomePageContentResult(HomePagerContent homePagerContent, int categoryId) {
        List<HomePagerContent.DataBean> data = homePagerContent.getData();
        //通知ui层更新数据
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId()==categoryId){
                if (homePagerContent==null||homePagerContent.getData().size()==0){
                    callback.onEmpty();
                }else {
                    List<HomePagerContent.DataBean> looperData = data.subList(data.size() - 5, data.size());
                    callback.onLooperListLoaded(looperData);
                    callback.onContentLoaded(data);
                }
            }
        }
    }

    @Override
    public void loaderMore(int categoryId) {
        //加载更多内容
        //1.拿到当前页码
        mCurrentPage = pageInfo.get(categoryId);
        //2.页码增加
        mCurrentPage++;
        pageInfo.put(categoryId,mCurrentPage);
        //3.加载数据
        Call<HomePagerContent> task = createTask(categoryId, mCurrentPage);
        //4.处理数据结果
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
//                LogUtils.d(this,"LoadCode-->"+code);
                if (code==HttpURLConnection.HTTP_OK){
                    HomePagerContent result = response.body();
                    //LogUtils.d(this,"loadMore-->"+result.toString());
                    handleLoaderResult(result,categoryId);
                }else {
                    handleLoadMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.e(this,t.toString());
                handleLoadMoreError(categoryId);
            }
        });
    }

    private void handleLoaderResult(HomePagerContent result, int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId()==categoryId) {
                if (result==null||result.getData().size()==0) {
                    callback.onLoadMoreEmpty();
                }else {
                    callback.onLoadMoreLoaded(result.getData());
                }
            }
        }
    }

    private void handleLoadMoreError(int categoryId) {
        mCurrentPage--;
        pageInfo.put(categoryId,mCurrentPage);
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId()==categoryId) {
                callback.onLoadMoreError();
            }
        }
    }

    @Override
    public void reLoad(int categoryId) {

    }
}
