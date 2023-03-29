package com.example.taobaounion.model.domain;

import com.example.taobaounion.base.IBaseInfo;

public interface ILinearItemInfo extends IBaseInfo {
    String getFinalPrise();

    long getCouponAmount();

    int getVolume();
}
