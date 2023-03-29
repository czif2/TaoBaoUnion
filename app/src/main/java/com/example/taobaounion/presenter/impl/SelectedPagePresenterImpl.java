package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.presenter.ISelectedPagePresenter;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.util.ArrayList;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {
    private ISelectedPageCallback mViewCallback =null;
    private int[] cId={1,2,3,4,5};

    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback =callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback=null;
    }

    @Override
    public void getCategories() {
        for (int i=0;i<5;i++){

        }
    }

    @Override
    public void getContentByCategory(SelectedPageCategory.DataBean item) {

    }

    @Override
    public void reloadContent() {

    }
}
