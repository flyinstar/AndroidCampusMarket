package com.campus.trade.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campus.trade.R;
import com.campus.trade.base.BaseActivity;
import com.campus.trade.model.entity.ChatMessage;
import com.campus.trade.model.repository.MessageRepository;
import com.campus.trade.presenter.message.ChatContract;
import com.campus.trade.presenter.message.ChatPresenter;
import com.campus.trade.ui.adapter.ChatAdapter;
import com.campus.trade.utils.SharedPrefUtils;

import java.util.List;

/**
 * 聊天窗口：进入开始轮询、离开停止轮询
 */
public class ChatActivity extends BaseActivity<ChatContract.View, ChatPresenter> implements ChatContract.View {

    private static final String EXTRA_TARGET_ID = "target_id";
    private static final String EXTRA_TARGET_NAME = "target_name";

    private TextView tvTitle;
    private RecyclerView rvMessages;
    private EditText etInput;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mTargetUserId;
    private boolean mMarkedOnce;

    public static Intent newIntent(Context context, int targetUserId, String nickname) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_TARGET_ID, targetUserId);
        intent.putExtra(EXTRA_TARGET_NAME, nickname);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected ChatPresenter createPresenter() {
        return new ChatPresenter();
    }

    @Override
    protected void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        rvMessages = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        mAdapter = new ChatAdapter();
        mAdapter.setMyUserId(SharedPrefUtils.getUserId(this));
        mLayoutManager = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(mLayoutManager);
        rvMessages.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String content = etInput.getText().toString();
            mPresenter.sendText(content);
            etInput.setText("");
        });
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 2) {
                    mPresenter.loadMoreHistory();
                }
            }
        });
    }

    @Override
    protected void initData() {
        mTargetUserId = getIntent().getIntExtra(EXTRA_TARGET_ID, 0);
        String name = getIntent().getStringExtra(EXTRA_TARGET_NAME);
        tvTitle.setText(name == null ? "聊天" : name);
        mPresenter.init(mTargetUserId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.startPolling();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.stopPolling();
        }
    }

    @Override
    public void showHistory(List<ChatMessage> messages, boolean isInitial) {
        if (messages == null) {
            return;
        }
        if (isInitial) {
            mAdapter.replaceInitial(messages);
            if (mAdapter.getSize() > 0) {
                rvMessages.scrollToPosition(mAdapter.getSize() - 1);
            }
            // 打开会话即把对方消息标已读
            if (!mMarkedOnce) {
                mMarkedOnce = true;
                new MessageRepository().markAsRead(mTargetUserId, null);
            }
        } else {
            int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
            int anchor = firstVisible <= 0 ? 0 : firstVisible;
            mAdapter.prependOlder(messages);
            if (anchor >= 0) {
                rvMessages.scrollToPosition(anchor + messages.size());
            }
        }
    }

    @Override
    public void showNewMessages(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        boolean nearBottom = mLayoutManager.findLastVisibleItemPosition()
                >= mAdapter.getSize() - 2;
        mAdapter.append(messages);
        if (nearBottom) {
            rvMessages.scrollToPosition(mAdapter.getSize() - 1);
        }
        // 对方发来消息即标记已读
        new MessageRepository().markAsRead(mTargetUserId, null);
    }

    @Override
    public void onSendSuccess(ChatMessage message) {
        mAdapter.appendOne(message);
        rvMessages.scrollToPosition(mAdapter.getSize() - 1);
    }
}
