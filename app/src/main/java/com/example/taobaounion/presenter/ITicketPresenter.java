package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.ITicketCallback;

public interface ITicketPresenter extends IBasePresenter<ITicketCallback> {
    void getTicket(String title,String url,String cover);
}
