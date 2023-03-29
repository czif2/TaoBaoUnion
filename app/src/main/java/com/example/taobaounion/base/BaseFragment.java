package com.example.taobaounion.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taobaounion.R;
import com.example.taobaounion.utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private State currentState=State.NONE;
    private View mLoadingView;
    private View mSuccessView;
    private View mErrorView;
    private View mEmptyView;

    public enum State{
        NONE,LOADING,SUCCESS,ERROR,EMPTY
    }

    private Unbinder mBind;
    private FrameLayout mBaseContainer;

    @OnClick(R.id.network_error_tips)
    public void retry(){
        //点击重新加载内容
        LogUtils.d(this,"retry");
        onRetryClick();
    }

    protected void onRetryClick() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = loadRootView(inflater,container);
        mBaseContainer = rootView.findViewById(R.id.base_container);
        loadStatesView(inflater,container);
        mBind = ButterKnife.bind(this, rootView);
        initView(rootView);
        initListener();
        initPresenter();
        loadData();

        return rootView;
    }

    protected void initListener() {

    }

    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_fragment_layout, container, false);
    }

    private void loadStatesView(LayoutInflater inflater, ViewGroup container) {
        //加载各种状态view
        //成功的View
        mSuccessView = loadSuccessView(inflater, container);
        mBaseContainer.addView(mSuccessView);

        //loading的view
        mLoadingView = loadLoadingView(inflater, container);
        mBaseContainer.addView(mLoadingView);

        //错误页面
        mErrorView = loadErrorView(inflater, container);
        mBaseContainer.addView(mErrorView);

        //内容为空页面
        mEmptyView = loadEmptyView(inflater, container);
        mBaseContainer.addView(mEmptyView);
        
        setUpStates(State.NONE);
    }

    protected View loadErrorView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error,container,false);
    }
    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_empty,container,false);
    }


    //子类通过这个方法来切换页面
    public void setUpStates(State state){
        this.currentState=state;
       mSuccessView.setVisibility(currentState==State.SUCCESS? View.VISIBLE: View.GONE);
       mLoadingView.setVisibility(currentState==State.LOADING? View.VISIBLE: View.GONE);
       mErrorView.setVisibility(currentState==State.ERROR? View.VISIBLE: View.GONE);
       mEmptyView.setVisibility(currentState==State.EMPTY? View.VISIBLE: View.GONE);
    }

   protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        //加载loading界面
        return inflater.inflate(R.layout.fragment_loading,container,false);

    }

    protected void initView(View rootView) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBind!=null) {
            mBind.unbind();
        }
        release();
    }

    protected void release() {

    }

    protected void initPresenter() {

    }

    protected void loadData() {

    }


    protected  View loadSuccessView(LayoutInflater inflater, ViewGroup container){
        int resId=getRootViewResId();
        return inflater.inflate(resId,container,false);
    }

    protected abstract int getRootViewResId();
}
