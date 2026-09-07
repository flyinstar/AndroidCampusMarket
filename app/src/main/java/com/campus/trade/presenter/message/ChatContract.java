package com.campus.trade.presenter.message;

import com.campus.trade.base.BaseView;
import com.campus.trade.model.entity.ChatMessage;

import java.util.List;

/**
 * 聊天窗口契约（含轮询）
 */
public interface ChatContract {

    interface View extends BaseView {
        /** 历史消息（倒序分页返回，前端前插；isInitial=true 为首屏） */
        void showHistory(List<ChatMessage> messages, boolean isInitial);

        void showNewMessages(List<ChatMessage> messages);

        void onSendSuccess(ChatMessage message);
    }

    interface Presenter {
        void init(int targetUserId);

        void startPolling();

        void stopPolling();

        void sendText(String content);

        void loadMoreHistory();
    }
}
