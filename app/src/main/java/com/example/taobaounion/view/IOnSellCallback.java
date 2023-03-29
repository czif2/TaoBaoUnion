package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.OnSellContent;

public interface IOnSellCallback extends IBaseCallback {
    void onContentLoadedSuccess(OnSellContent result);


    void onMoreLoad(OnSellContent moreResult);


    void onMoreLoadError();
}
