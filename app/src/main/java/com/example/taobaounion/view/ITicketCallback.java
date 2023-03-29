package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.TicketResult;

public interface ITicketCallback extends IBaseCallback {
    void onTicketLoaded(String cover, TicketResult result);
}
