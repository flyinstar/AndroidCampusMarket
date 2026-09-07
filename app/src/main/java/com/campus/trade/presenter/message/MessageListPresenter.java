package com.campus.trade.presenter.message;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.Conversation;
import com.campus.trade.model.repository.MessageRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.List;

/**
 * 会话列表 Presenter
 */
public class MessageListPresenter extends BasePresenter<MessageListContract.View>
        implements MessageListContract.Presenter {

    private final MessageRepository mRepository;

    public MessageListPresenter() {
        this.mRepository = new MessageRepository();
    }

    @Override
    public void load() {
        mRepository.conversations(new ApiCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> data) {
                MessageListContract.View v = getView();
                if (v != null) {
                    v.onConversations(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                MessageListContract.View v = getView();
                if (v != null) {
                    v.showError(msg);
                }
            }
        });
    }
}
