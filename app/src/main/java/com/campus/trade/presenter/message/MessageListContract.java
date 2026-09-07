package com.campus.trade.presenter.message;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.Conversation;

import java.util.List;

/**
 * 会话列表契约
 */
public interface MessageListContract {

    interface View extends BaseView {
        void onConversations(List<Conversation> conversations);
    }

    interface Presenter {
        void load();
    }
}
