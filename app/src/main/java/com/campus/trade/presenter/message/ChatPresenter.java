package com.campus.trade.presenter.message;

import android.os.Handler;
import android.os.Looper;

import com.campus.trade.base.BasePresenter;
import com.campus.trade.model.entity.ChatMessage;
import com.campus.trade.model.repository.MessageRepository;
import com.campus.trade.network.callback.ApiCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天 Presenter：3 秒轮询拉取新消息（文档 4.7 消息轮询实现）
 */
public class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    private static final long POLL_INTERVAL = 3000L;
    private static final int HISTORY_PAGE_SIZE = 20;

    private final MessageRepository mRepository;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private int mTargetUserId;
    private boolean mPolling = false;
    private boolean mLoadingHistory = false;
    private boolean mHasMoreHistory = true;
    private long mLastMessageId = 0;
    private int mHistoryPage = 0;
    private boolean mInitialLoaded = false;

    private Runnable mPollRunnable = new Runnable() {
        @Override
        public void run() {
            doPoll();
        }
    };

    public ChatPresenter() {
        this.mRepository = new MessageRepository();
    }

    @Override
    public void init(int targetUserId) {
        this.mTargetUserId = targetUserId;
        mHistoryPage = 0;
        mHasMoreHistory = true;
        mInitialLoaded = false;
        loadMoreHistory();
    }

    @Override
    public void startPolling() {
        if (mPolling) {
            return;
        }
        mPolling = true;
        mHandler.postDelayed(mPollRunnable, POLL_INTERVAL);
    }

    @Override
    public void stopPolling() {
        mPolling = false;
        mHandler.removeCallbacks(mPollRunnable);
    }

    private void doPoll() {
        if (!mPolling) {
            return;
        }
        mRepository.poll(mTargetUserId, mLastMessageId, new ApiCallback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> data) {
                List<ChatMessage> incoming = new ArrayList<>();
                if (data != null) {
                    for (ChatMessage msg : data) {
                        // 自己发的消息本地已插入，跳过
                        if (msg.getFromUserId() == mTargetUserId) {
                            incoming.add(msg);
                        }
                        if (msg.getId() > mLastMessageId) {
                            mLastMessageId = msg.getId();
                        }
                    }
                }
                ChatContract.View view = getView();
                if (view != null && !incoming.isEmpty()) {
                    view.showNewMessages(incoming);
                }
                scheduleNextPoll();
            }

            @Override
            public void onFailure(String msg) {
                // 轮询失败静默，继续下一次
                scheduleNextPoll();
            }
        });
    }

    private void scheduleNextPoll() {
        if (mPolling) {
            mHandler.postDelayed(mPollRunnable, POLL_INTERVAL);
        }
    }

    @Override
    public void sendText(String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }
        mRepository.send(mTargetUserId, content.trim(), ChatMessage.TYPE_TEXT,
                new ApiCallback<ChatMessage>() {
                    @Override
                    public void onSuccess(ChatMessage data) {
                        if (data != null) {
                            if (data.getId() > mLastMessageId) {
                                mLastMessageId = data.getId();
                            }
                            ChatContract.View view = getView();
                            if (view != null) {
                                view.onSendSuccess(data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ChatContract.View view = getView();
                        if (view != null) {
                            view.showError("发送失败: " + msg);
                        }
                    }
                });
    }

    @Override
    public void loadMoreHistory() {
        if (mLoadingHistory || !mHasMoreHistory) {
            return;
        }
        mLoadingHistory = true;
        final int page = mHistoryPage + 1;
        mRepository.history(mTargetUserId, page, HISTORY_PAGE_SIZE,
                new ApiCallback<List<ChatMessage>>() {
                    @Override
                    public void onSuccess(List<ChatMessage> data) {
                        mLoadingHistory = false;
                        mHistoryPage = page;
                        boolean isEmpty = data == null || data.isEmpty();
                        if (isEmpty) {
                            mHasMoreHistory = false;
                        } else {
                            mHasMoreHistory = data.size() >= HISTORY_PAGE_SIZE;
                            long maxId = 0;
                            for (ChatMessage m : data) {
                                if (m.getId() > maxId) {
                                    maxId = m.getId();
                                }
                            }
                            if (maxId > mLastMessageId) {
                                mLastMessageId = maxId;
                            }
                        }
                        ChatContract.View view = getView();
                        if (view != null) {
                            boolean initial = !mInitialLoaded;
                            mInitialLoaded = true;
                            view.showHistory(isEmpty ? null : data, initial);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        mLoadingHistory = false;
                        ChatContract.View view = getView();
                        if (view != null) {
                            view.showError(msg);
                        }
                    }
                });
    }
}
