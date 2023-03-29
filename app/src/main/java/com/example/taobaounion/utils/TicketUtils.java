package com.example.taobaounion.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.taobaounion.base.BaseApplication;
import com.example.taobaounion.base.IBaseInfo;
import com.example.taobaounion.presenter.ITicketPresenter;
import com.example.taobaounion.ui.activity.TicketActivity;

public class TicketUtils {

    public static void toTicketPage(Context context,IBaseInfo baseInfo){
        String title=baseInfo.getTitle();
        String url=baseInfo.getUrl();
        if (TextUtils.isEmpty(url)){
            url=baseInfo.getUrl();
        }
        String cover=baseInfo.getCover();
        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
        ticketPresenter.getTicket(title,url,cover);
        context.startActivity(new Intent(context, TicketActivity.class));
    }
}
