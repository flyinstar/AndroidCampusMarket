package com.campus.trade.model.repository;

import com.campus.trade.model.entity.ChatMessage;
import com.campus.trade.model.entity.Conversation;
import com.campus.trade.network.ApiClient;
import com.campus.trade.network.ApiRequest;
import com.campus.trade.network.callback.ApiCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息数据仓库（轮询）
 */
public class MessageRepository {

    public void send(int toUserId, String content, int msgType, ApiCallback<ChatMessage> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("toUserId", toUserId);
        params.put("content", content);
        params.put("msgType", msgType);
        ApiRequest.enqueue(ApiClient.getApiService().sendMessage(params), callback);
    }

    public void conversations(ApiCallback<List<Conversation>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getConversations(), callback);
    }

    public void poll(int targetUserId, long sinceId, ApiCallback<List<ChatMessage>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().pollMessages(targetUserId, sinceId), callback);
    }

    public void history(int targetUserId, int page, int size, ApiCallback<List<ChatMessage>> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getMessageHistory(targetUserId, page, size), callback);
    }

    public void unreadCount(ApiCallback<Integer> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().getUnreadCount(), callback);
    }

    public void markAsRead(int fromUserId, ApiCallback<String> callback) {
        ApiRequest.enqueue(ApiClient.getApiService().markAsRead(fromUserId), callback);
    }
}
