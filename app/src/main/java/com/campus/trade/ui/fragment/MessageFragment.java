package com.campus.trade.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.campus.trade.R;
import com.campus.trade.base.BaseFragment;
import com.campus.trade.model.entity.Conversation;
import com.campus.trade.presenter.message.MessageListContract;
import com.campus.trade.presenter.message.MessageListPresenter;
import com.campus.trade.ui.activity.ChatActivity;
import com.campus.trade.ui.adapter.ConversationAdapter;

import java.util.List;

/**
 * 消息页：会话列表
 */
public class MessageFragment extends BaseFragment<MessageListContract.View, MessageListPresenter>
        implements MessageListContract.View, ConversationAdapter.OnConversationClickListener {

    private ConversationAdapter mAdapter;
    private SwipeRefreshLayout srl;
    private View llEmpty;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected MessageListPresenter createPresenter() {
        return new MessageListPresenter();
    }

    @Override
    protected void initViews(View view) {
        RecyclerView rv = view.findViewById(R.id.rv_conversations);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ConversationAdapter();
        mAdapter.setListener(this);
        rv.setAdapter(mAdapter);
        srl = view.findViewById(R.id.srl_messages);
        llEmpty = view.findViewById(R.id.ll_empty);
    }

    @Override
    protected void initListeners() {
        srl.setOnRefreshListener(() -> mPresenter.load());
    }

    @Override
    protected void initData() {
        mPresenter.load();
    }

    @Override
    public void onConversations(List<Conversation> conversations) {
        if (!isAdded()) {
            return;
        }
        srl.setRefreshing(false);
        mAdapter.setData(conversations);
        boolean empty = mAdapter.isEmpty();
        llEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        Context ctx = getContext();
        if (ctx == null) {
            return;
        }
        startActivity(ChatActivity.newIntent(ctx,
                conversation.getTargetUserId(), conversation.getNickname()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.load();
        }
    }
}
