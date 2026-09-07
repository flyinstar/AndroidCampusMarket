package com.campus.trade.service;

import com.campus.trade.dto.request.MessageSendRequest;
import com.campus.trade.dto.response.ConversationVO;
import com.campus.trade.entity.ChatMessage;

import java.util.List;

/**
 * 消息服务（轮询）
 */
public interface IMessageService {

    ChatMessage send(Integer fromUserId, MessageSendRequest request);

    /** 两人之间历史消息（按 id 倒序，前端前插实现上拉加载） */
    List<ChatMessage> history(Integer me, Integer targetUserId, int page, int size);

    /** 轮询：两人之间 id > sinceId 的消息 */
    List<ChatMessage> poll(Integer me, Integer targetUserId, long sinceId);

    int unreadCount(Integer userId);

    void markAsRead(Integer fromUserId, Integer toUserId);

    List<ConversationVO> conversations(Integer userId);
}
